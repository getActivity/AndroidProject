package com.hjq.dialog;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : 消息对话框
 */
public final class MessageDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements View.OnClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true; // 设置点击按钮后自动消失

        private TextView mTitleView;
        private TextView mMessageView;

        private TextView mCancelView;
        private View mLineView;
        private TextView mConfirmView;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_message);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setGravity(Gravity.CENTER);

            mTitleView = findViewById(R.id.tv_dialog_message_title);
            mMessageView = findViewById(R.id.tv_dialog_message_message);

            mCancelView  = findViewById(R.id.tv_dialog_message_cancel);
            mLineView = findViewById(R.id.v_dialog_message_line);
            mConfirmView  = findViewById(R.id.tv_dialog_message_confirm);

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

        public Builder setMessage(int resId) {
            return setMessage(getContext().getText(resId));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
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
            if ("".equals(mMessageView.getText().toString())) {
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
                mListener.onConfirm(getDialog());
            }else if (v == mCancelView) {
                mListener.onCancel(getDialog());
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(Dialog dialog);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}