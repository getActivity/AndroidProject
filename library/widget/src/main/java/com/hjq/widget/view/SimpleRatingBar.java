package com.hjq.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.hjq.widget.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/07/11
 *    desc   : 自定义评分控件（系统的 RatingBar 不好用）
 */
public final class SimpleRatingBar extends View {

    /** 默认的星星图标 */
    @Nullable
    private Drawable mNormalDrawable;
    /** 选中的星星图标 */
    @Nullable
    private Drawable mFillDrawable;
    /** 选中的半星图标 */
    @Nullable
    private Drawable mHalfDrawable;

    /** 当前星等级 */
    private float mCurrentGrade;
    /** 星星总数量 */
    private int mGradeCount;
    /** 星星的宽度 */
    private int mGradeWidth;
    /** 星星的高度 */
    private int mGradeHeight;
    /** 星星之间的间隔 */
    private int mGradeSpace;

    /** 星星选择跨度 */
    @Nullable
    private GradleStep mGradeStep;

    /** 星星位置记录 */
    @NonNull
    private final Rect mGradeBounds = new Rect();

    /** 星星变化监听事件 */
    @Nullable
    private OnRatingChangeListener mListener;


    public SimpleRatingBar(@NonNull Context context) {
        this(context, null);
    }

    public SimpleRatingBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleRatingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleRatingBar);

        setRatingDrawable(ContextCompat.getDrawable(getContext(), array.getResourceId(R.styleable.SimpleRatingBar_normalDrawable, R.drawable.rating_star_off_ic)),
                ContextCompat.getDrawable(getContext(), array.getResourceId(R.styleable.SimpleRatingBar_halfDrawable, R.drawable.rating_star_half_ic)),
                ContextCompat.getDrawable(getContext(), array.getResourceId(R.styleable.SimpleRatingBar_fillDrawable, R.drawable.rating_star_fill_ic)));

        setGradeCount(array.getInt(R.styleable.SimpleRatingBar_gradeCount, 5));
        setGradeSpace((int) array.getDimension(R.styleable.SimpleRatingBar_gradeSpace, mGradeWidth / 4f));
        setGradeWidth(array.getDimensionPixelSize(R.styleable.SimpleRatingBar_gradeWidth, mNormalDrawable.getIntrinsicWidth()));
        setGradeHeight(array.getDimensionPixelSize(R.styleable.SimpleRatingBar_gradeHeight, mNormalDrawable.getIntrinsicHeight()));
        switch (array.getInt(R.styleable.SimpleRatingBar_gradeStep, 0)) {
            case 0x01:
                setGradeStep(GradleStep.ONE);
                break;
            case 0x00:
            default:
                setGradeStep(GradleStep.HALF);
                break;
        }
        setGrade(array.getFloat(R.styleable.SimpleRatingBar_grade, 0));

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = (mGradeWidth * mGradeCount) + (mGradeSpace * (mGradeCount + 1));
        int measuredHeight = mGradeHeight;

        setMeasuredDimension(measuredWidth + getPaddingLeft() + getPaddingRight(),
                measuredHeight + getPaddingTop() + getPaddingBottom());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // 如果控件处于不可用状态，直接不处理
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float grade = 0;
                float distance = event.getX() - getPaddingLeft() - mGradeSpace;
                if (distance > 0) {
                    grade = distance / (mGradeWidth + mGradeSpace);
                }

                grade = Math.min(Math.max(grade, 0), mGradeCount);

                if (grade - (int) grade > 0) {
                    if (grade - (int) grade > 0.5f) {
                        // 0.5 - 1 算一颗星
                        grade = (int) (grade + 0.5f);
                    } else {
                        // 0 - 0.5 算半颗星
                        grade = (int) grade + 0.5f;
                    }
                }

                if (grade * 10 != mCurrentGrade * 10) {
                    mCurrentGrade = grade;
                    optimizationGradeValue();
                    invalidate();
                    if (mListener != null) {
                        mListener.onRatingChanged(this, mCurrentGrade, true);
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        for (int i = 0; i < mGradeCount; i++) {

            int start = mGradeSpace + (mGradeWidth + mGradeSpace) * i;

            mGradeBounds.left = getPaddingLeft() + start;
            mGradeBounds.top = getPaddingTop();
            mGradeBounds.right = mGradeBounds.left + mGradeWidth;
            mGradeBounds.bottom = mGradeBounds.top + mGradeHeight;

            if (mCurrentGrade > i) {
                if (mHalfDrawable != null && mGradeStep == GradleStep.HALF &&
                        (int) mCurrentGrade == i && mCurrentGrade - (float) (int) mCurrentGrade == 0.5f) {
                    mHalfDrawable.setBounds(mGradeBounds);
                    mHalfDrawable.draw(canvas);
                } else {
                    mFillDrawable.setBounds(mGradeBounds);
                    mFillDrawable.draw(canvas);
                }
            } else {
                mNormalDrawable.setBounds(mGradeBounds);
                mNormalDrawable.draw(canvas);
            }
        }
    }

    public void setRatingDrawable(@DrawableRes int normalDrawableId, @DrawableRes int halfDrawableId, @DrawableRes int fillDrawableId) {
        setRatingDrawable(ContextCompat.getDrawable(getContext(), normalDrawableId),
                ContextCompat.getDrawable(getContext(), halfDrawableId),
                ContextCompat.getDrawable(getContext(), fillDrawableId));
    }

    public void setRatingDrawable(Drawable normalDrawable, Drawable halfDrawable, Drawable fillDrawable) {
        if (normalDrawable == null || fillDrawable == null) {
            throw new IllegalStateException("Drawable cannot be empty");
        }

        // 两张图片的宽高不一致
        if (normalDrawable.getIntrinsicWidth() != fillDrawable.getIntrinsicWidth() ||
                normalDrawable.getIntrinsicHeight() != fillDrawable.getIntrinsicHeight()) {
            throw new IllegalStateException("The width and height of the picture do not agree");
        }

        if (halfDrawable != null) {
            if (normalDrawable.getIntrinsicWidth() != halfDrawable.getIntrinsicWidth() ||
                    normalDrawable.getIntrinsicHeight() != halfDrawable.getIntrinsicHeight()) {
                throw new IllegalStateException("The width and height of the picture do not agree");
            }
        }

        if (mNormalDrawable != null) {
            if (mGradeWidth == mNormalDrawable.getIntrinsicWidth()) {
                mGradeWidth = 0;
            }
            if (mGradeHeight == mNormalDrawable.getIntrinsicHeight()) {
                mGradeHeight = 0;
            }
        }

        mNormalDrawable = normalDrawable;
        mHalfDrawable = halfDrawable;
        mFillDrawable = fillDrawable;
        if (mGradeWidth == 0) {
            mGradeWidth = mNormalDrawable.getIntrinsicWidth();
        }
        if (mGradeHeight == 0) {
            mGradeHeight = mNormalDrawable.getIntrinsicHeight();
        }
        requestLayout();
    }

    public float getGrade() {
        return mCurrentGrade;
    }

    public void setGrade(float grade) {
        mCurrentGrade = grade;
        if (mCurrentGrade > mGradeCount) {
            mCurrentGrade = mGradeCount;
        }
        optimizationGradeValue();
        invalidate();
        if (mListener != null) {
            mListener.onRatingChanged(this, mCurrentGrade, false);
        }
    }

    public int getGradeCount() {
        return mGradeCount;
    }

    public void setGradeCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("grade count cannot be less than or equal to 0");
        }
        if (count > mCurrentGrade) {
            mCurrentGrade = count;
        }
        mGradeCount = count;
        invalidate();
    }

    public int getGradeWidth() {
        return mGradeWidth;
    }

    public void setGradeWidth(int width) {
        mGradeWidth = width;
        requestLayout();
    }

    public int getGradeHeight() {
        return mGradeHeight;
    }

    public void setGradeHeight(int height) {
        mGradeHeight = height;
        requestLayout();
    }

    public void setGradeSpace(int space) {
        mGradeSpace = space;
        requestLayout();
    }

    public void setGradeStep(@NonNull GradleStep step) {
        mGradeStep = step;
        optimizationGradeValue();
        invalidate();
    }

    @Nullable
    public GradleStep getGradeStep() {
        return mGradeStep;
    }

    public void setOnRatingBarChangeListener(@Nullable OnRatingChangeListener listener) {
        mListener = listener;
    }

    private void optimizationGradeValue() {
        if (mCurrentGrade - (float) (int) mCurrentGrade == 0f) {
            return;
        }
        switch (mGradeStep) {
            case HALF:
                if (mCurrentGrade - (float) (int) mCurrentGrade > 0.5f) {
                    mCurrentGrade = Math.round(mCurrentGrade);
                } else if (mCurrentGrade - (float) (int) mCurrentGrade != 0.5f) {
                    mCurrentGrade += 0.5f;
                }
                break;
            case ONE:
            default:
                mCurrentGrade = Math.round(mCurrentGrade);
                break;
        }
    }

    public enum GradleStep {

        /** 半颗星 */
        HALF,
        /** 一颗星 */
        ONE
    }

    public interface OnRatingChangeListener {

        /**
         * 评分发生变化监听时回调
         *
         * @param grade             当前星星数
         * @param touch             是否通过触摸改变
         */
        void onRatingChanged(@NonNull SimpleRatingBar ratingBar, float grade, boolean touch);
    }
}