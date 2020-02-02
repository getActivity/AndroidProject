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
import com.hjq.demo.http.request.GetCodeApi;
import com.hjq.demo.http.request.PhoneApi;
import com.hjq.demo.other.IntentKey;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;

import butterknife.BindView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 更换手机号
 */
public final class PhoneResetActivity extends MyActivity {

    @DebugLog
    public static void start(Context context, String code) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(IntentKey.CODE, code);
        context.startActivity(intent);
    }

    @BindView(R.id.et_phone_reset_phone)
    EditText mPhoneView;
    @BindView(R.id.et_phone_reset_code)
    EditText mCodeView;

    @BindView(R.id.cv_phone_reset_countdown)
    CountdownView mCountdownView;

    @BindView(R.id.btn_phone_reset_commit)
    Button mCommitView;

    /** 验证码 */
    private String mCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_reset;
    }

    @Override
    protected void initView() {
        InputTextHelper.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .setListener(helper -> mPhoneView.getText().toString().length() == 11 && mCodeView.getText().toString().length() == 4)
                .build();

        setOnClickListener(R.id.cv_phone_reset_countdown, R.id.btn_phone_reset_commit);
    }

    @Override
    protected void initData() {
        mCode = getString(IntentKey.CODE);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_phone_reset_countdown:
                // 获取验证码
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
                break;
            case R.id.btn_phone_reset_commit:
                if (true) {
                    toast(R.string.phone_reset_commit_succeed);
                    finish();
                    return;
                }

                // 更换手机号
                EasyHttp.post(this)
                        .api(new PhoneApi()
                        .setPreCode(mCode)
                        .setPhone(mPhoneView.getText().toString())
                        .setCode(mCodeView.getText().toString()))
                        .request(new HttpCallback<HttpData<Void>>(this) {

                            @Override
                            public void onSucceed(HttpData<Void> data) {
                                toast(R.string.phone_reset_commit_succeed);
                                finish();
                            }
                        });
                break;
            default:
                break;
        }
    }
}