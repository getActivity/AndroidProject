package com.hjq.umeng;

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

        // 昵称
        private String mName;
        // 性别
        private String mSex;
        // 头像
        private String mIcon;
        // id
        private String mId;
        // token
        private String mToken;

        LoginData(Map<String, String> data) {
            // 第三方登录获取用户资料：https://developer.umeng.com/docs/66632/detail/66639#h3-u83B7u53D6u7528u6237u8D44u6599
            mName =  data.get("name");
            mSex = data.get("gender");
            mIcon = data.get("iconurl");
            mId = data.get("uid");
            mToken = data.get("accessToken");
        }

        public String getName() {
            return mName;
        }

        public String getSex() {
            return mSex;
        }

        public String getIcon() {
            return mIcon;
        }

        public String getId() {
            return mId;
        }

        public String getToken() {
            return mToken;
        }
    }

    public static final class LoginListenerWrapper implements UMAuthListener {

        private OnLoginListener mListener;
        private Platform mPlatform;

        LoginListenerWrapper(SHARE_MEDIA platform, OnLoginListener listener) {
            mListener = listener;
            switch (platform) {
                case QQ:
                    mPlatform = Platform.QQ;
                    break;
                case WEIXIN:
                    mPlatform = Platform.WEIXIN;
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
         * @param action        行为序号，开发者用不上
         * @param data          用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            mListener.onSucceed(mPlatform, new LoginData(data));
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
            mListener.onError(mPlatform, t);
        }

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         * @param action        行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            mListener.onCancel(mPlatform);
        }
    }

    public interface OnLoginListener {

//        /**
//         * 授权开始的回调
//         *
//         * @param platform      平台名称
//         */
//        void onStart(Platform platform);

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
        void onError(Platform platform, Throwable t);

        /**
         * 授权取消的回调
         *
         * @param platform      平台名称
         */
        void onCancel(Platform platform);
    }
}