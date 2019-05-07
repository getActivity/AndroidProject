package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.widget.CountdownView;

import butterknife.BindView;
import butterknife.OnClick;

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
    protected int getTitleId() {
        return R.id.tb_phone_verify_title;
    }

    @Override
    protected void initView() {
        new InputTextHelper.Builder(this)
                .setMain(mCommitView)
                .addView(mCodeView)
                .build();
    }

    @Override
    protected void initData() {
        mPhoneView.setText(String.format(getResources().getString(R.string.phone_verify_current_phone), "18888888888"));
    }

    @OnClick({R.id.cv_phone_verify_countdown, R.id.btn_phone_verify_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_phone_verify_countdown:
                // 获取验证码
                toast(getString(R.string.common_send_code_succeed));
                break;
            case R.id.btn_phone_verify_commit:
                // 修改手机号
                startActivityFinish(PhoneResetActivity.class);
                break;
            default:
                break;
        }
    }
}