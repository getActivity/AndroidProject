package com.hjq.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/24
 *    desc   : DialogFragment 基类
 */
public abstract class BaseDialogFragment extends DialogFragment {

    /**
     * 父类同名方法简化
     */
    public void show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), getClass().getName());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 不使用 Dialog，替换成 BaseDialog 对象
        return new BaseDialog(getActivity());
    }
}