package com.hjq.demo.ui.adapter.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/02/28
 *    desc   : Tab 适配器
 */
public final class TabAdapter extends AppAdapter<String> implements BaseAdapter.OnItemClickListener {

    public static final int TAB_MODE_DESIGN = 1;
    public static final int TAB_MODE_SLIDING = 2;

    /** 当前选中条目位置 */
    private int mSelectedPosition = 0;

    /** 导航栏监听对象 */
    @Nullable
    private OnTabListener mListener;

    /** Tab 样式 */
    private final int mTabMode;

    /** Tab 宽度是否固定 */
    private final boolean mFixed;

    public TabAdapter(@NonNull Context context) {
        this(context, TAB_MODE_DESIGN, true);
    }

    public TabAdapter(@NonNull Context context, int tabMode, boolean fixed) {
        super(context);
        mTabMode = tabMode;
        mFixed = fixed;
        setOnItemClickListener(this);
        registerAdapterDataObserver(new TabAdapterDataObserver());
    }

    @Override
    public int getItemViewType(int position) {
        return mTabMode;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TAB_MODE_DESIGN:
                return new DesignViewHolder();
            case TAB_MODE_SLIDING:
                return new SlidingViewHolder();
            default:
                throw new IllegalArgumentException("are you ok?");
        }
    }

    @NonNull
    @Override
    protected RecyclerView.LayoutManager generateDefaultLayoutManager(@NonNull Context context) {
        if (mFixed) {
            int count = getCount();
            if (count < 1) {
                count = 1;
            }
            return new GridLayoutManager(context, count, RecyclerView.VERTICAL, false);
        } else {
            return new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // 禁用 RecyclerView 条目动画
        recyclerView.setItemAnimator(null);
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int position) {
        if (mSelectedPosition == position) {
            return;
        }
        int oldPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(mSelectedPosition);

        // 如果当前 Tab 宽度是对等的模式，则不继续往下执行
        if (mFixed) {
            return;
        }

        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView == null) {
            return;
        }

        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        // 注意：GridLayoutManager 是 LinearLayoutManager 的子类
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        // 获取第一个和最后一个完全可见的条目位置
        int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

        // 如果要滚动的 Tab 是在完全可视区域范围内，则不往下执行
        if (position >= firstCompletelyVisibleItemPosition && position <= lastCompletelyVisibleItemPosition) {
            return;
        }
        // 处理在 Tab 过多的情况下，选中某个 Tab 选项中没有自动滚动的问题
        linearLayoutManager.scrollToPosition(position);
    }

    /**
     * 设置导航栏监听
     */
    public void setOnTabListener(@Nullable OnTabListener listener) {
        mListener = listener;
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     */

    @Override
    public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
        if (mSelectedPosition == position) {
            return;
        }

        Runnable runnable = () -> {
            int oldPosition = mSelectedPosition;
            mSelectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(mSelectedPosition);
        };

        if (mListener == null) {
            runnable.run();
            return;
        }

        if (mListener.onTabSelected(recyclerView, position)) {
            runnable.run();
        }
    }

    private final class DesignViewHolder extends AppViewHolder {

        private final TextView mTitleView;
        private final View mLineView;

        private DesignViewHolder() {
            super(R.layout.tab_item_design);
            mTitleView = findViewById(R.id.tv_tab_design_title);
            mLineView = findViewById(R.id.v_tab_design_line);
            if (!mFixed) {
                return;
            }
            View itemView = getItemView();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(layoutParams);
        }

        @Override
        public void onBindView(int position) {
            mTitleView.setText(getItem(position));
            mTitleView.setSelected(mSelectedPosition == position);
            mLineView.setVisibility(mSelectedPosition == position ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private final class SlidingViewHolder extends AppViewHolder
            implements ValueAnimator.AnimatorUpdateListener {

        private final int mDefaultTextSize;
        private final int mSelectedTextSize;

        private final TextView mTitleView;
        private final View mLineView;

        private SlidingViewHolder() {
            super(R.layout.tab_item_sliding);
            mTitleView = findViewById(R.id.tv_tab_sliding_title);
            mLineView = findViewById(R.id.v_tab_sliding_line);

            mDefaultTextSize = (int) SmallestWidthAdaptation.sp2px(getContext(), 14);
            mSelectedTextSize = (int) SmallestWidthAdaptation.sp2px(getContext(), 15);

            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDefaultTextSize);

            if (!mFixed) {
                return;
            }
            View itemView = getItemView();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(layoutParams);
        }

        @Override
        public void onBindView(int position) {
            mTitleView.setText(getItem(position));
            mTitleView.setSelected(mSelectedPosition == position);
            mLineView.setVisibility(mSelectedPosition == position ? View.VISIBLE : View.INVISIBLE);

            int textSize = (int) mTitleView.getTextSize();
            if (mSelectedPosition == position) {
                if (textSize != mSelectedTextSize) {
                    startAnimator(mDefaultTextSize, mSelectedTextSize);
                }
                return;
            }

            if (textSize != mDefaultTextSize) {
                startAnimator(mSelectedTextSize, mDefaultTextSize);
            }
        }

        private void startAnimator(int start, int end) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
            valueAnimator.addUpdateListener(this);
            valueAnimator.setDuration(100);
            valueAnimator.start();
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) animation.getAnimatedValue());
        }
    }

    /**
     * 数据改变监听器
     */
    private final class TabAdapterDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            refreshLayoutManager();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // default implementation ignored
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            // default implementation ignored
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            refreshLayoutManager();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            refreshLayoutManager();
            if (getSelectedPosition() > positionStart - itemCount) {
                setSelectedPosition(positionStart - itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            // default implementation ignored
        }

        private void refreshLayoutManager() {
            if (!mFixed) {
                return;
            }
            RecyclerView recyclerView = getRecyclerView();
            if (recyclerView == null) {
                return;
            }
            recyclerView.setLayoutManager(generateDefaultLayoutManager(getContext()));
        }
    }

    /**
     * Tab 监听器
     */
    public interface OnTabListener {

        /**
         * Tab 被选中了
         */
        boolean onTabSelected(@NonNull RecyclerView recyclerView, int position);
    }
}