package com.hjq.custom.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.custom.widget.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 简单的 Layout 容器，比 FrameLayout 更加轻量
 *             可以用于自定义组合控件继承的基类，可以起到性能优化的作用
 *             另外还支持限制最大宽高和最小宽高
 */
public class SimpleLayout extends ViewGroup {

    /** 布局最大宽度 */
    private int mMaxWidth;
    /** 布局最大高度 */
    private int mMaxHeight;

    public SimpleLayout(@NonNull Context context) {
        this(context, null);
    }

    public SimpleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SimpleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleLayout);
        mMaxWidth = array.getDimensionPixelSize(R.styleable.SimpleLayout_android_maxWidth, 0);
        mMaxHeight = array.getDimensionPixelSize(R.styleable.SimpleLayout_android_maxHeight, 0);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int viewMaxHeight = 0;
        int viewMaxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {

            final View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) {
                // 不测量隐藏的控件，因为没有任何意义
                continue;
            }

            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int measuredWidth = childView.getMeasuredWidth();
            int measuredHeight = childView.getMeasuredHeight();
            if ((mMaxWidth > 0 && measuredWidth > mMaxWidth) ||
                    (mMaxHeight > 0 && measuredHeight > mMaxHeight)) {
                int childWidthMeasureSpec = widthMeasureSpec;
                int childHeightMeasureSpec = heightMeasureSpec;
                if (mMaxWidth > 0 && measuredWidth > mMaxWidth) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
                }
                if (mMaxHeight > 0 && measuredHeight > mMaxHeight) {
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY);
                }
                // 如果测量出来的控件大小已经超过了布局自身的大小，那么就进行二次测量
                measureChildWithMargins(childView, childWidthMeasureSpec, 0, childHeightMeasureSpec, 0);
            }

            final MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            viewMaxWidth = Math.max(viewMaxWidth, childView.getMeasuredWidth() + params.leftMargin + params.rightMargin);
            viewMaxHeight = Math.max(viewMaxHeight, childView.getMeasuredHeight() + params.topMargin + params.bottomMargin);
            childState = combineMeasuredStates(childState, childView.getMeasuredState());
        }

        viewMaxWidth += (getPaddingLeft() + getPaddingRight());
        viewMaxHeight += (getPaddingTop() + getPaddingBottom());

        viewMaxWidth = Math.max(viewMaxWidth, getSuggestedMinimumWidth());
        viewMaxHeight = Math.max(viewMaxHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(resolveSizeAndState(viewMaxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(viewMaxHeight, heightMeasureSpec,
                childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 遍历子 View
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View childView = getChildAt(i);
            final MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
            int left = params.leftMargin + getPaddingLeft();
            int top = params.topMargin + getPaddingTop();
            int right = left + childView.getMeasuredWidth();
            int bottom = top + childView.getMeasuredHeight();
            // 将子 View 放置到左上角的位置
            childView.layout(left, top, right, bottom);
        }
    }

    @Nullable
    @Override
    public LayoutParams generateLayoutParams(@Nullable AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Nullable
    @Override
    protected LayoutParams generateLayoutParams(@NonNull LayoutParams params) {
        return new MarginLayoutParams(params);
    }

    @NonNull
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected boolean checkLayoutParams(@Nullable LayoutParams params) {
        return params instanceof MarginLayoutParams;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public void setMaxWidth(int width) {
        mMaxWidth = width;
        requestLayout();
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;
        requestLayout();
    }
}