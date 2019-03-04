package com.hjq.demo.ui.activity;

import android.view.View;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.helper.ActivityStackManager;
import com.hjq.demo.helper.CacheDataManager;
import com.hjq.demo.widget.SettingBar;
import com.hjq.image.ImageLoader;
import com.hjq.widget.SwitchButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 设置界面
 */
public class SettingActivity extends MyActivity
        implements SwitchButton.OnCheckedChangeListener {

    @BindView(R.id.sb_setting_cache)
    SettingBar mCleanCacheView;

    @BindView(R.id.sb_setting_auto)
    SettingBar mAutoLoginView;
    @BindView(R.id.sb_setting_switch)
    SwitchButton mAutoSwitchView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_setting_title;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        // 获取应用缓存大小
        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));

        // 设置切换按钮的监听
        mAutoSwitchView.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.sb_setting_language, R.id.sb_setting_update, R.id.sb_setting_agreement, R.id.sb_setting_about,
            R.id.sb_setting_cache, R.id.sb_setting_auto, R.id.sb_setting_exit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sb_setting_language:
                break;
            case R.id.sb_setting_update:
                break;
            case R.id.sb_setting_agreement:
                startActivity(WebActivity.class);
                break;
            case R.id.sb_setting_about:
                startActivity(AboutActivity.class);
                break;
            case R.id.sb_setting_auto: // 自动登录
                mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());
                break;
            case R.id.sb_setting_cache: // 清空缓存
                ImageLoader.clear(this);
                CacheDataManager.clearAllCache(this);
                // 重新获取应用缓存大小
                mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));
                break;
            case R.id.sb_setting_exit: // 退出登录
                startActivity(LoginActivity.class);
                // 进行内存优化，销毁掉所有的界面
                ActivityStackManager.getInstance().finishAllActivities(LoginActivity.class);
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