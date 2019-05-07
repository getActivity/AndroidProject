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
public final class ToastDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements Runnable, BaseDialog.OnShowListener {

        private TextView mMessageView;
        private ImageView mIconView;

        private Type mType = Type.WARN;
        private int mDuration = 2000;

        public Builder(FragmentActivity activity) {
            super(activity);

            setThemeStyle(R.style.TransparentDialogStyle);
            setContentView(R.layout.dialog_toast);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setGravity(Gravity.CENTER);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_dialog_toast_message);
            mIconView = findViewById(R.id.iv_dialog_toast_icon);
        }

        public Builder setType(Type type) {
            mType = type;
            switch (type) {
                case FINISH:
                    mIconView.setImageResource(R.mipmap.ic_dialog_finish);
                    break;
                case ERROR:
                    mIconView.setImageResource(R.mipmap.ic_dialog_error);
                    break;
                case WARN:
                    mIconView.setImageResource(R.mipmap.ic_dialog_warning);
                    break;
            }
            return this;
        }

        public Builder setDuration(int duration) {
            mDuration = duration;
            return this;
        }

        public Builder setMessage(int resId) {
            return setMessage(getText(resId));
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
            addOnShowListener(this);
            return super.create();
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */
        @Override
        public void onShow(BaseDialog dialog) {
            // 延迟自动关闭
            postDelayed(this, mDuration);
        }

        @Override
        public void run() {
            if (getDialogFragment() != null &&
                    getDialogFragment().isAdded() &&
                    getDialog() != null &&
                    getDialog().isShowing()) {
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