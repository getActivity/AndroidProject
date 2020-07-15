package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.StringRes;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 输入对话框
 */
public final class InputDialog {

    public static final class Builder
            extends UIDialog.Builder<Builder>
            implements BaseDialog.OnShowListener {

        private OnListener mListener;
        private final EditText mInputView;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.input_dialog);

            mInputView = findViewById(R.id.tv_input_message);

            addOnShowListener(this);
        }

        public Builder setHint(@StringRes int id) {
            return setHint(getString(id));
        }
        public Builder setHint(CharSequence text) {
            mInputView.setHint(text);
            return this;
        }

        public Builder setContent(@StringRes int id) {
            return setContent(getString(id));
        }
        public Builder setContent(CharSequence text) {
            mInputView.setText(text);
            int index = mInputView.getText().toString().length();
            if (index > 0) {
                mInputView.requestFocus();
                mInputView.setSelection(index);
            }
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */
        @Override
        public void onShow(BaseDialog dialog) {
            postDelayed(() -> getSystemService(InputMethodManager.class).showSoftInput(mInputView, 0), 500);
        }

        @SingleClick
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ui_confirm:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onConfirm(getDialog(), mInputView.getText().toString());
                    }
                    break;
                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onCancel(getDialog());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String content);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}