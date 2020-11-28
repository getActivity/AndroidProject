package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.GetCodeApi;
import com.hjq.demo.http.request.RegisterApi;
import com.hjq.demo.http.response.RegisterBean;
import com.hjq.demo.other.IntentKey;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 注册界面
 */
public final class RegisterActivity extends MyActivity {

    private EditText mPhoneView;
    private CountdownView mCountdownView;

    private EditText mCodeView;

    private EditText mPasswordView1;
    private EditText mPasswordView2;

    private Button mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_register_phone);
        mCountdownView = findViewById(R.id.cv_register_countdown);
        mCodeView = findViewById(R.id.et_register_code);
        mPasswordView1 = findViewById(R.id.et_register_password1);
        mPasswordView2 = findViewById(R.id.et_register_password2);
        mCommitView = findViewById(R.id.btn_register_commit);
        setOnClickListener(mCountdownView, mCommitView);

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextHelper.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .addView(mPasswordView1)
                .addView(mPasswordView2)
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

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            mCountdownView.start();
                        }
                    });
        } else if (v == mCommitView) {
            if (mPhoneView.getText().toString().length() != 11) {
                toast(R.string.common_phone_input_error);
                return;
            }

            if (!mPasswordView1.getText().toString().equals(mPasswordView2.getText().toString())) {
                toast(R.string.common_password_input_unlike);
                return;
            }

            if (true) {
                toast(R.string.register_succeed);
                setResult(RESULT_OK, new Intent()
                        .putExtra(IntentKey.PHONE, mPhoneView.getText().toString())
                        .putExtra(IntentKey.PASSWORD, mPasswordView1.getText().toString()));
                finish();
                return;
            }

            // 提交注册
            EasyHttp.post(this)
                    .api(new RegisterApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString())
                            .setPassword(mPasswordView1.getText().toString()))
                    .request(new HttpCallback<HttpData<RegisterBean>>(this) {

                        @Override
                        public void onSucceed(HttpData<RegisterBean> data) {
                            toast(R.string.register_succeed);
                            setResult(RESULT_OK, new Intent()
                                    .putExtra(IntentKey.PHONE, mPhoneView.getText().toString())
                                    .putExtra(IntentKey.PASSWORD, mPasswordView1.getText().toString()));
                            finish();
                        }
                    });
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    @Override
    public boolean isSwipeEnable() {
        return false;
    }
}