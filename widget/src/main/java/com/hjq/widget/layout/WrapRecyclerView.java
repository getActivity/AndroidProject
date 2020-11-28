package com.hjq.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 支持添加底部和头部的 RecyclerView
 */
public final class WrapRecyclerView extends RecyclerView {

    /** 原有的适配器 */
    private RecyclerView.Adapter mRealAdapter;

    /** 支持添加头部和底部的适配器 */
    private final WrapRecyclerAdapter mWrapAdapter = new WrapRecyclerAdapter();

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mRealAdapter = adapter;
        // 偷梁换柱
        mWrapAdapter.setRealAdapter(mRealAdapter);
        // 禁用条目动画
        setItemAnimator(null);
        super.setAdapter(mWrapAdapter);
    }

    @Override
    public Adapter getAdapter() {
        return mRealAdapter;
    }

    /**
     * 添加头部View
     */
    public void addHeaderView(View view) {
        mWrapAdapter.addHeaderView(view);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V addHeaderView(@LayoutRes int id) {
        View headerView = LayoutInflater.from(getContext()).inflate(id, this, false);
        addHeaderView(headerView);
        return (V) headerView;
    }

    /**
     * 移除头部View
     */
    public void removeHeaderView(View view) {
        mWrapAdapter.removeHeaderView(view);
    }

    /**
     * 添加底部View
     */
    public void addFooterView(View view) {
        mWrapAdapter.addFooterView(view);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V addFooterView(@LayoutRes int id) {
        View footerView = LayoutInflater.from(getContext()).inflate(id, this, false);
        addFooterView(footerView);
        return (V) footerView;
    }

    /**
     * 移除底部View
     */
    public void removeFooterView(View view) {
        mWrapAdapter.removeFooterView(view);
    }

    /**
     * 获取头部View总数
     */
    public int getHeaderViewsCount() {
        return mWrapAdapter.getHeaderViewsCount();
    }

    /**
     * 获取底部View总数
     */
    public int getFooterViewsCount() {
        return mWrapAdapter.getFooterViewsCount();
    }

    /**
     * 获取头部View集合
     */
    public List<View> getHeaderViews() {
        return mWrapAdapter.getHeaderViews();
    }

    /**
     * 获取底部View集合
     */
    public List<View> getFooterViews() {
        return mWrapAdapter.getFooterViews();
    }

    /**
     * 刷新头部和底部布局所有的 View 的状态
     */
    public void refreshHeaderFooterViews() {
        mWrapAdapter.notifyDataSetChanged();
    }

    /**
     * 设置在 GridLayoutManager 模式下头部和尾部都是独占一行的效果
     */
    public void adjustSpanSize() {

        final RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    return (position < mWrapAdapter.getHeaderViewsCount()
                            || position >= mWrapAdapter.getHeaderViewsCount() + (mRealAdapter == null ? 0 : mRealAdapter.getItemCount()))
                            ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 采用装饰设计模式，将原有的适配器包装起来
     */
    private static final class WrapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        /** 头部条目类型 */
        private static final int HEADER_VIEW_TYPE = Integer.MIN_VALUE >> 1;
        /** 底部条目类型 */
        private static final int FOOTER_VIEW_TYPE = Integer.MAX_VALUE >> 1;

        /** 原有的适配器 */
        private RecyclerView.Adapter mRealAdapter;
        /** 头部View集合 */
        private final List<View> mHeaderViews = new ArrayList<>();
        /** 底部View集合 */
        private final List<View> mFooterViews = new ArrayList<>();
        /** 当前调用的位置 */
        private int mCurrentPosition;

        /** RecyclerView对象 */
        private RecyclerView mRecyclerView;

        /** 数据观察者对象 */
        private WrapAdapterDataObserver mObserver;

        private void setRealAdapter(RecyclerView.Adapter adapter) {
            if (mRealAdapter != adapter) {

                if (mRealAdapter != null) {
                    if (mObserver != null) {
                        // 为原有的RecyclerAdapter移除数据监听对象
                        mRealAdapter.unregisterAdapterDataObserver(mObserver);
                    }
                }

                mRealAdapter = adapter;
                if (mRealAdapter != null) {
                    if (mObserver == null) {
                        mObserver = new WrapAdapterDataObserver(this);
                    }
                    // 为原有的RecyclerAdapter添加数据监听对象
                    mRealAdapter.registerAdapterDataObserver(mObserver);
                    // 适配器不是第一次被绑定到RecyclerView上需要发送通知，因为第一次绑定会自动通知
                    if (mRecyclerView != null) {
                        notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mRealAdapter != null) {
                return getHeaderViewsCount() + mRealAdapter.getItemCount() + getFooterViewsCount();
            } else {
                return getHeaderViewsCount() + getFooterViewsCount();
            }
        }

        @SuppressWarnings("all")
        @Override
        public int getItemViewType(int position) {
            mCurrentPosition = position;
            // 获取头部布局的总数
            int headerCount = getHeaderViewsCount();
            // 获取原有适配器的总数
            int adapterCount = mRealAdapter != null ? mRealAdapter.getItemCount() : 0;
            // 获取在原有适配器上的位置
            int adjPosition = position - headerCount;
            if (position < headerCount) {
                return HEADER_VIEW_TYPE;
            } else if (adjPosition < adapterCount) {
                return mRealAdapter.getItemViewType(adjPosition);
            } else {
                return FOOTER_VIEW_TYPE;
            }
        }

        public int getPosition() {
            return mCurrentPosition;
        }

        @SuppressWarnings("all")
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case HEADER_VIEW_TYPE:
                    return newWrapViewHolder(mHeaderViews.get(getPosition()));
                case FOOTER_VIEW_TYPE:
                    return newWrapViewHolder(mFooterViews.get(getPosition() - getHeaderViewsCount() - (mRealAdapter != null ? mRealAdapter.getItemCount() : 0)));
                default:
                    int itemViewType = mRealAdapter.getItemViewType(getPosition() - getHeaderViewsCount());
                    if (itemViewType == HEADER_VIEW_TYPE || itemViewType == FOOTER_VIEW_TYPE) {
                        throw new IllegalStateException("Please do not use this type as itemType");
                    }
                    if (mRealAdapter != null) {
                        return mRealAdapter.onCreateViewHolder(parent, itemViewType);
                    } else {
                        return null;
                    }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case HEADER_VIEW_TYPE:
                case FOOTER_VIEW_TYPE:
                    break;
                default:
                    if (mRealAdapter != null) {
                        mRealAdapter.onBindViewHolder(holder, getPosition() - getHeaderViewsCount());
                    }
                    break;
            }
        }

        private WrapViewHolder newWrapViewHolder(View view) {
            ViewParent parent =  view.getParent();
            if (parent instanceof ViewGroup) {
                // IllegalStateException: ViewHolder views must not be attached when created.
                // Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)
                ((ViewGroup) parent).removeView(view);
            }
            return new WrapViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            if (mRealAdapter != null && position > getHeaderViewsCount() - 1 && position < getHeaderViewsCount() + mRealAdapter.getItemCount()) {
                return mRealAdapter.getItemId(position - getHeaderViewsCount());
            } else {
                return super.getItemId(position);
            }
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            if (mRealAdapter != null) {
                mRealAdapter.onAttachedToRecyclerView(recyclerView);
            }
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            mRecyclerView = null;
            if (mRealAdapter != null) {
                mRealAdapter.onDetachedFromRecyclerView(recyclerView);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onViewRecycled(@NonNull ViewHolder holder) {
            if (holder instanceof WrapViewHolder) {
                // 防止这个 ViewHolder 被 RecyclerView 拿去复用
                holder.setIsRecyclable(false);
                return;
            }
            if (mRealAdapter != null) {
                mRealAdapter.onViewRecycled(holder);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
            if (mRealAdapter != null) {
                return mRealAdapter.onFailedToRecycleView(holder);
            }
            return super.onFailedToRecycleView(holder);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            if (mRealAdapter != null) {
                mRealAdapter.onViewAttachedToWindow(holder);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
            if (mRealAdapter != null) {
                mRealAdapter.onViewDetachedFromWindow(holder);
            }
        }

        /**
         * 添加头部View
         */
        private void addHeaderView(View view) {
            // 不能添加同一个View对象，否则会导致RecyclerView复用异常
            if (!mHeaderViews.contains(view) && !mFooterViews.contains(view)) {
                mHeaderViews.add(view);
                notifyDataSetChanged();
            }
        }

        /**
         * 移除头部View
         */
        private void removeHeaderView(View view) {
            if (mHeaderViews.remove(view)) {
                notifyDataSetChanged();
            }
        }

        /**
         * 添加底部View
         */
        private void addFooterView(View view) {
            // 不能添加同一个View对象，否则会导致RecyclerView复用异常
            if (!mFooterViews.contains(view) && !mHeaderViews.contains(view)) {
                mFooterViews.add(view);
                notifyDataSetChanged();
            }
        }

        /**
         * 移除底部View
         */
        private void removeFooterView(View view) {
            if (mFooterViews.remove(view)) {
                notifyDataSetChanged();
            }
        }

        /**
         * 获取头部View总数
         */
        private int getHeaderViewsCount() {
            return mHeaderViews.size();
        }

        /**
         * 获取底部View总数
         */
        private int getFooterViewsCount() {
            return mFooterViews.size();
        }

        /**
         * 获取头部View集合
         */
        private List<View> getHeaderViews() {
            return mHeaderViews;
        }

        /**
         * 获取底部View集合
         */
        private List<View> getFooterViews() {
            return mFooterViews;
        }
    }

    /**
     * 头部和底部通用的ViewHolder对象
     */
    private static final class WrapViewHolder extends RecyclerView.ViewHolder {

        private WrapViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 数据改变监听器
     */
    private static final class WrapAdapterDataObserver extends RecyclerView.AdapterDataObserver {

        private final WrapRecyclerAdapter mWrapAdapter;

        private WrapAdapterDataObserver(WrapRecyclerAdapter adapter) {
            mWrapAdapter = adapter;
        }

        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onItemRangeChanged(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(mWrapAdapter.getHeaderViewsCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(mWrapAdapter.getHeaderViewsCount() + fromPosition, toPosition);
        }
    }
}