package com.hjq.umeng;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/30
 *    desc   : 友盟辅助类
 */
public class UmengHelper {

    /**
     * 初始化友盟相关 SDK
     */
    public static void init(Context context) {
        // 友盟统计
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * Activity 统计
     */
    public static void onResume(Activity activity) {
        // 手动统计页面
        MobclickAgent.onPageStart(activity.getClass().getSimpleName());
        // 友盟统计
        MobclickAgent.onResume(activity);
    }

    /**
     * Activity 统计
     */
    public static void onPause(Activity activity) {
        // 手动统计页面，必须保证 onPageEnd 在 onPause 之前调用，因为SDK会在 onPause 中保存onPageEnd统计到的页面数据
        MobclickAgent.onPageEnd(activity.getClass().getSimpleName());
        // 友盟统计
        MobclickAgent.onPause(activity);
    }

    /**
     * Fragment 统计
     */
    public static void onResume(Fragment fragment) {
        // 友盟统计
        MobclickAgent.onResume(fragment.getContext());
    }

    /**
     * Fragment 统计
     */
    public static void onPause(Fragment fragment) {
        // 友盟统计
        MobclickAgent.onPause(fragment.getContext());
    }
}
