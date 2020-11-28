package com.hjq.demo.http.request;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 修改密码
 */
public final class PasswordApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/password";
    }

    /** 手机号（已登录可不传） */
    private String phone;
    /** 验证码 */
    private String code;
    /** 密码 */
    private String password;

    public PasswordApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public PasswordApi setCode(String code) {
        this.code = code;
        return this;
    }

    public PasswordApi setPassword(String password) {
        this.password = password;
        return this;
    }
}