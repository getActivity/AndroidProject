package com.hjq.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import com.hjq.core.action.BundleAction;
import com.hjq.core.action.ClickAction;
import com.hjq.core.action.HandlerAction;
import com.hjq.core.action.KeyboardAction;
import com.hjq.core.tools.AndroidVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : Fragment 技术基类
 */
public abstract class BaseFragment<A extends BaseActivity> extends Fragment implements
        Application.ActivityLifecycleCallbacks, HandlerAction, ClickAction, BundleAction, KeyboardAction {

    /** Activity 对象 */
    @Nullable
    private A mActivity;

    /** 根布局 */
    @Nullable
    private View mRootView;

    /** 当前是否加载过 */
    private boolean mLoading;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 获得全局的 Activity
        mActivity = (A) requireActivity();
        registerAttachActivityLifecycle();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() <= 0) {
            return null;
        }

        mLoading = false;
        mRootView = inflater.inflate(getLayoutId(), container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLoading) {
            return;
        }
        mLoading = true;
        initData();
    }

    /**
     * Activity 获取焦点回调
     */
    protected void onActivityStart(@NonNull A attachActivity) {
        // default implementation ignored
    }

    /**
     * Activity 可见回调
     */
    protected void onActivityResume(@NonNull A attachActivity) {
        // default implementation ignored
    }

    /**
     * Activity 不可见回调
     */
    protected void onActivityPause(@NonNull A attachActivity) {
        // default implementation ignored
    }

    /**
     * Activity 失去焦点回调
     */
    protected void onActivityStop(@NonNull A attachActivity) {
        // default implementation ignored
    }

    /**
     * Activity 销毁时回调
     */
    protected void onActivityDestroy(@NonNull A attachActivity) {
        // default implementation ignored
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }

    @Override
    public void onDestroy() {
        removeCallbacks();
        super.onDestroy();
        mLoading = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterAttachActivityLifecycle();
        mActivity = null;
    }

    /**
     * 这个 Fragment 是否已经加载过了
     */
    public boolean isLoading() {
        return mLoading;
    }

    @Nullable
    @Override
    public View getView() {
        return mRootView;
    }

    /**
     * 获取绑定的 Activity，防止出现 getActivity 为空
     */
    @Nullable
    public A getAttachActivity() {
        return mActivity;
    }

    /**
     * 获取 Application 对象
     */
    public Application getApplication() {
        return mActivity != null ? mActivity.getApplication() : null;
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
     * 根据资源 id 获取一个 View 对象
     */
    @Override
    public <V extends View> V findViewById(@IdRes int id) {
        if (mRootView == null) {
            return null;
        }
        return mRootView.findViewById(id);
    }

    @Nullable
    @Override
    public Bundle getBundle() {
        return getArguments();
    }

    /**
     * 跳转 Activity 简化版
     */
    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(getContext(), clazz));
    }

    /**
     * startActivityForResult 方法优化
     */

    public void startActivityForResult(Class<? extends Activity> clazz, BaseActivity.OnActivityCallback callback) {
        if (mActivity == null) {
            return;
        }
        mActivity.startActivityForResult(clazz, callback);
    }

    public void startActivityForResult(Intent intent, BaseActivity.OnActivityCallback callback) {
        if (mActivity == null) {
            return;
        }
        mActivity.startActivityForResult(intent, null, callback);
    }

    public void startActivityForResult(Intent intent, Bundle options, BaseActivity.OnActivityCallback callback) {
        if (mActivity == null) {
            return;
        }
        mActivity.startActivityForResult(intent, options, callback);
    }

    /**
     * 销毁当前 Fragment 所在的 Activity
     */
    public void finish() {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        mActivity.finish();
    }

    /**
     * Fragment 按键事件派发
     */
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            // 这个子 Fragment 必须是 BaseFragment 的子类，并且处于可见状态
            if (!(fragment instanceof BaseFragment) ||
                    fragment.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                continue;
            }
            // 将按键事件派发给子 Fragment 进行处理
            if (((BaseFragment<?>) fragment).dispatchKeyEvent(event)) {
                // 如果子 Fragment 拦截了这个事件，那么就不交给父 Fragment 处理
                return true;
            }
        }
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                return onKeyDown(event.getKeyCode(), event);
            case KeyEvent.ACTION_UP:
                return onKeyUp(event.getKeyCode(), event);
            default:
                return false;
        }
    }

    /**
     * 按键按下事件回调
     */
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        // 默认不拦截按键事件
        return false;
    }

    /**
     * 按键抬起事件回调
     */
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        // 默认不拦截按键事件
        return false;
    }

    @Override
    public final void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        // default implementation ignored
    }

    @Override
    public final void onActivityStarted(@NonNull Activity activity) {
        if (activity != mActivity) {
            return;
        }
        onActivityStart(mActivity);
    }

    @Override
    public final void onActivityResumed(@NonNull Activity activity) {
        if (activity != mActivity) {
            return;
        }
        onActivityResume(mActivity);
    }

    @Override
    public final void onActivityPaused(@NonNull Activity activity) {
        if (activity != mActivity) {
            return;
        }
        onActivityPause(mActivity);
    }

    @Override
    public final void onActivityStopped(@NonNull Activity activity) {
        if (activity != mActivity) {
            return;
        }
        onActivityStop(mActivity);
    }

    @Override
    public final void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // default implementation ignored
    }

    @Override
    public final void onActivityDestroyed(@NonNull Activity activity) {
        if (activity != mActivity) {
            return;
        }
        onActivityDestroy(mActivity);
        unregisterAttachActivityLifecycle();
    }

    /**
     * 注册绑定 Activity 生命周期回调
     */
    private void registerAttachActivityLifecycle() {
        if (mActivity == null) {
            return;
        }
        if (AndroidVersion.isAndroid10()) {
            mActivity.registerActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * 反注册绑定 Activity 生命周期回调
     */
    private void unregisterAttachActivityLifecycle() {
        if (mActivity == null) {
            return;
        }
        if (AndroidVersion.isAndroid10()) {
            mActivity.unregisterActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }
}