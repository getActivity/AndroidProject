package com.hjq.umeng;

import androidx.annotation.Nullable;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/03
 *    desc   : 友盟第三方分享
 */
public final class UmengShare {

    /**
     * 为什么要包起来？因为友盟会将监听回调（UMShareListener）持有成静态的，回调完没有及时释放
     */
    public static final class ShareListenerWrapper implements UMShareListener {

        private final Platform mPlatform;
        @Nullable
        private OnShareListener mListener;

        ShareListenerWrapper(SHARE_MEDIA platform, @Nullable OnShareListener listener) {
            mListener = listener;
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
         * 分享开始的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onStart(mPlatform);
        }

        /**
         * 分享成功的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onSucceed(mPlatform);
            mListener = null;
        }

        /**
         * 分享失败的回调
         *
         * @param platform      平台名称
         * @param t             错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            t.printStackTrace();
            if (mListener == null) {
                return;
            }
            mListener.onError(mPlatform, t);
            mListener = null;
        }

        /**
         * 分享取消的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onCancel(mPlatform);
            mListener = null;
        }
    }

    public interface OnShareListener {

        /**
         * 分享开始
         *
         * @param platform      平台对象
         */
        default void onStart(Platform platform) {}

        /**
         * 分享成功的回调
         *
         * @param platform      平台对象
         */
        void onSucceed(Platform platform);

        /**
         * 分享失败的回调
         *
         * @param platform      平台对象
         * @param t             错误原因
         */
        default void onError(Platform platform, Throwable t) {}

        /**
         * 分享取消的回调
         *
         * @param platform      平台对象
         */
        default void onCancel(Platform platform) {}
    }
}