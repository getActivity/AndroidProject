package com.hjq.umeng.sdk;

import androidx.annotation.NonNull;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/03
 *    desc   : 友盟平台
 */
public enum Platform {

    /** 微信 */
    WECHAT(SHARE_MEDIA.WEIXIN, "com.tencent.mm"),
    /** 微信朋友圈 */
    CIRCLE(SHARE_MEDIA.WEIXIN_CIRCLE, "com.tencent.mm"),

    /** QQ */
    QQ(SHARE_MEDIA.QQ, "com.tencent.mobileqq"),
    /** QQ 空间 */
    QZONE(SHARE_MEDIA.QZONE, "com.tencent.mobileqq");

    /** 第三方平台 */
    @NonNull
    private final SHARE_MEDIA mThirdParty;

    /** 第三方包名 */
    @NonNull
    private final String mPackageName;

    Platform(@NonNull SHARE_MEDIA thirdParty, @NonNull String packageName) {
        mThirdParty = thirdParty;
        mPackageName = packageName;
    }

    @NonNull
    SHARE_MEDIA getThirdParty() {
        return mThirdParty;
    }

    @NonNull
    String getPackageName() {
        return mPackageName;
    }
}