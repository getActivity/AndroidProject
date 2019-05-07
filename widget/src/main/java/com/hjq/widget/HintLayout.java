package com.hjq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/18
 *    desc   : 状态布局（网络错误，异常错误，空数据）
 */
public final class HintLayout extends SimpleLayout {

    //提示布局
    private ViewGroup mMainLayout;
    //提示图标
    private ImageView mImageView;
    //提示文本
    private TextView mTextView;

    public HintLayout(@NonNull Context context) {
        super(context);
    }

    public HintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HintLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 显示
     */
    public void show() {

        if (mMainLayout == null) {
            //初始化布局
            initLayout();
        }

        if (!isShow()) {
            // 显示布局
            mMainLayout.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏
     */
    public void hide() {

        if (mMainLayout != null && isShow()) {
            //隐藏布局
            mMainLayout.setVisibility(INVISIBLE);
        }
    }

    /**
     * 是否显示了
     */
    public boolean isShow() {
        return mMainLayout != null && mMainLayout.getVisibility() == VISIBLE;
    }

    /**
     * 设置提示图标，请在show方法之后调用
     */
    public void setIcon(@DrawableRes int iconId) {
        setIcon(getResources().getDrawable(iconId));
    }

    public void setIcon(Drawable drawable) {
        if (mImageView != null) {
            mImageView.setImageDrawable(drawable);
        }
    }

    /**
     * 设置提示文本，请在show方法之后调用
     */
    public void setHint(@StringRes int textId) {
        setHint(getResources().getString(textId));
    }

    public void setHint(CharSequence text) {
        if (mTextView != null && text != null) {
            mTextView.setText(text);
        }
    }

    /**
     * 初始化提示的布局
     */
    private void initLayout() {

        mMainLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.widget_hint_layout, null);

        mImageView = mMainLayout.findViewById(R.id.iv_hint_icon);
        mTextView = mMainLayout.findViewById(R.id.iv_hint_text);

        if (getBackground() == null) {
            // 默认使用 windowBackground 作为背景
            TypedArray ta = getContext().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
            setBackground(ta.getDrawable(0));
            ta.recycle();
        }

        addView(mMainLayout);
    }

    @Override
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackground(background);
            mMainLayout.setBackground(background);
        } else {
            setBackgroundDrawable(background);
            mMainLayout.setBackgroundDrawable(background);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isShow()) {
            // 拦截布局中的触摸事件，拦截事件，防止传递
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}