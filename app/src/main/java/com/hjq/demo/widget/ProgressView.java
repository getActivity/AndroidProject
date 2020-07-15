package com.hjq.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.demo.R;

/**
 *    author : Todd-Davies
 *    github : https://github.com/Todd-Davies/ProgressWheel
 *    time   : 2019/07/13
 *    desc   : 进度条控件
 */
public final class ProgressView extends View {

    private final static int BAR_LENGTH = 16;
    private final static int BAR_MAX_LENGTH = 270;
    private final static long PAUSE_GROWING_TIME = 200;

    /** Sizes (with defaults in DP) */
    private int mCircleRadius = 28;
    private int mBarWidth = 4;
    private int mRimWidth = 4;
    private boolean mFillRadius;
    private double mTimeStartGrowing = 0;
    private double mBarSpinCycleTime = 400;
    private float mBarExtraLength = 0;
    private boolean mBarGrowingFromFront = true;
    private long mPausedTimeWithoutGrowing = 0;
    /** Colors (with defaults) */
    private int mBarColor = 0xAA000000;
    private int mRimColor = 0x00FFFFFF;

    /** Paints */
    private final Paint mBarPaint = new Paint();
    private final Paint mRimPaint = new Paint();

    /** Rectangles */
    private RectF mCircleBounds = new RectF();

    /** Animation The amount of degrees per second */
    private float mSpinSpeed = 230.0f;
    // private float mSpinSpeed = 120.0f;
    /** The last time the spinner was animated */
    private long mLastTimeAnimated = 0;

    private boolean mLinearProgress;

    private float mProgress = 0.0f;
    private float mTargetProgress = 0.0f;
    private boolean isSpinning = false;

    private ProgressCallback mCallback;

    private final boolean mShouldAnimate;

    public ProgressView(Context context) {
        this(context, null, 0);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        mBarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBarWidth, getResources().getDisplayMetrics());
        mRimWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRimWidth, getResources().getDisplayMetrics());
        mCircleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRadius, getResources().getDisplayMetrics());
        mCircleRadius = (int) array.getDimension(R.styleable.ProgressView_circleRadius, mCircleRadius);
        mFillRadius = array.getBoolean(R.styleable.ProgressView_fillRadius, false);
        mBarWidth = (int) array.getDimension(R.styleable.ProgressView_barWidth, mBarWidth);
        mRimWidth = (int) array.getDimension(R.styleable.ProgressView_rimWidth, mRimWidth);
        float baseSpinSpeed = array.getFloat(R.styleable.ProgressView_spinSpeed, mSpinSpeed / 360.0f);
        mSpinSpeed = baseSpinSpeed * 360;
        mBarSpinCycleTime = array.getInt(R.styleable.ProgressView_barSpinCycleTime, (int) mBarSpinCycleTime);
        mBarColor = array.getColor(R.styleable.ProgressView_barColor, mBarColor);
        mRimColor = array.getColor(R.styleable.ProgressView_rimColor, mRimColor);
        mLinearProgress = array.getBoolean(R.styleable.ProgressView_linearProgress, false);
        if (array.getBoolean(R.styleable.ProgressView_progressIndeterminate, false)) {
            spin();
        }
        array.recycle();

        float animationValue = Settings.Global.getFloat(getContext().getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1);
        mShouldAnimate = animationValue != 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        int viewWidth = mCircleRadius + this.getPaddingLeft() + this.getPaddingRight();
        int viewHeight = mCircleRadius + this.getPaddingTop() + this.getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                width = Math.min(viewWidth, widthSize);
                break;
            default:
                width = viewWidth;
                break;
        }

        if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(viewHeight, heightSize);
        } else {
            height = viewHeight;
        }

        setMeasuredDimension(width, height);
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        setupBounds(width, height);
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to
     * draw the progress wheel
     */
    private void setupPaints() {
        mBarPaint.setColor(mBarColor);
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Style.STROKE);
        mBarPaint.setStrokeWidth(mBarWidth);

        mRimPaint.setColor(mRimColor);
        mRimPaint.setAntiAlias(true);
        mRimPaint.setStyle(Style.STROKE);
        mRimPaint.setStrokeWidth(mRimWidth);
    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds(int layoutWidth, int layoutHeight) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        if (!mFillRadius) {
            // Width should equal to Height, find the min value to setup the circle
            int minValue = Math.min(layoutWidth - paddingLeft - paddingRight,
                    layoutHeight - paddingBottom - paddingTop);

            int circleDiameter = Math.min(minValue, mCircleRadius * 2 - mBarWidth * 2);

            // Calc the Offset if needed for centering the wheel in the available space
            int xOffset = (layoutWidth - paddingLeft - paddingRight - circleDiameter) / 2 + paddingLeft;
            int yOffset = (layoutHeight - paddingTop - paddingBottom - circleDiameter) / 2 + paddingTop;

            mCircleBounds = new RectF(xOffset + mBarWidth, yOffset + mBarWidth, xOffset + circleDiameter - mBarWidth,
                            yOffset + circleDiameter - mBarWidth);
        } else {
            mCircleBounds = new RectF(paddingLeft + mBarWidth, paddingTop + mBarWidth,
                    layoutWidth - paddingRight - mBarWidth, layoutHeight - paddingBottom - mBarWidth);
        }
    }

    public void setCallback(ProgressCallback progressCallback) {
        mCallback = progressCallback;

        if (!isSpinning) {
            runCallback();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mCircleBounds, 360, 360, false, mRimPaint);

        boolean mustInvalidate = false;

        if (!mShouldAnimate) {
            return;
        }

        if (isSpinning) {
            //Draw the spinning bar
            mustInvalidate = true;

            long deltaTime = (SystemClock.uptimeMillis() - mLastTimeAnimated);
            float deltaNormalized = deltaTime * mSpinSpeed / 1000.0f;

            updateBarLength(deltaTime);

            mProgress += deltaNormalized;
            if (mProgress > 360) {
                mProgress -= 360f;

                // A full turn has been completed
                // we run the callback with -1 in case we want to
                // do something, like changing the color
                runCallback(-1.0f);
            }
            mLastTimeAnimated = SystemClock.uptimeMillis();

            float from = mProgress - 90;
            float length = BAR_LENGTH + mBarExtraLength;

            if (isInEditMode()) {
                from = 0;
                length = 135;
            }

            canvas.drawArc(mCircleBounds, from, length, false, mBarPaint);
        } else {
            float oldProgress = mProgress;

            if (mProgress != mTargetProgress) {
                //We smoothly increase the progress bar
                mustInvalidate = true;

                float deltaTime = (float) (SystemClock.uptimeMillis() - mLastTimeAnimated) / 1000;
                float deltaNormalized = deltaTime * mSpinSpeed;

                mProgress = Math.min(mProgress + deltaNormalized, mTargetProgress);
                mLastTimeAnimated = SystemClock.uptimeMillis();
            }

            if (oldProgress != mProgress) {
                runCallback();
            }

            float offset = 0.0f;
            float progress = mProgress;
            if (!mLinearProgress) {
                float factor = 2.0f;
                offset = (float) (1.0f - Math.pow(1.0f - mProgress / 360.0f, 2.0f * factor)) * 360.0f;
                progress = (float) (1.0f - Math.pow(1.0f - mProgress / 360.0f, factor)) * 360.0f;
            }

            if (isInEditMode()) {
                progress = 360;
            }

            canvas.drawArc(mCircleBounds, offset - 90, progress, false, mBarPaint);
        }

        if (mustInvalidate) {
            invalidate();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == VISIBLE) {
            mLastTimeAnimated = SystemClock.uptimeMillis();
        }
    }

    private void updateBarLength(long deltaTimeInMilliSeconds) {
        if (mPausedTimeWithoutGrowing >= PAUSE_GROWING_TIME) {
            mTimeStartGrowing += deltaTimeInMilliSeconds;

            if (mTimeStartGrowing > mBarSpinCycleTime) {
                // We completed a size change cycle
                // (growing or shrinking)
                mTimeStartGrowing -= mBarSpinCycleTime;
                //if(mBarGrowingFromFront) {
                mPausedTimeWithoutGrowing = 0;
                //}
                mBarGrowingFromFront = !mBarGrowingFromFront;
            }

            float distance =
                    (float) Math.cos((mTimeStartGrowing / mBarSpinCycleTime + 1) * Math.PI) / 2 + 0.5f;
            float destLength = (BAR_MAX_LENGTH - BAR_LENGTH);

            if (mBarGrowingFromFront) {
                mBarExtraLength = distance * destLength;
            } else {
                float newLength = destLength * (1 - distance);
                mProgress += (mBarExtraLength - newLength);
                mBarExtraLength = newLength;
            }
        } else {
            mPausedTimeWithoutGrowing += deltaTimeInMilliSeconds;
        }
    }

    /**
     * Check if the wheel is currently spinning
     */

    public boolean isSpinning() {
        return isSpinning;
    }

    /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        mProgress = 0.0f;
        mTargetProgress = 0.0f;
        invalidate();
    }

    /**
     * Turn off spin mode
     */
    public void stopSpinning() {
        isSpinning = false;
        mProgress = 0.0f;
        mTargetProgress = 0.0f;
        invalidate();
    }

    /**
     * Puts the view on spin mode
     */
    public void spin() {
        mLastTimeAnimated = SystemClock.uptimeMillis();
        isSpinning = true;
        invalidate();
    }

    private void runCallback(float value) {
        if (mCallback != null) {
            mCallback.onProgressUpdate(value);
        }
    }

    private void runCallback() {
        if (mCallback != null) {
            float normalizedProgress = (float) Math.round(mProgress * 100 / 360.0f) / 100;
            mCallback.onProgressUpdate(normalizedProgress);
        }
    }

    /**
     * Set the progress to a specific value,
     * the bar will be set instantly to that value
     *
     * @param progress the progress between 0 and 1
     */
    public void setInstantProgress(float progress) {
        if (isSpinning) {
            mProgress = 0.0f;
            isSpinning = false;
        }

        if (progress > 1.0f) {
            progress -= 1.0f;
        } else if (progress < 0) {
            progress = 0;
        }

        if (progress == mTargetProgress) {
            return;
        }

        mTargetProgress = Math.min(progress * 360.0f, 360.0f);
        mProgress = mTargetProgress;
        mLastTimeAnimated = SystemClock.uptimeMillis();
        invalidate();
    }

    // Great way to save a view's state http://stackoverflow.com/a/7089687/1991053
    @Override
    public Parcelable onSaveInstanceState() {
        WheelSavedState savedState = new WheelSavedState(super.onSaveInstanceState());
        // We save everything that can be changed at runtime
        savedState.mProgress = this.mProgress;
        savedState.mTargetProgress = this.mTargetProgress;
        savedState.isSpinning = this.isSpinning;
        savedState.spinSpeed = this.mSpinSpeed;
        savedState.barWidth = this.mBarWidth;
        savedState.barColor = this.mBarColor;
        savedState.rimWidth = this.mRimWidth;
        savedState.rimColor = this.mRimColor;
        savedState.circleRadius = this.mCircleRadius;
        savedState.linearProgress = this.mLinearProgress;
        savedState.fillRadius = this.mFillRadius;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof WheelSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        WheelSavedState savedState = (WheelSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        this.mProgress = savedState.mProgress;
        this.mTargetProgress = savedState.mTargetProgress;
        this.isSpinning = savedState.isSpinning;
        this.mSpinSpeed = savedState.spinSpeed;
        this.mBarWidth = savedState.barWidth;
        this.mBarColor = savedState.barColor;
        this.mRimWidth = savedState.rimWidth;
        this.mRimColor = savedState.rimColor;
        this.mCircleRadius = savedState.circleRadius;
        this.mLinearProgress = savedState.linearProgress;
        this.mFillRadius = savedState.fillRadius;

        this.mLastTimeAnimated = SystemClock.uptimeMillis();
    }

    /**
     * @return the current progress between 0.0 and 1.0,
     * if the wheel is indeterminate, then the result is -1
     */
    public float getProgress() {
        return isSpinning ? -1 : mProgress / 360.0f;
    }

    //----------------------------------
    //Getters + setters
    //----------------------------------

    /**
     * Set the progress to a specific value,
     * the bar will smoothly animate until that value
     *
     * @param progress the progress between 0 and 1
     */
    public void setProgress(float progress) {
        if (isSpinning) {
            mProgress = 0.0f;
            isSpinning = false;

            runCallback();
        }

        if (progress > 1.0f) {
            progress -= 1.0f;
        } else if (progress < 0) {
            progress = 0;
        }

        if (progress == mTargetProgress) {
            return;
        }

        // If we are currently in the right position
        // we set again the last time animated so the
        // animation starts smooth from here
        if (mProgress == mTargetProgress) {
            mLastTimeAnimated = SystemClock.uptimeMillis();
        }

        mTargetProgress = Math.min(progress * 360.0f, 360.0f);

        invalidate();
    }

    /**
     * Sets the determinate progress mode
     *
     * @param isLinear if the progress should increase linearly
     */
    public void setLinearProgress(boolean isLinear) {
        mLinearProgress = isLinear;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * @return the radius of the wheel in pixels
     */
    public int getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * Sets the radius of the wheel
     *
     * @param circleRadius the expected radius, in pixels
     */
    public void setCircleRadius(int circleRadius) {
        this.mCircleRadius = circleRadius;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * @return the width of the spinning bar
     */
    public int getBarWidth() {
        return mBarWidth;
    }

    /**
     * Sets the width of the spinning bar
     *
     * @param barWidth the spinning bar width in pixels
     */
    public void setBarWidth(int barWidth) {
        this.mBarWidth = barWidth;
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * @return the color of the spinning bar
     */
    public int getBarColor() {
        return mBarColor;
    }

    /**
     * Sets the color of the spinning bar
     *
     * @param barColor The spinning bar color
     */
    public void setBarColor(int barColor) {
        this.mBarColor = barColor;
        setupPaints();
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * @return the color of the wheel's contour
     */
    public int getRimColor() {
        return mRimColor;
    }

    /**
     * Sets the color of the wheel's contour
     *
     * @param rimColor the color for the wheel
     */
    public void setRimColor(int rimColor) {
        this.mRimColor = rimColor;
        setupPaints();
        if (!isSpinning) {
            invalidate();
        }
    }

    /**
     * @return the base spinning speed, in full circle turns per second
     * (1.0 equals on full turn in one second), this value also is applied for
     * the smoothness when setting a progress
     */
    public float getSpinSpeed() {
        return mSpinSpeed / 360.0f;
    }

    /**
     * Sets the base spinning speed, in full circle turns per second
     * (1.0 equals on full turn in one second), this value also is applied for
     * the smoothness when setting a progress
     *
     * @param spinSpeed the desired base speed in full turns per second
     */
    public void setSpinSpeed(float spinSpeed) {
        this.mSpinSpeed = spinSpeed * 360.0f;
    }

    /**
     * @return the width of the wheel's contour in pixels
     */
    public int getRimWidth() {
        return mRimWidth;
    }

    /**
     * Sets the width of the wheel's contour
     *
     * @param rimWidth the width in pixels
     */
    public void setRimWidth(int rimWidth) {
        this.mRimWidth = rimWidth;
        if (!isSpinning) {
            invalidate();
        }
    }

    public interface ProgressCallback {
        /**
         * Method to call when the progress reaches a value
         * in order to avoid float precision issues, the progress
         * is rounded to a float with two decimals.
         * <p>
         * In indeterminate mode, the callback is called each time
         * the wheel completes an animation cycle, with, the progress value is -1.0f
         *
         * @param progress a double value between 0.00 and 1.00 both included
         */
        void onProgressUpdate(float progress);
    }

    static class WheelSavedState extends BaseSavedState {
        // required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<WheelSavedState> CREATOR =
                new Parcelable.Creator<WheelSavedState>() {
                    @Override
                    public WheelSavedState createFromParcel(Parcel in) {
                        return new WheelSavedState(in);
                    }

                    @Override
                    public WheelSavedState[] newArray(int size) {
                        return new WheelSavedState[size];
                    }
                };
        float mProgress;
        float mTargetProgress;
        boolean isSpinning;
        float spinSpeed;
        int barWidth;
        int barColor;
        int rimWidth;
        int rimColor;
        int circleRadius;
        boolean linearProgress;
        boolean fillRadius;

        WheelSavedState(Parcelable superState) {
            super(superState);
        }

        private WheelSavedState(Parcel in) {
            super(in);
            this.mProgress = in.readFloat();
            this.mTargetProgress = in.readFloat();
            this.isSpinning = in.readByte() != 0;
            this.spinSpeed = in.readFloat();
            this.barWidth = in.readInt();
            this.barColor = in.readInt();
            this.rimWidth = in.readInt();
            this.rimColor = in.readInt();
            this.circleRadius = in.readInt();
            this.linearProgress = in.readByte() != 0;
            this.fillRadius = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.mProgress);
            out.writeFloat(this.mTargetProgress);
            out.writeByte((byte) (isSpinning ? 1 : 0));
            out.writeFloat(this.spinSpeed);
            out.writeInt(this.barWidth);
            out.writeInt(this.barColor);
            out.writeInt(this.rimWidth);
            out.writeInt(this.rimColor);
            out.writeInt(this.circleRadius);
            out.writeByte((byte) (linearProgress ? 1 : 0));
            out.writeByte((byte) (fillRadius ? 1 : 0));
        }
    }
}