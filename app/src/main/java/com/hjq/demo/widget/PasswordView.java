package com.hjq.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : 密码遮挡自定义控件
 */
public final class PasswordView extends View {

    private final Paint mPaint;
    private final Path mPath;
    private final Paint mPointPaint;

    /** 单个密码框的宽度 */
    private int mItemWidth = 44;
    /** 单个密码框的高度 */
    private int mItemHeight = 41;

    /** 中心黑点的半径大小 */
    private static final int POINT_RADIUS = 15;

    /** 中心黑点的颜色 */
    private static final int POINT_COLOR = 0xFF666666;

    /** 密码框边界线的颜色值 */
    private static final int STROKE_COLOR = 0xFFECECEC;

    /** 密码总个数 */
    public static final int PASSWORD_COUNT = 6;

    /** 已经输入的密码个数，也就是需要显示的小黑点个数 */
    private int mCurrentIndex = 0;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mItemWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mItemWidth, getResources().getDisplayMetrics());
        mItemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mItemHeight, getResources().getDisplayMetrics());

        mPaint = new Paint();
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        // 设置颜色
        mPaint.setColor(STROKE_COLOR);
        // 设置描边
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mItemWidth * PASSWORD_COUNT, 0);
        mPath.lineTo(mItemWidth * PASSWORD_COUNT, mItemHeight);
        mPath.lineTo(0, mItemHeight);
        mPath.close();

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(POINT_COLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mItemWidth * PASSWORD_COUNT, MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
            default:
                break;
        }

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
            default:
                break;
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(5);
        canvas.drawPath(mPath, mPaint);

        // 画单个的分割线
        mPaint.setStrokeWidth(3);
        for (int index = 1; index < PASSWORD_COUNT; index++) {
            canvas.drawLine(mItemWidth * index, 0, mItemWidth * index, mItemHeight, mPaint);
        }

        // 绘制中间的小黑点
        if (mCurrentIndex == 0) {
            return;
        }
        for (int i = 1; i <= mCurrentIndex; i++) {
            canvas.drawCircle(i * mItemWidth - (float) mItemWidth / 2, (float) mItemHeight / 2, POINT_RADIUS, mPointPaint);
        }
    }

    /**
     * 改变密码提示小黑点的个数
     */
    public void setPassWordLength(int index) {
        mCurrentIndex = index;
        invalidate();
    }
}