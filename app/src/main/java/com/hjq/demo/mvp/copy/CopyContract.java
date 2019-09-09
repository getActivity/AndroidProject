package com.hjq.demo.mvp.copy;

import com.hjq.demo.mvp.IMvpView;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的契约类
 */
public final class CopyContract {

    public interface View extends IMvpView {

        void loginSuccess(List<String> data);

        void loginError(String msg);
    }

    public interface Presenter {

        void login(String account, String password);
    }
}