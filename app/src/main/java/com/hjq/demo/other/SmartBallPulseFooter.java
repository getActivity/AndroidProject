package com.hjq.demo.other;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import com.hjq.demo.R;
import com.hjq.smallest.width.SmallestWidthAdaptation;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;

/**
 *    author : 树朾 & Android 轮子哥
 *    github : https://github.com/scwang90/SmartRefreshLayout/tree/master/refresh-footer-ball
 *    time   : 2020/08/01
 *    desc   : 球脉冲底部加载组件
 */
public final class SmartBallPulseFooter extends SimpleComponent implements RefreshFooter {

    private final TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private boolean mNoMoreData;

    private boolean mManualNormalColor;
    private boolean mManualAnimationColor;

    private final Paint mPaint;

    private int mNormalColor = Color.parseColor("#EEEEEE");
    private int[] mAnimatingColor = {
            Color.parseColor("#30B399"),
            Color.parseColor("#FF4600"),
            Color.parseColor("#142DCC")};

    private final float mCircleSpacing;

    private long mStartTime = 0;
    private boolean mStarted = false;

    private final float mTextWidth;

    public SmartBallPulseFooter(@NonNull Context context) {
        this(context, null);
    }

    public SmartBallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        setMinimumHeight((int) SmallestWidthAdaptation.dp2px(context, 60));

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mSpinnerStyle = SpinnerStyle.Translate;

        mCircleSpacing = SmallestWidthAdaptation.dp2px(context, 2);
        mPaint.setTextSize(SmallestWidthAdaptation.sp2px(context, 14));
        mTextWidth = mPaint.measureText(getContext().getString(R.string.common_no_more_data));
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();
        if (mNoMoreData) {
            mPaint.setColor(Color.parseColor("#898989"));
            canvas.drawText(getContext().getString(R.string.common_no_more_data),(width - mTextWidth) / 2,(height - mPaint.getTextSize()) / 2, mPaint);
        } else {
            float radius = (Math.min(width, height) - mCircleSpacing * 2) / 7;
            float x = width / 2f - (radius * 2 + mCircleSpacing);
            float y = height / 2f;
            final long now = System.currentTimeMillis();
            for (int i = 0; i < 3; i++) {
                long time = now - mStartTime - 120 * (i + 1);
                float percent = time > 0 ? ((time % 750) / 750f) : 0;
                percent = mInterpolator.getInterpolation(percent);
                canvas.save();
                float translateX = x + (radius * 2) * i + mCircleSpacing * i;

                if (percent < 0.5) {
                    float scale = 1 - percent * 2 * 0.7f;
                    float translateY = y - scale * 10;
                    canvas.translate(translateX, translateY);
                } else {
                    float scale = percent * 2 * 0.7f - 0.4f;
                    float translateY = y + scale * 10;
                    canvas.translate(translateX, translateY);
                }

                mPaint.setColor(mAnimatingColor[i % mAnimatingColor.length]);
                canvas.drawCircle(0, 0, radius / 3, mPaint);
                canvas.restore();
            }
        }

        if (mStarted) {
            postInvalidate();
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mStarted) {
            return;
        }

        invalidate();
        mStarted = true;
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mStarted = false;
        mStartTime = 0;
        mPaint.setColor(mNormalColor);
        return 0;
    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
        if (!mManualAnimationColor && colors.length > 1) {
            setAnimatingColor(colors[0]);
            mManualAnimationColor = false;
        }
        if (!mManualNormalColor) {
            if (colors.length > 1) {
                setNormalColor(colors[1]);
            } else if (colors.length > 0) {
                setNormalColor(ColorUtils.compositeColors(Color.parseColor("#99FFFFFF"), colors[0]));
            }
            mManualNormalColor = false;
        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        mNoMoreData = noMoreData;
        return true;
    }

    public SmartBallPulseFooter setSpinnerStyle(SpinnerStyle style) {
        mSpinnerStyle = style;
        return this;
    }

    public SmartBallPulseFooter setNormalColor(@ColorInt int color) {
        mNormalColor = color;
        mManualNormalColor = true;
        if (!mStarted) {
            mPaint.setColor(color);
        }
        return this;
    }

    public SmartBallPulseFooter setAnimatingColor(@ColorInt int color) {
        mAnimatingColor = new int[]{color};
        mManualAnimationColor = true;
        if (mStarted) {
            mPaint.setColor(color);
        }
        return this;
    }
}