package com.hjq.demo.ui.activity.account;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.hjq.core.manager.InputTextManager;
import com.hjq.custom.widget.view.CountdownView;
import com.hjq.custom.widget.view.SubmitButton;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.GetCodeApi;
import com.hjq.demo.http.api.RegisterApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.http.EasyHttp;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.listener.HttpCallbackProxy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 注册界面
 */
public final class RegisterActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_PHONE = "phone";
    private static final String INTENT_KEY_PASSWORD = "password";

    @Log
    public static void start(@NonNull BaseActivity activity, @Nullable String phone, @Nullable String password, @Nullable OnRegisterListener listener) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_PASSWORD, password);
        activity.startActivityForResult(intent, (resultCode, data) -> {
            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                String registerPhone = data.getStringExtra(INTENT_KEY_PHONE);
                String registerPassword = data.getStringExtra(INTENT_KEY_PASSWORD);
                listener.onRegisterSuccess(registerPhone != null ? registerPhone :"",
                                           registerPassword != null ? registerPassword : "");
            } else {
                listener.onRegisterCancel();
            }
        });
    }

    private EditText mPhoneView;
    private CountdownView mCountdownView;

    private EditText mCodeView;

    private EditText mFirstPassword;
    private EditText mSecondPassword;

    private SubmitButton mCommitView;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_register_phone);
        mCountdownView = findViewById(R.id.cv_register_countdown);
        mCodeView = findViewById(R.id.et_register_code);
        mFirstPassword = findViewById(R.id.et_register_password1);
        mSecondPassword = findViewById(R.id.et_register_password2);
        mCommitView = findViewById(R.id.btn_register_commit);

        setOnClickListener(mCountdownView, mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        // 自动填充手机号和密码
        mPhoneView.setText(getString(INTENT_KEY_PHONE));
        mFirstPassword.setText(getString(INTENT_KEY_PASSWORD));
        mSecondPassword.setText(getString(INTENT_KEY_PASSWORD));
    }

    @Override
    public View getImmersionTopView() {
        return findViewById(R.id.fl_register_container);
    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return findViewById(R.id.fl_register_container);
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

                        @Override
                        public void onHttpFail(@NonNull Throwable throwable) {
                            super.onHttpFail(throwable);
                            mCountdownView.start();
                        }
                    });
        } else if (view == mCommitView) {
            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_max_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_code_error_hint);
                return;
            }

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_password_input_unlike);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            if (true) {
                mCommitView.showProgress();
                postDelayed(() -> {
                    mCommitView.showSucceed();
                    postDelayed(() -> {
                        setResult(RESULT_OK, new Intent()
                                .putExtra(INTENT_KEY_PHONE, mPhoneView.getText().toString())
                                .putExtra(INTENT_KEY_PASSWORD, mFirstPassword.getText().toString()));
                        finish();
                    }, 1000);
                }, 2000);
                return;
            }

            // 提交注册
            EasyHttp.post(this)
                    .api(new RegisterApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString())
                            .setPassword(mFirstPassword.getText().toString()))
                    .request(new HttpCallbackProxy<HttpData<RegisterApi.Bean>>(this) {

                        @Override
                        public void onHttpStart(@NonNull IRequestApi api) {
                            mCommitView.showProgress();
                        }

                        @Override
                        public void onHttpEnd(@NonNull IRequestApi api) {
                            // default implementation ignored
                        }

                        @Override
                        public void onHttpSuccess(@NonNull HttpData<RegisterApi.Bean> data) {
                            postDelayed(() -> {
                                mCommitView.showSucceed();
                                postDelayed(() -> {
                                    setResult(RESULT_OK, new Intent()
                                            .putExtra(INTENT_KEY_PHONE, mPhoneView.getText().toString())
                                            .putExtra(INTENT_KEY_PASSWORD, mFirstPassword.getText().toString()));
                                    finish();
                                }, 1000);
                            }, 1000);
                        }

                        @Override
                        public void onHttpFail(@NonNull Throwable throwable) {
                            super.onHttpFail(throwable);
                            postDelayed(() -> mCommitView.showError(3000), 1000);
                        }
                    });
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击注册按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    /**
     * 注册监听
     */
    public interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone             手机号
         * @param password          密码
         */
        void onRegisterSuccess(@NonNull String phone, @NonNull String password);

        /**
         * 取消注册
         */
        default void onRegisterCancel() {
            // default implementation ignored
        }
    }
}