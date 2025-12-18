package com.hjq.custom.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.hjq.custom.widget.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/04/18
 *    desc   : 支持限定 Drawable 大小的 TextView
 */
public final class DrawableTextView extends AppCompatTextView {

    private static final int DRAWABLE_INDEX_LEFT = 0;
    private static final int DRAWABLE_INDEX_TOP = 1;
    private static final int DRAWABLE_INDEX_RIGHT = 2;
    private static final int DRAWABLE_INDEX_BOTTOM = 3;

    private int mDrawableWidth;
    private int mDrawableHeight;

    public DrawableTextView(@NonNull Context context) {
        this(context, null);
    }

    public DrawableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        mDrawableWidth = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableWidth, 0);
        mDrawableHeight = array.getDimensionPixelSize(R.styleable.DrawableTextView_drawableHeight, 0);
        array.recycle();

        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 大小
     */
    public void setDrawableSize(int width, int height) {
        mDrawableWidth = width;
        mDrawableHeight = height;
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 宽度
     */
    public void setDrawableWidth(int width) {
        mDrawableWidth = width;
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 高度
     */
    public void setDrawableHeight(int height) {
        mDrawableHeight = height;
        refreshDrawablesSize();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        refreshDrawablesSize();
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        refreshDrawablesSize();
    }

    /**
     * 刷新 Drawable 列表大小
     */
    private void refreshDrawablesSize() {
        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;
        }
        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();

        // 适配 RTL 特性
        int layoutDirection = getResources().getConfiguration().getLayoutDirection();

        Drawable leftDrawable = compoundDrawablesRelative[layoutDirection == LAYOUT_DIRECTION_LTR ?
                DRAWABLE_INDEX_LEFT : DRAWABLE_INDEX_RIGHT];
        if (leftDrawable == null) {
            leftDrawable = compoundDrawables[DRAWABLE_INDEX_LEFT];
        }

        Drawable topDrawable = compoundDrawablesRelative[DRAWABLE_INDEX_TOP];
        if (topDrawable == null) {
            topDrawable = compoundDrawables[DRAWABLE_INDEX_TOP];
        }

        Drawable rightDrawable = compoundDrawablesRelative[layoutDirection == LAYOUT_DIRECTION_LTR ?
                DRAWABLE_INDEX_RIGHT : DRAWABLE_INDEX_LEFT];
        if (rightDrawable == null) {
            rightDrawable = compoundDrawables[DRAWABLE_INDEX_RIGHT];
        }

        Drawable bottomDrawable = compoundDrawablesRelative[DRAWABLE_INDEX_BOTTOM];
        if (bottomDrawable == null) {
            bottomDrawable = compoundDrawables[DRAWABLE_INDEX_BOTTOM];
        }

        Drawable[] newDrawable = new Drawable[4];
        newDrawable[DRAWABLE_INDEX_LEFT] = limitDrawableSize(leftDrawable);
        newDrawable[DRAWABLE_INDEX_TOP] = limitDrawableSize(topDrawable);
        newDrawable[DRAWABLE_INDEX_RIGHT] = limitDrawableSize(rightDrawable);
        newDrawable[DRAWABLE_INDEX_BOTTOM] = limitDrawableSize(bottomDrawable);

        super.setCompoundDrawables(
                newDrawable[DRAWABLE_INDEX_LEFT], newDrawable[DRAWABLE_INDEX_TOP],
                newDrawable[DRAWABLE_INDEX_RIGHT], newDrawable[DRAWABLE_INDEX_BOTTOM]);
    }

    /**
     * 重新限定 Drawable 宽高
     */
    private Drawable limitDrawableSize(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return drawable;
        }
        drawable.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
        return drawable;
    }
}