package com.hjq.demo.ui.activity;

import android.view.Gravity;
import android.view.View;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.ActivityStackManager;
import com.hjq.demo.helper.CacheDataManager;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.LogoutApi;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.ui.dialog.SafeDialog;
import com.hjq.demo.ui.dialog.UpdateDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 设置界面
 */
public final class SettingActivity extends MyActivity
        implements SwitchButton.OnCheckedChangeListener {

    private SettingBar mLanguageView;
    private SettingBar mPhoneView;
    private SettingBar mPasswordView;
    private SettingBar mCleanCacheView;
    private SwitchButton mAutoSwitchView;

    @Override
    protected int getLayoutId() {
        return R.layout.setting_activity;
    }

    @Override
    protected void initView() {
        mLanguageView = findViewById(R.id.sb_setting_language);
        mPhoneView = findViewById(R.id.sb_setting_phone);
        mPasswordView = findViewById(R.id.sb_setting_password);
        mCleanCacheView = findViewById(R.id.sb_setting_cache);
        mAutoSwitchView = findViewById(R.id.sb_setting_switch);

        // 设置切换按钮的监听
        mAutoSwitchView.setOnCheckedChangeListener(this);

        setOnClickListener(R.id.sb_setting_language, R.id.sb_setting_update, R.id.sb_setting_phone,
                R.id.sb_setting_password, R.id.sb_setting_agreement, R.id.sb_setting_about,
                R.id.sb_setting_cache, R.id.sb_setting_auto, R.id.sb_setting_exit);
    }

    @Override
    protected void initData() {
        // 获取应用缓存大小
        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));

        mLanguageView.setRightText("简体中文");
        mPhoneView.setRightText("181****1413");
        mPasswordView.setRightText("密码强度较低");
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sb_setting_language:
                // 底部选择框
                new MenuDialog.Builder(this)
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setList(R.string.setting_language_simple, R.string.setting_language_complex)
                        .setListener((MenuDialog.OnListener<String>) (dialog, position, string) -> {
                            mLanguageView.setRightText(string);
                            BrowserActivity.start(getActivity(), "https://github.com/getActivity/MultiLanguages");
                        })
                        .setGravity(Gravity.BOTTOM)
                        .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                        .show();
                break;
            case R.id.sb_setting_update:
                // 本地的版本码和服务器的进行比较
                if (20 > AppConfig.getVersionCode()) {
                    new UpdateDialog.Builder(this)
                            // 版本名
                            .setVersionName("2.0")
                            // 是否强制更新
                            .setForceUpdate(false)
                            // 更新日志
                            .setUpdateLog("修复Bug\n优化用户体验")
                            // 下载 url
                            .setDownloadUrl("https://raw.githubusercontent.com/getActivity/AndroidProject/master/AndroidProject.apk")
                            .show();
                } else {
                    toast(R.string.update_no_update);
                }
                break;
            case R.id.sb_setting_phone:
                new SafeDialog.Builder(this)
                        .setListener((dialog, phone, code) -> PhoneResetActivity.start(getActivity(), code))
                        .show();
                break;
            case R.id.sb_setting_password:
                new SafeDialog.Builder(this)
                        .setListener((dialog, phone, code) -> PasswordResetActivity.start(getActivity(), phone, code))
                        .show();
                break;
            case R.id.sb_setting_agreement:
                BrowserActivity.start(this, "https://github.com/getActivity/Donate");
                break;
            case R.id.sb_setting_about:
                startActivity(AboutActivity.class);
                break;
            case R.id.sb_setting_auto:
                // 自动登录
                mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());
                break;
            case R.id.sb_setting_cache:
                // 清除内存缓存（必须在主线程）
                GlideApp.get(getActivity()).clearMemory();
                new Thread(() -> {
                    CacheDataManager.clearAllCache(this);
                    // 清除本地缓存（必须在子线程）
                    GlideApp.get(getActivity()).clearDiskCache();
                    post(() -> {
                        // 重新获取应用缓存大小
                        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getActivity()));
                    });
                }).start();
                break;
            case R.id.sb_setting_exit:
                if (true) {
                    startActivity(LoginActivity.class);
                    // 进行内存优化，销毁除登录页之外的所有界面
                    ActivityStackManager.getInstance().finishAllActivities(LoginActivity.class);
                    return;
                }

                // 退出登录
                EasyHttp.post(this)
                        .api(new LogoutApi())
                        .request(new HttpCallback<HttpData<Void>>(this) {

                            @Override
                            public void onSucceed(HttpData<Void> data) {
                                startActivity(LoginActivity.class);
                                // 进行内存优化，销毁除登录页之外的所有界面
                                ActivityStackManager.getInstance().finishAllActivities(LoginActivity.class);
                            }
                        });
                break;
            default:
                break;
        }
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean isChecked) {
        toast(isChecked);
    }
}