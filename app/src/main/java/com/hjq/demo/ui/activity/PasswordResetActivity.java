package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.InputTextHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 重置密码
 */
public class PasswordResetActivity extends MyActivity {

    @BindView(R.id.et_password_reset_password1)
    EditText mPasswordView1;
    @BindView(R.id.et_password_reset_password2)
    EditText mPasswordView2;
    @BindView(R.id.btn_password_reset_commit)
    Button mCommitView;

    private InputTextHelper mInputTextHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_password_reset;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_password_reset_title;
    }

    @Override
    protected void initView() {
        mInputTextHelper = new InputTextHelper(mCommitView);
        mInputTextHelper.addViews(mPasswordView1, mPasswordView2);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_password_reset_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_password_reset_commit: //提交注册
                if (!mPasswordView1.getText().toString().equals(mPasswordView2.getText().toString())) {
                    toast(getResources().getString(R.string.two_password_input_error));
                    break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mInputTextHelper.removeViews();
        super.onDestroy();
    }
}