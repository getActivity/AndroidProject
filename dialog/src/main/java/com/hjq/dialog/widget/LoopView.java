package com.hjq.dialog.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hjq.dialog.R;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *    author : brucetoo
 *    github : https://github.com/brucetoo/PickView
 *    time   : 2019/02/17
 *    desc   : 循环滚动列表自定义控件
 */
public final class LoopView extends View {

    private static final String TAG = "LoopView";

    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture mScheduledFuture;

    private LoopScrollListener mListener;

    private List<String> mData;

    private Paint mTopBottomTextPaint;
    private Paint mCenterTextPaint;
    private Paint mCenterLinePaint;

    private int mTotalScrollY;
    private GestureDetector mGestureDetector;
    private int mSelectedItem;
    private int mTextSize;

    private int mMaxTextWidth;
    private int mMaxTextHeight;

    private int mTopBottomTextColor;

    private int mCenterTextColor;
    private int mCenterLineColor;

    private float mLineSpacingMultiplier;
    private boolean mCanLoop;

    private float mTopLineY;
    private float mBottomLineY;

    private int mCurrentIndex;
    private int mInitPosition;

    private float mHorizontalPadding;
    private float mVerticalPadding;

    private float mItemHeight;
    private int mDrawItemsCount;
    private String[] mItemTempArray;

    private float mCircularDiameter;
    private float mCircularRadius;

    public LoopView(Context context) {
        this(context, null);
    }

    public LoopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoopView);
        if (array != null) {
            mTopBottomTextColor = array.getColor(R.styleable.LoopView_topBottomTextColor, 0xffafafaf);
            mCenterTextColor = array.getColor(R.styleable.LoopView_centerTextColor, 0xff313131);
            mCenterLineColor = array.getColor(R.styleable.LoopView_lineColor, 0xffc5c5c5);
            mCanLoop = array.getBoolean(R.styleable.LoopView_canLoop, true);
            mInitPosition = array.getInt(R.styleable.LoopView_initPosition, -1);
            mTextSize = array.getDimensionPixelSize(R.styleable.LoopView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getContext().getResources().getDisplayMetrics()));
            mDrawItemsCount = array.getInt(R.styleable.LoopView_drawItemCount, 7);
            mItemTempArray = new String[mDrawItemsCount];
            array.recycle();
        }

        mLineSpacingMultiplier = 3;

        mTopBottomTextPaint = new Paint();
        mCenterTextPaint = new Paint();
        mCenterLinePaint = new Paint();

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mGestureDetector = new GestureDetector(context, new LoopViewGestureListener());
        mGestureDetector.setIsLongpressEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxTextWidth, MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mCircularDiameter, MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mItemHeight = mLineSpacingMultiplier * mMaxTextHeight;
        // auto calculate the text's left/right value when draw
        mHorizontalPadding = (width - mMaxTextWidth) / 2;
        mVerticalPadding = (height - mCircularDiameter) / 2;

        // topLineY = diameter/2 - itemHeight(mItemHeight) / 2 + mVerticalPadding
        mTopLineY =  ((mCircularDiameter - mItemHeight) / 2) + mVerticalPadding;
        mBottomLineY = ((mCircularDiameter + mItemHeight) / 2) + mVerticalPadding;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mData == null) return;

        // the length of single item is mItemHeight
        int mChangingItem = (int) (mTotalScrollY / (mItemHeight));
        mCurrentIndex = mInitPosition + mChangingItem % mData.size();
        if (!mCanLoop) { // can loop
            if (mCurrentIndex < 0) {
                mCurrentIndex = 0;
            }
            if (mCurrentIndex > mData.size() - 1) {
                mCurrentIndex = mData.size() - 1;
            }
        } else { // can not loop
            if (mCurrentIndex < 0) {
                mCurrentIndex = mData.size() + mCurrentIndex;
            }
            if (mCurrentIndex > mData.size() - 1) {
                mCurrentIndex = mCurrentIndex - mData.size();
            }
        }

        int count = 0;
        // reconfirm each item's value from dataList according to currentIndex,
        while (count < mDrawItemsCount) {
            int templateItem = mCurrentIndex - (mDrawItemsCount / 2 - count);
            if (mCanLoop) {
                if (templateItem < 0) {
                    templateItem = templateItem + mData.size();
                }
                if (templateItem > mData.size() - 1) {
                    templateItem = templateItem - mData.size();
                }
                mItemTempArray[count] = mData.get(templateItem);
            } else if (templateItem < 0) {
                mItemTempArray[count] = "";
            } else if (templateItem > mData.size() - 1) {
                mItemTempArray[count] = "";
            } else {
                mItemTempArray[count] = mData.get(templateItem);
            }
            count++;
        }

        // draw top and bottom line
        canvas.drawLine(0, mTopLineY, getMeasuredWidth(), mTopLineY, mCenterLinePaint);
        canvas.drawLine(0, mBottomLineY, getMeasuredWidth(), mBottomLineY, mCenterLinePaint);

        count = 0;
        int changingLeftY = (int) (mTotalScrollY % (mItemHeight));
        while (count < mDrawItemsCount) {
            canvas.save();
            // L= å * r -> å = rad
            float itemHeight = mMaxTextHeight * mLineSpacingMultiplier;
            // get radian  L = (itemHeight * count - changingLeftY),r = mCircularRadius
            double radian = (itemHeight * count - changingLeftY) / mCircularRadius;
            // a = rad * 180 / π
            // get angle
            float angle = (float) (radian * 180 / Math.PI);

            // when angle >= 180 || angle <= 0 don't draw
            if (angle >= 180F || angle <= 0F) {
                canvas.restore();
            } else {
                // translateY = r - r*cos(å) -
                // (Math.sin(radian) * mMaxTextHeight) / 2 this is text offset
                float translateY = (float) (mCircularRadius - Math.cos(radian) * mCircularRadius - (Math.sin(radian) * mMaxTextHeight) / 2) + mVerticalPadding;
                canvas.translate(0.0F, translateY);
                // scale offset = Math.sin(radian) -> 0 - 1
                canvas.scale(1.0F, (float) Math.sin(radian));
                if (translateY <= mTopLineY) {
                    // draw text y between 0 -> mTopLineY,include incomplete text
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), mTopLineY - translateY);
                    canvas.drawText(mItemTempArray[count], mHorizontalPadding, mMaxTextHeight, mTopBottomTextPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, mTopLineY - translateY, getMeasuredWidth(), (int) (itemHeight));
                    canvas.drawText(mItemTempArray[count], mHorizontalPadding, mMaxTextHeight, mCenterTextPaint);
                    canvas.restore();
                } else if (mMaxTextHeight + translateY >= mBottomLineY) {
                    // draw text y between  mTopLineY -> mBottomLineY ,include incomplete text
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), mBottomLineY - translateY);
                    canvas.drawText(mItemTempArray[count], mHorizontalPadding, mMaxTextHeight, mCenterTextPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, mBottomLineY - translateY, getMeasuredWidth(), (int) (itemHeight));
                    canvas.drawText(mItemTempArray[count], mHorizontalPadding, mMaxTextHeight, mTopBottomTextPaint);
                    canvas.restore();
                } else if (translateY >= mTopLineY && mMaxTextHeight + translateY <= mBottomLineY) {
                    // draw center complete text
                    canvas.clipRect(0, 0, getMeasuredWidth(), (int) (itemHeight));
                    canvas.drawText(mItemTempArray[count], mHorizontalPadding, mMaxTextHeight, mCenterTextPaint);
                    // center one indicate selected item
                    mSelectedItem = mData.indexOf(mItemTempArray[count]);
                }
                canvas.restore();
            }
            count++;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionevent) {

        switch (motionevent.getAction()) {
            case MotionEvent.ACTION_UP:
            default:
                if (!mGestureDetector.onTouchEvent(motionevent)) {
                    startSmoothScrollTo();
                }
        }
        return true;
    }

    public final void setCanLoop(boolean canLoop) {
        mCanLoop = canLoop;
        invalidate();
    }

    /**
     * set text size
     *
     * @param size size indicate sp,not px
     */
    public final void setTextSize(float size) {
        if (size > 0) {
            mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getContext().getResources().getDisplayMetrics());
        }
    }

    public void setLineSpacingMultiplier(float spacing) {
        this.mLineSpacingMultiplier = spacing;
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }

    public void setInitPosition(int initPosition) {
        if (mData == null) return;
        if (initPosition > mData.size()) {
            initPosition = mData.size() - 1;
        }
        mInitPosition = initPosition;
        invalidate();
        if (mListener != null) {
            mListener.onItemSelect(this, initPosition);
        }
    }

    public void setLoopListener(LoopScrollListener l) {
        mListener = l;
    }

    /**
     * All public method must be called before this method
     * @param data data list
     */
    public final void setData(List<String> data) {
        mData = data;

        if (mData == null) {
            throw new IllegalArgumentException("data list must not be null!");
        }
        mTopBottomTextPaint.setColor(mTopBottomTextColor);
        mTopBottomTextPaint.setAntiAlias(true);
        mTopBottomTextPaint.setTypeface(Typeface.MONOSPACE);
        mTopBottomTextPaint.setTextSize(mTextSize);

        mCenterTextPaint.setColor(mCenterTextColor);
        mCenterTextPaint.setAntiAlias(true);
        mCenterTextPaint.setTextScaleX(1.05F);
        mCenterTextPaint.setTypeface(Typeface.MONOSPACE);
        mCenterTextPaint.setTextSize(mTextSize);

        mCenterLinePaint.setColor(mCenterLineColor);
        mCenterLinePaint.setAntiAlias(true);
        mCenterLinePaint.setTypeface(Typeface.MONOSPACE);
        mCenterLinePaint.setTextSize(mTextSize);

        // measureTextWidthHeight
        Rect rect = new Rect();
        for (int i = 0; i < mData.size(); i++) {
            String text = mData.get(i);
            mCenterTextPaint.getTextBounds(text, 0, text.length(), rect);

            int textWidth = rect.width();
            if (textWidth > mMaxTextWidth) {
                mMaxTextWidth = textWidth;
            }

            //int textHeight = rect.height();
            //if (textHeight > mMaxTextHeight) {
            //    mMaxTextHeight = textHeight;
            //}

            Paint.FontMetrics fontMetrics = mCenterTextPaint.getFontMetrics();
            mMaxTextHeight = (int) (fontMetrics.bottom - fontMetrics.top) * 2 / 3;
        }

        // 计算半圆周 -- mMaxTextHeight * mLineSpacingMultiplier 表示每个item的高度  mDrawItemsCount = 7
        // 实际显示5个,留两个是在圆周的上下面
        // lineSpacingMultiplier是指text上下的距离的值和maxTextHeight一样的意思 所以 = 2
        // mDrawItemsCount - 1 代表圆周的上下两面各被剪切了一半 相当于高度少了一个 mMaxTextHeight
        int halfCircumference = (int) (mMaxTextHeight * mLineSpacingMultiplier * (mDrawItemsCount - 1));
        // the diameter of circular 2πr = cir, 2r = height
        mCircularDiameter = (int) ((halfCircumference * 2) / Math.PI);
        // the radius of circular
        mCircularRadius = (int) (halfCircumference / Math.PI);
        // FIXME: 7/8/16  通过控件的高度来计算圆弧的周长

        if (mInitPosition == -1) {
            if (mCanLoop) {
                mInitPosition = (mData.size() + 1) / 2;
            } else {
                mInitPosition = 0;
            }
        }
        mCurrentIndex = mInitPosition;
        invalidate();
    }

    private void cancelSchedule() {

        if (mScheduledFuture != null && !mScheduledFuture.isCancelled()) {
            mScheduledFuture.cancel(true);
            mScheduledFuture = null;
        }
    }

    private void startSmoothScrollTo() {
        int offset = (int) (mTotalScrollY % (mItemHeight));
        cancelSchedule();
        mScheduledFuture = mExecutor.scheduleWithFixedDelay(new HalfHeightRunnable(offset), 0, 10, TimeUnit.MILLISECONDS);
    }

    private void startSmoothScrollTo(float velocityY) {
        cancelSchedule();
        int velocityFling = 20;
        mScheduledFuture = mExecutor.scheduleWithFixedDelay(new FlingRunnable(velocityY), 0, velocityFling, TimeUnit.MILLISECONDS);
    }

    class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

        @Override
        public final boolean onDown(MotionEvent motionevent) {
            cancelSchedule();
            Log.i(TAG, "LoopViewGestureListener->onDown");
            return true;
        }

        @Override
        public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            startSmoothScrollTo(velocityY);
            Log.i(TAG, "LoopViewGestureListener->onFling");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "LoopViewGestureListener->onScroll");
            mTotalScrollY = (int) ((float) mTotalScrollY + distanceY);
            if (!mCanLoop) {
                int initPositionCircleLength = (int) (mInitPosition * (mItemHeight));
                int initPositionStartY = -1 * initPositionCircleLength;
                if (mTotalScrollY < initPositionStartY) {
                    mTotalScrollY = initPositionStartY;
                }

                int circleLength = (int) ((float) (mData.size() - 1 - mInitPosition) * (mItemHeight));
                if (mTotalScrollY >= circleLength) {
                    mTotalScrollY = circleLength;
                }
            }

            invalidate();
            return true;
        }
    }

    class SelectedRunnable implements Runnable {

        @Override
        public final void run() {
            if (mListener != null) {
                mListener.onItemSelect(LoopView.this, getSelectedItem());
            }
        }
    }

    /**
     * Use in ACTION_UP
     */
    private class HalfHeightRunnable implements Runnable {

        int realTotalOffset;
        int realOffset;
        int offset;

        HalfHeightRunnable(int offset) {
            this.offset = offset;
            realTotalOffset = Integer.MAX_VALUE;
            realOffset = 0;
        }

        @Override
        public void run() {
            // first in
            if (realTotalOffset == Integer.MAX_VALUE) {

                if ((float) offset > mItemHeight / 2.0F) {
                    // move to next item
                    realTotalOffset = (int) (mItemHeight - (float) offset);
                } else {
                    // move to pre item
                    realTotalOffset = -offset;
                }
            }

            realOffset = (int) ((float) realTotalOffset * 0.1F);

            if (realOffset == 0) {

                if (realTotalOffset < 0) {
                    realOffset = -1;
                } else {
                    realOffset = 1;
                }
            }
            if (Math.abs(realTotalOffset) <= 0) {
                cancelSchedule();
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            postDelayed(new SelectedRunnable(), 200L);
                        }
                    }
                });
            } else {
                mTotalScrollY = mTotalScrollY + realOffset;
                postInvalidate();
                realTotalOffset = realTotalOffset - realOffset;
            }
        }
    }

    /**
     * Use in {@link LoopViewGestureListener#onFling(MotionEvent, MotionEvent, float, float)}
     */
    private class FlingRunnable implements Runnable {

        float velocity;
        final float velocityY;

        FlingRunnable(float velocityY) {
            this.velocityY = velocityY;
            this.velocity = Integer.MAX_VALUE;
        }

        @Override
        public void run() {
            if (velocity == Integer.MAX_VALUE) {
                if (Math.abs(velocityY) > 2000F) {
                    if (velocityY > 0.0F) {
                        velocity = 2000F;
                    } else {
                        velocity = -2000F;
                    }
                } else {
                    velocity = velocityY;
                }
            }
            Log.i(TAG, "velocity->" + velocity);
            if (Math.abs(velocity) >= 0.0F && Math.abs(velocity) <= 20F) {
                cancelSchedule();
                post(new Runnable() {
                    @Override
                    public void run() {
                        startSmoothScrollTo();
                    }
                });
                return;
            }
            int i = (int) ((velocity * 10F) / 1000F);
            mTotalScrollY = mTotalScrollY - i;
            if (!mCanLoop) {
                float itemHeight = mLineSpacingMultiplier * mMaxTextHeight;
                if (mTotalScrollY <= (int) ((float) (-mInitPosition) * itemHeight)) {
                    velocity = 40F;
                    mTotalScrollY = (int) ((float) (-mInitPosition) * itemHeight);
                } else if (mTotalScrollY >= (int) ((float) (mData.size() - 1 - mInitPosition) * itemHeight)) {
                    mTotalScrollY = (int) ((float) (mData.size() - 1 - mInitPosition) * itemHeight);
                    velocity = -40F;
                }
            }
            if (velocity < 0.0F) {
                velocity = velocity + 20F;
            } else {
                velocity = velocity - 20F;
            }
            postInvalidate();
        }
    }

    public interface LoopScrollListener {
        void onItemSelect(LoopView loopView, int position);
    }
}