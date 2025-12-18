package com.hjq.custom.widget.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
 *    time   : 2019/08/25
 *    desc   : 密码隐藏显示 EditText
 */
public final class PasswordEditText extends RegexEditText
        implements View.OnTouchListener,
        View.OnFocusChangeListener, TextWatcher {

    private Drawable mCurrentDrawable;
    private final Drawable mVisibleDrawable;
    private final Drawable mInvisibleDrawable;

    @Nullable
    private OnTouchListener mTouchListener;
    @Nullable
    private OnFocusChangeListener mFocusChangeListener;

    public PasswordEditText(@NonNull Context context) {
        this(context, null);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    @SuppressWarnings("all")
    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mVisibleDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.password_off_ic));
        mVisibleDrawable.setBounds(0, 0, mVisibleDrawable.getIntrinsicWidth(), mVisibleDrawable.getIntrinsicHeight());

        mInvisibleDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.password_on_ic));
        mInvisibleDrawable.setBounds(0, 0, mInvisibleDrawable.getIntrinsicWidth(), mInvisibleDrawable.getIntrinsicHeight());

        mCurrentDrawable = mVisibleDrawable;

        if (getInputRegex() == null) {
            // 密码输入规则
            setInputRegex(REGEX_PASSWORD);
        }

        setDrawableVisible(false);
        setPasswordMode(true);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        super.addTextChangedListener(this);

        // 适配 RTL 特性
        if (getTextAlignment() == TEXT_ALIGNMENT_GRAVITY) {
            setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        }
    }

    private void setDrawableVisible(boolean visible) {
        if (mCurrentDrawable.isVisible() == visible) {
            return;
        }

        mCurrentDrawable.setVisible(visible, false);
        Drawable[] drawables = getCompoundDrawablesRelative();
        setCompoundDrawablesRelative(
                drawables[0],
                drawables[1],
                visible ? mCurrentDrawable : null,
                drawables[3]);
    }

    private void setPasswordMode(boolean passwordMode) {
        if (passwordMode) {
            // 密码不可见
            setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            // 密码可见
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }

    private void refreshDrawableStatus() {
        Drawable[] drawables = getCompoundDrawablesRelative();
        setCompoundDrawablesRelative(
                drawables[0],
                drawables[1],
                mCurrentDrawable,
                drawables[3]);
    }

    @Override
    public void setOnFocusChangeListener(@Nullable OnFocusChangeListener onFocusChangeListener) {
        mFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(@Nullable OnTouchListener onTouchListener) {
        mTouchListener = onTouchListener;
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
            touchDrawable = x > getPaddingStart() && x < getPaddingStart() + mCurrentDrawable.getIntrinsicWidth();
        } else {
            touchDrawable = x > getWidth() - mCurrentDrawable.getIntrinsicWidth() - getPaddingEnd() && x < getWidth() - getPaddingEnd();
        }

        if (mCurrentDrawable.isVisible() && touchDrawable) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mCurrentDrawable == mVisibleDrawable) {
                    mCurrentDrawable = mInvisibleDrawable;
                    setPasswordMode(false);
                    refreshDrawableStatus();
                } else if (mCurrentDrawable == mInvisibleDrawable) {
                    mCurrentDrawable = mVisibleDrawable;
                    setPasswordMode(true);
                    refreshDrawableStatus();
                }
                Editable editable = getText();
                if (editable != null) {
                    setSelection(editable.toString().length());
                }
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