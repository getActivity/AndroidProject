package com.hjq.widget.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/08/18
 *    desc   : 自动显示和隐藏的 TextView
 */
public final class SmartTextView extends AppCompatTextView {

    public SmartTextView(Context context) {
        this(context, null);
    }

    public SmartTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SmartTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshVisibilityStatus();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        refreshVisibilityStatus();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        refreshVisibilityStatus();
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        refreshVisibilityStatus();
    }

    /**
     * 刷新当前可见状态
     */
    private void refreshVisibilityStatus() {
        // 判断当前有没有设置文本达到自动隐藏和显示的效果
        if (isEmptyContent() && getVisibility() != GONE) {
            setVisibility(GONE);
            return;
        }

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
    }

    /**
     * TextView 内容是否为空
     */
    private boolean isEmptyContent() {
        if (!TextUtils.isEmpty(getText())) {
            return false;
        }
        Drawable[] compoundDrawables = getCompoundDrawables();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        for (Drawable drawable : compoundDrawables) {
            if (drawable != null) {
                return false;
            }
        }

        for (Drawable drawable : compoundDrawablesRelative) {
            if (drawable != null) {
                return false;
            }
        }
        return true;
    }
}