package com.hjq.demo.other;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import com.hjq.demo.R;
import com.hjq.toast.style.BlackToastStyle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/02/27
 *    desc   : Toast 样式配置
 */
public final class ToastStyle extends BlackToastStyle {

    @Override
    protected Drawable getBackgroundDrawable(@NonNull Context context) {
        GradientDrawable drawable = new GradientDrawable();
        // 设置颜色
        drawable.setColor(0X88000000);
        // 设置圆角
        drawable.setCornerRadius((int) context.getResources().getDimension(R.dimen.button_circle_size));
        return drawable;
    }

    @Override
    protected float getTextSize(@NonNull Context context) {
        return context.getResources().getDimension(R.dimen.sp_14);
    }

    @Override
    protected int getHorizontalPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.sp_24);
    }

    @Override
    protected int getVerticalPadding(@NonNull Context context) {
        return (int) context.getResources().getDimension(R.dimen.sp_16);
    }
}