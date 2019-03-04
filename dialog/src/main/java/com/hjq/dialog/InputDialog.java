package com.hjq.dialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 输入对话框
 */
public final class InputDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements View.OnClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true; // 设置点击按钮后自动消失

        private TextView mTitleView;
        private EditText mContentView;

        private TextView mCancelView;
        private View mLineView;
        private TextView mConfirmView;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_input);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setGravity(Gravity.CENTER);

            mTitleView = findViewById(R.id.tv_dialog_input_title);
            mContentView = findViewById(R.id.tv_dialog_input_message);

            mCancelView  = findViewById(R.id.tv_dialog_input_cancel);
            mLineView = findViewById(R.id.v_dialog_input_line);
            mConfirmView  = findViewById(R.id.tv_dialog_input_confirm);

            mCancelView.setOnClickListener(this);
            mConfirmView.setOnClickListener(this);
        }

        public Builder setTitle(int resId) {
            return setTitle(getString(resId));
        }
        public Builder setTitle(CharSequence text) {
            mTitleView.setText(text);
            return this;
        }

        public Builder setHint(int resId) {
            return setHint(getContext().getText(resId));
        }
        public Builder setHint(CharSequence text) {
            mContentView.setHint(text);
            return this;
        }

        public Builder setContent(int resId) {
            return setContent(getContext().getText(resId));
        }
        public Builder setContent(CharSequence text) {
            mContentView.setText(text);
            int index = mContentView.getText().toString().length();
            if (index > 0) {
                mContentView.requestFocus();
                mContentView.setSelection(index);
            }
            return this;
        }

        public Builder setCancel(int resId) {
            return setCancel(getContext().getText(resId));
        }
        public Builder setCancel(CharSequence text) {
            mCancelView.setText(text);

            mCancelView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            mLineView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            mConfirmView.setBackgroundResource((text == null || "".equals(text.toString())) ?
                    R.drawable.dialog_message_one_button : R.drawable.dialog_message_right_button);
            return this;
        }

        public Builder setConfirm(int resId) {
            return setConfirm(getContext().getText(resId));
        }
        public Builder setConfirm(CharSequence text) {
            mConfirmView.setText(text);
            return this;
        }

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public Builder setListener(OnListener l) {
            mListener = l;
            return this;
        }

        @Override
        public BaseDialog create() {
            // 如果标题为空就隐藏
            if ("".equals(mTitleView.getText().toString())) {
                mTitleView.setVisibility(View.GONE);
            }
            // 如果内容为空就抛出异常
            if ("".equals(mContentView.getText().toString())) {
                throw new IllegalArgumentException("Dialog message not null");
            }
            return super.create();
        }

        /**
         * {@link View.OnClickListener}
         */
        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener == null) return;

            if (v == mConfirmView) {
                // 判断输入是否为空
                mListener.onConfirm(getDialog(), mContentView.getText().toString());
            }else if (v == mCancelView) {
                mListener.onCancel(getDialog());
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(Dialog dialog, String content);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}