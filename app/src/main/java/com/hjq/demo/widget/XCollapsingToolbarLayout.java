package com.hjq.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 支持监听渐变的 CollapsingToolbarLayout
 */
public final class XCollapsingToolbarLayout extends CollapsingToolbarLayout {

    /** 当前渐变状态 */
    private boolean mScrimsShownStatus;

    /** 渐变监听 */
    @Nullable
    private OnScrimsListener mListener;

    public XCollapsingToolbarLayout(@NonNull Context context) {
        super(context);
    }

    public XCollapsingToolbarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XCollapsingToolbarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, true);
        // 判断渐变状态是否改变了
        if (mScrimsShownStatus == shown) {
            return;
        }
        // 如果是就记录并且回调监听器
        mScrimsShownStatus = shown;
        if (mListener == null) {
            return;
        }
        mListener.onScrimsStateChange(this, mScrimsShownStatus);
    }

    /**
     * 获取当前的渐变状态
     */
    public boolean isScrimsShown() {
        return mScrimsShownStatus;
    }

    /**
     * 设置CollapsingToolbarLayout渐变监听
     */
    public void setOnScrimsListener(@Nullable OnScrimsListener listener) {
        mListener = listener;
    }

    /**
     * CollapsingToolbarLayout渐变监听器
     */
    public interface OnScrimsListener {

        /**
         * 渐变状态变化
         *
         * @param shown         渐变开关
         */
        void onScrimsStateChange(@NonNull XCollapsingToolbarLayout layout, boolean shown);
    }
}