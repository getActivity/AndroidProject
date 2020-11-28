package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : Toast 效果对话框
 */
public final class HintDialog {

    public final static int ICON_FINISH = R.drawable.finish_ic;
    public final static int ICON_ERROR = R.drawable.error_ic;
    public final static int ICON_WARNING = R.drawable.warning_ic;

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements Runnable, BaseDialog.OnShowListener {

        private final TextView mMessageView;
        private final ImageView mIconView;

        private int mDuration = 2000;

        public Builder(Context context) {
            super(context);
            setContentView(R.layout.hint_dialog);
            setAnimStyle(BaseDialog.ANIM_TOAST);
            setBackgroundDimEnabled(false);
            setCancelable(false);

            mMessageView = findViewById(R.id.tv_hint_message);
            mIconView = findViewById(R.id.iv_hint_icon);

            addOnShowListener(this);
        }

        public Builder setIcon(@DrawableRes int id) {
            mIconView.setImageResource(id);
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
            // 如果显示的图标为空就抛出异常
            if (mIconView.getDrawable() == null) {
                throw new IllegalArgumentException("The display type must be specified");
            }
            // 如果内容为空就抛出异常
            if (TextUtils.isEmpty(mMessageView.getText().toString())) {
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
}