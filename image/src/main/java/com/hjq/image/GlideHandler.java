package com.hjq.image;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;

import java.security.MessageDigest;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : Glide 加载实现类
 */
final class GlideHandler implements ImageHandler {

    private Drawable mPlaceholder;
    private Drawable mError;

    void setPlaceholder(Drawable placeholder) {
        mPlaceholder = placeholder;
    }

    void setError(Drawable error) {
        mError = error;
    }

    @Override
    public void loadImage(Object object, ImageView imageView, String url) {
        if (url != null && !"".equals(url)) {
            getGlide(object)
                    .load(url.trim())
                    .apply(RequestOptions.errorOf(mError).placeholder(mPlaceholder))
                    .into(imageView);
        }
    }

    @Override
    public void loadImage(Object object, ImageView imageView, int resourceId) {
        getGlide(object)
                .load(resourceId)
                .apply(RequestOptions.errorOf(mError).placeholder(mPlaceholder))
                .into(imageView);
    }


    @Override
    public void loadCircleImage(Object object, ImageView imageView, String url) {
        if (url != null && !"".equals(url)) {
            getGlide(object)
                    .load(url.trim())
                    .apply(RequestOptions.errorOf(mError).placeholder(mPlaceholder).transform(new CircleTransformation()))
                    .into(imageView);
        }
    }

    @Override
    public void loadCircleImage(Object object, ImageView imageView, int resourceId) {
        getGlide(object)
                .load(resourceId)
                .apply(RequestOptions.errorOf(mError).placeholder(mPlaceholder).transform(new CircleTransformation()))
                .into(imageView);
    }

    @Override
    public void loadRoundImage(Object object, ImageView imageView, String url, float radius) {
        if (url != null && !"".equals(url)) {
            getGlide(object)
                    .load(url.trim())
                    .apply(RequestOptions.errorOf(mError).placeholder(mPlaceholder).transform(new RadiusTransformation(radius)))
                    .into(imageView);
        }
    }

    @Override
    public void loadRoundImage(Object object, ImageView imageView, int resourceId, float radius) {
        getGlide(object)
                .load(resourceId)
                .apply(RequestOptions.errorOf(mError).placeholder(mPlaceholder).transform(new RadiusTransformation(radius)))
                .into(imageView);
    }

    /**
     * 获取一个 Glide 的请求对象
     */
    private RequestManager getGlide(Object object) {
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
        }else if (object instanceof android.support.v4.app.Fragment) {
            return Glide.with((android.support.v4.app.Fragment) object);
        }
        // 直接抛出异常
        throw new IllegalArgumentException("This object is illegal");
    }

    /**
     * 圆形图片加载：https://github.com/sunfusheng/GlideImageView/blob/master/GlideImageView/src/main/java/com/sunfusheng/transformation/CircleTransformation.java
     */
    private static final class CircleTransformation extends BitmapTransformation {

        private final String ID = getClass().getName();

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
            int x = (toTransform.getWidth() - size) / 2;
            int y = (toTransform.getHeight() - size) / 2;

            Bitmap square = Bitmap.createBitmap(toTransform, x, y, size, size);
            Bitmap circle = pool.get(size, size, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(circle);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(square, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return circle;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CircleTransformation) {
                return this == obj;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Util.hashCode(ID.hashCode());
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            messageDigest.update(ID.getBytes(CHARSET));
        }
    }

    /**
     * 圆角图片加载：https://github.com/sunfusheng/GlideImageView/blob/master/GlideImageView/src/main/java/com/sunfusheng/transformation/RadiusTransformation.java
     */
    private static final class RadiusTransformation extends BitmapTransformation {

        private final String ID = getClass().getName();

        private float radius;

        RadiusTransformation(float radius) {
            this.radius = radius;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            int width = toTransform.getWidth();
            int height = toTransform.getHeight();

            Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setHasAlpha(true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);
            return bitmap;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RadiusTransformation) {
                RadiusTransformation other = (RadiusTransformation) obj;
                return radius == other.radius;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Util.hashCode(ID.hashCode(), Util.hashCode(radius));
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            messageDigest.update((ID + radius).getBytes(CHARSET));
        }
    }
}