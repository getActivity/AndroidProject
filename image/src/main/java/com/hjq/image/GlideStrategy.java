package com.hjq.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : Glide 加载策略
 */
final class GlideStrategy implements ImageStrategy {

    @SuppressLint("CheckResult")
    @Override
    public void load(ImageLoader loader) {
        RequestManager manager = getRequestManager(loader.context);

        if (loader.isGif) {
            manager.asGif();
        }

        final RequestBuilder<Drawable> builder;
        if (loader.url != null && !"".equals(loader.url)) {
            builder = manager.load(loader.url.trim());
        } else if (loader.resourceId != 0) {
            builder = manager.load(loader.resourceId);
        } else {
            builder = manager.load(loader.error);
        }

        if (loader.placeholder != null) {
            final RequestOptions options = RequestOptions.errorOf(loader.error).placeholder(loader.placeholder);
            if (loader.circle != 0) {
                if (loader.circle == Integer.MAX_VALUE) {
                    // 裁剪成圆形
                    options.circleCrop();
                } else {
                    // 圆角裁剪
                    options.transform(new RoundedCorners(loader.circle));
                }
            }

            builder.apply(options);
        }

        if (loader.width != 0 && loader.height != 0) {
            builder.override(loader.width, loader.height);
        }

        builder.into(loader.view);
    }

    /**
     * 获取一个 Glide 的请求对象
     */
    private RequestManager getRequestManager(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (object instanceof Context) {
            if (object instanceof FragmentActivity) {
                return Glide.with((FragmentActivity) object);
            } else if (object instanceof Activity) {
                return Glide.with((Activity) object);
            } else {
                return Glide.with((Context) object);
            }
        } else if (object instanceof Fragment) {
            return Glide.with((Fragment) object);
        } else if (object instanceof androidx.fragment.app.Fragment) {
            return Glide.with((androidx.fragment.app.Fragment) object);
        }
        // 如果不是上面这几种类型就直接抛出异常
        throw new IllegalArgumentException("This object is illegal");
    }
}