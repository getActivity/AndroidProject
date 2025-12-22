package com.hjq.demo.ui.activity.account;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.hjq.core.manager.InputTextManager;
import com.hjq.custom.widget.view.CountdownView;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.GetCodeApi;
import com.hjq.demo.http.api.VerifyCodeApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallbackProxy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 忘记密码
 */
public final class PasswordForgetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

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

        mCodeView.setOnEditorActionListener(this);

        InputTextManager.with(this)
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
    public void onClick(@NonNull View view) {
        if (view == mCountdownView) {
            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            if (true) {
                toast(R.string.common_code_send_hint);
                mCountdownView.start();
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            // 获取验证码
            EasyHttp.post(this)
                    .api(new GetCodeApi()
                            .setPhone(mPhoneView.getText().toString()))
                    .request(new HttpCallbackProxy<HttpData<?>>(this) {

                        @Override
                        public void onHttpSuccess(@NonNull HttpData<?> data) {
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();
                        }
                    });
        } else if (view == mCommitView) {

            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_max_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_code_error_hint);
                return;
            }

            if (true) {
                PasswordResetActivity.start(this, mPhoneView.getText().toString(), mCodeView.getText().toString());
                finish();
                return;
            }

            // 验证码校验
            EasyHttp.post(this)
                    .api(new VerifyCodeApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString()))
                    .request(new HttpCallbackProxy<HttpData<?>>(this) {

                        @Override
                        public void onHttpSuccess(@NonNull HttpData<?> data) {
                            PasswordResetActivity.start(PasswordForgetActivity.this, mPhoneView.getText().toString(), mCodeView.getText().toString());
                            finish();
                        }
                    });
        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击下一步按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}