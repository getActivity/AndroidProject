package com.hjq.dialog;

import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : Toast 效果对话框
 */
public class ToastDialog {

    public static class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements Runnable {

        private TextView mMessageView;
        private ImageView mIconView;

        private Type mType;

        public Builder(FragmentActivity activity) {
            super(activity);
            initialize();
        }

        public Builder(FragmentActivity activity, int themeResId) {
            super(activity, themeResId);
            initialize();
        }

        private void initialize() {
            setContentView(R.layout.dialog_toast);
            setGravity(Gravity.CENTER);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_dialog_toast_message);
            mIconView = findViewById(R.id.iv_dialog_toast_icon);
        }

        public Builder setType(Type type) {
            mType = type;
            switch (type) {
                case FINISH:
                    mIconView.setImageResource(R.mipmap.ic_dialog_tip_finish);
                    break;
                case ERROR:
                    mIconView.setImageResource(R.mipmap.ic_dialog_tip_error);
                    break;
                case WARN:
                    mIconView.setImageResource(R.mipmap.ic_dialog_tip_warning);
                    break;
            }
            return this;
        }

        public Builder setMessage(int resId) {
            return setMessage(getContext().getText(resId));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
            return this;
        }

        @Override
        public BaseDialog show() {
            // 如果显示的类型为空就抛出异常
            if (mType == null) {
                throw new IllegalArgumentException("The display type must be specified");
            }
            // 如果内容为空就抛出异常
            if ("".equals(mMessageView.getText().toString())) {
                throw new IllegalArgumentException("Dialog message not null");
            }
            // 延迟自动关闭
            mMessageView.postDelayed(this, 3000);
            return super.show();
        }

        @Override
        public void run() {
            if (getDialog() != null && getDialog().isShowing()) {
                dismiss();
            }
        }
    }

    /**
     * 显示的类型
     */
    public enum Type {
        // 完成，错误，警告
        FINISH, ERROR, WARN
    }
}