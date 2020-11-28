package com.hjq.demo.other;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 *    author : 钉某人 & Android 轮子哥
 *    github : https://github.com/DingMouRen/LayoutManagerGroup
 *    time   : 2019/09/11
 *    desc   : 选择器布局管理器
 */
public class PickerLayoutManager extends LinearLayoutManager {

    private final LinearSnapHelper mLinearSnapHelper;
    private final int mOrientation;
    private final int mMaxItem;
    private final float mScale;
    private final boolean mAlpha;

    private RecyclerView mRecyclerView;
    private OnPickerListener mListener;

    private PickerLayoutManager(Context context, int orientation, boolean reverseLayout, int maxItem, float scale, boolean alpha) {
        super(context, orientation, reverseLayout);
        mLinearSnapHelper = new LinearSnapHelper();
        mMaxItem = maxItem;
        mOrientation = orientation;
        mAlpha = alpha;
        mScale = scale;
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        mRecyclerView = recyclerView;
        // 设置子控件的边界可以超过父布局的范围
        mRecyclerView.setClipToPadding(false);
        // 添加 LinearSnapHelper
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView recyclerView, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        mRecyclerView = null;
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return mMaxItem == 0;
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        int width = RecyclerView.LayoutManager.chooseSize(widthSpec,
                getPaddingLeft() + getPaddingRight(),
                ViewCompat.getMinimumWidth(mRecyclerView));
        int height = RecyclerView.LayoutManager.chooseSize(heightSpec,
                getPaddingTop() + getPaddingBottom(),
                ViewCompat.getMinimumHeight(mRecyclerView));

        if (state.getItemCount() != 0 && mMaxItem != 0) {

            View itemView = recycler.getViewForPosition(0);
            measureChildWithMargins(itemView, widthSpec, heightSpec);

            if (mOrientation == HORIZONTAL) {
                int measuredWidth = itemView.getMeasuredWidth();
                int paddingHorizontal = (mMaxItem - 1) / 2 * measuredWidth;
                mRecyclerView.setPadding(paddingHorizontal,0, paddingHorizontal,0);
                width = measuredWidth * mMaxItem;
            } else if (mOrientation == VERTICAL) {
                int measuredHeight = itemView.getMeasuredHeight();
                int paddingVertical = (mMaxItem - 1) / 2 * measuredHeight;
                mRecyclerView.setPadding(0, paddingVertical,0, paddingVertical);
                height = measuredHeight * mMaxItem;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 当 RecyclerView 停止滚动时
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mListener != null) {
                mListener.onPicked(mRecyclerView, getPickedPosition());
            }
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() < 0 || state.isPreLayout()) {
            return;
        }

        if (mOrientation == HORIZONTAL){
            scaleHorizontalChildView();
        } else if (mOrientation == VERTICAL){
            scaleVerticalChildView();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleHorizontalChildView();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleVerticalChildView();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 横向情况下的缩放
     */
    private void scaleHorizontalChildView() {
        float mid = getWidth() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View childView =  getChildAt(i);
            if (childView != null) {
                float childMid = (getDecoratedLeft(childView) + getDecoratedRight(childView)) / 2.0f;
                float scale = 1.0f + (-1 * (1 - mScale)) * (Math.min(mid, Math.abs(mid - childMid))) / mid;
                childView.setScaleX(scale);
                childView.setScaleY(scale);
                if (mAlpha) {
                    childView.setAlpha(scale);
                }
            }
        }
    }

    /**
     * 竖向方向上的缩放
     */
    private void scaleVerticalChildView(){
        float mid = getHeight() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View childView =  getChildAt(i);
            if (childView != null) {
                float childMid = (getDecoratedTop(childView) + getDecoratedBottom(childView)) / 2.0f;
                float scale = 1.0f + (-1 *  (1 - mScale)) * (Math.min(mid, Math.abs(mid - childMid))) / mid;
                childView.setScaleX(scale);
                childView.setScaleY(scale);
                if (mAlpha) {
                    childView.setAlpha(scale);
                }
            }
        }
    }

    /**
     * 获取选中的位置
     */
    public int getPickedPosition() {
        View itemView = mLinearSnapHelper.findSnapView(this);
        if(itemView != null) {
            return getPosition(itemView);
        }
        return 0;
    }

    /**
     * 设置监听器
     */
    public void setOnPickerListener(OnPickerListener listener) {
        mListener = listener;
    }

    public interface OnPickerListener {

        /**
         * 滚动停止时触发的监听
         *
         * @param recyclerView              RecyclerView 对象
         * @param position                  当前滚动的位置
         */
        void onPicked(RecyclerView recyclerView, int position);
    }

    public static final class Builder {

        private final Context mContext;
        private int mOrientation = VERTICAL;
        private boolean mReverseLayout;
        private OnPickerListener mListener;

        private int mMaxItem = 3;
        private float mScale = 0.6f;
        private boolean mAlpha = true;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置布局摆放器方向
         */
        public Builder setOrientation(@RecyclerView.Orientation int orientation) {
            mOrientation = orientation;
            return this;
        }

        /**
         * 设置是否反向显示
         */
        public Builder setReverseLayout(boolean reverseLayout) {
            mReverseLayout = reverseLayout;
            return this;
        }

        /**
         * 设置最大显示条目数
         */
        public Builder setMaxItem(int maxItem) {
            mMaxItem = maxItem;
            return this;
        }

        /**
         * 设置缩放比例
         */
        public Builder setScale(float scale) {
            mScale = scale;
            return this;
        }

        /**
         * 设置透明开关
         */
        public Builder setAlpha(boolean alpha) {
            mAlpha = alpha;
            return this;
        }

        public Builder setOnPickerListener(OnPickerListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * 构建布局管理器
         */
        public PickerLayoutManager build() {
            PickerLayoutManager layoutManager = new PickerLayoutManager(mContext, mOrientation, mReverseLayout, mMaxItem, mScale, mAlpha);
            if (mListener != null) {
                layoutManager.setOnPickerListener(mListener);
            }
            return layoutManager;
        }

        /**
         * 应用到 RecyclerView
         */
        public void into(RecyclerView recyclerView) {
            recyclerView.setLayoutManager(build());
        }
    }
}