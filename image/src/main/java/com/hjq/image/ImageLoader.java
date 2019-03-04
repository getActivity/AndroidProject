package com.hjq.image;

import android.app.Application;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : 图片加载器
 */
public class ImageLoader {

    private static ImageFactory sImageFactory; // 图片生产工厂
    private static LoadHandler sLoadHandler; // 图片加载处理对象

    public static void init(Application application) {
        // 使用 Glide 进行初始化图片加载器
        init(application, new GlideFactory());
    }

    /**
     * 使用指定的图片加载器进行初始化
     *
     * @param application               上下文对象
     * @param factory                   图片加载器生成对象
     */
    public static void init(Application application, ImageFactory factory) {
        sImageFactory = factory;
        sLoadHandler = factory.create();
        sImageFactory.init(application, sLoadHandler);
    }

    /**
     * 清除图片缓存
     */
    public static void clear(Context context) {
        sImageFactory.clear(context);
    }

    /**
     * 加载普通图片
     */

    public static void loadImage(ImageView imageView, String url) {
        loadImage(imageView.getContext(), imageView, url);
    }

    public static void loadImage(Context context, ImageView imageView, String url) {
        if (url != null && !"".equals(url)) {
            sLoadHandler.loadImage(context, imageView, url.trim());
        }
    }

    public static void loadImage(ImageView imageView, @DrawableRes int resourceId) {
        loadImage(imageView.getContext(), imageView, resourceId);
    }

    public static void loadImage(Context context, ImageView imageView, @DrawableRes int resourceId) {
        sLoadHandler.loadImage(context, imageView, resourceId);
    }

    /**
     * 加载圆形图片
     */

    public static void loadCircleImage(ImageView imageView, String url) {
        loadCircleImage(imageView.getContext(), imageView, url);
    }

    public static void loadCircleImage(Context context, ImageView imageView, String url) {
        if (url != null && !"".equals(url)) {
            sLoadHandler.loadCircleImage(context, imageView, url.trim());
        }
    }

    public static void loadCircleImage(ImageView imageView, @DrawableRes int resourceId) {
        loadCircleImage(imageView.getContext(), imageView, resourceId);
    }

    public static void loadCircleImage(Context context, ImageView imageView, @DrawableRes int resourceId) {
        sLoadHandler.loadCircleImage(context, imageView, resourceId);
    }

    /**
     * 加载圆角图片
     */

    public static void loadRoundImage(ImageView imageView, String url, float cornerRadius) {
        loadRoundImage(imageView.getContext(), imageView, url, cornerRadius);
    }

    public static void loadRoundImage(Context context, ImageView imageView, String url, float cornerRadius) {
        if (url != null && !"".equals(url)) {
            sLoadHandler.loadRoundImage(context, imageView, url.trim(), cornerRadius);
        }
    }

    public static void loadRoundImage(ImageView imageView, @DrawableRes int resourceId, float cornerRadius) {
        loadRoundImage(imageView.getContext(), imageView, resourceId, cornerRadius);
    }

    public static void loadRoundImage(Context context, ImageView imageView, @DrawableRes int resourceId, float cornerRadius) {
        sLoadHandler.loadRoundImage(context, imageView, resourceId, cornerRadius);
    }
}