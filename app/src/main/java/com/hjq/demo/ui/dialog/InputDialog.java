package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.widget.view.RegexEditText;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 输入对话框
 */
public final class InputDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener,
            TextView.OnEditorActionListener {

        @Nullable
        private OnListener mListener;
        private final RegexEditText mInputView;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.input_dialog);

            mInputView = findViewById(R.id.tv_input_message);
            mInputView.setOnEditorActionListener(this);

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
            Editable editable = mInputView.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mInputView.requestFocus();
            mInputView.setSelection(index);
            return this;
        }

        public Builder setInputRegex(String regex) {
            mInputView.setInputRegex(regex);
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
            postDelayed(() -> showKeyboard(mInputView), 500);
        }

        @SingleClick
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tv_ui_confirm) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                Editable editable = mInputView.getText();
                mListener.onConfirm(getDialog(), editable != null ? editable.toString() : "");
            } else if (viewId == R.id.tv_ui_cancel) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        /**
         * {@link TextView.OnEditorActionListener}
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 模拟点击确认按钮
                onClick(findViewById(R.id.tv_ui_confirm));
                return true;
            }
            return false;
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