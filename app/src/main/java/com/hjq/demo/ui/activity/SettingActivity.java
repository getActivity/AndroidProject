package com.hjq.demo.ui.activity;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.base.BaseDialog;
import com.hjq.core.manager.ActivityManager;
import com.hjq.core.manager.CacheDataManager;
import com.hjq.core.manager.ThreadPoolManager;
import com.hjq.custom.widget.layout.SettingBar;
import com.hjq.custom.widget.view.SwitchButton;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.LogoutApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.activity.account.LoginActivity;
import com.hjq.demo.ui.activity.account.PasswordResetActivity;
import com.hjq.demo.ui.activity.account.PhoneResetActivity;
import com.hjq.demo.ui.activity.common.BrowserActivity;
import com.hjq.demo.ui.dialog.SafeDialog;
import com.hjq.demo.ui.dialog.UpdateDialog;
import com.hjq.demo.ui.dialog.common.MenuDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallbackProxy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 设置界面
 */
public final class SettingActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {

    private SettingBar mChangeLanguageView;
    private SettingBar mCheckUpdateView;
    private SettingBar mModifyPhoneView;
    private SettingBar mModifyPasswordView;
    private SettingBar mReadAgreementView;
    private SettingBar mAboutAppView;
    private SettingBar mAutoLoginView;
    private SettingBar mCleanCacheView;
    private SettingBar mExitLoginView;

    private SwitchButton mAutoSwitchView;

    @Override
    protected int getLayoutId() {
        return R.layout.setting_activity;
    }

    @Override
    protected void initView() {
        mChangeLanguageView = findViewById(R.id.sb_setting_change_language);
        mCheckUpdateView = findViewById(R.id.sb_setting_check_update);
        mModifyPhoneView = findViewById(R.id.sb_setting_modify_phone);
        mModifyPasswordView = findViewById(R.id.sb_setting_modify_password);
        mReadAgreementView = findViewById(R.id.sb_setting_read_agreement);
        mAboutAppView = findViewById(R.id.sb_setting_about_app);
        mAutoLoginView = findViewById(R.id.sb_setting_auto_login);
        mCleanCacheView = findViewById(R.id.sb_setting_clear_cache);
        mExitLoginView = findViewById(R.id.sb_setting_exit_login);

        mAutoSwitchView = findViewById(R.id.sb_setting_switch);

        // 适配 RTL 特性
        Drawable iconDrawable;
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            iconDrawable = getDrawable(R.drawable.arrows_left_ic);
        } else {
            iconDrawable = getDrawable(R.drawable.arrows_right_ic);
        }
        mChangeLanguageView.setEndDrawable(iconDrawable);
        mModifyPhoneView.setEndDrawable(iconDrawable);
        mModifyPasswordView.setEndDrawable(iconDrawable);
        mReadAgreementView.setEndDrawable(iconDrawable);
        mAboutAppView.setEndDrawable(iconDrawable);
        mAutoLoginView.setEndDrawable(iconDrawable);
        mCleanCacheView.setEndDrawable(iconDrawable);
        mExitLoginView.setEndDrawable(iconDrawable);

        // 设置切换按钮的监听
        mAutoSwitchView.setOnCheckedChangeListener(this);

        setOnClickListener(mChangeLanguageView, mCheckUpdateView, mModifyPhoneView, mModifyPasswordView,
                           mReadAgreementView, mAboutAppView, mAutoLoginView, mCleanCacheView, mExitLoginView);
    }

    @Override
    protected void initData() {
        // 获取应用缓存大小
        mCleanCacheView.setEndText(CacheDataManager.getTotalCacheSize(this));
        mChangeLanguageView.setEndText("简体中文");
        mModifyPhoneView.setEndText("181****1413");
        mModifyPasswordView.setEndText("密码强度较低");
    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return findViewById(R.id.ll_setting_content);
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        if (view == mChangeLanguageView) {

            // 底部选择框
            new MenuDialog.Builder(this)
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(R.string.setting_language_simple, R.string.setting_language_complex)
                    .setListener((MenuDialog.OnListener<String>) (dialog, position, string) -> {
                        mChangeLanguageView.setEndText(string);
                        BrowserActivity.start(SettingActivity.this, "https://github.com/getActivity/MultiLanguages");
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                    .show();

        } else if (view == mCheckUpdateView) {

            // 本地的版本码和服务器的进行比较
            if (20 > AppConfig.getVersionCode()) {
                new UpdateDialog.Builder(this)
                        .setVersionName("2.0")
                        .setForceUpdate(false)
                        .setUpdateLog("修复Bug\n优化用户体验")
                        .setDownloadUrl("https://dldir1.qq.com/weixin/android/weixin8015android2020_arm64.apk")
                        .setFileMd5("b05b25d4738ea31091dd9f80f4416469")
                        .show();
            } else {
                toast(R.string.update_no_update);
            }

        } else if (view == mModifyPhoneView) {

            new SafeDialog.Builder(this)
                    .setListener((dialog, phone, code) -> PhoneResetActivity.start(this, code))
                    .show();

        } else if (view == mModifyPasswordView) {

            new SafeDialog.Builder(this)
                    .setListener((dialog, phone, code) -> PasswordResetActivity.start(this, phone, code))
                    .show();

        } else if (view == mReadAgreementView) {

            BrowserActivity.start(this, "https://github.com/getActivity/Donate");

        } else if (view == mAboutAppView) {

            startActivity(AboutActivity.class);

        } else if (view == mAutoLoginView) {

            // 自动登录
            mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());

        } else if (view == mCleanCacheView) {

            // 清除内存缓存（必须在主线程）
            GlideApp.get(this).clearMemory();
            ThreadPoolManager.getInstance().execute(() -> {
                CacheDataManager.clearAllCache(this);
                // 清除本地缓存（必须在子线程）
                GlideApp.get(SettingActivity.this).clearDiskCache();
                post(() -> {
                    // 重新获取应用缓存大小
                    mCleanCacheView.setEndText(CacheDataManager.getTotalCacheSize(SettingActivity.this));
                });
            });

        } else if (view == mExitLoginView) {

            if (true) {
                startActivity(LoginActivity.class);
                // 进行内存优化，销毁除登录页之外的所有界面
                ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                return;
            }

            // 退出登录
            EasyHttp.post(this)
                    .api(new LogoutApi())
                    .request(new HttpCallbackProxy<HttpData<?>>(this) {

                        @Override
                        public void onHttpSuccess(@NonNull HttpData<?> data) {
                            startActivity(LoginActivity.class);
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                        }
                    });

        }
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(@NonNull SwitchButton button, boolean checked) {
        toast(checked);
    }
}