package com.hjq.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/20
 *    desc   : 数字进度条
 */
public final class NumberProgressBar extends View {

    /** 文本颜色 */
    private int mTextColor;
    /** 文字大小 */
    private float mTextSize;

    /** 最大进度 */
    private int mMaxProgress = 100;
    /** 当前进度 */
    private int mCurrentProgress = 0;

    /** 进度栏颜色 */
    private int mReachedBarColor;
    /** 条未到达区域颜色 */
    private int mUnreachedBarColor;

    /** 到达区域的高度 */
    private float mReachedBarHeight;
    /** 未到达区域的高度 */
    private float mUnreachedBarHeight;

    /** 到达区域的画笔 */
    private final Paint mReachedBarPaint;
    /** 未触及区域的画笔 */
    private final Paint mUnreachedBarPaint;

    /** 进度文本的绘制 */
    private final Paint mTextPaint;

    /** 到达的栏区绘制矩形 */
    private final RectF mReachedBound = new RectF(0, 0, 0, 0);
    /** 未到达的栏区绘制矩形 */
    private final RectF mUnreachedBound = new RectF(0, 0, 0, 0);

    /** 进度文本偏移量 */
    private final float mTextOffset;

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberProgressBar, defStyleAttr, 0);

        mReachedBarColor = array.getColor(R.styleable.NumberProgressBar_pb_reachedColor, Color.rgb(66, 145, 241));
        mUnreachedBarColor = array.getColor(R.styleable.NumberProgressBar_pb_unreachedColor, Color.rgb(204, 204, 204));
        mTextColor = array.getColor(R.styleable.NumberProgressBar_pb_textColor, Color.rgb(66, 145, 241));
        mTextSize = array.getDimension(R.styleable.NumberProgressBar_pb_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

        mReachedBarHeight = array.getDimension(R.styleable.NumberProgressBar_pb_reachedHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics()));
        mUnreachedBarHeight = array.getDimension(R.styleable.NumberProgressBar_pb_unreachedHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mTextOffset = array.getDimension(R.styleable.NumberProgressBar_pb_textOffset, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));

        setProgress(array.getInt(R.styleable.NumberProgressBar_pb_progress, 0));
        setMax(array.getInt(R.styleable.NumberProgressBar_pb_maxProgress, 100));
        array.recycle();

        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mTextSize, Math.max((int) mReachedBarHeight, (int) mUnreachedBarHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getSuggestedMinimumWidth()
                        + getPaddingLeft() + getPaddingRight(), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
                break;
        }
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(getSuggestedMinimumHeight()
                        + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
                break;
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 要在onDraw()中绘制的文本
        String text = (getProgress() * 100 / getMax()) + "%";

        // 要绘制的文本的宽度
        float textWidth = mTextPaint.measureText(text);

        // 文本开始位置
        float textStart;

        boolean drawReachedBar;
        if (getProgress() == 0) {
            drawReachedBar = false;
            textStart = getPaddingLeft();
        } else {
            drawReachedBar = true;
            mReachedBound.left = getPaddingLeft();
            mReachedBound.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
            mReachedBound.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() - mTextOffset + getPaddingLeft();
            mReachedBound.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
            textStart = (mReachedBound.right + mTextOffset);
        }

        // 文本结束位置
        float textEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        if ((textStart + textWidth) >= getWidth() - getPaddingRight()) {
            textStart = getWidth() - getPaddingRight() - textWidth;
            mReachedBound.right = textStart - mTextOffset;
        }

        float unreachedBarStart = textStart + textWidth + mTextOffset;
        // 确定是否需要绘制未到达区域
        boolean drawUnreachedBar;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) {
            drawUnreachedBar = false;
        } else {
            drawUnreachedBar = true;
            mUnreachedBound.left = unreachedBarStart;
            mUnreachedBound.right = getWidth() - getPaddingRight();
            mUnreachedBound.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
            mUnreachedBound.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
        }

        if (drawReachedBar) {
            canvas.drawRect(mReachedBound, mReachedBarPaint);
        }

        if (drawUnreachedBar) {
            canvas.drawRect(mUnreachedBound, mUnreachedBarPaint);
        }

        canvas.drawText(text, textStart, textEnd, mTextPaint);
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public void setProgress(int progress) {
        if (progress <= getMax() && progress >= 0) {
            mCurrentProgress = progress;
            invalidate();
        }
    }

    public int getMax() {
        return mMaxProgress;
    }

    public void setMax(int maxProgress) {
        if (maxProgress > 0) {
            mMaxProgress = maxProgress;
            invalidate();
        }
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }

    public void setReachedBarHeight(float height) {
        mReachedBarHeight = height;
    }

    public float getUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public void setUnreachedBarHeight(float height) {
        mUnreachedBarHeight = height;
    }

    public void setProgressTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setProgressTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getUnreachedBarColor() {
        return mUnreachedBarColor;
    }

    public void setUnreachedBarColor(int barColor) {
        mUnreachedBarColor = barColor;
        mUnreachedBarPaint.setColor(mUnreachedBarColor);
        invalidate();
    }

    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public void setReachedBarColor(int progressColor) {
        mReachedBarColor = progressColor;
        mReachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }
}