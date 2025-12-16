package com.hjq.demo.ui.dialog.common;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : 消息对话框
 */
public final class MessageDialog {

    public static class Builder
            extends StyleDialog.Builder<Builder> {

        @Nullable
        private OnListener mListener;

        @NonNull
        private final TextView mMessageView;

        public Builder(@NonNull Context context) {
            super(context);
            setCustomView(R.layout.message_dialog);
            mMessageView = findViewById(R.id.tv_message_message);

            // 让 TextView 支持滚动
            mMessageView.setMovementMethod(new ScrollingMovementMethod());
        }

        public Builder setMessage(@StringRes int id) {
            return setMessage(getString(id));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
            return this;
        }

        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public BaseDialog create() {
            // 如果内容为空就抛出异常
            if (TextUtils.isEmpty(mMessageView.getText().toString())) {
                throw new IllegalArgumentException("Dialog message not null");
            }
            return super.create();
        }

        @SingleClick
        @Override
        public void onClick(@NonNull View view) {
            int viewId = view.getId();
            if (viewId == R.id.tv_ui_confirm) {
                performClickDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm(getDialog());
            } else if (viewId == R.id.tv_ui_cancel) {
                performClickDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        @NonNull
        public TextView getMessageView() {
            return mMessageView;
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(@NonNull BaseDialog dialog);

        /**
         * 点击取消时回调
         */
        default void onCancel(@NonNull BaseDialog dialog) {
            // default implementation ignored
        }
    }
}