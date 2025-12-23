package com.hjq.demo.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import androidx.annotation.NonNull;
import com.airbnb.lottie.LottieAnimationView;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.core.manager.ActivityManager;
import com.hjq.custom.widget.view.SlantedTextView;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.UserInfoApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.manager.InitManager;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.dialog.PrivacyAgreementDialog;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallbackProxy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 闪屏界面
 */
@SuppressLint("CustomSplashScreen")
public final class SplashActivity extends AppActivity {

    private LottieAnimationView mLottieView;
    private SlantedTextView mDebugView;

    @Override
    protected int getLayoutId() {
        return R.layout.splash_activity;
    }

    @Override
    protected void initView() {
        mLottieView = findViewById(R.id.lav_splash_lottie);
        mDebugView = findViewById(R.id.iv_splash_debug);
        // 设置动画监听
        mLottieView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mLottieView.removeAnimatorListener(this);

                if (InitManager.isAgreePrivacy(SplashActivity.this)) {
                    agreePrivacyAfter();
                    return;
                }

                // 弹窗用户协议与隐私政策对话框
                new PrivacyAgreementDialog.Builder(SplashActivity.this)
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(@NonNull BaseDialog dialog) {
                                InitManager.setAgreePrivacy(SplashActivity.this, true);
                                agreePrivacyAfter();
                            }

                            @Override
                            public void onCancel(@NonNull BaseDialog dialog) {
                                ActivityManager.getInstance().finishAllActivities();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void initData() {
        mDebugView.setText(AppConfig.getBuildType().toUpperCase());
        if (AppConfig.isDebug()) {
            mDebugView.setVisibility(View.VISIBLE);
        } else {
            mDebugView.setVisibility(View.INVISIBLE);
        }

        if (true) {
            return;
        }
        // 刷新用户信息
        EasyHttp.post(this)
                .api(new UserInfoApi())
                .request(new HttpCallbackProxy<HttpData<UserInfoApi.Bean>>(this) {

                    @Override
                    public void onHttpSuccess(@NonNull HttpData<UserInfoApi.Bean> data) {

                    }
                });
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // 禁用返回键
        //super.onBackPressed();
    }

    @Override
    protected void initActivity() {
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (intent != null && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && Intent.ACTION_MAIN.equals(intent.getAction())) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish();
                return;
            }
        }
        super.initActivity();
    }

    @Override
    protected void onDestroy() {
        // 因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        // 所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy();
    }

    /**
     * 同意隐私后需要做的事情
     */
    private void agreePrivacyAfter() {
        InitManager.initSdk(getApplication());
        HomeActivity.start(this);
        finish();
    }
}