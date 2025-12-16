package com.hjq.demo.app;

import android.graphics.Insets;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.demo.R;
import com.hjq.demo.action.ImmersionAction;
import com.hjq.demo.action.TitleBarAction;
import com.hjq.demo.other.AndroidVersion;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/31
 *    desc   : 带标题栏的 Fragment 业务基类
 */
public abstract class TitleBarFragment<A extends AppActivity>
        extends AppFragment<A> implements TitleBarAction, ImmersionAction {

    /** 标题栏对象 */
    private TitleBar mTitleBar;
    /** 状态栏沉浸 */
    private ImmersionBar mImmersionBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置标题栏点击监听
        TitleBar titleBar = acquireTitleBar();
        if (titleBar != null) {
            titleBar.setOnTitleBarListener(this);
        }

        if (isStatusBarEnabled()) {
            // 初始化沉浸式状态栏
            getStatusBarConfig().init();
        }

        // 适配 Android 15 EdgeToEdge 特性
        if (AndroidVersion.isAndroid15()) {
            view.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener()  {

                @NonNull
                @Override
                public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
                    Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                    View immersionTopView = getImmersionTopView();
                    View immersionBottomView = getImmersionBottomView();
                    if (immersionTopView != null && immersionTopView == immersionBottomView) {
                        immersionTopView.setPadding(immersionTopView.getPaddingLeft(), systemBars.top,
                                                    immersionTopView.getPaddingRight(), systemBars.bottom);
                        return insets;
                    }
                    if (immersionTopView != null) {
                        immersionTopView.setPadding(immersionTopView.getPaddingLeft(), systemBars.top,
                                                    immersionTopView.getPaddingRight(), immersionTopView.getPaddingBottom());
                    }
                    if (immersionBottomView != null) {
                        immersionBottomView.setPadding(immersionBottomView.getPaddingLeft(), immersionBottomView.getPaddingTop(),
                                                       immersionBottomView.getPaddingRight(), systemBars.bottom);
                    }
                    return insets;
                }
            });
        } else {
            View immersionTopView = getImmersionTopView();
            if (immersionTopView != null) {
                ImmersionBar.setTitleBar(this, immersionTopView);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStatusBarEnabled()) {
            // 重新初始化状态栏
            getStatusBarConfig().init();
        }
    }

    /**
     * 是否在 Fragment 使用沉浸式
     */
    public boolean isStatusBarEnabled() {
        return false;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @NonNull
    protected ImmersionBar getStatusBarConfig() {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig();
        }
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式
     */
    @NonNull
    protected ImmersionBar createStatusBarConfig() {
        return ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(isStatusBarDarkFont())
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
                .autoDarkModeEnable(true, 0.2f);
    }

    /**
     * 获取状态栏字体颜色
     */
    protected boolean isStatusBarDarkFont() {
        A activity = getAttachActivity();
        if (activity == null) {
            return false;
        }
        // 返回真表示黑色字体
        return activity.isStatusBarDarkFont();
    }

    @Override
    @Nullable
    public TitleBar acquireTitleBar() {
        if (mTitleBar == null || !isLoading()) {
            mTitleBar = findTitleBar(getView());
        }
        return mTitleBar;
    }

    @Nullable
    @Override
    public View getImmersionTopView() {
        return acquireTitleBar();
    }
}