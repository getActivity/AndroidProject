package com.hjq.umeng;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/03
 *    desc   : 友盟第三方分享
 */
public final class UmengShare {

    public static final class ShareData {

        private Context mContext;
        // 分享标题
        private String mShareTitle;
        // 分享 URL
        private String mShareUrl;
        // 分享描述
        private String mShareDescription;
        // 分享缩略图
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

        public void setShareLogo(@DrawableRes int resId) {
            mShareLogo = new UMImage(mContext, resId);
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

    public static final class ShareListenerWrapper implements UMShareListener {

        private OnShareListener mListener;
        private Platform mPlatform;

        ShareListenerWrapper(SHARE_MEDIA platform, OnShareListener listener) {
            mListener = listener;
            switch (platform) {
                case QQ:
                    mPlatform = Platform.QQ;
                    break;
                case QZONE:
                    mPlatform = Platform.QZONE;
                    break;
                case WEIXIN:
                    mPlatform = Platform.WEIXIN;
                    break;
                case WEIXIN_CIRCLE:
                    mPlatform = Platform.CIRCLE;
                    break;
                case SINA:
                    mPlatform = Platform.SINA;
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
        public void onStart(SHARE_MEDIA platform) {
//            mListener.onStart(mPlatform);
        }

        /**
         * 授权成功的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            mListener.onSucceed(mPlatform);
        }

        /**
         * 授权失败的回调
         *
         * @param platform      平台名称
         * @param t             错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            mListener.onError(mPlatform, t);
        }

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            mListener.onCancel(mPlatform);
        }
    }

    public interface OnShareListener {

//        /**
//         * 分享开始的回调
//         *
//         * @param platform      平台名称
//         */
//        void onStart(Platform platform);

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
        void onError(Platform platform, Throwable t);

        /**
         * 分享取消的回调
         *
         * @param platform      平台名称
         */
        void onCancel(Platform platform);
    }
}