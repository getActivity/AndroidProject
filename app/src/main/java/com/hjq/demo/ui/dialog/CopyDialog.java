package com.hjq.demo.ui.dialog;

import android.support.v4.app.FragmentActivity;
import android.view.Gravity;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 可进行拷贝的副本
 */
public final class CopyDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder> {

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.item_copy);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);
            setGravity(Gravity.BOTTOM);

        }
    }
}