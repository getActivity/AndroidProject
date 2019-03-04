package com.hjq.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/20
 *    desc   : 开关按钮
 */
public class SwitchButton extends View {

    private static final int STATE_SWITCH_OFF = 1;
    private static final int STATE_SWITCH_OFF2 = 2;
    private static final int STATE_SWITCH_ON = 3;
    private static final int STATE_SWITCH_ON2 = 4;

    private final AccelerateInterpolator mInterpolator = new AccelerateInterpolator(2);
    private final Paint mPaint = new Paint();
    private final Path mBackgroundPath = new Path();
    private final Path mBarPath = new Path();
    private final RectF mBound = new RectF();

    private float mAnim1, mAnim2;
    private RadialGradient mShadowGradient;

    protected float mAspectRatio = 0.68f; // 按钮宽高形状比率(0,1] 不推荐大幅度调整
    protected float mAnimationSpeed = 0.1f; // (0,1]

    private int mLastCheckedState; // 上一个选中状态
    private int mCheckedState; // 当前的选中状态

    private boolean isCanVisibleDrawing = false;

    protected boolean isShadow; // 是否显示按钮阴影
    protected boolean mChecked; // 是否选中

    protected int mAccentColor = 0xFF4BD763; // 开启状态背景色
    protected int mPrimaryDarkColor = 0xFF3AC652; // 开启状态按钮描边色
    protected int mOffColor = 0xFFE3E3E3; // 关闭状态描边色
    protected int mOffDarkColor = 0xFFBFBFBF; // 关闭状态按钮描边色
    protected int mShadowColor = 0xFF333333; // 按钮阴影色
    private OnCheckedChangeListener mListener; // 监听器

    private float mRight;
    private float mCenterX, mCenterY;
    private float mScale;

    private float mOffset;
    private float mRadius, mStrokeWidth;
    private float mWidth;
    private float mLeft;
    private float bRight;
    private float mOnLeftX, mOn2LeftX, mOff2LeftX, mOffLeftX;

    private float mShadowReservedHeight;

    public SwitchButton(Context context) {
        super(context);
        initialize(null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        mChecked = array.getBoolean(R.styleable.SwitchButton_android_checked, mChecked);
        setEnabled(array.getBoolean(R.styleable.SwitchButton_android_enabled, isEnabled()));
        mLastCheckedState = mCheckedState = mChecked ? STATE_SWITCH_ON : STATE_SWITCH_OFF;

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics())
                        + getPaddingLeft() + getPaddingRight()), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * mAspectRatio)
                        + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        isCanVisibleDrawing = w > getPaddingLeft() + getPaddingRight() && h > getPaddingTop() + getPaddingBottom();

        if (isCanVisibleDrawing) {
            int actuallyDrawingAreaWidth = w - getPaddingLeft() - getPaddingRight();
            int actuallyDrawingAreaHeight = h - getPaddingTop() - getPaddingBottom();

            int actuallyDrawingAreaLeft;
            int actuallyDrawingAreaRight;
            int actuallyDrawingAreaTop;
            int actuallyDrawingAreaBottom;
            if (actuallyDrawingAreaWidth * mAspectRatio < actuallyDrawingAreaHeight) {
                actuallyDrawingAreaLeft = getPaddingLeft();
                actuallyDrawingAreaRight = w - getPaddingRight();
                int heightExtraSize = (int) (actuallyDrawingAreaHeight - actuallyDrawingAreaWidth * mAspectRatio);
                actuallyDrawingAreaTop = getPaddingTop() + heightExtraSize / 2;
                actuallyDrawingAreaBottom = getHeight() - getPaddingBottom() - heightExtraSize / 2;
            } else {
                int widthExtraSize = (int) (actuallyDrawingAreaWidth - actuallyDrawingAreaHeight / mAspectRatio);
                actuallyDrawingAreaLeft = getPaddingLeft() + widthExtraSize / 2;
                actuallyDrawingAreaRight = getWidth() - getPaddingRight() - widthExtraSize / 2;
                actuallyDrawingAreaTop = getPaddingTop();
                actuallyDrawingAreaBottom = getHeight() - getPaddingBottom();
            }

            mShadowReservedHeight = (int) ((actuallyDrawingAreaBottom - actuallyDrawingAreaTop) * 0.07f);
            float left = actuallyDrawingAreaLeft;
            float top = actuallyDrawingAreaTop + mShadowReservedHeight;
            mRight = actuallyDrawingAreaRight;
            float bottom = actuallyDrawingAreaBottom - mShadowReservedHeight;

            float sHeight = bottom - top;
            mCenterX = (mRight + left) / 2;
            mCenterY = (bottom + top) / 2;

            mLeft = left;
            mWidth = bottom - top;
            bRight = left + mWidth;
            final float halfHeightOfS = mWidth / 2; // OfB
            mRadius = halfHeightOfS * 0.95f;
            mOffset = mRadius * 0.2f; // offset of switching
            mStrokeWidth = (halfHeightOfS - mRadius) * 2;
            mOnLeftX = mRight - mWidth;
            mOn2LeftX = mOnLeftX - mOffset;
            mOffLeftX = left;
            mOff2LeftX = mOffLeftX + mOffset;
            mScale = 1 - mStrokeWidth / sHeight;

            mBackgroundPath.reset();
            RectF bound = new RectF();
            bound.top = top;
            bound.bottom = bottom;
            bound.left = left;
            bound.right = left + sHeight;
            mBackgroundPath.arcTo(bound, 90, 180);
            bound.left = mRight - sHeight;
            bound.right = mRight;
            mBackgroundPath.arcTo(bound, 270, 180);
            mBackgroundPath.close();

            mBound.left = mLeft;
            mBound.right = bRight;
            mBound.top = top + mStrokeWidth / 2;  // bTop = sTop
            mBound.bottom = bottom - mStrokeWidth / 2; // bBottom = sBottom
            float bCenterX = (bRight + mLeft) / 2;
            float bCenterY = (bottom + top) / 2;

            int red = mShadowColor >> 16 & 0xFF;
            int green = mShadowColor >> 8 & 0xFF;
            int blue = mShadowColor & 0xFF;
            mShadowGradient = new RadialGradient(bCenterX, bCenterY, mRadius, Color.argb(200, red, green, blue),
                    Color.argb(25, red, green, blue), Shader.TileMode.CLAMP);
        }
    }

    private void calcBPath(float percent) {
        mBarPath.reset();
        mBound.left = mLeft + mStrokeWidth / 2;
        mBound.right = bRight - mStrokeWidth / 2;
        mBarPath.arcTo(mBound, 90, 180);
        mBound.left = mLeft + percent * mOffset + mStrokeWidth / 2;
        mBound.right = bRight + percent * mOffset - mStrokeWidth / 2;
        mBarPath.arcTo(mBound, 270, 180);
        mBarPath.close();
    }

    private float calcBTranslate(float percent) {
        float result = 0;
        switch (mCheckedState - mLastCheckedState) {
            case 1:
                if (mCheckedState == STATE_SWITCH_OFF2) {
                    result = mOffLeftX; // off -> off2
                } else if (mCheckedState == STATE_SWITCH_ON) {
                    result = mOnLeftX - (mOnLeftX - mOn2LeftX) * percent; // on2 -> on
                }
                break;
            case 2:
                if (mCheckedState == STATE_SWITCH_ON) {
                    result = mOnLeftX - (mOnLeftX - mOffLeftX) * percent; // off2 -> on
                } else if (mCheckedState == STATE_SWITCH_ON2) {
                    result = mOn2LeftX - (mOn2LeftX - mOffLeftX) * percent;  // off -> on2
                }
                break;
            case 3:
                result = mOnLeftX - (mOnLeftX - mOffLeftX) * percent; // off -> on
                break;
            case -1:
                if (mCheckedState == STATE_SWITCH_ON2) {
                    result = mOn2LeftX + (mOnLeftX - mOn2LeftX) * percent; // on -> on2
                } else if (mCheckedState == STATE_SWITCH_OFF) {
                    result = mOffLeftX;  // off2 -> off
                }
                break;
            case -2:
                if (mCheckedState == STATE_SWITCH_OFF) {
                    result = mOffLeftX + (mOn2LeftX - mOffLeftX) * percent;  // on2 -> off
                } else if (mCheckedState == STATE_SWITCH_OFF2) {
                    result = mOff2LeftX + (mOnLeftX - mOff2LeftX) * percent;  // on -> off2
                }
                break;
            case -3:
                result = mOffLeftX + (mOnLeftX - mOffLeftX) * percent;  // on -> off
                break;
            default: // init
            case 0:
                if (mCheckedState == STATE_SWITCH_OFF) {
                    result = mOffLeftX; //  off -> off
                } else if (mCheckedState == STATE_SWITCH_ON) {
                    result = mOnLeftX; // on -> on
                }
                break;
        }
        return result - mOffLeftX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isCanVisibleDrawing) return;

        mPaint.setAntiAlias(true);

        final boolean isOn = (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_ON2);
        // Draw background
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(isOn ? mAccentColor : mOffColor);
        canvas.drawPath(mBackgroundPath, mPaint);

        mAnim1 = mAnim1 - mAnimationSpeed > 0 ? mAnim1 - mAnimationSpeed : 0;
        mAnim2 = mAnim2 - mAnimationSpeed > 0 ? mAnim2 - mAnimationSpeed : 0;

        final float dsAnim = mInterpolator.getInterpolation(mAnim1);
        final float dbAnim = mInterpolator.getInterpolation(mAnim2);
        // Draw background animation
        final float scale = mScale * (isOn ? dsAnim : 1 - dsAnim);
        final float scaleOffset = (mRight - mCenterX - mRadius) * (isOn ? 1 - dsAnim : dsAnim);
        canvas.save();
        canvas.scale(scale, scale, mCenterX + scaleOffset, mCenterY);
        mPaint.setColor(0xFFFFFFFF);
        canvas.drawPath(mBackgroundPath, mPaint);
        canvas.restore();
        // To prepare center bar path
        canvas.save();
        canvas.translate(calcBTranslate(dbAnim), mShadowReservedHeight);
        final boolean isState2 = (mCheckedState == STATE_SWITCH_ON2 || mCheckedState == STATE_SWITCH_OFF2);
        calcBPath(isState2 ? 1 - dbAnim : dbAnim);
        // Use center bar path to draw shadow
        if (isShadow) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(mShadowGradient);
            canvas.drawPath(mBarPath, mPaint);
            mPaint.setShader(null);
        }
        canvas.translate(0, -mShadowReservedHeight);
        // draw bar
        canvas.scale(0.98f, 0.98f, mWidth / 2, mWidth / 2);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xFFFFFFFF);
        canvas.drawPath(mBarPath, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth * 0.5f);
        mPaint.setColor(isOn ? mPrimaryDarkColor : mOffDarkColor);
        canvas.drawPath(mBarPath, mPaint);
        canvas.restore();

        mPaint.reset();
        if (mAnim1 > 0 || mAnim2 > 0) invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (isEnabled()
                && (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_OFF)
                && (mAnim1 * mAnim2 == 0)) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    mLastCheckedState = mCheckedState;
                    mAnim2 = 1;

                    switch (mCheckedState) {
                        case STATE_SWITCH_OFF:
                            setChecked(true, false);
                            if (mListener != null) {
                                mListener.onCheckedChanged(this, true);
                            }
                            break;
                        case STATE_SWITCH_ON:
                            setChecked(false, false);
                            if (mListener != null) {
                                mListener.onCheckedChanged(this, false);
                            }
                            break;
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.checked = mChecked;
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mChecked = savedState.checked;
        mCheckedState = mChecked ? STATE_SWITCH_ON : STATE_SWITCH_OFF;
        invalidate();
    }

    public void setColor(int newColorPrimary, int newColorPrimaryDark) {
        setColor(newColorPrimary, newColorPrimaryDark, mOffColor, mOffDarkColor);
    }

    public void setColor(int newColorPrimary, int newColorPrimaryDark, int newColorOff, int newColorOffDark) {
        setColor(newColorPrimary, newColorPrimaryDark, newColorOff, newColorOffDark, mShadowColor);
    }

    public void setColor(int newColorPrimary, int newColorPrimaryDark, int newColorOff, int newColorOffDark, int newColorShadow) {
        mAccentColor = newColorPrimary;
        mPrimaryDarkColor = newColorPrimaryDark;
        mOffColor = newColorOff;
        mOffDarkColor = newColorOffDark;
        mShadowColor = newColorShadow;
        invalidate();
    }

    /**
     * 设置按钮阴影开关
     */
    public void setShadow(boolean shadow) {
        isShadow = shadow;
        invalidate();
    }

    /**
     * 当前状态是否选中
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * 设置选择状态（默认会回调监听器）
     */
    public void setChecked(boolean checked) {
        setChecked(checked, true); // 回调监听器
    }

    /**
     * 设置选择状态
     */
    public void setChecked(boolean checked, boolean callback) {
        int newState = checked ? STATE_SWITCH_ON : STATE_SWITCH_OFF;
        if (newState == mCheckedState) {
            return;
        }
        if ((newState == STATE_SWITCH_ON && (mCheckedState == STATE_SWITCH_OFF || mCheckedState == STATE_SWITCH_OFF2))
                || (newState == STATE_SWITCH_OFF && (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_ON2))) {
            mAnim1 = 1;
        }
        mAnim2 = 1;

        if (!mChecked && newState == STATE_SWITCH_ON) {
            mChecked = true;
        } else if (mChecked && newState == STATE_SWITCH_OFF) {
            mChecked = false;
        }
        mLastCheckedState = mCheckedState;
        mCheckedState = newState;
        postInvalidate();

        if (callback && mListener != null) {
            mListener.onCheckedChanged(this, checked);
        }
    }

    /**
     * 设置选中状态改变监听
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        mListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(SwitchButton button, boolean isChecked);
    }

    /**
     * 保存开关状态
     */
    private static final class SavedState extends BaseSavedState {

        private boolean checked;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            checked = 1 == in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(checked ? 1 : 0);
        }

        // fixed by Night99 https://github.com/g19980115
        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}