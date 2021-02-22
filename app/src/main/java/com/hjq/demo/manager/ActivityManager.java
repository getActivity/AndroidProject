package com.hjq.demo.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/18
 *    desc   : Activity 管理类
 */
public final class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private static volatile ActivityManager sInstance;

    private final ArrayMap<String, Activity> mActivitySet = new ArrayMap<>();

    /** 当前应用上下文对象 */
    private Application mApplication;
    /** 最后一个可见 Activity 标记 */
    private String mLastVisibleTag;
    /** 最后一个不可见 Activity 标记 */
    private String mLastInvisibleTag;

    private ActivityManager() {}

    public static ActivityManager getInstance() {
        if(sInstance == null) {
            synchronized (ActivityManager.class) {
                if(sInstance == null) {
                    sInstance = new ActivityManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Application application) {
        mApplication = application;
        mApplication.registerActivityLifecycleCallbacks(this);
    }

    /**
     * 获取 Application 对象
     */
    public Application getApplication() {
        return mApplication;
    }

    /**
     * 获取栈顶的 Activity
     */
    public Activity getTopActivity() {
        return mActivitySet.get(mLastVisibleTag);
    }

    /**
     * 判断当前应用是否处于前台状态
     */
    public boolean isForeground() {
        // 如果最后一个可见的 Activity 和最后一个不可见的 Activity 是同一个的话
        if (mLastVisibleTag.equals(mLastInvisibleTag)) {
            return false;
        }
        Activity activity = getTopActivity();
        return activity != null;
    }

    /**
     * 销毁所有的 Activity
     */
    public void finishAllActivities() {
        finishAllActivities((Class<? extends Activity>) null);
    }

    /**
     * 销毁所有的 Activity，除这些 Class 之外的 Activity
     */
    @SafeVarargs
    public final void finishAllActivities(Class<? extends Activity>... classArray) {
        String[] keys = mActivitySet.keySet().toArray(new String[]{});
        for (String key : keys) {
            Activity activity = mActivitySet.get(key);
            if (activity != null && !activity.isFinishing()) {
                boolean whiteClazz = false;
                if (classArray != null) {
                    for (Class<? extends Activity> clazz : classArray) {
                        if (activity.getClass() == clazz) {
                            whiteClazz = true;
                        }
                    }
                }
                // 如果不是白名单上面的 Activity 就销毁掉
                if (!whiteClazz) {
                    activity.finish();
                    mActivitySet.remove(key);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Timber.i("%s - onCreate", activity.getClass().getSimpleName());
        mLastVisibleTag = getObjectTag(activity);
        mActivitySet.put(getObjectTag(activity), activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Timber.i("%s - onStart", activity.getClass().getSimpleName());
        mLastVisibleTag = getObjectTag(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Timber.i("%s - onResume", activity.getClass().getSimpleName());
        mLastVisibleTag = getObjectTag(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Timber.i("%s - onPause", activity.getClass().getSimpleName());
        mLastInvisibleTag = getObjectTag(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Timber.i("%s - onStop", activity.getClass().getSimpleName());
        mLastInvisibleTag = getObjectTag(activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Timber.i("%s - onSaveInstanceState", activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Timber.i("%s - onDestroy", activity.getClass().getSimpleName());
        mActivitySet.remove(getObjectTag(activity));
        mLastInvisibleTag = getObjectTag(activity);
        if (getObjectTag(activity).equals(mLastVisibleTag)) {
            // 清除当前标记
            mLastVisibleTag = null;
        }
    }

    /**
     * 获取一个对象的独立无二的标记
     */
    private static String getObjectTag(Object object) {
        // 对象所在的包名 + 对象的内存地址
        return object.getClass().getName() + Integer.toHexString(object.hashCode());
    }
}