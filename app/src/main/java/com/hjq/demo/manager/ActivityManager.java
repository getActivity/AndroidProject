package com.hjq.demo.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import java.util.ArrayList;

import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/18
 *    desc   : Activity 管理类
 */
public final class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private static volatile ActivityManager sInstance;

    /** Activity 存放集合 */
    private final ArrayMap<String, Activity> mActivitySet = new ArrayMap<>();

    /** 应用生命周期回调 */
    private final ArrayList<ApplicationLifecycleCallback> mLifecycleCallbacks = new ArrayList<>();

    /** 当前应用上下文对象 */
    private Application mApplication;
    /** 栈顶的 Activity 对象 */
    private Activity mTopActivity;
    /** 前台并且可见的 Activity 对象 */
    private Activity mResumedActivity;

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
    @Nullable
    public Activity getTopActivity() {
        return mTopActivity;
    }

    /**
     * 获取前台并且可见的 Activity
     */
    @Nullable
    public Activity getResumedActivity() {
        return mResumedActivity;
    }

    /**
     * 判断当前应用是否处于前台状态
     */
    public boolean isForeground() {
        return getResumedActivity() != null;
    }

    /**
     * 注册应用生命周期回调
     */
    public void registerApplicationLifecycleCallback(ApplicationLifecycleCallback callback) {
        mLifecycleCallbacks.add(callback);
    }

    /**
     * 取消注册应用生命周期回调
     */
    public void unregisterApplicationLifecycleCallback(ApplicationLifecycleCallback callback) {
        mLifecycleCallbacks.remove(callback);
    }

    /**
     * 销毁指定的 Activity
     */
    public void finishActivity(Class<? extends Activity> clazz) {
        if (clazz == null) {
            return;
        }
        String[] keys = mActivitySet.keySet().toArray(new String[]{});
        for (String key : keys) {
            Activity activity = mActivitySet.get(key);
            if (activity == null || activity.isFinishing()) {
                continue;
            }

            if (activity.getClass().equals(clazz)) {
                activity.finish();
                mActivitySet.remove(key);
                break;
            }
        }
    }

    /**
     * 销毁所有的 Activity
     */
    public void finishAllActivities() {
        finishAllActivities((Class<? extends Activity>) null);
    }

    /**
     * 销毁所有的 Activity
     *
     * @param classArray            白名单 Activity
     */
    @SafeVarargs
    public final void finishAllActivities(Class<? extends Activity>... classArray) {
        String[] keys = mActivitySet.keySet().toArray(new String[]{});
        for (String key : keys) {
            Activity activity = mActivitySet.get(key);
            if (activity == null || activity.isFinishing()) {
                continue;
            }

            boolean whiteClazz = false;
            if (classArray != null) {
                for (Class<? extends Activity> clazz : classArray) {
                    if (activity.getClass().equals(clazz)) {
                        whiteClazz = true;
                    }
                }
            }

            if (whiteClazz) {
                continue;
            }

            // 如果不是白名单上面的 Activity 就销毁掉
            activity.finish();
            mActivitySet.remove(key);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Timber.i("%s - onCreate", activity.getClass().getSimpleName());
        if (mActivitySet.size() == 0) {
            for (ApplicationLifecycleCallback callback : mLifecycleCallbacks) {
                callback.onApplicationCreate(activity);
            }
            Timber.i("%s - onApplicationCreate", activity.getClass().getSimpleName());
        }
        mActivitySet.put(getObjectTag(activity), activity);
        mTopActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Timber.i("%s - onStart", activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Timber.i("%s - onResume", activity.getClass().getSimpleName());
        if (mTopActivity == activity && mResumedActivity == null) {
            for (ApplicationLifecycleCallback callback : mLifecycleCallbacks) {
                callback.onApplicationForeground(activity);
            }
            Timber.i("%s - onApplicationForeground", activity.getClass().getSimpleName());
        }
        mTopActivity = activity;
        mResumedActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Timber.i("%s - onPause", activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Timber.i("%s - onStop", activity.getClass().getSimpleName());
        if (mResumedActivity == activity) {
            mResumedActivity = null;
        }
        if (mResumedActivity == null) {
            for (ApplicationLifecycleCallback callback : mLifecycleCallbacks) {
                callback.onApplicationBackground(activity);
            }
            Timber.i("%s - onApplicationBackground", activity.getClass().getSimpleName());
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Timber.i("%s - onSaveInstanceState", activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Timber.i("%s - onDestroy", activity.getClass().getSimpleName());
        mActivitySet.remove(getObjectTag(activity));
        if (mTopActivity == activity) {
            mTopActivity = null;
        }
        if (mActivitySet.size() == 0) {
            for (ApplicationLifecycleCallback callback : mLifecycleCallbacks) {
                callback.onApplicationDestroy(activity);
            }
            Timber.i("%s - onApplicationDestroy", activity.getClass().getSimpleName());
        }
    }

    /**
     * 获取一个对象的独立无二的标记
     */
    private static String getObjectTag(Object object) {
        // 对象所在的包名 + 对象的内存地址
        return object.getClass().getName() + Integer.toHexString(object.hashCode());
    }

    /**
     * 应用生命周期回调
     */
    public interface ApplicationLifecycleCallback {

        /**
         * 第一个 Activity 创建了
         */
        void onApplicationCreate(Activity activity);

        /**
         * 最后一个 Activity 销毁了
         */
        void onApplicationDestroy(Activity activity);

        /**
         * 应用从前台进入到后台
         */
        void onApplicationBackground(Activity activity);

        /**
         * 应用从后台进入到前台
         */
        void onApplicationForeground(Activity activity);
    }
}