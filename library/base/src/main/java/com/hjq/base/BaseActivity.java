package com.hjq.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import com.hjq.core.action.ActivityAction;
import com.hjq.core.action.BundleAction;
import com.hjq.core.action.ClickAction;
import com.hjq.core.action.FixOrientationAction;
import com.hjq.core.action.HandlerAction;
import com.hjq.core.action.KeyboardAction;
import java.util.List;
import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : Activity 技术基类
 */
public abstract class BaseActivity extends AppCompatActivity
        implements ActivityAction, ClickAction, HandlerAction,
        BundleAction, KeyboardAction, FixOrientationAction {

    /** 错误结果码 */
    public static final int RESULT_ERROR = -2;

    /** Activity 回调集合 */
    private SparseArray<OnActivityCallback> mActivityCallbacks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isAllowOrientation(this)) {
            fixScreenOrientation(this);
        }
        super.onCreate(savedInstanceState);
        initActivity();
    }

    protected void initActivity() {
        initLayout();
        initView();
        initData();
    }

    /**
     * 获取布局 ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化布局
     */
    protected void initLayout() {
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            initSoftKeyboard();
        }
    }

    /**
     * 初始化软键盘
     */
    protected void initSoftKeyboard() {
        // 点击外部隐藏软键盘，提升用户体验
        getContentView().setOnClickListener(v -> {
            // 隐藏软键，避免内存泄漏
            hideKeyboard(getCurrentFocus());
        });
    }

    @Override
    protected void onDestroy() {
        removeCallbacks();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        // 隐藏软键，避免内存泄漏
        hideKeyboard(getCurrentFocus());
    }

    /**
     * 如果当前的 Activity（singleTop 启动模式） 被复用时会回调
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent);
    }

    @Nullable
    @Override
    public Bundle getBundle() {
        return getIntent().getExtras();
    }

    /**
     * 和 setContentView 对应的方法
     */
    public ViewGroup getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (!isAllowOrientation(this)) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            // 这个 Fragment 必须是 BaseFragment 的子类，并且处于可见状态
            if (!(fragment instanceof BaseFragment) ||
                    fragment.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                continue;
            }
            // 将按键事件派发给 Fragment 进行处理
            if (((BaseFragment<?>) fragment).dispatchKeyEvent(event)) {
                // 如果 Fragment 拦截了这个事件，那么就不交给 Activity 处理
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * startActivityForResult 方法优化
     */

    public void startActivityForResult(@NonNull Class<? extends Activity> clazz, @Nullable OnActivityCallback callback) {
        startActivityForResult(new Intent(this, clazz), null, callback);
    }

    public void startActivityForResult(@NonNull Intent intent, @Nullable OnActivityCallback callback) {
        startActivityForResult(intent, null, callback);
    }

    public void startActivityForResult(@NonNull Intent intent, @Nullable Bundle options, @Nullable OnActivityCallback callback) {
        if (mActivityCallbacks == null) {
            mActivityCallbacks = new SparseArray<>(1);
        }

        // 请求码必须在 2 的 16 次方以内
        int requestCode;
        do {
            requestCode = new Random().nextInt((int) Math.pow(2, 16));
        } while (mActivityCallbacks.indexOfKey(requestCode) != -1);
        mActivityCallbacks.put(requestCode, callback);

        startActivityForResult(intent, requestCode, options);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        // 隐藏软键，避免内存泄漏
        hideKeyboard(getCurrentFocus());
        // 查看源码得知 startActivity 最终也会调用 startActivityForResult
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        OnActivityCallback callback;
        if (mActivityCallbacks != null && (callback = mActivityCallbacks.get(requestCode)) != null) {
            callback.onActivityResult(resultCode, data);
            mActivityCallbacks.remove(requestCode);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnActivityCallback {

        /**
         * 结果回调
         *
         * @param resultCode        结果码
         * @param data              数据
         */
        void onActivityResult(int resultCode, @Nullable Intent data);
    }
}