package com.hjq.umeng;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/03
 *    desc   : 友盟客户端
 */
public final class UmengClient {

    /**
     * 初始化友盟相关 SDK
     */
    public static void init(Application application) {

        try {
            Bundle metaData = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA).metaData;
            // 友盟统计，API 说明：https://developer.umeng.com/docs/66632/detail/101814#h1-u521Du59CBu5316u53CAu901Au7528u63A5u53E32
            UMConfigure.init(application, String.valueOf(metaData.get("UMENG_APPKEY")),"umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
            // 选用自动采集模式：https://developer.umeng.com/docs/119267/detail/118588#h1-u9875u9762u91C7u96C63
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
            // 初始化各个平台的 Key
            PlatformConfig.setWeixin(String.valueOf(metaData.get("WX_APPID")), String.valueOf(metaData.get("WX_APPKEY")));
            PlatformConfig.setQQZone(String.valueOf(metaData.get("QQ_APPID")), String.valueOf(metaData.get("QQ_APPKEY")));

            //PlatformConfig.setSinaWeibo(String.valueOf(metaData.get("SN_APPID")), String.valueOf(metaData.get("SN_APPKEY")),"http://sns.whalecloud.com");
            // 豆瓣RENREN平台目前只能在服务器端配置
            //PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
            //PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
            //PlatformConfig.setAlipay("2015111700822536");
            //PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
            //PlatformConfig.setPinterest("1439206");
            //PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
            //PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
            //PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
            //PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享
     *
     * @param activity              Activity对象
     * @param platform              分享平台
     * @param data                  分享内容
     * @param listener              分享监听
     */
    public static void share(Activity activity, Platform platform, UmengShare.ShareData data, UmengShare.OnShareListener listener) {
        if (isAppInstalled(activity, platform.getPackageName())) {
            new ShareAction(activity)
                    .setPlatform(platform.getThirdParty())
                    .withMedia(data.create())
                    .setCallback(listener != null ? new UmengShare.ShareListenerWrapper(platform.getThirdParty(), listener) : null)
                    .share();
        } else {
            // 当分享的平台软件可能没有被安装的时候
            if (listener != null) {
                listener.onError(platform, new PackageManager.NameNotFoundException("Is not installed"));
            }
        }
    }

    /**
     * 登录
     *
     * @param activity              Activity对象
     * @param platform              登录平台
     * @param listener              登录监听
     */
    public static void login(Activity activity, Platform platform, UmengLogin.OnLoginListener listener) {
        if (isAppInstalled(activity, platform)) {

            try {
                // 删除旧的第三方登录授权
                UMShareAPI.get(activity).deleteOauth(activity, platform.getThirdParty(), null);
                // 要先等上面的代码执行完毕之后
                Thread.sleep(200);
                // 开启新的第三方登录授权
                UMShareAPI.get(activity).getPlatformInfo(activity, platform.getThirdParty(), listener != null ? new UmengLogin.LoginListenerWrapper(platform.getThirdParty(), listener) : null);
            } catch (InterruptedException ignored) {}

        } else {
            // 当登录的平台软件可能没有被安装的时候
            if (listener != null) {
                listener.onError(platform, new PackageManager.NameNotFoundException("Is not installed"));
            }
        }
    }

    /**
     * 设置回调
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 判断 App 是否安装
     */
    public static boolean isAppInstalled(Context context, Platform platform) {
        return isAppInstalled(context, platform.getPackageName());
    }

    private static boolean isAppInstalled(Context context, @NonNull final String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }
}