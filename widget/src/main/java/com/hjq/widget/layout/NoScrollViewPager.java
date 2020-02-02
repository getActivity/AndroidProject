package com.hjq.widget.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 禁用水平滑动的ViewPager（一般用于 APP 主页的 ViewPager + Fragment）
 */
public final class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 不拦截这个事件
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 不处理这个事件
        return false;
    }

    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        // 不响应按键事件
        return false;
    }

    @Override
    public void setCurrentItem(int item) {
        boolean smoothScroll;
        int currentItem = getCurrentItem();
        if (currentItem == 0) {
            // 如果当前是第一页，只有第二页才会有动画
            smoothScroll = item == currentItem + 1;
        } else if (currentItem == getCount() - 1) {
            // 如果当前是最后一页，只有最后第二页才会有动画
            smoothScroll = item == currentItem - 1;
        } else {
            // 如果当前是中间页，只有相邻页才会有动画
            smoothScroll = Math.abs(currentItem - item) == 1;
        }
        super.setCurrentItem(item, smoothScroll);
    }

    public int getCount() {
        PagerAdapter adapter = getAdapter();
        return adapter != null ? adapter.getCount() : 0;
    }
}