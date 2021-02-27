package com.hjq.demo.manager;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.base.BaseDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/01/29
 *    desc   : Dialog 显示管理类
 */
public final class DialogManager implements LifecycleEventObserver, BaseDialog.OnDismissListener {

    private final static HashMap<LifecycleOwner, DialogManager> DIALOG_MANAGER = new HashMap<>();

    public static DialogManager getInstance(LifecycleOwner lifecycleOwner) {
        DialogManager manager = DIALOG_MANAGER.get(lifecycleOwner);
        if (manager == null) {
            manager = new DialogManager(lifecycleOwner);
            DIALOG_MANAGER.put(lifecycleOwner, manager);
        }
        return manager;
    }

    private final List<BaseDialog> mDialogs = new ArrayList<>();

    private DialogManager(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    /**
     * 排队显示 Dialog
     */
    public void addShow(BaseDialog dialog) {
        if (dialog == null || dialog.isShowing()) {
            throw new IllegalStateException("are you ok?");
        }
        mDialogs.add(dialog);
        BaseDialog firstDialog = mDialogs.get(0);
        if (!firstDialog.isShowing()) {
            firstDialog.addOnDismissListener(this);
            firstDialog.show();
        }
    }

    /**
     * 取消所有 Dialog 的显示
     */
    public void clearShow() {
        if (mDialogs.isEmpty()) {
            return;
        }
        BaseDialog firstDialog = mDialogs.get(0);
        if (firstDialog.isShowing()) {
            firstDialog.removeOnDismissListener(this);
            firstDialog.dismiss();
        }
        mDialogs.clear();
    }

    @Override
    public void onDismiss(BaseDialog dialog) {
        dialog.removeOnDismissListener(this);
        mDialogs.remove(dialog);
        for (BaseDialog nextDialog : mDialogs) {
            if (!nextDialog.isShowing()) {
                nextDialog.addOnDismissListener(this);
                nextDialog.show();
                break;
            }
        }
    }

    /**
     * {@link LifecycleEventObserver}
     */

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        if (event != Lifecycle.Event.ON_DESTROY) {
            return;
        }
        DIALOG_MANAGER.remove(lifecycleOwner);
        lifecycleOwner.getLifecycle().removeObserver(this);
        clearShow();
    }
}