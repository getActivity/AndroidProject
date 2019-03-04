package com.hjq.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hjq.demo.R;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/01/23
 *    desc   : 设置条自定义控件
 */
public class SettingBar extends FrameLayout {

    private TextView mLeftView;
    private TextView mRightView;
    private View mLineView;

    public SettingBar(Context context) {
        this(context, null);
    }

    public SettingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_setting_bar, this);
        mLeftView = (TextView) findViewById(R.id.tv_setting_bar_left);
        mRightView = (TextView) findViewById(R.id.tv_setting_bar_right);
        mLineView  = (View) findViewById(R.id.v_setting_bar_line);

        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SettingBar);

        // 文本设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftText)) {
            setLeftText(array.getString(R.styleable.SettingBar_bar_leftText));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightText)) {
            setRightText(array.getString(R.styleable.SettingBar_bar_rightText));
        }

        // 提示设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftHint)) {
            setLeftHint(array.getString(R.styleable.SettingBar_bar_leftHint));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightHint)) {
            setRightHint(array.getString(R.styleable.SettingBar_bar_rightHint));
        }

        // 图标设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftIcon)) {
            setLeftIcon(getContext().getResources().getDrawable(array.getResourceId(R.styleable.SettingBar_bar_leftIcon, 0)));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightIcon)) {
            setRightIcon(getContext().getResources().getDrawable(array.getResourceId(R.styleable.SettingBar_bar_rightIcon, 0)));
        }

        // 文字颜色设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftColor)) {
            setLeftColor(array.getColor(R.styleable.SettingBar_bar_leftColor, 0));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightColor)) {
            setRightColor(array.getColor(R.styleable.SettingBar_bar_rightColor, 0));
        }

        // 文字大小设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftSize)) {
            setLeftSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SettingBar_bar_leftSize, 0));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightSize)) {
            setRightSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SettingBar_bar_rightSize, 0));
        }

        // 分割线设置
        if (array.hasValue(R.styleable.SettingBar_bar_lineColor)) {
            setLineDrawable(array.getDrawable(R.styleable.SettingBar_bar_lineColor));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_lineVisible)) {
            setLineVisible(array.getBoolean(R.styleable.SettingBar_bar_lineVisible, true));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_lineSize)) {
            setLineSize(array.getDimensionPixelSize(R.styleable.SettingBar_bar_lineSize, 0));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_lineMargin)) {
            setLineMargin(array.getDimensionPixelSize(R.styleable.SettingBar_bar_lineMargin, 0));
        }

        // 设置默认背景选择器
        if (getBackground() == null) {
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.selector_selectable_white);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(drawable);
            }else {
                setBackgroundDrawable(drawable);
            }
        }

        // 回收TypedArray
        array.recycle();
    }

    /**
     * 设置左边的标题
     */
    public void setLeftText(int stringId) {
        setLeftText(getResources().getString(stringId));
    }

    public void setLeftText(CharSequence text) {
        mLeftView.setText(text);
    }

    /**
     * 设置左边的提示
     */
    public void setLeftHint(int stringId) {
        setLeftHint(getResources().getString(stringId));
    }

    public void setLeftHint(CharSequence hint) {
        mLeftView.setHint(hint);
    }

    /**
     * 设置右边的标题
     */
    public void setRightText(int stringId) {
        setRightText(getResources().getString(stringId));
    }

    public void setRightText(CharSequence text) {
        mRightView.setText(text);
    }

    /**
     * 设置右边的提示
     */
    public void setRightHint(int stringId) {
        setRightHint(getResources().getString(stringId));
    }

    public void setRightHint(CharSequence hint) {
        mRightView.setHint(hint);
    }

    /**
     * 设置左边的图标
     */
    public void setLeftIcon(int iconId) {
        if (iconId > 0) {
            setLeftIcon(getContext().getResources().getDrawable(iconId));
        }
    }

    public void setLeftIcon(Drawable drawable) {
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    /**
     * 设置右边的图标
     */
    public void setRightIcon(int iconId) {
        if (iconId > 0) {
            setRightIcon(getContext().getResources().getDrawable(iconId));
        }
    }

    public void setRightIcon(Drawable drawable) {
        mRightView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    /**
     * 设置左标题颜色
     */
    public void setLeftColor(int color) {
        mLeftView.setTextColor(color);
    }

    /**
     * 设置右标题颜色
     */
    public void setRightColor(int color) {
        mRightView.setTextColor(color);
    }

    /**
     * 设置左标题的文本大小
     */
    public void setLeftSize(int unit, float size) {
        mLeftView.setTextSize(unit, size);
    }

    /**
     * 设置右标题的文本大小
     */
    public void setRightSize(int unit, float size) {
        mRightView.setTextSize(unit, size);
    }

    /**
     * 设置分割线是否显示
     */
    public void setLineVisible(boolean visible) {
        mLineView.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * 设置分割线的颜色
     */
    public void setLineColor(int color) {
        setLineDrawable(new ColorDrawable(color));
    }
    public void setLineDrawable(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mLineView.setBackground(drawable);
        }else {
            mLineView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 设置分割线的大小
     */
    public void setLineSize(int size) {
        ViewGroup.LayoutParams layoutParams = mLineView.getLayoutParams();
        layoutParams.height = size;
        mLineView.setLayoutParams(layoutParams);
    }

    /**
     * 设置分割线边界
     */
    public void setLineMargin(int margin) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLineView.getLayoutParams();
        params.leftMargin = margin;
        params.rightMargin = margin;
        mLineView.setLayoutParams(params);
    }

    /**
     * 获取左标题View对象
     */
    public TextView getLeftView() {
        return mLeftView;
    }

    /**
     * 获取右标题View对象
     */
    public TextView getRightView() {
        return mRightView;
    }

    /**
     * 获取分割线View对象
     */
    public View getLineView() {
        return mLineView;
    }
}