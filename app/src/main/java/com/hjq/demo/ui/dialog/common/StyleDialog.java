package com.hjq.demo.ui.dialog.common;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 项目通用样式 Dialog 布局封装
 */
public final class StyleDialog {

    @SuppressWarnings("unchecked")
    public static class Builder<B extends StyleDialog.Builder<?>>
            extends BaseDialog.Builder<B> {

        private boolean mClickDismiss = true;

        @NonNull
        private final ViewGroup mContainerLayout;
        @NonNull
        private final TextView mTitleView;

        @NonNull
        private final TextView mCancelView;
        @NonNull
        private final View mLineView;
        @NonNull
        private final TextView mConfirmView;

        public Builder(@NonNull Context context) {
            super(context);

            setContentView(R.layout.ui_dialog);
            setAnimStyle(BaseDialog.ANIM_IOS);
            setGravity(Gravity.CENTER);

            mContainerLayout = findViewById(R.id.ll_ui_container);
            mTitleView = findViewById(R.id.tv_ui_title);
            mCancelView  = findViewById(R.id.tv_ui_cancel);
            mLineView = findViewById(R.id.v_ui_line);
            mConfirmView  = findViewById(R.id.tv_ui_confirm);
            setOnClickListener(mCancelView, mConfirmView);
        }

        public B setCustomView(@LayoutRes int id) {
            return setCustomView(LayoutInflater.from(getContext()).inflate(id, mContainerLayout, false));
        }

        public B setCustomView(View view) {
            mContainerLayout.addView(view, 1);
            return (B) this;
        }

        public B setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }
        public B setTitle(CharSequence text) {
            mTitleView.setText(text);
            return (B) this;
        }

        public B setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }
        public B setCancel(CharSequence text) {
            mCancelView.setText(text);
            mLineView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            return (B) this;
        }

        public B setConfirm(@StringRes int id) {
            return setConfirm(getString(id));
        }
        public B setConfirm(CharSequence text) {
            mConfirmView.setText(text);
            return (B) this;
        }

        public B setClickDismiss(boolean enable) {
            mClickDismiss = enable;
            return (B) this;
        }

        public void performClickDismiss() {
            if (!mClickDismiss) {
              return;
            }
            dismiss();
        }
    }
}