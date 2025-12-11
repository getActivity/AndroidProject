package com.hjq.demo.http.api;

import androidx.annotation.NonNull;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 退出登录
 */
public final class LogoutApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "user/logout";
    }
}