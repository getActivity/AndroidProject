package com.hjq.dialog;

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
 *    desc   : 等待加载对话框
 */
public final class WaitDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder> {

        private TextView mMessageView;
        //private CircleProgressView mProgressView;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_wait);
            setGravity(Gravity.CENTER);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_dialog_wait_message);
            //mProgressView = findViewById(R.id.pv_dialog_wait_progress);
        }

        public Builder setMessage(int resId) {
            return setMessage(getContext().getText(resId));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
            mMessageView.setVisibility(text == null ? View.GONE : View.VISIBLE);
            return this;
        }

        @Override
        public BaseDialog show() {
            // 如果内容为空就设置隐藏
            if ("".equals(mMessageView.getText().toString())) {
                mMessageView.setVisibility(View.GONE);
            }
            return super.show();
        }
    }
}