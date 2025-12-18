package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.base.BaseDialog;
import com.hjq.custom.widget.view.CountdownView;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.http.api.GetCodeApi;
import com.hjq.demo.http.api.VerifyCodeApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.ui.dialog.common.StyleDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/02/06
 *    desc   : 身份校验对话框
 */
public final class SafeDialog {

    public static final class Builder
            extends StyleDialog.Builder<Builder> {

        @NonNull
        private final TextView mPhoneView;
        @NonNull
        private final EditText mCodeView;
        @NonNull
        private final CountdownView mCountdownView;

        @Nullable
        private OnListener mListener;

        /** 当前手机号 */
        private final String mPhoneNumber;

        public Builder(@NonNull Context context) {
            super(context);
            setTitle(R.string.safe_title);
            setCustomView(R.layout.safe_dialog);
            mPhoneView = findViewById(R.id.tv_safe_phone);
            mCodeView = findViewById(R.id.et_safe_code);
            mCountdownView = findViewById(R.id.cv_safe_countdown);
            setOnClickListener(mCountdownView);

            mPhoneNumber = "18100001413";
            // 为了保护用户的隐私，不明文显示中间四个数字
            mPhoneView.setText(String.format("%s****%s", mPhoneNumber.substring(0, 3), mPhoneNumber.substring(mPhoneNumber.length() - 4)));
        }

        public Builder setCode(String code) {
            mCodeView.setText(code);
            return this;
        }

        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(@NonNull View view) {
            int viewId = view.getId();
            if (viewId == R.id.cv_safe_countdown) {
                if (true) {
                    Toaster.show(R.string.common_code_send_hint);
                    mCountdownView.start();
                    setCancelable(false);
                    return;
                }

                // 获取验证码
                EasyHttp.post(getDialog())
                        .api(new GetCodeApi()
                                .setPhone(mPhoneNumber))
                        .request(new OnHttpListener<HttpData<?>>() {

                            @Override
                            public void onHttpSuccess(@NonNull HttpData<?> data) {
                                Toaster.show(R.string.common_code_send_hint);
                                mCountdownView.start();
                                setCancelable(false);
                            }

                            @Override
                            public void onHttpFail(@NonNull Throwable throwable) {
                                Toaster.show(throwable.getMessage());
                            }
                        });
            } else if (viewId == R.id.tv_ui_confirm) {
                if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_max_length)) {
                    Toaster.show(R.string.common_code_error_hint);
                    return;
                }

                if (true) {
                    performClickDismiss();
                    if (mListener == null) {
                        return;
                    }
                    mListener.onConfirm(getDialog(), mPhoneNumber, mCodeView.getText().toString());
                    return;
                }

                // 验证码校验
                EasyHttp.post(getDialog())
                        .api(new VerifyCodeApi()
                                .setPhone(mPhoneNumber)
                                .setCode(mCodeView.getText().toString()))
                        .request(new OnHttpListener<HttpData<?>>() {

                            @Override
                            public void onHttpSuccess(@NonNull HttpData<?> data) {
                                performClickDismiss();
                                if (mListener == null) {
                                    return;
                                }
                                mListener.onConfirm(getDialog(), mPhoneNumber, mCodeView.getText().toString());
                            }

                            @Override
                            public void onHttpFail(@NonNull Throwable throwable) {
                                Toaster.show(throwable.getMessage());
                            }
                        });
            } else if (viewId == R.id.tv_ui_cancel) {
                performClickDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(@NonNull BaseDialog dialog, @NonNull String phone, @NonNull String code);

        /**
         * 点击取消时回调
         */
        default void onCancel(@NonNull BaseDialog dialog) {
            // default implementation ignored
        }
    }
}