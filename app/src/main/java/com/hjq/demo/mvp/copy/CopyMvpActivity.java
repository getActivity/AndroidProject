package com.hjq.demo.mvp.copy;

import android.view.View;

import com.hjq.demo.mvp.MvpActivity;

import java.util.List;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的MVP Activity 类
 */
public class CopyMvpActivity extends MvpActivity<CopyPresenter> implements CopyContract.View {

    @Override
    protected CopyPresenter initPresenter() {
        return new CopyPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    public void onLogin(View view) {
        // 登录操作
        getPresenter().login("账户", "密码");
    }

    /**
     * {@link CopyContract.View}
     */

    @Override
    public void loginError(String msg) {
        toast(msg);
    }

    @Override
    public void loginSuccess(List<String> data) {
        toast("登录成功了");
    }
}