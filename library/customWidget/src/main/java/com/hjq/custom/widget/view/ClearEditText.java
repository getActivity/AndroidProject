package com.hjq.custom.widget.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.hjq.custom.widget.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 带清除按钮的 EditText
 */
public final class ClearEditText extends RegexEditText
        implements View.OnTouchListener,
        View.OnFocusChangeListener, TextWatcher {

    @NonNull
    private final Drawable mClearDrawable;

    @Nullable
    private OnTouchListener mTouchListener;
    @Nullable
    private OnFocusChangeListener mFocusChangeListener;

    public ClearEditText(@NonNull Context context) {
        this(context, null);
    }

    public ClearEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    @SuppressWarnings("all")
    public ClearEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mClearDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.input_delete_ic));
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setDrawableVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        super.addTextChangedListener(this);

        // 适配 RTL 特性
        if (getTextAlignment() == TEXT_ALIGNMENT_GRAVITY) {
            setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        }
    }

    private void setDrawableVisible(boolean visible) {
        if (mClearDrawable.isVisible() == visible) {
            return;
        }

        mClearDrawable.setVisible(visible, false);
        Drawable[] drawables = getCompoundDrawablesRelative();
        setCompoundDrawablesRelative(
                drawables[0],
                drawables[1],
                visible ? mClearDrawable : null,
                drawables[3]);
    }

    @Override
    public void setOnFocusChangeListener(@Nullable OnFocusChangeListener listener) {
        mFocusChangeListener = listener;
    }

    @Override
    public void setOnTouchListener(@Nullable OnTouchListener listener) {
        mTouchListener = listener;
    }

    /**
     * {@link OnFocusChangeListener}
     */

    @Override
    public void onFocusChange(@NonNull View view, boolean hasFocus) {
        setDrawableVisible(hasFocus && !TextUtils.isEmpty(getText()));
        if (mFocusChangeListener != null) {
            mFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    /**
     * {@link OnTouchListener}
     */

    @Override
    public boolean onTouch(@NonNull View view, @NonNull MotionEvent event) {
        int x = (int) event.getX();
        // 是否触摸了 Drawable
        final boolean touchDrawable;
        // 适配 RTL 特性
        if (getResources().getConfiguration().getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
            touchDrawable = x > getPaddingStart() && x < getPaddingStart() + mClearDrawable.getIntrinsicWidth();
        } else {
            touchDrawable = x > getWidth() - mClearDrawable.getIntrinsicWidth() - getPaddingEnd() &&
                x < getWidth() - getPaddingEnd();
        }

        if (mClearDrawable.isVisible() && touchDrawable) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                setText("");
            }
            return true;
        }
        return mTouchListener != null && mTouchListener.onTouch(view, event);
    }

    /**
     * {@link TextWatcher}
     */

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setDrawableVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
        // default implementation ignored
    }

    @Override
    public void afterTextChanged(@NonNull Editable s) {
        // default implementation ignored
    }
}