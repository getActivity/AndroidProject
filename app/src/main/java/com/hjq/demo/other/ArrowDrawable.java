package com.hjq.demo.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.hjq.demo.R;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : 王浩 & Android 轮子哥
 *    github : https://github.com/bingoogolapple/BGATransformersTip-Android
 *    time   : 2019/08/19
 *    desc   : 带箭头背景的 Drawable
 */
@SuppressLint("RtlHardcoded")
public final class ArrowDrawable extends Drawable {

    private final Builder mBuilder;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath;

    private ArrowDrawable(Builder builder) {
        mBuilder = builder;
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBuilder.mShadowSize > 0) {
            mPaint.setMaskFilter(new BlurMaskFilter(mBuilder.mShadowSize, BlurMaskFilter.Blur.OUTER));
            mPaint.setColor(mBuilder.mShadowColor);
            canvas.drawPath(mPath, mPaint);
        }
        mPaint.setMaskFilter(null);
        mPaint.setColor(mBuilder.mBackgroundColor);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onBoundsChange(@NonNull Rect viewRect) {
        if (mPath == null) {
            mPath = new Path();
        } else {
            mPath.reset();
        }

        RectF excludeShadowRectF = new RectF(viewRect);
        excludeShadowRectF.inset(mBuilder.mShadowSize, mBuilder.mShadowSize);

        PointF centerPointF = new PointF();

        // 判断箭头的位置
        switch (mBuilder.mArrowOrientation) {
            case Gravity.LEFT:
                excludeShadowRectF.left += mBuilder.mArrowHeight;
                centerPointF.x = excludeShadowRectF.left;
                break;
            case Gravity.RIGHT:
                excludeShadowRectF.right -= mBuilder.mArrowHeight;
                centerPointF.x = excludeShadowRectF.right;
                break;
            case Gravity.TOP:
                excludeShadowRectF.top += mBuilder.mArrowHeight;
                centerPointF.y = excludeShadowRectF.top;
                break;
            case Gravity.BOTTOM:
                excludeShadowRectF.bottom -= mBuilder.mArrowHeight;
                centerPointF.y = excludeShadowRectF.bottom;
                break;
            default:
                break;
        }

        // 判断箭头的重心
        switch (mBuilder.mArrowGravity) {
            case Gravity.LEFT:
                centerPointF.x = excludeShadowRectF.left + mBuilder.mArrowHeight;
                break;
            case Gravity.CENTER_HORIZONTAL:
                centerPointF.x = viewRect.width() / 2f;
                break;
            case Gravity.RIGHT:
                centerPointF.x = excludeShadowRectF.right - mBuilder.mArrowHeight;
                break;
            case Gravity.TOP:
                centerPointF.y = excludeShadowRectF.top + mBuilder.mArrowHeight;
                break;
            case Gravity.CENTER_VERTICAL:
                centerPointF.y = viewRect.height() / 2f;
                break;
            case Gravity.BOTTOM:
                centerPointF.y = excludeShadowRectF.bottom - mBuilder.mArrowHeight;
                break;
            default:
                break;
        }

        // 更新箭头偏移量
        centerPointF.x += mBuilder.mArrowOffsetX;
        centerPointF.y += mBuilder.mArrowOffsetY;

        switch (mBuilder.mArrowGravity) {
            case Gravity.LEFT:
            case Gravity.RIGHT:
            case Gravity.CENTER_HORIZONTAL:
                centerPointF.x = Math.max(centerPointF.x, excludeShadowRectF.left + mBuilder.mRadius + mBuilder.mArrowHeight);
                centerPointF.x = Math.min(centerPointF.x, excludeShadowRectF.right - mBuilder.mRadius - mBuilder.mArrowHeight);
                break;
            case Gravity.TOP:
            case Gravity.BOTTOM:
            case Gravity.CENTER_VERTICAL:
                centerPointF.y = Math.max(centerPointF.y, excludeShadowRectF.top + mBuilder.mRadius + mBuilder.mArrowHeight);
                centerPointF.y = Math.min(centerPointF.y, excludeShadowRectF.bottom - mBuilder.mRadius - mBuilder.mArrowHeight);
                break;
            default:
                break;
        }

        switch (mBuilder.mArrowOrientation) {
            case Gravity.LEFT:
            case Gravity.RIGHT:
                centerPointF.x = Math.max(centerPointF.x, excludeShadowRectF.left);
                centerPointF.x = Math.min(centerPointF.x, excludeShadowRectF.right);
                break;
            case Gravity.TOP:
            case Gravity.BOTTOM:
                centerPointF.y = Math.max(centerPointF.y, excludeShadowRectF.top);
                centerPointF.y = Math.min(centerPointF.y, excludeShadowRectF.bottom);
                break;
            default:
                break;
        }

        // 箭头区域（其实是旋转了 90 度后的正方形区域）
        Path arrowPath = new Path();
        arrowPath.moveTo(centerPointF.x - mBuilder.mArrowHeight, centerPointF.y);
        arrowPath.lineTo(centerPointF.x, centerPointF.y - mBuilder.mArrowHeight);
        arrowPath.lineTo(centerPointF.x + mBuilder.mArrowHeight, centerPointF.y);
        arrowPath.lineTo(centerPointF.x, centerPointF.y + mBuilder.mArrowHeight);
        arrowPath.close();

        mPath.addRoundRect(excludeShadowRectF, mBuilder.mRadius, mBuilder.mRadius, Path.Direction.CW);
        mPath.addPath(arrowPath);

        invalidateSelf();
    }

    public static final class Builder {

        /** 上下文对象 */
        @NonNull
        private final Context mContext;
        /** 箭头高度 */
        private int mArrowHeight;
        /** 背景圆角大小 */
        private int mRadius;
        /** 箭头方向 */
        private int mArrowOrientation;
        /** 箭头重心 */
        private int mArrowGravity;
        /** 箭头水平方向偏移 */
        private int mArrowOffsetX;
        /** 箭头垂直方向偏移 */
        private int mArrowOffsetY;
        /** 阴影大小 */
        private int mShadowSize;
        /** 背景颜色 */
        private int mBackgroundColor;
        /** 阴影颜色 */
        private int mShadowColor;

        public Builder(@NonNull Context context) {
            mContext = context;
            mBackgroundColor = ContextCompat.getColor(context, R.color.black);
            mShadowColor = ContextCompat.getColor(context, R.color.black20);
            mArrowHeight = (int) SmallestWidthAdaptation.dp2px(context, 6);
            mRadius = (int) SmallestWidthAdaptation.dp2px(context, 4);
            mShadowSize = 0;
            mArrowOffsetX = 0;
            mArrowOffsetY = 0;
            mArrowOrientation = Gravity.NO_GRAVITY;
            mArrowGravity = Gravity.NO_GRAVITY;
        }

        /**
         * 设置背景色
         */
        public Builder setBackgroundColor(@ColorInt int color) {
            mBackgroundColor = color;
            return this;
        }

        /**
         * 设置阴影色
         */
        public Builder setShadowColor(@ColorInt int color) {
            mShadowColor = color;
            return this;
        }

        /**
         * 设置箭头高度
         */
        public Builder setArrowHeight(int height) {
            mArrowHeight = height;
            return this;
        }

        /**
         * 设置浮窗圆角半径
         */
        public Builder setRadius(int radius) {
            mRadius = radius;
            return this;
        }

        /**
         * 设置箭头方向（左上右下）
         */
        public Builder setArrowOrientation(int orientation) {
            switch (orientation = Gravity.getAbsoluteGravity(orientation, mContext.getResources().getConfiguration().getLayoutDirection())) {
                case Gravity.LEFT:
                case Gravity.TOP:
                case Gravity.RIGHT:
                case Gravity.BOTTOM:
                    mArrowOrientation = orientation;
                    break;
                default:
                    // 箭头只能在左上右下这四个位置
                    throw new IllegalArgumentException("The arrow can only be in the four positions: left, top, right, and bottom");
            }
            return this;
        }

        /**
         * 设置箭头布局重心
         */
        public Builder setArrowGravity(int gravity) {
            gravity = Gravity.getAbsoluteGravity(gravity, mContext.getResources().getConfiguration().getLayoutDirection());
            if (gravity == Gravity.CENTER) {
                switch (mArrowOrientation) {
                    case Gravity.LEFT:
                    case Gravity.RIGHT:
                        gravity = Gravity.CENTER_VERTICAL;
                        break;
                    case Gravity.TOP:
                    case Gravity.BOTTOM:
                        gravity = Gravity.CENTER_HORIZONTAL;
                        break;
                    default:
                        break;
                }
            }
            switch (gravity) {
                case Gravity.LEFT:
                case Gravity.RIGHT:
                    if (mArrowOrientation == Gravity.LEFT || mArrowOrientation == Gravity.RIGHT) {
                        throw new IllegalArgumentException("The arrow direction cannot be the same as the arrow gravity");
                    }
                    break;
                case Gravity.TOP:
                case Gravity.BOTTOM:
                    if (mArrowOrientation == Gravity.TOP || mArrowOrientation == Gravity.BOTTOM) {
                        throw new IllegalArgumentException("The arrow direction cannot be the same as the arrow gravity");
                    }
                    break;
                case Gravity.CENTER_VERTICAL:
                case Gravity.CENTER_HORIZONTAL:
                    break;
                default:
                    // 箭头只能在左上右下这四个位置
                    throw new IllegalArgumentException("The arrow can only be in the four positions: left, top, right, and bottom");
            }
            mArrowGravity = gravity;
            return this;
        }

        /**
         * 设置箭头在 x 轴的偏移量
         */
        public Builder setArrowOffsetX(int offsetX) {
            mArrowOffsetX = offsetX;
            return this;
        }

        /**
         * 设置箭头在 y 轴的偏移量
         */
        public Builder setArrowOffsetY(int offsetY) {
            mArrowOffsetY = offsetY;
            return this;
        }

        /**
         * 设置阴影宽度
         */
        public Builder setShadowSize(int size) {
            mShadowSize = size;
            return this;
        }

        /**
         * 构建 Drawable
         */
        public ArrowDrawable build() {
            if (mArrowOrientation == Gravity.NO_GRAVITY || mArrowGravity == Gravity.NO_GRAVITY) {
                // 必须要先设置箭头的方向及重心
                throw new IllegalArgumentException("You must set the direction and gravity of the arrow");
            }
            return new ArrowDrawable(this);
        }

        /**
         * 应用到 View
         */
        public void apply(View view) {
            view.setBackground(build());
            if (mShadowSize > 0 || mArrowHeight > 0) {
                if (view.getPaddingTop() == 0 && view.getBottom() == 0 &&
                        view.getPaddingLeft() == 0 && view.getPaddingRight() == 0) {
                    view.setPadding(mShadowSize, mShadowSize + mArrowHeight, mShadowSize, mShadowSize);
                }
            }
        }
    }
}