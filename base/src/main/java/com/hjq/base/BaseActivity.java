package com.hjq.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Random;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : Activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 获取一个 Handler 对象
     */
    public static Handler getHandler() {
        return HANDLER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
    }

    protected void initActivity() {
        initLayout();
        initView();
        initData();
    }

    protected void initLayout() {
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }
    }

    // 引入布局
    protected abstract int getLayoutId();

    // 标题栏id
    protected abstract int getTitleBarId();

    // 初始化控件
    protected abstract void initView();

    // 初始化数据
    protected abstract void initData();

    @Override
    public void finish() {
        hideSoftKeyboard();
        super.finish();
    }

    /**
     * 获取一个 Context 对象
     */
    public Context getContext() {
        return getBaseContext();
    }


    /**
     * 获取当前 Activity 对象
     */
    public <A extends BaseActivity> A getActivity() {
        return (A) this;
    }


    /**
     * startActivity 方法优化
     */

    public void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    public void startActivityFinish(Class<? extends Activity> cls) {
        startActivityFinish(new Intent(this, cls));
    }

    public void startActivityFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

    /**
     * setResult 方法优化
     */

    public void finishResult(int resultCode) {
        finishResult(resultCode, null);
    }

    public void finishResult(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }

    /**
     * startActivityForResult 方法优化
     */

    private ActivityCallback mActivityCallback;
    private int mActivityRequestCode;

    public void startActivityForResult(Class<? extends Activity> cls, ActivityCallback callback) {
        startActivityForResult(new Intent(this, cls), null, callback);
    }

    public void startActivityForResult(Intent intent, ActivityCallback callback) {
        startActivityForResult(intent, null, callback);
    }

    public void startActivityForResult(Intent intent, Bundle options, ActivityCallback callback) {
        if (mActivityCallback == null) {
            mActivityCallback = callback;
            // 随机生成请求码，这个请求码在 0 - 255 之间
            mActivityRequestCode = new Random().nextInt(255);
            startActivityForResult(intent, mActivityRequestCode, options);
        }else {
            // 回调还没有结束，所以不能再次调用此方法，这个方法只适合一对一回调，其他需求请使用原生的方法实现
            throw new IllegalArgumentException("Error, The callback is not over yet");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mActivityCallback != null && mActivityRequestCode == requestCode) {
            mActivityCallback.onActivityResult(resultCode, data);
            mActivityCallback = null;
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 防 Activity 多重跳转：https://www.jianshu.com/p/579f1f118161
     */

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (startActivitySelfCheck(intent)) {
            hideSoftKeyboard();
            // 查看源码得知 startActivity 最终也会调用 startActivityForResult
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    private String mStartActivityTag;
    private long mStartActivityTime;

    /**
     * 检查当前 Activity 是否重复跳转了，不需要检查则重写此方法并返回 true 即可
     *
     * @param intent          用于跳转的 Intent 对象
     * @return                检查通过返回true, 检查不通过返回false
     */
    protected boolean startActivitySelfCheck(Intent intent) {
        // 默认检查通过
        boolean result = true;
        // 标记对象
        String tag;
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
        }else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
        }else { // 其他方式
            return result;
        }

        if (tag.equals(mStartActivityTag) && mStartActivityTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false;
        }

        mStartActivityTag = tag;
        mStartActivityTime = SystemClock.uptimeMillis();
        return result;
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyboard() {
        // 隐藏软键盘，避免软键盘引发的内存泄露
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Activity 回调接口
     */
    public interface ActivityCallback {

        /**
         * 结果回调
         *
         * @param resultCode        结果码
         * @param data              数据
         */
        void onActivityResult(int resultCode, @Nullable Intent data);
    }
}