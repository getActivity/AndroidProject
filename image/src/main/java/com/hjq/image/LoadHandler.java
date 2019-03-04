package com.hjq.image;

import android.content.Context;
import android.widget.ImageView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : 图片加载通用性接口
 */
public interface LoadHandler {

    /**
     * 通过 url 加载普通图片
     */
    void loadImage(Context context, ImageView imageView, String url);

    /**
     * 通过资源 id 加载普通图片
     */
    void loadImage(Context context, ImageView imageView, int resourceId);

    /**
     * 通过 url 加载圆形图片
     */
    void loadCircleImage(Context context, ImageView imageView, String url);

    /**
     * 通过资源 id 加载圆形图片
     */
    void loadCircleImage(Context context, ImageView imageView, int resourceId);

    /**
     * 通过 url 加载圆角图片
     */
    void loadRoundImage(Context context, ImageView imageView, String url, float cornerRadius);

    /**
     * 通过资源 id 加载圆角图片
     */
    void loadRoundImage(Context context, ImageView imageView, int resourceId, float cornerRadius);
}