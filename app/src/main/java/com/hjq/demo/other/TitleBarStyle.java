package com.hjq.demo.other;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.hjq.bar.style.LightBarStyle;
import com.hjq.demo.R;
import com.hjq.widget.view.PressAlphaTextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/02/27
 *    desc   : 标题栏初始器
 */
public final class TitleBarStyle extends LightBarStyle {

    @Override
    public TextView newTitleView(Context context) {
        return new AppCompatTextView(context);
    }

    @Override
    public TextView newLeftView(Context context) {
        return new PressAlphaTextView(context);
    }

    @Override
    public TextView newRightView(Context context) {
        return new PressAlphaTextView(context);
    }

    @Override
    public Drawable getTitleBarBackground(Context context) {
        return new ColorDrawable(ContextCompat.getColor(context, R.color.common_primary_color));
    }

    @Override
    public Drawable getBackButtonDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.arrows_left_ic);
    }

    @Override
    public Drawable getLeftTitleBackground(Context context) {
        return null;
    }

    @Override
    public Drawable getRightTitleBackground(Context context) {
        return null;
    }

    @Override
    public int getChildHorizontalPadding(Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_12);
    }

    @Override
    public int getChildVerticalPadding(Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_14);
    }

    @Override
    public float getTitleSize(Context context) {
        return context.getResources().getDimension(R.dimen.sp_15);
    }

    @Override
    public float getLeftTitleSize(Context context) {
        return context.getResources().getDimension(R.dimen.sp_13);
    }

    @Override
    public float getRightTitleSize(Context context) {
        return context.getResources().getDimension(R.dimen.sp_13);
    }

    @Override
    public int getTitleIconPadding(Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_2);
    }

    @Override
    public int getLeftIconPadding(Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_2);
    }

    @Override
    public int getRightIconPadding(Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_2);
    }
}