package com.hjq.demo.other;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
    public TextView newTitleView(@NonNull Context context) {
        return new AppCompatTextView(context);
    }

    @Override
    public TextView newLeftView(@NonNull Context context) {
        return new PressAlphaTextView(context);
    }

    @Override
    public TextView newRightView(@NonNull Context context) {
        return new PressAlphaTextView(context);
    }

    @Override
    public Drawable getTitleBarBackground(@NonNull Context context) {
        return new ColorDrawable(ContextCompat.getColor(context, R.color.common_primary_color));
    }

    @Override
    public Drawable getBackButtonDrawable(@NonNull Context context) {
        return ContextCompat.getDrawable(context, R.drawable.arrows_left_ic);
    }

    @Override
    public Drawable getLeftTitleBackground(@NonNull Context context) {
        return null;
    }

    @Override
    public Drawable getRightTitleBackground(@NonNull Context context) {
        return null;
    }

    @Override
    public int getTitleHorizontalPadding(@NonNull Context context) {
        return 0;
    }

    @Override
    public int getLeftHorizontalPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_10);
    }

    @Override
    public int getRightHorizontalPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_10);
    }

    @Override
    public int getChildVerticalPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_14);
    }

    @Override
    public float getTitleSize(@NonNull Context context) {
        return context.getResources().getDimension(R.dimen.sp_15);
    }

    @Override
    public float getLeftTitleSize(@NonNull Context context) {
        return context.getResources().getDimension(R.dimen.sp_13);
    }

    @Override
    public float getRightTitleSize(@NonNull Context context) {
        return context.getResources().getDimension(R.dimen.sp_13);
    }

    @Override
    public int getTitleIconPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_2);
    }

    @Override
    public int getLeftIconPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_2);
    }

    @Override
    public int getRightIconPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.dp_2);
    }
}