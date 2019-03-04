package com.hjq.image;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/27
 *    desc   : 图片加载器生产机器
 */
public interface ImageFactory<T extends LoadHandler> {

    /**
     * 创建一个图片加载器
     */
    T create();

    /**
     * 初始化图片加载器
     */
    void init(Application application, T handler);

    /**
     * 获取图片加载占位图
     */
    Drawable getLoadingPic(Context context);

    /**
     * 获取图片加载失败占位图
     */
    Drawable getErrorPic(Context context);

    /**
     * 清理缓存
     */
    void clear(Context context);
}