package com.hjq.demo.ui.activity.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.GetCodeApi;
import com.hjq.demo.http.api.PhoneApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.ui.dialog.common.TipsDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.toast.Toaster;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 设置手机号
 */
public final class PhoneResetActivity extends AppActivity implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_IN_CODE = "code";

    @Log
    public static void start(@NonNull Context context, @NonNull String code) {
        Intent intent = new Intent(context, PhoneResetActivity.class);
        intent.putExtra(INTENT_KEY_IN_CODE, code);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private EditText mPhoneView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;

    /** 验证码 */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.phone_reset_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_phone_reset_phone);
        mCodeView = findViewById(R.id.et_phone_reset_code);
        mCountdownView = findViewById(R.id.cv_phone_reset_countdown);
        mCommitView = findViewById(R.id.btn_phone_reset_commit);

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
        mVerifyCode = getString(INTENT_KEY_IN_CODE);
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
                    });
        } else if (view == mCommitView) {

            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_max_length)) {
                Toaster.show(R.string.common_code_error_hint);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            if (true) {
                new TipsDialog.Builder(this)
                        .setIcon(TipsDialog.ICON_FINISH)
                        .setMessage(R.string.phone_reset_commit_succeed)
                        .setDuration(2000)
                        .addOnDismissListener(dialog -> finish())
                        .show();
                return;
            }

            // 更换手机号
            EasyHttp.post(this)
                    .api(new PhoneApi()
                            .setPreCode(mVerifyCode)
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString()))
                    .request(new HttpCallbackProxy<HttpData<?>>(this) {

                        @Override
                        public void onHttpSuccess(@NonNull HttpData<?> data) {
                            new TipsDialog.Builder(PhoneResetActivity.this)
                                    .setIcon(TipsDialog.ICON_FINISH)
                                    .setMessage(R.string.phone_reset_commit_succeed)
                                    .setDuration(2000)
                                    .addOnDismissListener(dialog -> finish())
                                    .show();
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
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}