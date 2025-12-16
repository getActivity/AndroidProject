package com.hjq.demo.manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import com.hjq.base.BaseDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/01/29
 *    desc   : Dialog 显示管理类
 */
@SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
public final class DialogManager implements LifecycleEventObserver, BaseDialog.OnDismissListener {

    @NonNull
    private static final Map<LifecycleOwner, DialogManager> DIALOG_MANAGER = new HashMap<>();

    public static DialogManager getInstance(LifecycleOwner lifecycleOwner) {
        DialogManager manager = DIALOG_MANAGER.get(lifecycleOwner);
        if (manager == null) {
            manager = new DialogManager(lifecycleOwner);
            DIALOG_MANAGER.put(lifecycleOwner, manager);
        }
        return manager;
    }

    @NonNull
    private final List<BaseDialog> mDialogList = new ArrayList<>();

    @NonNull
    private final Map<BaseDialog, Integer> mDialogPriority = new HashMap<>();

    private DialogManager(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    /**
     * 获取所有排队显示的对话框
     */
    @NonNull
    public List<BaseDialog> getDialogList() {
        return mDialogList;
    }

    public void addDialog(@Nullable BaseDialog dialog) {
        addDialog(dialog, 0);
    }

    /**
     * 添加 Dialog 对象
     *
     * @param priority        弹窗优先级
     */
    public void addDialog(@Nullable BaseDialog dialog, int priority) {
        if (dialog == null) {
            return;
        }

        if (mDialogList.contains(dialog)) {
            return;
        }

        int dialogIndex = mDialogList.size();
        for (int i = 0; i < mDialogList.size(); i++) {
            BaseDialog itemDialog = mDialogList.get(i);
            Integer itemPriority = mDialogPriority.get(itemDialog);
            if (itemPriority == null) {
                continue;
            }
            if (priority > itemPriority && !itemDialog.isShowing()) {
                dialogIndex = i;
            }
        }
        mDialogList.add(dialogIndex, dialog);
        mDialogPriority.put(dialog, priority);
    }

    /**
     * 排队显示 Dialog
     */
    public void startShow() {
        if (mDialogList.isEmpty()) {
            return;
        }
        BaseDialog firstDialog = mDialogList.get(0);
        if (!firstDialog.isShowing()) {
            firstDialog.addOnDismissListener(this);
            firstDialog.show();
        }
    }

    /**
     * 取消所有 Dialog 的显示
     */
    public void clearShow() {
        if (mDialogList.isEmpty()) {
            return;
        }
        BaseDialog firstDialog = mDialogList.get(0);
        if (firstDialog.isShowing()) {
            firstDialog.removeOnDismissListener(this);
            firstDialog.dismiss();
        }
        mDialogList.clear();
        mDialogPriority.clear();
    }

    @Override
    public void onDismiss(@NonNull BaseDialog dialog) {
        dialog.removeOnDismissListener(this);
        mDialogList.remove(dialog);
        mDialogPriority.remove(dialog);
        for (BaseDialog nextDialog : mDialogList) {
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
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event != Lifecycle.Event.ON_DESTROY) {
            return;
        }
        DIALOG_MANAGER.remove(source);
        source.getLifecycle().removeObserver(this);
        clearShow();
    }
}