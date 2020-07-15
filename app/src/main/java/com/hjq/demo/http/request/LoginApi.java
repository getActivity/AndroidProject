package com.hjq.demo.http.request;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 用户登录
 */
public final class LoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/login";
    }

    /** 手机号 */
    private String phone;
    /** 登录密码 */
    private String password;

    public LoginApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public LoginApi setPassword(String password) {
        this.password = password;
        return this;
    }
}