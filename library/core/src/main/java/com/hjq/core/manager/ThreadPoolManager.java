package com.hjq.core.manager;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/01/11
 *    desc   : 线程池管理类
 */
public final class ThreadPoolManager extends ThreadPoolExecutor {

    private static volatile ThreadPoolManager sInstance;

    private ThreadPoolManager() {
        // 这里最大线程数为什么不是 Int 最大值？因为在华为荣耀机子上面有最大线程数限制
        // 经过测试华为荣耀手机不能超过 300 个线程，否则会出现内存溢出
        // java.lang.OutOfMemoryError：pthread_create (1040KB stack) failed: Out of memory
        // 由于应用自身占用了一些线程数，故减去 300 - 100 = 200 个
        super(0, 200,
                60, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    public static ThreadPoolManager getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolManager();
                }
            }
        }
        return sInstance;
    }
}