package com.hjq.demo.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 支持监听渐变的 CollapsingToolbarLayout
 */
public final class XCollapsingToolbarLayout extends CollapsingToolbarLayout {

    /** 渐变监听 */
    private OnScrimsListener mListener;
    /** 当前渐变状态 */
    private boolean isScrimsShown;

    public XCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public XCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, true);
        // 判断渐变状态是否改变了
        if (isScrimsShown != shown) {
            // 如果是就记录并且回调监听器
            isScrimsShown = shown;
            if (mListener != null) {
                mListener.onScrimsStateChange(this, isScrimsShown);
            }
        }
    }

    /**
     * 获取当前的渐变状态
     */
    public boolean isScrimsShown() {
        return isScrimsShown;
    }

    /**
     * 设置CollapsingToolbarLayout渐变监听
     */
    public void setOnScrimsListener(OnScrimsListener listener) {
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
        void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown);
    }
}