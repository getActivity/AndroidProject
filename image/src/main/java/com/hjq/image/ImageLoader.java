package com.hjq.image;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : 图片加载器
 */
public final class ImageLoader {

    private static ImageFactory sImageFactory; // 图片生产工厂
    private static ImageHandler sImageHandler; // 图片加载处理对象

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
    public static void init(@NonNull Application application,@NonNull ImageFactory factory) {
        sImageFactory = factory;
        sImageHandler = factory.create();
        sImageFactory.init(application, sImageHandler);
    }

    /**
     * 清除图片缓存
     */
    public static void clear(@NonNull Context context) {
        sImageFactory.clear(context);
    }

    /**
     * 加载普通图片
     */

    public static void loadImage(@NonNull ImageView imageView, String url) {
        loadImage(imageView.getContext(), imageView, url);
    }
    public static void loadImage(@NonNull Context context, @NonNull ImageView imageView, String url) {
        sImageHandler.loadImage(context, imageView, url);
    }
    public static void loadImage(Fragment fragment, @NonNull ImageView imageView, String url) {
        sImageHandler.loadImage(fragment, imageView, url);
    }
    public static void loadImage(android.support.v4.app.Fragment fragment, @NonNull ImageView imageView, String url) {
        sImageHandler.loadImage(fragment, imageView, url);
    }

    public static void loadImage(@NonNull ImageView imageView, @DrawableRes int resourceId) {
        loadImage(imageView.getContext(), imageView, resourceId);
    }
    public static void loadImage(@NonNull Context context, @NonNull ImageView imageView, @DrawableRes int resourceId) {
        sImageHandler.loadImage(context, imageView, resourceId);
    }
    public static void loadImage(Fragment fragment, @NonNull ImageView imageView, @DrawableRes int resourceId) {
        sImageHandler.loadImage(fragment, imageView, resourceId);
    }
    public static void loadImage(android.support.v4.app.Fragment fragment, @NonNull ImageView imageView, @DrawableRes int resourceId) {
        sImageHandler.loadImage(fragment, imageView, resourceId);
    }

    /**
     * 加载圆形图片
     */

    public static void loadCircleImage(@NonNull ImageView imageView, String url) {
        loadCircleImage(imageView.getContext(), imageView, url);
    }
    public static void loadCircleImage(@NonNull Context context, @NonNull ImageView imageView, String url) {
        sImageHandler.loadCircleImage(context, imageView, url);
    }
    public static void loadCircleImage(Fragment fragment, @NonNull ImageView imageView, String url) {
        sImageHandler.loadCircleImage(fragment, imageView, url);
    }
    public static void loadCircleImage(android.support.v4.app.Fragment fragment, @NonNull ImageView imageView, String url) {
        sImageHandler.loadCircleImage(fragment, imageView, url);
    }

    public static void loadCircleImage(@NonNull ImageView imageView, @DrawableRes int resourceId) {
        loadCircleImage(imageView.getContext(), imageView, resourceId);
    }
    public static void loadCircleImage(@NonNull Context context, @NonNull ImageView imageView, @DrawableRes int resourceId) {
        sImageHandler.loadCircleImage(context, imageView, resourceId);
    }
    public static void loadCircleImage(Fragment fragment, @NonNull ImageView imageView, @DrawableRes int resourceId) {
        sImageHandler.loadCircleImage(fragment, imageView, resourceId);
    }
    public static void loadCircleImage(android.support.v4.app.Fragment fragment, @NonNull ImageView imageView, @DrawableRes int resourceId) {
        sImageHandler.loadCircleImage(fragment, imageView, resourceId);
    }

    /**
     * 加载圆角图片
     */

    public static void loadRoundImage(@NonNull ImageView imageView, String url, float radius) {
        loadRoundImage(imageView.getContext(), imageView, url, radius);
    }
    public static void loadRoundImage(@NonNull Context context, @NonNull ImageView imageView, String url, float radius) {
        sImageHandler.loadRoundImage(context, imageView, url, radius);
    }
    public static void loadRoundImage(Fragment fragment, @NonNull ImageView imageView, String url, float radius) {
        sImageHandler.loadRoundImage(fragment, imageView, url, radius);
    }
    public static void loadRoundImage(android.support.v4.app.Fragment fragment, @NonNull ImageView imageView, String url, float radius) {
        sImageHandler.loadRoundImage(fragment, imageView, url, radius);
    }

    public static void loadRoundImage(@NonNull ImageView imageView, @DrawableRes int resourceId, float radius) {
        loadRoundImage(imageView.getContext(), imageView, resourceId, radius);
    }
    public static void loadRoundImage(@NonNull Context context, @NonNull ImageView imageView, @DrawableRes int resourceId, float radius) {
        sImageHandler.loadRoundImage(context, imageView, resourceId, radius);
    }
    public static void loadRoundImage(Fragment fragment, @NonNull ImageView imageView, @DrawableRes int resourceId, float radius) {
        sImageHandler.loadRoundImage(fragment, imageView, resourceId, radius);
    }
    public static void loadRoundImage(android.support.v4.app.Fragment fragment, @NonNull ImageView imageView, @DrawableRes int resourceId, float radius) {
        sImageHandler.loadRoundImage(fragment, imageView, resourceId, radius);
    }
}