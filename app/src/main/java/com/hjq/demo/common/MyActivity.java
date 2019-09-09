package com.hjq.demo.common;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.base.BuildConfig;
import com.hjq.demo.R;
import com.hjq.demo.helper.ActivityStackManager;
import com.hjq.demo.other.EventBusManager;
import com.hjq.demo.other.StatusManager;
import com.hjq.toast.ToastUtils;
import com.hjq.umeng.UmengClient;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的 Activity 基类
 */
public abstract class MyActivity extends BaseActivity
        implements OnTitleBarListener {

    /** 标题栏对象 */
    private TitleBar mTitleBar;
    /** 状态栏沉浸 */
    private ImmersionBar mImmersionBar;
    /** ButterKnife 注解 */
    private Unbinder mButterKnife;

    /**
     * 获取标题栏 id
     */
    protected int getTitleId() {
        return 0;
    }

    @Override
    protected void initActivity() {
        super.initActivity();
        ActivityStackManager.getInstance().onCreated(this);
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        // 初始化标题栏的监听
        if (getTitleId() > 0) {
            // 勤快模式
            View view = findViewById(getTitleId());
            if (view instanceof TitleBar) {
                mTitleBar = (TitleBar) view;
            }
        } else if (getTitleId() == 0) {
            // 懒人模式
            mTitleBar = findTitleBar(getContentView());
        }
        if (mTitleBar != null) {
            mTitleBar.setOnTitleBarListener(this);
        }

        mButterKnife = ButterKnife.bind(this);
        EventBusManager.register(this);
        initImmersion();
    }

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    static TitleBar findTitleBar(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof TitleBar)) {
                return (TitleBar) view;
            } else if (view instanceof ViewGroup) {
                TitleBar titleBar = findTitleBar((ViewGroup) view);
                if (titleBar != null) {
                    return titleBar;
                }
            }
        }
        return null;
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersion() {
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            statusBarConfig().init();

            // 设置标题栏沉浸
            if (getTitleId() > 0) {
                ImmersionBar.setTitleBar(this, findViewById(getTitleId()));
            } else if (mTitleBar != null) {
                ImmersionBar.setTitleBar(this, mTitleBar);
            }
        }
    }

    /**
     * 是否使用沉浸式状态栏
     */
    public boolean isStatusBarEnabled() {
        return true;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    public ImmersionBar getStatusBarConfig() {
        return mImmersionBar;
    }

    /**
     * 获取状态栏字体颜色
     */
    public boolean statusBarDarkFont() {
        // 返回真表示黑色字体
        return true;
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected ImmersionBar statusBarConfig() {
        // 在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(statusBarDarkFont());
        return mImmersionBar;
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(@StringRes int id) {
        setTitle(getString(id));
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
        }
    }

    /**
     * 设置标题栏的左标题
     */
    public void setLeftTitle(int id) {
        if (mTitleBar != null) {
            mTitleBar.setLeftTitle(id);
        }
    }

    public void setLeftTitle(CharSequence text) {
        if (mTitleBar != null) {
            mTitleBar.setLeftTitle(text);
        }
    }

    public CharSequence getLeftTitle() {
        if (mTitleBar != null) {
            return mTitleBar.getLeftTitle();
        }
        return "";
    }

    /**
     * 设置标题栏的右标题
     */
    public void setRightTitle(int id) {
        if (mTitleBar != null) {
            mTitleBar.setRightTitle(id);
        }
    }

    public void setRightTitle(CharSequence text) {
        if (mTitleBar != null) {
            mTitleBar.setRightTitle(text);
        }
    }

    public CharSequence getRightTitle() {
        if (mTitleBar != null) {
            return mTitleBar.getRightTitle();
        }
        return "";
    }

    /**
     * 设置标题栏的左图标
     */
    public void setLeftIcon(int id) {
        if (mTitleBar != null) {
            mTitleBar.setLeftIcon(id);
        }
    }

    public void setLeftIcon(Drawable drawable) {
        if (mTitleBar != null) {
            mTitleBar.setLeftIcon(drawable);
        }
    }

    @Nullable
    public Drawable getLeftIcon() {
        if (mTitleBar != null) {
            return mTitleBar.getLeftIcon();
        }
        return null;
    }

    /**
     * 设置标题栏的右图标
     */
    public void setRightIcon(int id) {
        if (mTitleBar != null) {
            mTitleBar.setRightIcon(id);
        }
    }

    public void setRightIcon(Drawable drawable) {
        if (mTitleBar != null) {
            mTitleBar.setRightIcon(drawable);
        }
    }

    @Nullable
    public Drawable getRightIcon() {
        if (mTitleBar != null) {
            return mTitleBar.getRightIcon();
        }
        return null;
    }

    @Nullable
    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    /**
     * {@link OnTitleBarListener}
     */

    /**
     * TitleBar 左边的View被点击了
     */
    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }

    /**
     * TitleBar 中间的View被点击了
     */
    @Override
    public void onTitleClick(View v) {}

    /**
     * TitleBar 右边的View被点击了
     */
    @Override
    public void onRightClick(View v) {}

    @Override
    protected void onResume() {
        super.onResume();
        UmengClient.onResume(this);
    }

    @Override
    protected void onPause() {
        UmengClient.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mButterKnife != null) {
            mButterKnife.unbind();
        }
        EventBusManager.unregister(this);
        ActivityStackManager.getInstance().onDestroyed(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_right_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
    }

    /**
     * 显示吐司
     */
    public void toast(CharSequence text) {
        ToastUtils.show(text);
    }

    public void toast(@StringRes int id) {
        ToastUtils.show(id);
    }

    public void toast(Object object) {
        ToastUtils.show(object);
    }

    /**
     * 打印日志
     */
    public void log(Object object) {
        if (BuildConfig.DEBUG) {
            Log.v(getClass().getSimpleName(), object != null ? object.toString() : "null");
        }
    }

    private final StatusManager mStatusManager = new StatusManager();

    /**
     * 显示加载中
     */
    public void showLoading() {
        mStatusManager.showLoading(this);
    }

    public void showLoading(@StringRes int id) {
        mStatusManager.showLoading(this, getString(id));
    }

    public void showLoading(CharSequence text) {
        mStatusManager.showLoading(this, text);
    }

    /**
     * 显示加载完成
     */
    public void showComplete() {
        mStatusManager.showComplete();
    }

    /**
     * 显示空提示
     */
    public void showEmpty() {
        mStatusManager.showEmpty(getContentView());
    }

    /**
     * 显示错误提示
     */
    public void showError() {
        mStatusManager.showError(getContentView());
    }

    /**
     * 显示自定义提示
     */
    public void showLayout(@DrawableRes int drawableId, @StringRes int stringId) {
        mStatusManager.showLayout(getContentView(), drawableId, stringId);
    }

    public void showLayout(Drawable drawable, CharSequence hint) {
        mStatusManager.showLayout(getContentView(), drawable, hint);
    }
}