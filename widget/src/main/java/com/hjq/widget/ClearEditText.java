package com.hjq.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 带清除按钮的EditText
 */
@SuppressLint("AppCompatCustomView")
public class ClearEditText extends EditText
        implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private Drawable mClearIcon;

    private OnTouchListener mOnTouchListener;
    private OnFocusChangeListener mOnFocusChangeListener;

    public ClearEditText(Context context) {
        super(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ClearEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    private void initialize(final Context context) {

        final Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.widget_input_delete);

        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //Wrap the drawable so that it can be tinted pre Lollipop
        //DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        mClearIcon = wrappedDrawable;
        mClearIcon.setBounds(0, 0, mClearIcon.getIntrinsicWidth(), mClearIcon.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        super.addTextChangedListener(this);

        ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(0x99000000));
    }

    private void setClearIconVisible(final boolean visible) {
        if (mClearIcon.isVisible() == visible) return;

        mClearIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearIcon : null,
                compoundDrawables[3]);
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    /**
     * {@link View.OnFocusChangeListener}
     */

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        if (hasFocus && getText() != null) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    /**
     * {@link View.OnTouchListener}
     */

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (mClearIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setText("");
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    /**
     * {@link TextWatcher}
     */

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

//    @Override
//    protected void drawableStateChanged() {
//        super.drawableStateChanged();
//    }
}