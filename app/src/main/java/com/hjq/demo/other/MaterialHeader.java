package com.hjq.demo.other;

import static android.view.View.MeasureSpec.getSize;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.hjq.demo.R;
import com.hjq.smallest.width.SmallestWidthAdaptation;
import com.scwang.smart.refresh.header.material.CircleImageView;
import com.scwang.smart.refresh.header.material.MaterialProgressDrawable;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;

/**
 *    author : 树朾 & Android 轮子哥
 *    github : https://github.com/scwang90/SmartRefreshLayout/tree/master/refresh-header-material
 *    time   : 2021/02/28
 *    desc   : Material 风格的刷新球，参考 {@link com.scwang.smart.refresh.header.MaterialHeader}
 */
public final class MaterialHeader extends SimpleComponent implements RefreshHeader {

    /** 刷新球大样式 */
    public static final int BALL_STYLE_LARGE = 0;
    /** 刷新球默认样式 */
    public static final int BALL_STYLE_DEFAULT = 1;

    private static final int CIRCLE_BG_LIGHT = Color.parseColor("#FAFAFA");
    private static final float MAX_PROGRESS_ANGLE = 0.8f;

    private boolean mFinished;
    private int mCircleDiameter;
    private final ImageView mCircleView;
    private final MaterialProgressDrawable mProgressDrawable;

    private int mWaveHeight;
    private int mHeadHeight;
    private final Path mBezierPath;
    private final Paint mBezierPaint;
    private RefreshState mRefreshState;
    private boolean mShowBezierWave = false;
    private boolean mScrollableWhenRefreshing = true;

    public MaterialHeader(@NonNull Context context) {
        this(context, null);
    }

    public MaterialHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        mSpinnerStyle = SpinnerStyle.MatchLayout;
        setMinimumHeight((int) SmallestWidthAdaptation.dp2px(context, 100));

        mProgressDrawable = new MaterialProgressDrawable(this);
        mProgressDrawable.setColorSchemeColors(
                Color.parseColor("#0099CC"),
                Color.parseColor("#FF4444"),
                Color.parseColor("#669900"),
                Color.parseColor("#AA66CC"),
                Color.parseColor("#FF8800"));
        mCircleView = new CircleImageView(context, CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgressDrawable);
        mCircleView.setAlpha(0f);
        addView(mCircleView);

        mCircleDiameter = (int) SmallestWidthAdaptation.dp2px(context, 40);

        mBezierPath = new Path();
        mBezierPaint = new Paint();
        mBezierPaint.setAntiAlias(true);
        mBezierPaint.setStyle(Paint.Style.FILL);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialHeader);
        mShowBezierWave = typedArray.getBoolean(R.styleable.MaterialHeader_srlShowBezierWave, mShowBezierWave);
        mScrollableWhenRefreshing = typedArray.getBoolean(R.styleable.MaterialHeader_srlScrollableWhenRefreshing, mScrollableWhenRefreshing);
        mBezierPaint.setColor(typedArray.getColor(R.styleable.MaterialHeader_srlPrimaryColor, Color.parseColor("#11BBFF")));
        if (typedArray.hasValue(R.styleable.MaterialHeader_srlShadowRadius)) {
            int radius = typedArray.getDimensionPixelOffset(R.styleable.MaterialHeader_srlShadowRadius, 0);
            int color = typedArray.getColor(R.styleable.MaterialHeader_mhShadowColor, Color.parseColor("#000000"));
            mBezierPaint.setShadowLayer(radius, 0, 0, color);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mShowBezierWave = typedArray.getBoolean(R.styleable.MaterialHeader_mhShowBezierWave, mShowBezierWave);
        mScrollableWhenRefreshing = typedArray.getBoolean(R.styleable.MaterialHeader_mhScrollableWhenRefreshing, mScrollableWhenRefreshing);
        if (typedArray.hasValue(R.styleable.MaterialHeader_mhPrimaryColor)) {
            mBezierPaint.setColor(typedArray.getColor(R.styleable.MaterialHeader_mhPrimaryColor, Color.parseColor("#11BBFF")));
        }
        if (typedArray.hasValue(R.styleable.MaterialHeader_mhShadowRadius)) {
            int radius = typedArray.getDimensionPixelOffset(R.styleable.MaterialHeader_mhShadowRadius, 0);
            int color = typedArray.getColor(R.styleable.MaterialHeader_mhShadowColor, Color.parseColor("#000000"));
            mBezierPaint.setShadowLayer(radius, 0, 0, color);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        typedArray.recycle();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec));
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            return;
        }
        final int width = getMeasuredWidth();
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();

        if (isInEditMode() && mHeadHeight > 0) {
            int circleTop = mHeadHeight - circleHeight / 2;
            mCircleView.layout((width / 2 - circleWidth / 2), circleTop,
                    (width / 2 + circleWidth / 2), circleTop + circleHeight);

            mProgressDrawable.showArrow(true);
            mProgressDrawable.setStartEndTrim(0f, MAX_PROGRESS_ANGLE);
            mProgressDrawable.setArrowScale(1);
            mCircleView.setAlpha(1f);
            mCircleView.setVisibility(VISIBLE);
        } else {
            mCircleView.layout((width / 2 - circleWidth / 2), -circleHeight, (width / 2 + circleWidth / 2), 0);
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (mShowBezierWave) {
            // 重置画笔
            mBezierPath.reset();
            mBezierPath.lineTo(0, mHeadHeight);
            // 绘制贝塞尔曲线
            mBezierPath.quadTo(getMeasuredWidth() / 2f, mHeadHeight + mWaveHeight * 1.9f, getMeasuredWidth(), mHeadHeight);
            mBezierPath.lineTo(getMeasuredWidth(), 0);
            canvas.drawPath(mBezierPath, mBezierPaint);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        if (!mShowBezierWave) {
            kernel.requestDefaultTranslationContentFor(this, false);
        }
        if (isInEditMode()) {
            mWaveHeight = mHeadHeight = height / 2;
        }
    }

    @Override
    public void onMoving(boolean dragging, float percent, int offset, int height, int maxDragHeight) {
        if (mRefreshState == RefreshState.Refreshing) {
            return;
        }

        if (mShowBezierWave) {
            mHeadHeight = Math.min(offset, height);
            mWaveHeight = Math.max(0, offset - height);
            postInvalidate();
        }

        if (dragging || (!mProgressDrawable.isRunning() && !mFinished)) {

            if (mRefreshState != RefreshState.Refreshing) {
                float originalDragPercent = 1f * offset / height;

                float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
                float extraOs = Math.abs(offset) - height;
                float tensionSlingshotPercent = Math.max(0, Math.min(extraOs, (float) height * 2)
                        / (float) height);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                        (tensionSlingshotPercent / 4), 2)) * 2f;
                float strokeStart = adjustedPercent * .8f;
                mProgressDrawable.showArrow(true);
                mProgressDrawable.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
                mProgressDrawable.setArrowScale(Math.min(1f, adjustedPercent));

                float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
                mProgressDrawable.setProgressRotation(rotation);
            }

            float targetY = offset / 2f + mCircleDiameter / 2f;
            mCircleView.setTranslationY(Math.min(offset, targetY));
            mCircleView.setAlpha(Math.min(1f, 4f * offset / mCircleDiameter));
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        mProgressDrawable.start();
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        mRefreshState = newState;
        if (newState == RefreshState.PullDownToRefresh) {
            mFinished = false;
            mCircleView.setVisibility(VISIBLE);
            mCircleView.setTranslationY(0);
            mCircleView.setScaleX(1);
            mCircleView.setScaleY(1);
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mProgressDrawable.stop();
        mCircleView.animate().scaleX(0).scaleY(0);
        mFinished = true;
        return 0;
    }

    /**
     * 设置背景色
     */
    public MaterialHeader setProgressBackgroundResource(@ColorRes int id) {
        setProgressBackgroundColor(ContextCompat.getColor(getContext(), id));
        return this;
    }

    public MaterialHeader setProgressBackgroundColor(@ColorInt int color) {
        mCircleView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置 ColorScheme
     *
     * @param colors ColorScheme
     */
    public MaterialHeader setColorSchemeColors(@ColorInt int... colors) {
        mProgressDrawable.setColorSchemeColors(colors);
        return this;
    }

    /**
     * 设置 ColorScheme
     *
     * @param ids ColorSchemeResources
     */
    public MaterialHeader setColorSchemeResources(@ColorRes int... ids) {
        int[] colors = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            colors[i] = ContextCompat.getColor(getContext(), ids[i]);
        }
        return setColorSchemeColors(colors);
    }

    /**
     * 设置刷新球样式
     *
     * @param style         可传入：{@link #BALL_STYLE_LARGE，#BALL_STYLE_DEFAULT}
     */
    public MaterialHeader setBallStyle(int style) {
        if (style != BALL_STYLE_LARGE && style != BALL_STYLE_DEFAULT) {
            return this;
        }
        if (style == BALL_STYLE_LARGE) {
            mCircleDiameter = (int) SmallestWidthAdaptation.dp2px(getContext(), 56);
        } else {
            mCircleDiameter = (int) SmallestWidthAdaptation.dp2px(getContext(), 40);
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null);
        mProgressDrawable.updateSizes(style);
        mCircleView.setImageDrawable(mProgressDrawable);
        return this;
    }

    /**
     * 是否显示贝塞尔图形
     */
    public MaterialHeader setShowBezierWave(boolean show) {
        mShowBezierWave = show;
        return this;
    }

    /**
     * 设置实在正在刷新的时候可以上下滚动 Header
     */
    public MaterialHeader setScrollableWhenRefreshing(boolean scrollable) {
        mScrollableWhenRefreshing = scrollable;
        return this;
    }
}