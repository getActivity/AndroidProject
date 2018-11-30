package com.hjq.demo.mvp.copy;

import com.hjq.demo.mvp.MvpPresenter;

import java.util.List;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的业务处理类
 */
public class CopyPresenter extends MvpPresenter<CopyMvpActivity> implements CopyContract.Presenter {

    private CopyModel mModel;

    @Override
    public void start() {
        mModel = new CopyModel();
    }

    @Override
    public void login(String account, String password) {
        mModel.setAccount(account);
        mModel.setPassword(password);
        mModel.setListener(new CopyOnListener() {

            @Override
            public void onSucceed(List<String> data) {
                getView().loginSuccess(data);
            }

            @Override
            public void onFail(String msg) {
                getView().loginError(msg);
            }
        });
        mModel.login();
    }
}