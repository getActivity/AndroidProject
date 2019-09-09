package com.hjq.umeng;

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
    private final SHARE_MEDIA mThirdParty;
    /** 第三方包名 */
    private final String mPackageName;

    Platform(SHARE_MEDIA thirdParty, String packageName) {
        mThirdParty = thirdParty;
        mPackageName = packageName;
    }

    SHARE_MEDIA getThirdParty() {
        return mThirdParty;
    }

    String getPackageName() {
        return mPackageName;
    }
}