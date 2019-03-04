package com.hjq.image;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.hjq.copy.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/27
 *    desc   : Glide 加工厂
 */
class GlideFactory implements ImageFactory<GlideHandler> {

    @Override
    public GlideHandler create() {
        return new GlideHandler();
    }

    @Override
    public void init(Application application, GlideHandler handler) {
        handler.setPlaceholder(getLoadingPic(application));
        handler.setError(getErrorPic(application));
    }

    @Override
    public Drawable getLoadingPic(Context context) {
        return context.getResources().getDrawable(R.mipmap.image_loading);
    }

    @Override
    public Drawable getErrorPic(Context context) {
        return context.getResources().getDrawable(R.mipmap.image_load_err);
    }

    @Override
    public void clear(final Context context) {
        // 清除内存缓存（必须在主线程）
        Glide.get(context).clearMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 清除本地缓存（必须在子线程）
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }
}