package com.hjq.demo.app;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.demo.action.ImmersionAction;
import com.hjq.demo.action.TitleBarAction;

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
    /** 状态栏高度 LiveData */
    @NonNull
    private final MutableLiveData<Integer> mStatusBarHeightLiveData = new MutableLiveData<>();
    /** 导航栏高度 LiveData */
    @NonNull
    private final MutableLiveData<Integer> mNavigationBarHeightLiveData = new MutableLiveData<>();

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

        A attachActivity = getAttachActivity();
        if (attachActivity != null) {
            // 监听状态栏和导航栏高度变化
            mStatusBarHeightLiveData.observe(this, statusBarHeight -> {
                if (statusBarHeight == null) {
                    return;
                }
                View immersionTopView = getImmersionTopView();
                if (immersionTopView == null) {
                    return;
                }
                immersionTopView.setPadding(immersionTopView.getPaddingLeft(), statusBarHeight,
                    immersionTopView.getPaddingRight(), immersionTopView.getPaddingBottom());
            });
            mNavigationBarHeightLiveData.observe(this, navigationBarHeight -> {
                if (navigationBarHeight == null) {
                    return;
                }
                View immersionBottomView = getImmersionBottomView();
                if (immersionBottomView == null) {
                    return;
                }
                immersionBottomView.setPadding(immersionBottomView.getPaddingLeft(), immersionBottomView.getPaddingTop(),
                    immersionBottomView.getPaddingRight(), navigationBarHeight);
            });
            attachActivity.observeStatusBarHeight(mStatusBarHeightLiveData::postValue);
            attachActivity.observeNavigationBarHeight(mNavigationBarHeightLiveData::postValue);
        }
    }

    /**
     * 监听状态栏高度变化
     */
    public void observeStatusBarHeight(@NonNull Observer<Integer> observer) {
        observeStatusBarHeight(this, observer);
    }

    public void observeStatusBarHeight(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<Integer> observer) {
        mStatusBarHeightLiveData.observe(lifecycleOwner, observer);
    }

    /**
     * 监听导航栏高度变化
     */
    public void observeNavigationBarHeight(@NonNull Observer<Integer> observer) {
        observeNavigationBarHeight(this, observer);
    }

    public void observeNavigationBarHeight(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<Integer> observer) {
        mNavigationBarHeightLiveData.observe(lifecycleOwner, observer);
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
            // 设置状态栏字体的颜色
            .statusBarDarkFont(isStatusBarDarkFont())
            // 设置透明的导航栏
            .transparentNavigationBar()
            // 设置导航栏图标的颜色
            .navigationBarDarkIcon(isNavigationBarDarkIcon());
    }

    /**
     * 获取状态栏字体颜色
     */
    protected boolean isStatusBarDarkFont() {
        A activity = getAttachActivity();
        if (activity == null) {
            return false;
        }
        return activity.isStatusBarDarkFont();
    }

    /**
     * 获取导航栏图标颜色
     */
    protected boolean isNavigationBarDarkIcon() {
        A activity = getAttachActivity();
        if (activity == null) {
            return false;
        }
        return activity.isNavigationBarDarkIcon();
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