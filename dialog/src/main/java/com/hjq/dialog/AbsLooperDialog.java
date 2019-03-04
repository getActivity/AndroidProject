package com.hjq.dialog;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.dialog.widget.LoopView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/17
 *    desc   : 滚动选择列表基类
 */
public class AbsLooperDialog {

    public static abstract class Builder<B extends Builder>
            extends BaseDialogFragment.Builder<B>
            implements View.OnClickListener {

        private TextView mCancelView;
        private TextView mTitleView;
        private TextView mConfirmView;
        private LinearLayout mLinearLayout;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_wheel);
            setGravity(Gravity.BOTTOM);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            mCancelView = findViewById(R.id.tv_dialog_wheel_cancel);
            mTitleView = findViewById(R.id.tv_dialog_wheel_title);
            mConfirmView = findViewById(R.id.tv_dialog_wheel_confirm);
            mLinearLayout = findViewById(R.id.ll_dialog_wheel_list);

            mCancelView.setOnClickListener(this);
            mConfirmView.setOnClickListener(this);
        }

        public B setTitle(int resId) {
            return setTitle(getContext().getResources().getText(resId));
        }
        public B setTitle(CharSequence text) {
            mTitleView.setText(text);
            return (B) this;
        }

        public B setCancel(int resId) {
            return setCancel(getContext().getText(resId));
        }
        public B setCancel(CharSequence text) {
            mCancelView.setText(text);
            return (B) this;
        }

        public B setConfirm(int resId) {
            return setConfirm(getContext().getText(resId));
        }
        public B setConfirm(CharSequence text) {
            mConfirmView.setText(text);
            return (B) this;
        }

        @Override
        public void onClick(View v) {
            if (v == mCancelView) {
                onCancel();
            }else if (v == mConfirmView) {
                onConfirm();
            }
        }

        protected abstract void onConfirm();

        protected abstract void onCancel();

        protected LoopView createLoopView() {

            LoopView loopView = new LoopView(getContext());
            loopView.setTextSize(20);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            loopView.setLayoutParams(layoutParams);
            mLinearLayout.addView(loopView);

            return loopView;
        }
    }
}
