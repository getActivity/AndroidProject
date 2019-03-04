package com.hjq.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : Glide 加载实现类
 */
class GlideHandler implements LoadHandler {

    private Drawable mPlaceholder;
    private Drawable mError;

    public void setPlaceholder(Drawable placeholder) {
        mPlaceholder = placeholder;
    }

    public void setError(Drawable error) {
        mError = error;
    }

    @Override
    public void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .placeholder(mPlaceholder)
                .error(mError)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, int resourceId) {
        Glide.with(context)
                .load(resourceId)
                .placeholder(mPlaceholder)
                .error(mError)
                .into(imageView);
    }


    @Override
    public void loadCircleImage(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .asBitmap() // 转换成Bitmap
                .placeholder(mPlaceholder)
                .error(mError)
                .into(new CircleBitmapImageView(imageView));
    }

    @Override
    public void loadCircleImage(Context context, ImageView imageView, int resourceId) {
        Glide.with(context)
                .load(resourceId)
                .asBitmap() // 转换成Bitmap
                .placeholder(mPlaceholder)
                .error(mError)
                .into(new CircleBitmapImageView(imageView));
    }

    @Override
    public void loadRoundImage(Context context, ImageView imageView, String url, float cornerRadius) {
        Glide.with(context)
                .load(url)
                .asBitmap() // 转换成Bitmap
                .placeholder(mPlaceholder)
                .error(mError)
                .into(new RoundBitmapImageView(imageView, cornerRadius));
    }

    @Override
    public void loadRoundImage(Context context, ImageView imageView, int resourceId, float cornerRadius) {
        Glide.with(context)
                .load(resourceId)
                .asBitmap() // 转换成Bitmap
                .placeholder(mPlaceholder)
                .error(mError)
                .into(new RoundBitmapImageView(imageView, cornerRadius));
    }

    /**
     * 圆形图片加载
     */
    final class CircleBitmapImageView extends BitmapImageViewTarget {

        private ImageView mImageView;

        CircleBitmapImageView(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }

        @Override
        protected void setResource(Bitmap resource) {
            RoundedBitmapDrawable circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(view.getContext().getResources(), resource);
            // 设置成圆形的
            circularBitmapDrawable.setCircular(true);
            mImageView.setImageDrawable(circularBitmapDrawable);
        }
    }

    /**
     * 圆角图片加载
     */
    final class RoundBitmapImageView extends BitmapImageViewTarget {

        private ImageView mImageView;
        private float mCornerRadius;

        RoundBitmapImageView(ImageView imageView, float cornerRadius) {
            super(imageView);
            mImageView = imageView;
            mCornerRadius = cornerRadius;
        }

        @Override
        protected void setResource(Bitmap resource) {
            RoundedBitmapDrawable circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(view.getContext().getResources(), resource);
            // 设置圆角大小
            circularBitmapDrawable.setCornerRadius(mCornerRadius);
            mImageView.setImageDrawable(circularBitmapDrawable);
        }
    }
}