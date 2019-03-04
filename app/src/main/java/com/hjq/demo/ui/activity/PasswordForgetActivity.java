package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;
import com.hjq.widget.CountdownView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 忘记密码
 */
public class PasswordForgetActivity extends MyActivity {

    @BindView(R.id.et_password_forget_phone)
    EditText mPhoneView;
    @BindView(R.id.et_password_forget_code)
    EditText mCodeView;
    @BindView(R.id.cv_password_forget_countdown)
    CountdownView mCountdownView;
    @BindView(R.id.btn_password_forget_commit)
    Button mCommitView;

    private InputTextHelper mInputTextHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_password_forget;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_password_forget_title;
    }

    @Override
    protected void initView() {
        mInputTextHelper = new InputTextHelper(mCommitView);
        mInputTextHelper.addViews(mPhoneView, mCodeView);
    }

    @Override
    protected void initData() {

    }


    @OnClick({R.id.cv_password_forget_countdown, R.id.btn_password_forget_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_password_forget_countdown: // 获取验证码
                if (mPhoneView.getText().toString().length() != 11) {
                    // 重置验证码倒计时控件
                    mCountdownView.resetState();
                    toast(getResources().getString(R.string.phone_input_error));
                    break;
                }
                toast(getResources().getString(R.string.countdown_code_send_succeed));
                break;
            case R.id.btn_password_forget_commit: //提交注册
                if (mPhoneView.getText().toString().length() != 11) {
                    toast(getResources().getString(R.string.phone_input_error));
                    break;
                }
                startActivityFinish(PasswordResetActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mInputTextHelper.removeViews();
        super.onDestroy();
    }
}