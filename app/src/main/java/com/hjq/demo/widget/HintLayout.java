package com.hjq.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.hjq.demo.R;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/18
 *    desc   : 状态布局（网络错误，异常错误，空数据）
 */
public final class HintLayout extends FrameLayout {

    /** 提示布局 */
    private ViewGroup mMainLayout;
    /** 提示图标 */
    private LottieAnimationView mImageView;
    /** 提示文本 */
    private TextView mTextView;

    public HintLayout(@NonNull Context context) {
        this(context, null);
    }

    public HintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HintLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
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
    public void setIcon(@DrawableRes int id) {
        setIcon(ContextCompat.getDrawable(getContext(), id));
    }

    public void setIcon(Drawable drawable) {
        if (mImageView != null) {
            mImageView.setImageDrawable(drawable);
        }
    }

    /**
     * 设置提示动画
     */
    public void setAnim(@RawRes int id) {
        mImageView.setAnimation(id);
        // 这里需要调用播放动画，否则会出现第一次显示动画效果正常，第二次显示动画会不动
        mImageView.playAnimation();
    }

    /**
     * 设置提示文本，请在show方法之后调用
     */
    public void setHint(@StringRes int id) {
        setHint(getResources().getString(id));
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

        mMainLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.widget_hint_layout, this, false);

        mImageView = mMainLayout.findViewById(R.id.iv_hint_icon);
        mTextView = mMainLayout.findViewById(R.id.iv_hint_text);

        if (mMainLayout.getBackground() == null) {
            // 默认使用 windowBackground 作为背景
            TypedArray ta = getContext().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
            mMainLayout.setBackground(ta.getDrawable(0));
            ta.recycle();
        }

        addView(mMainLayout);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (isShow()) {
            mMainLayout.setOnClickListener(l);
        } else {
            super.setOnClickListener(l);
        }
    }
}