package com.hjq.demo.mvp.copy;

import com.hjq.demo.mvp.IMvpView;

import java.util.List;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的契约类
 */
public class CopyContract {

    /**
     * 这里的 {@link IMvpView} 可继承可不继承，看实际情况而定
     */
    public interface View /*extends IMvpView*/ {

        void loginError(String msg);

        void loginSuccess(List<String> data);
    }

    public interface Presenter {
        void login(String account, String password);
    }
}