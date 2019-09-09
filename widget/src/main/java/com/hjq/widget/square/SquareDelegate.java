package com.hjq.widget.square;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/27
 *    desc   : 用于限定正方形大小的控件的算法
 */
@SuppressWarnings("all")
public final class SquareDelegate {

    /**
     * 测量出正方形的宽度
     *
     * @param widthMeasureSpec          onMeasure中的同名参数
     * @param heightMeasureSpec         onMeasure中的同名参数
     * @return                          返回用于测量的参数
     */
    public static int measureWidth(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

        // 如果当前宽度不是写死的
        if (widthSpecMode != View.MeasureSpec.EXACTLY) {
            // 如果当前高度不是写死的
            if (heightSpecMode != View.MeasureSpec.EXACTLY) {
                // 对比高度和宽度，返回最大值的那个
                return View.MeasureSpec.makeMeasureSpec(Math.max(widthSpecSize, heightSpecSize), View.MeasureSpec.EXACTLY);
            } else {
                // 如果已经定死了高度，而宽度没有写死，则使用高度代替宽度
                return heightMeasureSpec;
            }
        } else {
            // 宽度和高度已经写死，这里不做任何处理
            return widthMeasureSpec;
        }
    }

    /**
     * 测量出正方形的高度
     *
     * @param widthMeasureSpec          onMeasure中的同名参数
     * @param heightMeasureSpec         onMeasure中的同名参数
     * @return                          返回用于测量的参数
     */
    public static int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

        // 如果当前高度不是写死的
        if (heightSpecMode != View.MeasureSpec.EXACTLY) {
            // 如果当前宽度不是写死的
            if (widthSpecMode != View.MeasureSpec.EXACTLY) {
                // 对比高度和宽度，返回最大值的那个
                return View.MeasureSpec.makeMeasureSpec(Math.max(widthSpecSize, heightSpecSize), View.MeasureSpec.EXACTLY);
            } else {
                // 如果已经定死了宽度，而高度没有写死，则使用高度代替宽度
                return widthMeasureSpec;
            }
        } else {
            // 宽度和高度已经写死，这里不做任何处理
            return heightMeasureSpec;
        }
    }
}