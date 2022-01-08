package com.hjq.widget.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.FloatRange;
import androidx.appcompat.widget.AppCompatButton;

import com.hjq.widget.R;

/**
 *    author : Unstoppable & Android 轮子哥
 *    github : https://github.com/Someonewow/SubmitButton
 *    time   : 2016/12/31
 *    desc   : 带提交动画按钮
 */
public final class SubmitButton extends AppCompatButton {

    /** 无进度 */
    private static final int STYLE_LOADING = 0x00;
    /** 带进度 */
    private static final int STYLE_PROGRESS = 0x01;

    /** 默认状态 */
    private static final int STATE_NONE = 0;
    /** 提交状态 */
    private static final int STATE_SUBMIT = 1;
    /** 加载状态 */
    private static final int STATE_LOADING = 2;
    /** 结果状态 */
    private static final int STATE_RESULT = 3;

    /** 当前按钮状态 */
    private int mButtonState = STATE_NONE;

    /** 当前进度条样式 */
    private final int mProgressStyle;
    private float mCurrentProgress;

    /** View 宽高 */
    private int mViewWidth;
    private int mViewHeight;

    /** View 最大宽高 */
    private int mMaxWidth;
    private int mMaxHeight;

    /** 画布坐标原点 */
    private int mX, mY;

    /** 进度按钮的颜色 */
    private final int mProgressColor;
    /** 成功按钮的颜色 */
    private final int mSucceedColor;
    /** 失败按钮的颜色 */
    private final int mErrorColor;

    private Paint mBackgroundPaint, mLoadingPaint, mResultPaint;

    private Path mButtonPath;
    private Path mLoadPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;
    private Path mResultPath;

    private RectF mCircleLeft, mCircleMid, mCircleRight;

    private float mLoadValue;

    private ValueAnimator mSubmitAnim, mLoadingAnim, mResultAnim;

    /** 是否有结果 */
    private boolean mDoResult;
    /** 是否成功了 */
    private boolean mSucceed;

    public SubmitButton(Context context) {
        this(context, null);
    }

    public SubmitButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubmitButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SubmitButton, defStyleAttr, 0);
        mProgressColor = typedArray.getColor(R.styleable.SubmitButton_progressColor, getAccentColor());
        mSucceedColor = typedArray.getColor(R.styleable.SubmitButton_succeedColor, Color.parseColor("#19CC95"));
        mErrorColor = typedArray.getColor(R.styleable.SubmitButton_errorColor, Color.parseColor("#FC8E34"));
        mProgressStyle = typedArray.getInt(R.styleable.SubmitButton_progressStyle, STYLE_LOADING);
        typedArray.recycle();

        initPaint();
        resetPaint();
    }

    private void initPaint() {
        mBackgroundPaint = new Paint();
        mLoadingPaint = new Paint();
        mResultPaint = new Paint();

        mButtonPath = new Path();
        mLoadPath = new Path();
        mResultPath = new Path();
        mDstPath = new Path();

        mCircleMid = new RectF();
        mCircleLeft = new RectF();
        mCircleRight = new RectF();

        mPathMeasure = new PathMeasure();
    }

    /**
     * 重置画笔
     */
    private void resetPaint() {
        mBackgroundPaint.setColor(mProgressColor);
        mBackgroundPaint.setStrokeWidth(5);
        mBackgroundPaint.setAntiAlias(true);

        mLoadingPaint.setColor(mProgressColor);
        mLoadingPaint.setStyle(Paint.Style.STROKE);
        mLoadingPaint.setStrokeWidth(9);
        mLoadingPaint.setAntiAlias(true);

        mResultPaint.setColor(Color.WHITE);
        mResultPaint.setStyle(Paint.Style.STROKE);
        mResultPaint.setStrokeWidth(9);
        mResultPaint.setStrokeCap(Paint.Cap.ROUND);
        mResultPaint.setAntiAlias(true);

        mButtonPath.reset();
        mLoadPath.reset();
        mResultPath.reset();
        mDstPath.reset();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        // 当前必须不是在动画执行过程中
        if (mButtonState != STATE_LOADING) {
            mViewWidth = width - 10;
            mViewHeight = height - 10;

            mX = (int) (width * 0.5);
            mY = (int) (height * 0.5);

            mMaxWidth = mViewWidth;
            mMaxHeight = mViewHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mButtonState) {
            case STATE_NONE:
                super.onDraw(canvas);
                break;
            case STATE_SUBMIT:
            case STATE_LOADING:
                // 清除画布之前绘制的背景
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.translate(mX, mY);
                drawButton(canvas);
                drawLoading(canvas);
                break;
            case STATE_RESULT:
                // 清除画布之前绘制的背景
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.translate(mX, mY);
                drawButton(canvas);
                drawResult(canvas, mSucceed);
                break;
            default:
                break;
        }
    }

    /**
     * 绘制按钮
     */
    private void drawButton(Canvas canvas) {
        mButtonPath.reset();
        mCircleLeft.set(- mViewWidth / 2f, - mViewHeight / 2f, - mViewWidth / 2f + mViewHeight, mViewHeight / 2f);
        mButtonPath.arcTo(mCircleLeft, 90, 180);
        mButtonPath.lineTo(mViewWidth / 2f - mViewHeight / 2f, - mViewHeight / 2f);
        mCircleRight.set(mViewWidth / 2f - mViewHeight, - mViewHeight / 2f, mViewWidth / 2f, mViewHeight / 2f);
        mButtonPath.arcTo(mCircleRight, 270, 180);
        mButtonPath.lineTo(- mViewWidth / 2f + mViewHeight / 2f, mViewHeight / 2f);
        canvas.drawPath(mButtonPath, mBackgroundPaint);
    }

    /**
     * 绘制加载转圈
     */
    private void drawLoading(Canvas canvas) {
        mDstPath.reset();
        mCircleMid.set(- mMaxHeight / 2f, - mMaxHeight / 2f, mMaxHeight / 2f, mMaxHeight / 2f);
        mLoadPath.addArc(mCircleMid, 270, 359.999f);
        mPathMeasure.setPath(mLoadPath, true);
        float startD = 0f, stopD;
        if (mProgressStyle == STYLE_LOADING) {
            startD = mPathMeasure.getLength() * mLoadValue;
            stopD = startD + mPathMeasure.getLength() / 2 * mLoadValue;
        } else {
            stopD = mPathMeasure.getLength() * mCurrentProgress;
        }
        mPathMeasure.getSegment(startD, stopD, mDstPath, true);
        canvas.drawPath(mDstPath, mLoadingPaint);
    }

    /**
     * 绘制结果按钮
     */
    private void drawResult(Canvas canvas, boolean isSucceed) {
        if (isSucceed) {
            mResultPath.moveTo(- mViewHeight / 6f, 0);
            mResultPath.lineTo(0, (float) (- mViewHeight / 6 + (1 + Math.sqrt(5)) * mViewHeight / 12));
            mResultPath.lineTo(mViewHeight / 6f, - mViewHeight / 6f);
        } else {
            mResultPath.moveTo(- mViewHeight / 6f, mViewHeight / 6f);
            mResultPath.lineTo(mViewHeight / 6f, - mViewHeight / 6f);
            mResultPath.moveTo(- mViewHeight / 6f, - mViewHeight / 6f);
            mResultPath.lineTo(mViewHeight / 6f, mViewHeight / 6f);
        }
        canvas.drawPath(mResultPath, mResultPaint);
    }

    /**
     * 开始提交动画
     */
    private void startSubmitAnim() {
        mButtonState = STATE_SUBMIT;
        mSubmitAnim = ValueAnimator.ofInt(mMaxWidth, mMaxHeight);
        mSubmitAnim.addUpdateListener(animation -> {
            mViewWidth = (int) animation.getAnimatedValue();
            if (mViewWidth == mViewHeight) {
                mBackgroundPaint.setColor(Color.parseColor("#DDDDDD"));
                mBackgroundPaint.setStyle(Paint.Style.STROKE);
            }
            invalidate();
        });
        mSubmitAnim.setDuration(300);
        mSubmitAnim.setInterpolator(new AccelerateInterpolator());
        mSubmitAnim.start();
        mSubmitAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mDoResult) {
                    startResultAnim();
                } else {
                    startLoadingAnim();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    /**
     * 开始加载动画
     */
    private void startLoadingAnim() {
        mButtonState = STATE_LOADING;
        if (mProgressStyle == STYLE_PROGRESS) {
            return;
        }
        mLoadingAnim = ValueAnimator.ofFloat(0f, 1f);
        mLoadingAnim.addUpdateListener(animation -> {
            mLoadValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        mLoadingAnim.setDuration(2000);
        mLoadingAnim.setRepeatCount(ValueAnimator.INFINITE);
        mLoadingAnim.start();
    }

    /**
     * 开始结果动画
     */
    private void startResultAnim() {
        mButtonState = STATE_RESULT;
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        mResultAnim = ValueAnimator.ofInt(mMaxHeight, mMaxWidth);
        mResultAnim.addUpdateListener(animation -> {
            mViewWidth = (int) animation.getAnimatedValue();
            mResultPaint.setAlpha(((mViewWidth - mViewHeight) * 255) / (mMaxWidth - mMaxHeight));
            if (mViewWidth == mViewHeight) {
                if (mSucceed) {
                    mBackgroundPaint.setColor(mSucceedColor);
                } else {
                    mBackgroundPaint.setColor(mErrorColor);
                }
                mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            }
            invalidate();
        });
        mResultAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // 请求重新测量自身，因为 onMeasure 方法中避开了动画执行中获取 View 宽高
                requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        mResultAnim.setDuration(300);
        mResultAnim.setInterpolator(new AccelerateInterpolator());
        mResultAnim.start();
    }

    @Override
    public boolean performClick() {
        if (mButtonState == STATE_NONE) {
            startSubmitAnim();
            return super.performClick();
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSubmitAnim != null) {
            mSubmitAnim.cancel();
        }
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mResultAnim != null) {
            mResultAnim.cancel();
        }
    }

    /**
     * 显示进度
     */
    public void showProgress() {
        if (mButtonState == STATE_NONE) {
            startSubmitAnim();
        }
    }

    /**
     * 显示成功
     */
    public void showSucceed() {
        showResult(true);
    }

    /**
     * 显示错误
     */
    public void showError() {
        showResult(false);
    }

    /**
     * 显示错误之后延迟重置
     */
    public void showError(long delayMillis) {
        showResult(false);
        postDelayed(this::reset, delayMillis);
    }

    /**
     * 显示提交结果
     */
    private void showResult(boolean succeed) {
        if (mButtonState == STATE_NONE || mButtonState == STATE_RESULT || mDoResult) {
            return;
        }
        mDoResult = true;
        mSucceed = succeed;
        if (mButtonState == STATE_LOADING) {
            startResultAnim();
        }
    }

    /**
     * 重置按钮的状态
     */
    public void reset() {
        if (mSubmitAnim != null) {
            mSubmitAnim.cancel();
        }
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mResultAnim != null) {
            mResultAnim.cancel();
        }
        mButtonState = STATE_NONE;
        mViewWidth = mMaxWidth;
        mViewHeight = mMaxHeight;
        mSucceed = false;
        mDoResult = false;
        mCurrentProgress = 0;
        resetPaint();
        invalidate();
    }

    /**
     * 设置按钮进度
     */
    public void setProgress(@FloatRange(from = 0.0, to = 1.0) float progress) {
        mCurrentProgress = progress;
        if (mProgressStyle == STYLE_PROGRESS && mButtonState == STATE_LOADING) {
            invalidate();
        }
    }

    /**
     * 获取当前主题的强调色
     */
    private int getAccentColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }
}