package com.hjq.umeng.sdk;

import androidx.annotation.NonNull;
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

        @NonNull
        private final Platform mPlatform;

        @Nullable
        private OnShareListener mListener;

        ShareListenerWrapper(@NonNull SHARE_MEDIA platform, @Nullable OnShareListener listener) {
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
        public void onStart(@NonNull SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onShareStart(mPlatform);
        }

        /**
         * 分享成功的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onResult(@NonNull SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onShareSuccess(mPlatform);
            mListener = null;
        }

        /**
         * 分享失败的回调
         *
         * @param platform      平台名称
         * @param throwable     错误原因
         */
        @Override
        public void onError(@NonNull SHARE_MEDIA platform, @NonNull Throwable throwable) {
            throwable.printStackTrace();
            if (mListener == null) {
                return;
            }
            mListener.onShareFail(mPlatform, throwable);
            mListener = null;
        }

        /**
         * 分享取消的回调
         *
         * @param platform      平台名称
         */
        @Override
        public void onCancel(@NonNull SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onShareCancel(mPlatform);
            mListener = null;
        }
    }

    public interface OnShareListener {

        /**
         * 分享开始
         *
         * @param platform      平台对象
         */
        default void onShareStart(@NonNull Platform platform) {
            // default implementation ignored
        }

        /**
         * 分享成功的回调
         *
         * @param platform      平台对象
         */
        void onShareSuccess(@NonNull Platform platform);

        /**
         * 分享失败的回调
         *
         * @param platform      平台对象
         * @param throwable     错误原因
         */
        default void onShareFail(@NonNull Platform platform, @NonNull Throwable throwable) {
            // default implementation ignored
        }

        /**
         * 分享取消的回调
         *
         * @param platform      平台对象
         */
        default void onShareCancel(@NonNull Platform platform) {
            // default implementation ignored
        }
    }
}