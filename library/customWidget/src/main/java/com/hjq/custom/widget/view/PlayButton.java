package com.hjq.custom.widget.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.custom.widget.R;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : codeestX & Android 轮子哥
 *    github : https://github.com/codeestX/ENViews
 *    time   : 2021/09/12
 *    desc   : 播放暂停动效的按钮
 */
public final class PlayButton extends View {

    /** 播放状态 */
    public static final int STATE_PLAY = 0;
    /** 暂停状态 */
    public static final int STATE_PAUSE = 1;

    /** 当前状态 */
    private int mCurrentState = STATE_PAUSE;

    /** 动画时间 */
    private int mAnimDuration;

    private final Paint mPaint;

    private int mViewWidth, mViewHeight;

    private int mCenterX, mCenterY;

    private int mCircleRadius;

    @NonNull
    private final RectF mRectF = new RectF();

    @NonNull
    private final RectF mBgRectF = new RectF();;

    private float mFraction = 1;

    private final Path mPath, mDstPath;

    private final PathMeasure mPathMeasure;

    private float mPathLength;

    public PlayButton(@NonNull Context context) {
        this(context, null);
    }

    public PlayButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayButton);
        int lineColor = typedArray.getColor(R.styleable.PlayButton_pb_lineColor, Color.WHITE);
        int lineSize = typedArray.getInteger(R.styleable.PlayButton_pb_lineSize, (int) SmallestWidthAdaptation.dp2px(context, 4));
        mAnimDuration = typedArray.getInteger(R.styleable.PlayButton_pb_animDuration, 200);
        typedArray.recycle();

        // 关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineSize);
        mPaint.setPathEffect(new CornerPathEffect(1));

        mPath = new Path();
        mDstPath = new Path();
        mPathMeasure = new PathMeasure();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mViewWidth = width * 9 / 10;
        mViewHeight = height * 9 / 10;
        mCircleRadius = mViewWidth / (int) SmallestWidthAdaptation.dp2px(this, 4);
        mCenterX = width / 2;
        mCenterY = height / 2;
        mRectF.set(mCenterX - mCircleRadius, mCenterY + 0.6f * mCircleRadius,
                  mCenterX + mCircleRadius, mCenterY + 2.6f * mCircleRadius);
        mBgRectF.set(mCenterX - mViewWidth / 2f ,mCenterY - mViewHeight / 2f ,
                    mCenterX + mViewWidth / 2f, mCenterY + mViewHeight / 2f);
        mPath.moveTo(mCenterX - mCircleRadius, mCenterY + 1.8f * mCircleRadius);
        mPath.lineTo(mCenterX - mCircleRadius, mCenterY - 1.8f * mCircleRadius);
        mPath.lineTo(mCenterX + mCircleRadius, mCenterY);
        mPath.close();
        mPathMeasure.setPath(mPath, false);
        mPathLength = mPathMeasure.getLength();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) SmallestWidthAdaptation.dp2px(this, 60), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
            default:
                break;
        }

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) SmallestWidthAdaptation.dp2px(this, 60), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
            default:
                break;
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX, mCenterY, mViewWidth / 2f, mPaint);
        if (mFraction < 0) {
            // 弹性部分
            canvas.drawLine(mCenterX + mCircleRadius, mCenterY - 1.6f * mCircleRadius + 10 * mCircleRadius * mFraction,
                    mCenterX + mCircleRadius, mCenterY + 1.6f * mCircleRadius + 10 * mCircleRadius * mFraction, mPaint);

            canvas.drawLine(mCenterX - mCircleRadius, mCenterY - 1.6f * mCircleRadius,
                    mCenterX - mCircleRadius, mCenterY + 1.6f * mCircleRadius, mPaint);

            canvas.drawArc(mBgRectF, -105 , 360 , false, mPaint);
        } else if (mFraction <= 0.3) {
            // 右侧直线和下方曲线
            canvas.drawLine(mCenterX + mCircleRadius, mCenterY - 1.6f * mCircleRadius + mCircleRadius * 3.2f / 0.3f * mFraction,
                    mCenterX + mCircleRadius, mCenterY + 1.6f * mCircleRadius, mPaint);

            canvas.drawLine(mCenterX - mCircleRadius, mCenterY - 1.6f * mCircleRadius,
                    mCenterX - mCircleRadius, mCenterY + 1.6f * mCircleRadius, mPaint);

            if (mFraction != 0) {
                canvas.drawArc(mRectF, 0f, 180f / 0.3f * mFraction, false, mPaint);
            }

            canvas.drawArc(mBgRectF, -105 + 360 * mFraction, 360 * (1 - mFraction), false, mPaint);
        } else if (mFraction <= 0.6) {
            // 下方曲线和三角形
            canvas.drawArc(mRectF, 180f / 0.3f * (mFraction - 0.3f), 180 - 180f / 0.3f * (mFraction - 0.3f), false , mPaint);

            mDstPath.reset();
            mPathMeasure.getSegment(0.02f * mPathLength, 0.38f * mPathLength + 0.42f * mPathLength / 0.3f * (mFraction - 0.3f) ,
                    mDstPath, true);
            canvas.drawPath(mDstPath, mPaint);

            canvas.drawArc(mBgRectF, -105 + 360 * mFraction, 360 * (1 - mFraction), false, mPaint);
        } else if (mFraction <= 0.8) {
            // 三角形
            mDstPath.reset();
            mPathMeasure.getSegment(0.02f * mPathLength + 0.2f * mPathLength / 0.2f * (mFraction - 0.6f)
                    , 0.8f * mPathLength + 0.2f * mPathLength / 0.2f * (mFraction - 0.6f) ,
                    mDstPath, true);
            canvas.drawPath(mDstPath, mPaint);

            canvas.drawArc(mBgRectF, -105 + 360 * mFraction, 360 * (1 - mFraction), false, mPaint);
        } else {
            // 弹性部分
            mDstPath.reset();
            mPathMeasure.getSegment(10 * mCircleRadius * (mFraction - 1)
                    , mPathLength ,
                    mDstPath, true);
            canvas.drawPath(mDstPath, mPaint);
        }
    }

    /**
     * 播放状态
     */
    public void play() {
        if (mCurrentState == STATE_PLAY) {
            return;
        }
        mCurrentState = STATE_PLAY;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 100f);
        valueAnimator.setDuration(mAnimDuration);
        valueAnimator.setInterpolator(new AnticipateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            mFraction = 1 - animation.getAnimatedFraction();
            invalidate();
        });
        valueAnimator.start();
    }

    /**
     * 暂停状态
     */
    public void pause() {
        if (mCurrentState == STATE_PAUSE) {
            return;
        }
        mCurrentState = STATE_PAUSE;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.f, 100f);
        valueAnimator.setDuration(mAnimDuration);
        valueAnimator.setInterpolator(new AnticipateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            mFraction = animation.getAnimatedFraction();
            invalidate();
        });
        valueAnimator.start();
    }

    /**
     * 获取当前状态
     */
    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * 设置动画时间
     */
    public void setAnimDuration(int duration) {
        mAnimDuration = duration;
    }

    /**
     * 设置线条颜色
     */
    public void setLineColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置线条大小
     */
    public void setLineSize(int size) {
        mPaint.setStrokeWidth(size);
        invalidate();
    }
}