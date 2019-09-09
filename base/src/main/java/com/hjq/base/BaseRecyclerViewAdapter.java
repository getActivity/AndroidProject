package com.hjq.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : RecyclerView 适配器基类
 */
public abstract class BaseRecyclerViewAdapter<VH extends BaseRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    /** 上下文对象 */
    private final Context mContext;

    /** RecyclerView 对象 */
    private RecyclerView mRecyclerView;

    /** 条目点击事件 */
    private OnItemClickListener mItemClickListener;
    /** 条目长按事件 */
    private OnItemLongClickListener mItemLongClickListener;
    /** RecyclerView 滚动事件 */
    private OnScrollingListener mScrollingListener;

    /** 条目子 View 点击事件 */
    private SparseArray<OnChildClickListener> mChildClickListeners;
    /** 条目子 View 长按事件 */
    private SparseArray<OnChildLongClickListener> mChildLongClickListeners;

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.onBindView(position);
    }

    /**
     * 获取RecyclerView对象，需要在setAdapter之后绑定
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 获取上下文对象
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取资源对象
     */
    protected Resources getResources() {
        return mContext.getResources();
    }

    /**
     * 获取资源文本
     */
    public String getString(@StringRes int id) {
        return mContext.getString(id);
    }

    /**
     * 获取资源颜色
     */
    protected int getColor(@ColorRes int id) {
        return ContextCompat.getColor(mContext, id);
    }

    /**
     * 获取资源图像
     */
    protected Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(mContext, id);
    }

    /**
     * 条目 ViewHolder，需要子类 ViewHolder 继承
     */
    public abstract class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(@LayoutRes int id) {
            this(LayoutInflater.from(getContext()).inflate(id, getRecyclerView(), false));
        }

        public ViewHolder(View itemView) {
            super(itemView);

            // 设置条目的点击和长按事件
            if (mItemClickListener != null) {
                getItemView().setOnClickListener(this);
            }
            if (mItemLongClickListener != null) {
                getItemView().setOnLongClickListener(this);
            }

            // 设置条目子 View 点击和长按事件
            if (mChildClickListeners != null) {
                for (int i = 0; i < mChildClickListeners.size(); i++) {
                    View childView = findViewById(mChildClickListeners.keyAt(i));
                    if (childView != null) {
                        childView.setOnClickListener(this);
                    }
                }
            }
            if (mChildLongClickListeners != null) {
                for (int i = 0; i < mChildLongClickListeners.size(); i++) {
                    View childView = findViewById(mChildLongClickListeners.keyAt(i));
                    if (childView != null) {
                        childView.setOnLongClickListener(this);
                    }
                }
            }
        }

        public abstract void onBindView(int position);

        /**
         * {@link View.OnClickListener}
         */

        @Override
        public void onClick(View v) {
            if (v == getItemView()) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(mRecyclerView, v, getLayoutPosition());
                    return;
                }
            }
            if (mChildClickListeners != null) {
                OnChildClickListener childClickListener = mChildClickListeners.get(v.getId());
                if (childClickListener != null) {
                    childClickListener.onChildClick(mRecyclerView, v, getLayoutPosition());
                }
            }
        }

        /**
         * {@link View.OnLongClickListener}
         */

        @Override
        public boolean onLongClick(View v) {
            if (v == getItemView()) {
                if (mItemLongClickListener != null) {
                    return mItemLongClickListener.onItemLongClick(mRecyclerView, v, getLayoutPosition());
                }
            }
            if (mChildLongClickListeners != null) {
                OnChildLongClickListener childClickLongListener = mChildLongClickListeners.get(v.getId());
                if (childClickLongListener != null) {
                    childClickLongListener.onChildLongClick(mRecyclerView, v, getLayoutPosition());
                }
            }
            return false;
        }

        public final View  getItemView() {
            return itemView;
        }

        public final <V extends View> V findViewById(@IdRes int id) {
            return getItemView().findViewById(id);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        //用户设置了滚动监听，需要给RecyclerView设置监听
        if (mScrollListener != null) {
            //添加滚动监听
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
        //判断当前的布局管理器是否为空，如果为空则设置默认的布局管理器
        if (mRecyclerView.getLayoutManager() == null) {
            RecyclerView.LayoutManager manager = getDefaultLayoutManager(mContext);
            if (manager != null) {
                mRecyclerView.setLayoutManager(manager);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        //移除滚动监听
        if (mScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mScrollListener);
        }
        mRecyclerView = null;
    }

    /**
     * 获取默认的布局摆放器
     */
    protected RecyclerView.LayoutManager getDefaultLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    /**
     * 设置RecyclerView条目点击监听
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        checkRecyclerViewState();
        mItemClickListener = listener;
    }

    /**
     * 设置 RecyclerView 条目子 View 点击监听
     */
    public void setOnChildClickListener(@IdRes int id, OnChildClickListener listener) {
        checkRecyclerViewState();
        if (mChildClickListeners == null) {
            mChildClickListeners = new SparseArray<>();
        }
        mChildClickListeners.put(id, listener);
    }

    /**
     * 设置RecyclerView条目长按监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        checkRecyclerViewState();
        mItemLongClickListener = listener;
    }

    /**
     * 设置 RecyclerView 条目子 View 长按监听
     */
    public void setOnChildLongClickListener(@IdRes int id, OnChildLongClickListener listener) {
        checkRecyclerViewState();
        if (mChildLongClickListeners == null) {
            mChildLongClickListeners = new SparseArray<>();
        }
        mChildLongClickListeners.put(id, listener);
    }

    private void checkRecyclerViewState() {
        if (mRecyclerView != null) {
            // 必须在 RecyclerView.setAdapter() 之前设置监听
            throw new IllegalStateException("Binding adapters is not allowed before setting listeners");
        }
    }

    /**
     * 设置RecyclerView条目滚动监听
     */
    public void setOnScrollingListener(OnScrollingListener listener) {
        mScrollingListener = listener;

        //如果当前已经有设置滚动监听，再次设置需要移除原有的监听器
        if (mScrollListener == null) {
            mScrollListener = new ScrollListener();
        } else {
            mRecyclerView.removeOnScrollListener(mScrollListener);
        }
        //用户设置了滚动监听，需要给RecyclerView设置监听
        if (mRecyclerView != null) {
            //添加滚动监听
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
    }

    /** 自定义滚动监听器 */
    private ScrollListener mScrollListener;

    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

            if (mScrollingListener == null) {
                return;
            }

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                if (!recyclerView.canScrollVertically(1)) {
                    //是否能向下滚动，false表示已经滚动到底部
                    mScrollingListener.onScrollDown(recyclerView);
                } else if (!recyclerView.canScrollVertically(-1)) {
                    //是否能向上滚动，false表示已经滚动到顶部
                    mScrollingListener.onScrollTop(recyclerView);
                }

            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                //正在滚动中
                mScrollingListener.onScrolling(recyclerView);
            }
        }
    }

    /**
     * RecyclerView 滚动监听类
     */
    public interface OnScrollingListener {

        /**
         * 列表滚动到最顶部
         *
         * @param recyclerView      RecyclerView对象
         */
        void onScrollTop(RecyclerView recyclerView);

        /**
         * 列表滚动中
         *
         * @param recyclerView      RecyclerView对象
         */
        void onScrolling(RecyclerView recyclerView);

        /**
         * 列表滚动到最底部
         *
         * @param recyclerView      RecyclerView对象
         */
        void onScrollDown(RecyclerView recyclerView);
    }

    /**
     * RecyclerView 条目点击监听类
     */
    public interface OnItemClickListener{

        /**
         * 当 RecyclerView 某个条目被点击时回调
         *
         * @param recyclerView      RecyclerView对象
         * @param itemView          被点击的条目对象
         * @param position          被点击的条目位置
         */
        void onItemClick(RecyclerView recyclerView, View itemView, int position);
    }

    /**
     * RecyclerView 条目长按监听类
     */
    public interface OnItemLongClickListener {

        /**
         * 当 RecyclerView 某个条目被长按时回调
         *
         * @param recyclerView      RecyclerView对象
         * @param itemView          被点击的条目对象
         * @param position          被点击的条目位置
         * @return                  是否拦截事件
         */
        boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position);
    }

    /**
     * RecyclerView 条目子 View 点击监听类
     */
    public interface OnChildClickListener {

        /**
         * 当 RecyclerView 某个条目 子 View 被点击时回调
         *
         * @param recyclerView      RecyclerView对象
         * @param childView         被点击的条目子 View Id
         * @param position          被点击的条目位置
         */
        void onChildClick(RecyclerView recyclerView, View childView, int position);
    }

    /**
     * RecyclerView 条目子 View 长按监听类
     */
    public interface OnChildLongClickListener {

        /**
         * 当 RecyclerView 某个条目子 View 被长按时回调
         *
         * @param recyclerView      RecyclerView对象
         * @param childView         被点击的条目子 View Id
         * @param position          被点击的条目位置
         */
        void onChildLongClick(RecyclerView recyclerView, View childView, int position);
    }
}