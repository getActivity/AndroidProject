package com.hjq.demo.app;

import android.content.Intent;
import android.graphics.Insets;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.core.tools.AndroidVersion;
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

        // 适配 Android 15 EdgeToEdge 特性
        if (AndroidVersion.isAndroid15()) {
            getWindow().getDecorView().setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener()  {

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
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(isStatusBarDarkFont())
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
                .autoDarkModeEnable(true, 0.2f);
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