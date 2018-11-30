package com.hjq.demo.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.HashMap;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/18
 *    desc   : Activity 栈管理
 */
public class ActivityStackManager implements Application.ActivityLifecycleCallbacks {

    private static ActivityStackManager sInstance;

    private HashMap<String, Activity> mActivitySet;

    // 当前 Activity 对象标记
    private String mCurrentTag;

    private ActivityStackManager(Application application) {
        mActivitySet = new HashMap<>();
        application.registerActivityLifecycleCallbacks(this);
    }

    public static void init(Application application) {
        sInstance = new ActivityStackManager(application);
    }

    public static ActivityStackManager getInstance() {
        return sInstance;
    }

    /**
     * 获取栈顶的 Activity
     */
    public Activity getTopActivity() {
        return mActivitySet.get(mCurrentTag);
    }

    /**
     * 销毁所有的 Activity
     */
    public void finishAllActivities() {
        finishAllActivities(null);
    }

    /**
     * 销毁所有的 Activity，除这个 Class 之外的 Activity
     */
    public void finishAllActivities(Class<? extends Activity> clazz) {
        String[] keys = mActivitySet.keySet().toArray(new String[]{});
        for (String key : keys) {
            Activity activity = mActivitySet.get(key);
            if (activity != null && !activity.isFinishing() &&
                    !(activity.getClass() == clazz)) {
                activity.finish();
                mActivitySet.remove(key);
            }
        }
    }

    /**
     * {@link Application.ActivityLifecycleCallbacks}
     */

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mCurrentTag = getObjectTag(activity);
        mActivitySet.put(getObjectTag(activity), activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mCurrentTag = getObjectTag(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurrentTag = getObjectTag(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivitySet.remove(getObjectTag(activity));
        // 如果当前的 Activity 是最后一个的话
        if (getObjectTag(activity).equals(mCurrentTag)) {
            // 清除当前标记
            mCurrentTag = null;
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
