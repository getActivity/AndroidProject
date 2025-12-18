package com.hjq.custom.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.hjq.custom.widget.R;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : HaoZhang & Android 轮子哥
 *    github : https://github.com/HeZaiJin/SlantedTextView
 *    time   : 2016/06/30
 *    desc   : 一个倾斜的 TextView，适用于标签效果
 */
@SuppressLint("RtlHardcoded")
public final class SlantedTextView extends View {

    /** 旋转角度 */
    public static final int ROTATE_ANGLE = 45;

    /** 背景画笔 */
    @NonNull
    private final Paint mBackgroundPaint;
    /** 文字画笔 */
    @NonNull
    private final TextPaint mTextPaint;

    /** 显示的文本 */
    @NonNull
    private String mText = "";
    /** 倾斜重心 */
    private int mGravity;
    /** 是否绘制成三角形的 */
    private boolean mTriangle;
    /** 背景颜色 */
    private int mColorBackground;

    /** 文字测量范围装载 */
    @NonNull
    private final Rect mTextBounds = new Rect();
    /** 测量出来的文本高度 */
    private int mTextHeight;

    public SlantedTextView(@NonNull Context context) {
        this(context, null);
    }

    public SlantedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlantedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        mBackgroundPaint.setAntiAlias(true);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlantedTextView);

        setText(array.getString(R.styleable.SlantedTextView_android_text));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SlantedTextView_android_textSize, (int) SmallestWidthAdaptation.sp2px(context, 12)));
        setTextColor(array.getColor(R.styleable.SlantedTextView_android_textColor, Color.WHITE));
        setTextStyle(Typeface.defaultFromStyle(array.getInt(R.styleable.SlantedTextView_android_textStyle, Typeface.NORMAL)));
        setGravity(array.getInt(R.styleable.SlantedTextView_android_gravity, Gravity.END));
        setColorBackground(array.getColor(R.styleable.SlantedTextView_android_colorBackground, getAccentColor()));
        setTriangle(array.getBoolean(R.styleable.SlantedTextView_triangle, false));

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
        mTextHeight = mTextBounds.height() + getPaddingTop() + getPaddingBottom();

        int width = 0;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                width = mTextBounds.width() + getPaddingLeft() + getPaddingRight();
                break;
            default:
                break;
        }

        int height = 0;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                height = mTextBounds.height() + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }

        setMeasuredDimension(Math.max(width, height), Math.max(width, height));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawBackground(canvas);
        drawText(canvas);
    }

    /**
     * 绘制背景
     */
    private void drawBackground(@NonNull Canvas canvas) {
        Path path = new Path();
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        switch (mGravity) {
            // 左上角
            case Gravity.LEFT:
            case Gravity.LEFT | Gravity.TOP:
                if (mTriangle) {
                    path.lineTo(0, height);
                    path.lineTo(width, 0);
                } else {
                    path.moveTo(width, 0);
                    path.lineTo(0, height);
                    path.lineTo(0, height - mTextHeight);
                    path.lineTo(width - mTextHeight, 0);
                }
                break;
            // 右上角
            case Gravity.NO_GRAVITY:
            case Gravity.RIGHT:
            case Gravity.RIGHT | Gravity.TOP:
                if (mTriangle) {
                    path.lineTo(width, 0);
                    path.lineTo(width, height);
                } else {
                    path.lineTo(width, height);
                    path.lineTo(width, height - mTextHeight);
                    path.lineTo(mTextHeight * 1f, 0);
                }
                break;
            // 左下角
            case Gravity.BOTTOM:
            case Gravity.LEFT | Gravity.BOTTOM:
                if (mTriangle) {
                    path.lineTo(width, height);
                    path.lineTo(0, height);
                } else {
                    path.lineTo(width, height);
                    path.lineTo(width - mTextHeight, height);
                    path.lineTo(0, mTextHeight);
                }
                break;
            // 右下角
            case Gravity.RIGHT | Gravity.BOTTOM:
                if (mTriangle) {
                    path.moveTo(0, height);
                    path.lineTo(width, height);
                    path.lineTo(width, 0);
                } else {
                    path.moveTo(0, height);
                    path.lineTo(mTextHeight * 1f, height);
                    path.lineTo(width, mTextHeight);
                    path.lineTo(width, 0);
                }
                break;
            default:
                throw new IllegalArgumentException("are you ok?");
        }
        path.close();
        canvas.drawPath(path, mBackgroundPaint);
        canvas.save();
    }

    /**
     * 绘制文本
     */
    private void drawText(@NonNull Canvas canvas) {
        int width = canvas.getWidth() - mTextHeight / 2;
        int height = canvas.getHeight() - mTextHeight / 2;
        Rect rect;
        RectF rectF;
        int offset = mTextHeight / 2;

        float toX;
        float toY;
        float centerX;
        float centerY;
        float angle;

        switch (mGravity) {
            // 左上角
            case Gravity.LEFT:
            case Gravity.LEFT | Gravity.TOP:
                rect = new Rect(0, 0, width, height);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mText, 0, mText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                toX = rectF.left;
                toY = rectF.top - mTextPaint.ascent();
                centerX = width / 2f;
                centerY = height / 2f;
                angle = - ROTATE_ANGLE;
                break;
            // 右上角
            case Gravity.NO_GRAVITY:
            case Gravity.RIGHT:
            case Gravity.RIGHT | Gravity.TOP:
                rect = new Rect(offset, 0, width + offset, height);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mText, 0, mText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                toX = rectF.left;
                toY = rectF.top - mTextPaint.ascent();
                centerX = width / 2f + offset;
                centerY = height / 2f;
                angle = ROTATE_ANGLE;
                break;
            // 左下角
            case Gravity.BOTTOM:
            case Gravity.LEFT | Gravity.BOTTOM:
                rect = new Rect(0, offset, width, height + offset);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mText, 0, mText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                toX = rectF.left;
                toY = rectF.top - mTextPaint.ascent();
                centerX = width / 2f;
                centerY = height / 2f + offset;
                angle = ROTATE_ANGLE;
                break;
            // 右下角
            case Gravity.RIGHT | Gravity.BOTTOM:
                rect = new Rect(offset, offset, width + offset, height + offset);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mText, 0, mText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                toX = rectF.left;
                toY = rectF.top - mTextPaint.ascent();
                centerX = width / 2f + offset;
                centerY = height / 2f + offset;
                angle = - ROTATE_ANGLE;
                break;
            default:
                throw new IllegalArgumentException("are you ok?");
        }

        canvas.rotate(angle, centerX , centerY);
        canvas.drawText(mText, toX, toY, mTextPaint);
    }

    /**
     * 获取显示文本
     */
    @NonNull
    public String getText() {
        return mText;
    }

    /**
     * 设置显示文本
     */
    public void setText(@StringRes int id) {
        setText(getResources().getString(id));
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        if (!TextUtils.equals(text, getText())) {
            mText = text;
            invalidate();
        }
    }

    /**
     * 获取字体颜色
     */
    public int getTextColor() {
        return mTextPaint.getColor();
    }

    /**
     * 设置字体颜色
     */
    public void setTextColor(int color) {
        if (getTextColor() != color) {
            mTextPaint.setColor(color);
            invalidate();
        }
    }

    /**
     * 获取字体大小
     */
    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    /**
     * 设置字体大小
     */
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setTextSize(int unit, float size) {
        float textSize = TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
        if (getTextSize() != textSize) {
            mTextPaint.setTextSize(textSize);
            invalidate();
        }
    }

    /**
     * 获取文本样式
     */
    @Nullable
    public Typeface getTextStyle() {
        return mTextPaint.getTypeface();
    }

    /**
     * 设置文本样式
     */
    public void setTextStyle(@Nullable Typeface typeface) {
        if (getTextStyle() != typeface) {
            mTextPaint.setTypeface(typeface);
            invalidate();
        }
    }

    /**
     * 获取背景颜色
     */
    public int getColorBackground() {
        return mColorBackground;
    }

    /**
     * 设置背景颜色
     */
    public void setColorBackground(int color) {
        if (getColorBackground() != color) {
            mColorBackground = color;
            mBackgroundPaint.setColor(mColorBackground);
            invalidate();
        }
    }

    /**
     * 获取倾斜重心
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置倾斜重心
     */
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            // 适配布局反方向
            mGravity = Gravity.getAbsoluteGravity(gravity, getResources().getConfiguration().getLayoutDirection());
            invalidate();
        }
    }

    /**
     * 当前是否是三角形
     */
    public boolean isTriangle() {
        return mTriangle;
    }

    /**
     * 是否设置成三角形
     */
    public void setTriangle(boolean triangle) {
        if (isTriangle() != triangle) {
            mTriangle = triangle;
            invalidate();
        }
    }

    /**
     * 获取当前主题的强调色
     */
    private int getAccentColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }
}