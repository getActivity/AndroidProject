package com.hjq.demo.mvp;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : MVP 通用性接口
 */
public interface IMvpView {

    /**
     * 用于页面请求数据时显示加载状态
     */
    void showLoading();

    /**
     * 用于请求的数据为空的状态
     */
    void showEmpty();

    /**
     * 用于请求数据出错
     */
    void showError();

    /**
     * 用于请求数据完成
     */
    void loadingComplete();
}