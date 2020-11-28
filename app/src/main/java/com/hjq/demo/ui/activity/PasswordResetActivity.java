package com.hjq.demo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.demo.R;
import com.hjq.demo.aop.DebugLog;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.PasswordApi;
import com.hjq.demo.other.IntentKey;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 重置密码
 */
public final class PasswordResetActivity extends MyActivity {

    @DebugLog
    public static void start(Context context, String phone, String code) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(IntentKey.PHONE, phone);
        intent.putExtra(IntentKey.CODE, code);
        context.startActivity(intent);
    }

    private EditText mPasswordView1;
    private EditText mPasswordView2;
    private Button mCommitView;

    /** 手机号 */
    private String mPhoneNumber;
    /** 验证码 */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.password_reset_activity;
    }

    @Override
    protected void initView() {
        mPasswordView1 = findViewById(R.id.et_password_reset_password1);
        mPasswordView2 = findViewById(R.id.et_password_reset_password2);
        mCommitView = findViewById(R.id.btn_password_reset_commit);
        setOnClickListener(mCommitView);

        InputTextHelper.with(this)
                .addView(mPasswordView1)
                .addView(mPasswordView2)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        mPhoneNumber = getString(IntentKey.PHONE);
        mVerifyCode = getString(IntentKey.CODE);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mCommitView) {

            if (!mPasswordView1.getText().toString().equals(mPasswordView2.getText().toString())) {
                toast(R.string.common_password_input_unlike);
                return;
            }

            if (true) {
                toast(R.string.password_reset_success);
                finish();
                return;
            }

            // 重置密码
            EasyHttp.post(this)
                    .api(new PasswordApi()
                            .setPhone(mPhoneNumber)
                            .setCode(mVerifyCode)
                            .setPassword(mPasswordView1.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            toast(R.string.password_reset_success);
                            finish();
                        }
                    });
        }
    }
}