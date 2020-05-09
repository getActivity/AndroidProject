package com.hjq.umeng;

import android.content.Context;

import androidx.annotation.DrawableRes;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.lang.ref.SoftReference;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/03
 *    desc   : 友盟第三方分享
 */
public final class UmengShare {

    public static final class ShareData {

        /** 上下文对象 */
        private final Context mContext;
        /** 分享标题 */
        private String mShareTitle;
        /** 分享 URL */
        private String mShareUrl;
        /** 分享描述 */
        private String mShareDescription;
        /** 分享缩略图 */
        private UMImage mShareLogo;

        public ShareData(Context context) {
            mContext = context;
        }

        public void setShareTitle(String title) {
            mShareTitle = title;
        }

        public void setShareUrl(String url) {
            mShareUrl = url;
        }

        public void setShareDescription(String description) {
            mShareDescription = description;
        }

        public void setShareLogo(String logo) {
            mShareLogo = new UMImage(mContext, logo);
        }

        public void setShareLogo(@DrawableRes int id) {
            mShareLogo = new UMImage(mContext, id);
        }

        public String getShareUrl() {
            return mShareUrl;
        }

        UMWeb create() {
            UMWeb content = new UMWeb(mShareUrl);
            content.setTitle(mShareTitle);
            if (mShareLogo != null) {
                content.setThumb(mShareLogo);
            }
            content.setDescription(mShareDescription);
            return content;
        }
    }

    /**
     * 为什么要用软引用，因为友盟会将监听回调（UMShareListener）持有成静态的
     */
    public static final class ShareListenerWrapper extends SoftReference<OnShareListener> implements UMShareListener {

        private final Platform mPlatform;

        ShareListenerWrapper(SHARE_MEDIA platform, OnShareListener listener) {
            super(listener);
            switch (platform) {
                case QQ:
                    mPlatform = Platform.QQ;
                    break;
                case QZONE:
                    mPlatform = Platform.QZONE;
                    break;
                case WEIXIN:
                    mPlatform = Platform.WECHAT;
                    break;
                case WEIXIN_CIRCLE:
                    mPlatform = Platform.CIRCLE;
                    break;
                default:
                    throw new IllegalStateException("are you ok?");
            }
        }

        /**
         * 授权开始的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {}

        /**
         * 授权成功的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (get() != null) {
                get().onSucceed(mPlatform);
            }
        }

        /**
         * 授权失败的回调
         *
         * @param platform      平台名称
         * @param t             错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (get() != null) {
                get().onError(mPlatform, t);
            }
        }

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (get() != null) {
                get().onCancel(mPlatform);
            }
        }
    }

    public interface OnShareListener {

        /**
         * 分享成功的回调
         *
         * @param platform      平台名称
         */
        void onSucceed(Platform platform);

        /**
         * 分享失败的回调
         *
         * @param platform      平台名称
         * @param t             错误原因
         */
        default void onError(Platform platform, Throwable t) {}

        /**
         * 分享取消的回调
         *
         * @param platform      平台名称
         */
        default void onCancel(Platform platform) {}
    }
}