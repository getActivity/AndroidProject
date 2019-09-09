package com.hjq.image;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : 图片加载器
 */
public final class ImageLoader {

    /** 图片生产工厂 */
    private static ImageFactory sImageFactory;
    /** 图片加载策略 */
    private static ImageStrategy sImageStrategy;

    /** 加载中占位图 */
    private static Drawable sPlaceholder;
    /** 加载出错占位图 */
    private static Drawable sError;

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
        sImageStrategy = factory.createImageStrategy();
        sPlaceholder = factory.createPlaceholder(application);
        sError = factory.createError(application);
    }

    /**
     * 清除图片缓存
     */
    public static void clear(Context context) {
        clearMemoryCache(context);
        clearDiskCache(context);
    }

    /**
     * 清除内存缓存
     */
    public static void clearMemoryCache(Context context) {
        sImageFactory.clearMemoryCache(context);
    }

    /**
     * 清除磁盘缓存
     */
    public static void clearDiskCache(final Context context) {
        sImageFactory.clearDiskCache(context);
    }

    final Object context;
    int circle;
    String url;
    @DrawableRes int resourceId;
    boolean isGif;

    Drawable placeholder = sPlaceholder;
    Drawable error = sError;

    int width;
    int height;

    ImageView view;

    public ImageLoader(Object context) {
        this.context = context;
    }

    public static ImageLoader with(Context context) {
        return new ImageLoader(context);
    }

    public static ImageLoader with(Fragment fragment) {
        return new ImageLoader(fragment);
    }

    public static ImageLoader with(androidx.fragment.app.Fragment fragment) {
        return new ImageLoader(fragment);
    }

    public ImageLoader gif() {
        this.isGif = true;
        return this;
    }

    public ImageLoader circle() {
        return circle(Integer.MAX_VALUE);
    }

    public ImageLoader circle(int circle) {
        this.circle = circle;
        return this;
    }

    public ImageLoader load(String url) {
        this.url = url;
        return this;
    }

    public ImageLoader load(File file) {
        this.url = Uri.fromFile(file).toString();
        return this;
    }

    public ImageLoader load(@DrawableRes int id) {
        this.resourceId = id;
        return this;
    }

    public ImageLoader placeholder(Drawable placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public ImageLoader error(Drawable error) {
        this.error = error;
        return this;
    }

    public ImageLoader override(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public void into(ImageView view) {
        this.view = view;
        sImageStrategy.load(this);
    }
}