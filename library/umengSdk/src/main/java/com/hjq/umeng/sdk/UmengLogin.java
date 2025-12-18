package com.hjq.umeng.sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/03
 *    desc   : 友盟第三方登录
 */
public final class UmengLogin {

    public static final class LoginData {

        /** 用户 id */
        @Nullable
        private String mId;

        /** 昵称 */
        @Nullable
        private String mName;

        /** 性别 */
        @Nullable
        private String mSex;

        /** 头像 */
        @Nullable
        private String mAvatar;

        /** Token */
        @Nullable
        private String mToken;

        LoginData(@Nullable Map<String, String> data) {
            if (data == null) {
                return;
            }
            // 第三方登录获取用户资料：https://developer.umeng.com/docs/66632/detail/66639#h3-u83B7u53D6u7528u6237u8D44u6599
            mId = data.get("uid");
            mName =  data.get("name");
            mSex = data.get("gender");
            mAvatar = data.get("iconurl");
            mToken = data.get("accessToken");
        }

        @Nullable
        public String getName() {
            return mName;
        }

        @Nullable
        public String getSex() {
            return mSex;
        }

        @Nullable
        public String getAvatar() {
            return mAvatar;
        }

        @Nullable
        public String getId() {
            return mId;
        }

        @Nullable
        public String getToken() {
            return mToken;
        }

        /**
         * 判断当前的性别是否为男性
         */
        public boolean isMan() {
            return "男".equals(mSex);
        }
    }

    /**
     * 为什么要包起来？因为友盟会将监听回调（UMAuthListener）持有成静态的，回调完没有及时释放
     */
    public static final class LoginListenerWrapper implements UMAuthListener {

        @NonNull
        private final Platform mPlatform;

        @Nullable
        private OnLoginListener mListener;

        LoginListenerWrapper(@NonNull SHARE_MEDIA platform, @Nullable OnLoginListener listener) {
            mListener = listener;
            switch (platform) {
                case QQ:
                    mPlatform = Platform.QQ;
                    break;
                case WEIXIN:
                    mPlatform = Platform.WECHAT;
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
        public void onStart(@NonNull SHARE_MEDIA platform) {
            if (mListener == null) {
                return;
            }
            mListener.onLoginStart(mPlatform);
        }

        /**
         * 授权成功的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         * @param data          用户资料返回
         */
        @Override
        public void onComplete(@NonNull SHARE_MEDIA platform, int action, @Nullable Map<String, String> data) {
            if (mListener == null) {
                return;
            }
            mListener.onLoginSuccess(mPlatform, new LoginData(data));
            mListener = null;
        }

        /**
         * 授权失败的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         * @param t             错误原因
         */
        @Override
        public void onError(@NonNull SHARE_MEDIA platform, int action, @NonNull Throwable t) {
            t.printStackTrace();
            if (mListener == null) {
                return;
            }
            mListener.onLoginFail(mPlatform, t);
            mListener = null;
        }

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         */
        @Override
        public void onCancel(@NonNull SHARE_MEDIA platform, int action) {
            if (mListener == null) {
                return;
            }
            mListener.onLoginCancel(mPlatform);
            mListener = null;
        }
    }

    public interface OnLoginListener {

        /**
         * 授权开始
         *
         * @param platform      平台对象
         */
        default void onLoginStart(@NonNull Platform platform) {
            // default implementation ignored
        }

        /**
         * 授权成功的回调
         *
         * @param platform      平台对象
         * @param data          用户资料返回
         */
        void onLoginSuccess(@NonNull Platform platform, @NonNull LoginData data);

        /**
         * 授权失败的回调
         *
         * @param platform      平台对象
         * @param throwable     错误原因
         */
        default void onLoginFail(@NonNull Platform platform, @NonNull Throwable throwable) {
            // default implementation ignored
        }

        /**
         * 授权取消的回调
         *
         * @param platform      平台对象
         */
        default void onLoginCancel(@NonNull Platform platform) {
            // default implementation ignored
        }
    }
}