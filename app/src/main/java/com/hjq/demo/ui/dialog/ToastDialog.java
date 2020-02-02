package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.hjq.base.BaseDialog;
import com.hjq.base.action.AnimAction;
import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : Toast 效果对话框
 */
public final class ToastDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements Runnable, BaseDialog.OnShowListener {

        private final TextView mMessageView;
        private final ImageView mIconView;

        private Type mType = Type.WARN;
        private int mDuration = 2000;

        public Builder(Context context) {
            super(context);
            setContentView(R.layout.dialog_toast);
            setAnimStyle(AnimAction.TOAST);
            setBackgroundDimEnabled(false);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_toast_message);
            mIconView = findViewById(R.id.iv_toast_icon);

            addOnShowListener(this);
        }

        public Builder setType(Type type) {
            mType = type;
            switch (type) {
                case FINISH:
                    mIconView.setImageResource(R.drawable.ic_dialog_finish);
                    break;
                case ERROR:
                    mIconView.setImageResource(R.drawable.ic_dialog_error);
                    break;
                case WARN:
                    mIconView.setImageResource(R.drawable.ic_dialog_warning);
                    break;
                default:
                    break;
            }
            return this;
        }

        public Builder setDuration(int duration) {
            mDuration = duration;
            return this;
        }

        public Builder setMessage(@StringRes int id) {
            return setMessage(getString(id));
        }
        public Builder setMessage(CharSequence text) {
            mMessageView.setText(text);
            return this;
        }

        @Override
        public BaseDialog create() {
            // 如果显示的类型为空就抛出异常
            if (mType == null) {
                throw new IllegalArgumentException("The display type must be specified");
            }
            // 如果内容为空就抛出异常
            if ("".equals(mMessageView.getText().toString())) {
                throw new IllegalArgumentException("Dialog message not null");
            }

            return super.create();
        }

        @Override
        public void onShow(BaseDialog dialog) {
            // 延迟自动关闭
            postDelayed(this, mDuration);
        }

        @Override
        public void run() {
            if (isShowing()) {
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