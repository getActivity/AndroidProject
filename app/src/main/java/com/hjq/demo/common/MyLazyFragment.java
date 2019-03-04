package com.hjq.demo.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.hjq.umeng.UmengHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中 Fragment 懒加载基类
 */
public abstract class MyLazyFragment extends UILazyFragment {

    private Unbinder mButterKnife; // View注解

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mButterKnife = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 友盟统计
        UmengHelper.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 友盟统计
        UmengHelper.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mButterKnife.unbind();
    }

    @Nullable
    public TitleBar getTitleBar() {
        if (getTitleBarId() > 0 && findViewById(getTitleBarId()) instanceof TitleBar) {
            return findViewById(getTitleBarId());
        }
        return null;
    }

    /**
     * 显示吐司
     */
    public void toast(CharSequence s) {
        ToastUtils.show(s);
    }

    public void toast(int id) {
        ToastUtils.show(id);
    }

    public void toast(Object object) {
        ToastUtils.show(object);
    }
}