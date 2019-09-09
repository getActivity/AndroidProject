package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.widget.view.CountdownView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 更换手机号
 */
public final class PhoneResetActivity extends MyActivity {

    @BindView(R.id.et_phone_reset_phone)
    EditText mPhoneView;
    @BindView(R.id.et_phone_reset_code)
    EditText mCodeView;

    @BindView(R.id.cv_phone_reset_countdown)
    CountdownView mCountdownView;

    @BindView(R.id.btn_phone_reset_commit)
    Button mCommitView;

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
                .setListener(new InputTextHelper.OnInputTextListener() {

                    @Override
                    public boolean onInputChange(InputTextHelper helper) {
                        return mPhoneView.getText().toString().length() == 11 && mCodeView.getText().toString().length() == 4;
                    }
                })
                .build();
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.cv_phone_reset_countdown, R.id.btn_phone_reset_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_phone_reset_countdown:
                // 获取验证码
                if (mPhoneView.getText().toString().length() != 11) {
                    // 重置验证码倒计时控件
                    mCountdownView.resetState();
                    toast(R.string.common_phone_input_error);
                } else {
                    toast(R.string.common_code_send_hint);
                }
                break;
            case R.id.btn_phone_reset_commit:
                // 更换手机号
                toast(R.string.phone_reset_commit_succeed);
                finish();
                break;
            default:
                break;
        }
    }
}