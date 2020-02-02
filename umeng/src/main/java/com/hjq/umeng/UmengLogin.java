package com.hjq.umeng;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.SoftReference;
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
        private final String mId;
        /** 昵称 */
        private final String mName;
        /** 性别 */
        private final String mSex;
        /** 头像 */
        private final String mAvatar;
        /** Token */
        private final String mToken;

        LoginData(Map<String, String> data) {
            // 第三方登录获取用户资料：https://developer.umeng.com/docs/66632/detail/66639#h3-u83B7u53D6u7528u6237u8D44u6599
            mId = data.get("uid");
            mName =  data.get("name");
            mSex = data.get("gender");
            mAvatar = data.get("iconurl");
            mToken = data.get("accessToken");
        }

        public String getName() {
            return mName;
        }

        public String getSex() {
            return mSex;
        }

        public String getAvatar() {
            return mAvatar;
        }

        public String getId() {
            return mId;
        }

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
     * 为什么要用软引用，因为友盟会将监听回调（UMAuthListener）持有成静态的
     */
    public static final class LoginListenerWrapper extends SoftReference<OnLoginListener> implements UMAuthListener {

        private final Platform mPlatform;

        LoginListenerWrapper(SHARE_MEDIA platform, OnLoginListener listener) {
            super(listener);
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
        public void onStart(SHARE_MEDIA platform) {}

        /**
         * 授权成功的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         * @param data          用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (get() != null) {
                get().onSucceed(mPlatform, new LoginData(data));
            }
        }

        /**
         * 授权失败的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         * @param t             错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if (get() != null) {
                get().onError(mPlatform, t);
            }
        }

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if (get() != null) {
                get().onCancel(mPlatform);
            }
        }
    }

    public interface OnLoginListener {

        /**
         * 授权成功的回调
         *
         * @param platform      平台名称
         * @param data          用户资料返回
         */
        void onSucceed(Platform platform, LoginData data);

        /**
         * 授权失败的回调
         *
         * @param platform      平台名称
         * @param t             错误原因
         */
        default void onError(Platform platform, Throwable t) {}

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         */
        default void onCancel(Platform platform) {}
    }
}