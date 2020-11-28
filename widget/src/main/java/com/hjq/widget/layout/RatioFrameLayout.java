package com.hjq.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.widget.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/08/23
 *    desc   : 按照比例显示的 FrameLayout
 */
public final class RatioFrameLayout extends FrameLayout {

    /** 宽高比 */
    private final float mSizeRatio;

    public RatioFrameLayout(Context context) {
        this(context, null);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioFrameLayout);
        mSizeRatio = array.getFloat(R.styleable.RatioFrameLayout_sizeRatio, 0);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSizeRatio != 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // 如果当前宽度和高度都是写死的
                if (widthSpecSize / mSizeRatio <= heightSpecSize) {
                    // 如果宽度经过比例换算不超过原有的高度
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSpecSize / mSizeRatio), MeasureSpec.EXACTLY);
                } else if (heightSpecSize * mSizeRatio <= widthSpecSize) {
                    // 如果高度经过比例换算不超过原有的宽度
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (heightSpecSize * mSizeRatio), MeasureSpec.EXACTLY);
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // 如果当前宽度是写死的，但是高度不写死
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSpecSize / mSizeRatio), MeasureSpec.EXACTLY);
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // 如果当前高度是写死的，但是宽度不写死
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (heightSpecSize * mSizeRatio), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}