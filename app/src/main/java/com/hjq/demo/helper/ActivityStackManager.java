package com.hjq.demo.helper;

import android.app.Activity;

import java.util.HashMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/18
 *    desc   : Activity 栈管理
 */
public class ActivityStackManager {

    private static volatile ActivityStackManager sInstance;

    private HashMap<String, Activity> mActivitySet = new HashMap<>();

    // 当前 Activity 对象标记
    private String mCurrentTag;

    private ActivityStackManager() {}

    public static ActivityStackManager getInstance() {
        // 加入双重校验锁
        if(sInstance == null) {
            synchronized (ActivityStackManager.class) {
                if(sInstance == null){
                    sInstance = new ActivityStackManager();
                }
            }
        }
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

    public void onActivityCreated(Activity activity) {
        mCurrentTag = getObjectTag(activity);
        mActivitySet.put(getObjectTag(activity), activity);
    }

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