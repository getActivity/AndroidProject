package com.hjq.demo.app;

import android.content.Intent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.action.ImmersionAction;
import com.hjq.demo.action.TitleBarAction;
import com.hjq.demo.action.ToastAction;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.ui.dialog.common.WaitDialog;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.umeng.sdk.UmengClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : Activity 业务基类
 */
public abstract class AppActivity extends BaseActivity
    implements ToastAction, TitleBarAction, ImmersionAction, OnHttpListener<Object> {

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

    /** 加载对话框 */
    private WaitDialog.Builder mDialog;
    /** 对话框数量 */
    private int mDialogCount;

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        return mDialog != null && mDialog.isShowing();
    }

    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        showLoadingDialog(getString(R.string.common_loading));
    }

    public void showLoadingDialog(String message) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        mDialogCount++;
        postDelayed(() -> {
            if (mDialogCount <= 0 || isFinishing() || isDestroyed()) {
                return;
            }

            if (mDialog == null) {
                mDialog = new WaitDialog.Builder(this)
                        .setCancelable(false);
            }
            mDialog.setMessage(message);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }, 300);
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoadingDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        if (mDialogCount > 0) {
            mDialogCount--;
        }

        if (mDialogCount != 0 || mDialog == null || !mDialog.isShowing()) {
            return;
        }

        mDialog.dismiss();
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        TitleBar titleBar = acquireTitleBar();
        if (titleBar != null) {
            titleBar.setOnTitleBarListener(this);
        }

        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            getStatusBarConfig().init();
        }

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
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), new OnApplyWindowInsetsListener() {

            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets windowInsets = getWindowInsets(insets);
                Integer statusBarHeight = mStatusBarHeightLiveData.getValue();
                if (statusBarHeight == null || statusBarHeight != windowInsets.top) {
                    mStatusBarHeightLiveData.postValue(windowInsets.top);
                }
                Integer navigationBarHeight = mNavigationBarHeightLiveData.getValue();
                if (navigationBarHeight == null || navigationBarHeight != windowInsets.bottom) {
                    mNavigationBarHeightLiveData.postValue(windowInsets.bottom);
                }
                return insets;
            }
        });
    }

    /**
     * 获取系统栏的高度
     */
    public Insets getWindowInsets(@NonNull WindowInsetsCompat insets) {
        return insets.getInsets(WindowInsetsCompat.Type.systemBars());
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

    /**
     * 是否使用沉浸式状态栏
     */
    protected boolean isStatusBarEnabled() {
        return true;
    }

    /**
     * 状态栏字体深色模式
     */
    protected boolean isStatusBarDarkFont() {
        // 返回 true 表示黑色字体
        return true;
    }

    /**
     * 获取导航栏图标颜色
     */
    protected boolean isNavigationBarDarkIcon() {
        // 返回 true 表示黑色图标
        return true;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @NonNull
    public ImmersionBar getStatusBarConfig() {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig();
        }
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式状态栏
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
        TitleBar titleBar = acquireTitleBar();
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    @Nullable
    @Override
    public TitleBar acquireTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = findTitleBar(getContentView());
        }
        return mTitleBar;
    }

    /**
     * 获取需要沉浸的顶部 View 对象
     */
    @Nullable
    @Override
    public View getImmersionTopView() {
        return acquireTitleBar();
    }

    @Override
    public void onLeftClick(TitleBar titleBar) {
        onBackPressed();
    }

    /**
     * {@link OnHttpListener}
     */

    @Override
    public void onHttpStart(@NonNull IRequestApi api) {
        showLoadingDialog();
    }

    @Override
    public void onHttpSuccess(@NonNull Object result) {
        if (result instanceof HttpData) {
            toast(((HttpData<?>) result).getMessage());
        }
    }

    @Override
    public void onHttpFail(@NonNull Throwable throwable) {
        toast(throwable.getMessage());
    }

    @Override
    public void onHttpEnd(@NonNull IRequestApi api) {
        hideLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isShowDialog()) {
            hideLoadingDialog();
        }
        mDialog = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟回调
        UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }
}