package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.GetCodeApi;
import com.hjq.demo.http.request.VerifyCodeApi;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.CountdownView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 忘记密码
 */
public final class PasswordForgetActivity extends MyActivity {

    private EditText mPhoneView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.password_forget_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_password_forget_phone);
        mCodeView = findViewById(R.id.et_password_forget_code);
        mCountdownView = findViewById(R.id.cv_password_forget_countdown);
        mCommitView = findViewById(R.id.btn_password_forget_commit);
        setOnClickListener(mCountdownView, mCommitView);

        InputTextHelper.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mCountdownView) {
            if (mPhoneView.getText().toString().length() != 11) {
                toast(R.string.common_phone_input_error);
                return;
            }

            if (true) {
                toast(R.string.common_code_send_hint);
                mCountdownView.start();
                return;
            }

            // 获取验证码
            EasyHttp.post(this)
                    .api(new GetCodeApi()
                            .setPhone(mPhoneView.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();
                        }
                    });
        } else if (v == mCommitView) {

            if (mPhoneView.getText().toString().length() != 11) {
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                ToastUtils.show(R.string.common_code_error_hint);
                return;
            }

            if (true) {
                PasswordResetActivity.start(getActivity(), mPhoneView.getText().toString(), mCodeView.getText().toString());
                finish();
                return;
            }

            // 验证码校验
            EasyHttp.post(this)
                    .api(new VerifyCodeApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            PasswordResetActivity.start(getActivity(), mPhoneView.getText().toString(), mCodeView.getText().toString());
                            finish();
                        }
                    });
        }
    }
}