package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.GetCodeApi;
import com.hjq.demo.http.request.VerifyCodeApi;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;

import butterknife.BindView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 校验手机号
 */
public final class PhoneVerifyActivity extends MyActivity {

    @BindView(R.id.tv_phone_verify_phone)
    TextView mPhoneView;
    @BindView(R.id.et_phone_verify_code)
    EditText mCodeView;
    @BindView(R.id.cv_phone_verify_countdown)
    CountdownView mCountdownView;
    @BindView(R.id.btn_phone_verify_commit)
    Button mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_verify;
    }

    @Override
    protected void initView() {
        InputTextHelper.with(this)
                .addView(mCodeView)
                .setMain(mCommitView)
                .setListener(helper -> mCodeView.getText().toString().length() == 4)
                .build();

        setOnClickListener(R.id.cv_phone_verify_countdown, R.id.btn_phone_verify_commit);
    }

    @Override
    protected void initData() {
        mPhoneView.setText(String.format(getString(R.string.phone_verify_current_phone), "18888888888"));
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_phone_verify_countdown:
                if (true) {
                    toast(R.string.common_code_send_hint);
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
                            }
                        });
                break;
            case R.id.btn_phone_verify_commit:
                if (true) {
                    // 跳转到绑定手机号页面
                    PhoneResetActivity.start(getActivity(), mCodeView.getText().toString());
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
                                // 跳转到绑定手机号页面
                                PhoneResetActivity.start(getActivity(), mCodeView.getText().toString());
                                finish();
                            }
                        });
                break;
            default:
                break;
        }
    }
}