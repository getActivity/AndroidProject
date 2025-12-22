package com.hjq.custom.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.custom.widget.R;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/01/23
 *    desc   : 设置条自定义控件
 */
public final class SettingBar extends FrameLayout {

    /** 无色值 */
    public static final int NO_COLOR = Color.TRANSPARENT;

    @NonNull
    private final LinearLayout mMainLayout;

    @NonNull
    private final TextView mStartView;

    @NonNull
    private final TextView mEndView;

    @NonNull
    private final View mLineView;

    /** 图标着色器 */
    private int mStartDrawableTint, mEndDrawableTint;

    /** 图标显示大小 */
    private int mStartDrawableSize, mEndDrawableSize;

    public SettingBar(@NonNull Context context) {
        this(context, null);
    }

    public SettingBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SettingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mMainLayout = new LinearLayout(getContext());
        mStartView = new TextView(getContext());
        mEndView = new TextView(getContext());
        mLineView  = new View(getContext());

        mMainLayout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));

        LinearLayout.LayoutParams startLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
        startLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        startLayoutParams.weight = 1;
        mStartView.setLayoutParams(startLayoutParams);

        LinearLayout.LayoutParams endLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        endLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        mEndView.setLayoutParams(endLayoutParams);

        mLineView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1, Gravity.BOTTOM));

        mStartView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        mEndView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        mStartView.setSingleLine(true);
        mEndView.setSingleLine(true);

        mStartView.setEllipsize(TextUtils.TruncateAt.END);
        mEndView.setEllipsize(TextUtils.TruncateAt.END);

        mStartView.setLineSpacing(SmallestWidthAdaptation.dp2px(context, 5), mStartView.getLineSpacingMultiplier());
        mEndView.setLineSpacing(SmallestWidthAdaptation.dp2px(context, 5), mEndView.getLineSpacingMultiplier());

        mStartView.setPaddingRelative((int) SmallestWidthAdaptation.dp2px(context, 15),
                (int) SmallestWidthAdaptation.dp2px(context, 12),
                (int) SmallestWidthAdaptation.dp2px(context, 15),
                (int) SmallestWidthAdaptation.dp2px(context, 12));
        mEndView.setPaddingRelative((int) SmallestWidthAdaptation.dp2px(context, 15),
                (int) SmallestWidthAdaptation.dp2px(context, 12),
                (int) SmallestWidthAdaptation.dp2px(context, 15),
                (int) SmallestWidthAdaptation.dp2px(context, 12));

        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SettingBar);

        // 文本设置
        if (array.hasValue(R.styleable.SettingBar_bar_startText)) {
            setStartText(array.getString(R.styleable.SettingBar_bar_startText));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_endText)) {
            setEndText(array.getString(R.styleable.SettingBar_bar_endText));
        }

        // 提示设置
        if (array.hasValue(R.styleable.SettingBar_bar_startTextHint)) {
            setStartTextHint(array.getString(R.styleable.SettingBar_bar_startTextHint));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_endTextHint)) {
            setEndTextHint(array.getString(R.styleable.SettingBar_bar_endTextHint));
        }

        // 图标显示的大小
        if (array.hasValue(R.styleable.SettingBar_bar_startDrawableSize)) {
            setStartDrawableSize(array.getDimensionPixelSize(R.styleable.SettingBar_bar_startDrawableSize, 0));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_endDrawableSize)) {
            setEndDrawableSize(array.getDimensionPixelSize(R.styleable.SettingBar_bar_endDrawableSize, 0));
        }

        // 图标着色器
        if (array.hasValue(R.styleable.SettingBar_bar_startDrawableTint)) {
            setStartDrawableTint(array.getColor(R.styleable.SettingBar_bar_startDrawableTint, NO_COLOR));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_endDrawableTint)) {
            setEndDrawableTint(array.getColor(R.styleable.SettingBar_bar_endDrawableTint, NO_COLOR));
        }

        // 图标和文字之间的间距
        setStartDrawablePadding(array.hasValue(R.styleable.SettingBar_bar_startDrawablePadding) ?
                array.getDimensionPixelSize(R.styleable.SettingBar_bar_startDrawablePadding, 0) :
                (int) SmallestWidthAdaptation.dp2px(context, 10));
        setEndDrawablePadding(array.hasValue(R.styleable.SettingBar_bar_endDrawablePadding) ?
                array.getDimensionPixelSize(R.styleable.SettingBar_bar_endDrawablePadding, 0) :
                (int) SmallestWidthAdaptation.dp2px(context, 10));

        // 图标设置
        if (array.hasValue(R.styleable.SettingBar_bar_startDrawable)) {
            setStartDrawable(array.getDrawable(R.styleable.SettingBar_bar_startDrawable));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_endDrawable)) {
            setEndDrawable(array.getDrawable(R.styleable.SettingBar_bar_endDrawable));
        }

        // 文字颜色设置
        setStartTextColor(array.getColor(R.styleable.SettingBar_bar_startTextColor, ContextCompat.getColor(getContext(), R.color.black80)));
        setEndTextColor(array.getColor(R.styleable.SettingBar_bar_endTextColor, ContextCompat.getColor(getContext(), R.color.black60)));

        // 文字大小设置
        setStartTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SettingBar_bar_startTextSize, (int) SmallestWidthAdaptation.sp2px(context, 15)));
        setEndTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SettingBar_bar_endTextSize, (int) SmallestWidthAdaptation.sp2px(context, 14)));

        // 分割线设置
        if (array.hasValue(R.styleable.SettingBar_bar_lineDrawable)) {
            setLineDrawable(array.getDrawable(R.styleable.SettingBar_bar_lineDrawable));
        } else {
            setLineDrawable(new ColorDrawable(Color.parseColor("#ECECEC")));
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

        if (getBackground() == null) {
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.black5)));
            drawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.black5)));
            drawable.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.black5)));
            drawable.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.white)));
            setBackground(drawable);

            // 必须要设置可点击，否则点击屏幕任何角落都会触发按压事件
            setFocusable(true);
            setClickable(true);
        }

        array.recycle();

        // 适配 RTL 特性
        if (mStartView.getTextAlignment() == TEXT_ALIGNMENT_GRAVITY) {
            mStartView.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        }

        mMainLayout.addView(mStartView);
        mMainLayout.addView(mEndView);

        addView(mMainLayout, 0);
        addView(mLineView, 1);

        mMainLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                // 限制右边 View 的宽度，避免文本过长挤掉左边 View
                mEndView.setMaxWidth((right - left) / 3 * 2);
            }
        });
    }

    /**
     * 设置左边的文本
     */
    public SettingBar setStartText(@StringRes int id) {
        return setStartText(getResources().getString(id));
    }

    public SettingBar setStartText(CharSequence text) {
        mStartView.setText(text);
        return this;
    }

    public CharSequence getStartText() {
        return mStartView.getText();
    }

    /**
     * 设置左边的提示
     */
    public SettingBar setStartTextHint(@StringRes int id) {
        return setStartTextHint(getResources().getString(id));
    }

    public SettingBar setStartTextHint(CharSequence hint) {
        mStartView.setHint(hint);
        return this;
    }

    /**
     * 设置右边的标题
     */
    public SettingBar setEndText(@StringRes int id) {
        setEndText(getResources().getString(id));
        return this;
    }

    public SettingBar setEndText(CharSequence text) {
        mEndView.setText(text);
        return this;
    }

    public CharSequence getEndText() {
        return mEndView.getText();
    }

    /**
     * 设置右边的提示
     */
    public SettingBar setEndTextHint(@StringRes int id) {
        return setEndTextHint(getResources().getString(id));
    }

    public SettingBar setEndTextHint(CharSequence hint) {
        mEndView.setHint(hint);
        return this;
    }

    /**
     * 设置左边的图标
     */
    public SettingBar setStartDrawable(@DrawableRes int id) {
        setStartDrawable(ContextCompat.getDrawable(getContext(), id));
        return this;
    }

    public SettingBar setStartDrawable(@Nullable Drawable drawable) {
        mStartView.setCompoundDrawablesRelative(drawable, null, null, null);
        setStartDrawableSize(mStartDrawableSize);
        setStartDrawableTint(mStartDrawableTint);
        return this;
    }

    public Drawable getStartDrawable() {
        return mStartView.getCompoundDrawablesRelative()[0];
    }

    /**
     * 设置右边的图标
     */
    public SettingBar setEndDrawable(@DrawableRes int id) {
        setEndDrawable(ContextCompat.getDrawable(getContext(), id));
        return this;
    }

    public SettingBar setEndDrawable(@Nullable Drawable drawable) {
        mEndView.setCompoundDrawablesRelative(null, null, drawable, null);
        setEndDrawableSize(mEndDrawableSize);
        setEndDrawableTint(mEndDrawableTint);
        return this;
    }

    public Drawable getEndDrawable() {
        return mEndView.getCompoundDrawablesRelative()[2];
    }

    /**
     * 设置左边的图标间距
     */
    public SettingBar setStartDrawablePadding(int padding) {
        mStartView.setCompoundDrawablePadding(padding);
        return this;
    }

    /**
     * 设置右边的图标间距
     */
    public SettingBar setEndDrawablePadding(int padding) {
        mEndView.setCompoundDrawablePadding(padding);
        return this;
    }

    /**
     * 设置左边的图标大小
     */
    public SettingBar setStartDrawableSize(int size) {
        mStartDrawableSize = size;
        Drawable drawable = getStartDrawable();
        if (drawable != null) {
            if (size > 0) {
                drawable.setBounds(0 ,0, size, size);
            } else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            mStartView.setCompoundDrawablesRelative(drawable, null, null, null);
        }
        return this;
    }

    /**
     * 设置右边的图标大小
     */
    public SettingBar setEndDrawableSize(int size) {
        mEndDrawableSize = size;
        Drawable drawable = getEndDrawable();
        if (drawable != null) {
            if (size > 0) {
                drawable.setBounds(0 ,0, size, size);
            } else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            mEndView.setCompoundDrawablesRelative(null, null, drawable, null);
        }
        return this;
    }

    /**
     * 设置左边的图标着色器
     */
    public SettingBar setStartDrawableTint(int color) {
        mStartDrawableTint = color;
        Drawable drawable = getStartDrawable();
        if (drawable != null && color != NO_COLOR) {
            drawable.mutate();
            if (AndroidVersion.isAndroid10()) {
                drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
        return this;
    }

    /**
     * 设置右边的图标着色器
     */
    public SettingBar setEndDrawableTint(int color) {
        mEndDrawableTint = color;
        Drawable drawable = getEndDrawable();
        if (drawable != null && color != NO_COLOR) {
            drawable.mutate();
            if (AndroidVersion.isAndroid10()) {
                drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
        return this;
    }

    /**
     * 设置左边的文本颜色
     */
    public SettingBar setStartTextColor(@ColorInt int color) {
        mStartView.setTextColor(color);
        return this;
    }

    /**
     * 设置右边的文本颜色
     */
    public SettingBar setEndTextColor(@ColorInt int color) {
        mEndView.setTextColor(color);
        return this;
    }

    /**
     * 设置左边的文字大小
     */
    public SettingBar setStartTextSize(int unit, float size) {
        mStartView.setTextSize(unit, size);
        return this;
    }

    /**
     * 设置右边的文字大小
     */
    public SettingBar setEndTextSize(int unit, float size) {
        mEndView.setTextSize(unit, size);
        return this;
    }

    /**
     * 设置分割线是否显示
     */
    public SettingBar setLineVisible(boolean visible) {
        mLineView.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }

    /**
     * 设置分割线的颜色
     */
    public SettingBar setLineColor(@ColorInt int color) {
        return setLineDrawable(new ColorDrawable(color));
    }
    public SettingBar setLineDrawable(Drawable drawable) {
        mLineView.setBackground(drawable);
        return this;
    }

    /**
     * 设置分割线的大小
     */
    public SettingBar setLineSize(int size) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLineView.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        params.height = size;
        mLineView.setLayoutParams(params);
        return this;
    }

    /**
     * 设置分割线边界
     */
    public SettingBar setLineMargin(int margin) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLineView.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        params.leftMargin = margin;
        params.rightMargin = margin;
        mLineView.setLayoutParams(params);
        return this;
    }

    /**
     * 获取主布局
     */
    @NonNull
    public LinearLayout getMainLayout() {
        return mMainLayout;
    }

    /**
     * 获取左 TextView
     */
    @NonNull
    public TextView getStartView() {
        return mStartView;
    }

    /**
     * 获取右 TextView
     */
    @NonNull
    public TextView getEndView() {
        return mEndView;
    }

    /**
     * 获取分割线
     */
    @NonNull
    public View getLineView() {
        return mLineView;
    }
}