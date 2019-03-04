package com.hjq.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : RecyclerView 适配器基类
 */
public abstract class BaseRecyclerViewAdapter
        <T, VH extends BaseRecyclerViewAdapter.ViewHolder>
                        extends RecyclerView.Adapter<VH> {

    // 列表数据
    private List<T> mDataSet;

    // RecyclerView 对象
    private RecyclerView mRecyclerView;
    // 上下文对象，注意不要在构造函数中使用
    private Context mContext;

    // 条目点击事件
    private OnItemClickListener mItemClickListener;
    // 条目长按事件
    private OnItemLongClickListener mItemLongClickListener;
    // RecyclerView 滚动事件
    private OnScrollingListener mScrollingListener;

    // 条目子 View 点击事件
    private SparseArray<OnChildClickListener> mChildClickListeners;
    // 条目子 View 长按事件
    private SparseArray<OnChildLongClickListener> mChildLongClickListeners;

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置新的数据
     */
    public void setData(List<T> data) {
        mDataSet = data;
        notifyDataSetChanged();
    }

    /**
     * 获取当前数据
     */
    public List<T> getData() {
        return mDataSet;
    }

    /**
     * 追加一些数据
     */
    public void addData(List<T> data) {
        if (data == null || data.size() == 0) return;

        if (mDataSet == null || mDataSet.size() == 0) {
            setData(data);
        }else {
            mDataSet.addAll(data);
            notifyItemRangeInserted(mDataSet.size() - data.size(), data.size());
        }
    }

    /**
     * 清空当前数据
     */
    public void clearData() {
        if (mDataSet == null || mDataSet.size() == 0) return;

        mDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取某个位置上的数据
     */
    public T getItem(int position) {
        return mDataSet.get(position);
    }

    /**
     * 更新某个位置上的数据
     */
    public void setItem(int position, T item) {
        if (mDataSet == null) mDataSet = new ArrayList<>();
        mDataSet.set(position, item);
        notifyItemChanged(position);
    }

    /**
     * 添加单条数据
     */
    public void addItem(T item) {
        addItem(mDataSet.size() - 1, item);
    }

    public void addItem(int position, T item) {
        if (mDataSet == null) mDataSet = new ArrayList<>();

        if (position < mDataSet.size()) {
            mDataSet.add(position, item);
        }else {
            mDataSet.add(item);
            position = mDataSet.size() - 1;
        }
        notifyItemInserted(position);
    }

    /**
     * 删除单条数据
     */
    public void removeItem(T item) {
        int index = mDataSet.indexOf(item);
        if (index != -1) {
            removeItem(index);
        }
    }

    public void removeItem(int position) {
        //如果是在for循环删除后要记得i--
        mDataSet.remove(position);
        //告诉适配器删除数据的位置，会有动画效果
        notifyItemRemoved(position);
    }

    /**
     * 获取RecyclerView对象，需要在setAdapter之后绑定
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 获取上下文对象，注意不要在构造方法中调用
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
    public String getString(@StringRes int resId) {
        return mContext.getString(resId);
    }

    /**
     * 获取资源颜色
     */
    protected int getColor(@ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.getColor(id);
        }else {
            return mContext.getResources().getColor(id);
        }
    }

    /**
     * 获取资源图像
     */
    protected Drawable getDrawable(@DrawableRes int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mContext.getDrawable(id);
        }else {
            return mContext.getResources().getDrawable(id);
        }
    }

    /**
     * 条目ViewHolder，需要子类ViewHolder继承
     */
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        // 内存优化和防止泄露
        private SparseArray<WeakReference<View>> mViews = new SparseArray<>();

        public ViewHolder(ViewGroup parent, int layoutId) {
            this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);
            initViewListener();
        }

        /**
         * 初始化 View 的监听
         */
        protected void initViewListener() {

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

        public final <V extends View> V findViewById(@IdRes int viewId) {
            WeakReference<View> reference = mViews.get(viewId);
            if (reference != null && reference.get() != null) {
                return (V) reference.get();
            }else {
                View view = getItemView().findViewById(viewId);
                mViews.put(viewId, new WeakReference<>(view));
                return (V) view;
            }
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
    public void setOnItemClickListener(OnItemClickListener l) {
        checkRecyclerViewState();
        mItemClickListener = l;
    }

    /**
     * 设置 RecyclerView 条目子 View 点击监听
     */
    public void setOnChildClickListener(@IdRes int childId, OnChildClickListener l) {
        checkRecyclerViewState();
        if (mChildClickListeners == null) {
            mChildClickListeners = new SparseArray<>();
        }
        mChildClickListeners.put(childId, l);
    }

    /**
     * 设置RecyclerView条目长按监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        checkRecyclerViewState();
        mItemLongClickListener = l;
    }

    /**
     * 设置 RecyclerView 条目子 View 长按监听
     */
    public void setOnChildLongClickListener(@IdRes int childId, OnChildLongClickListener l) {
        checkRecyclerViewState();
        if (mChildLongClickListeners == null) {
            mChildLongClickListeners = new SparseArray<>();
        }
        mChildLongClickListeners.put(childId, l);
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
    public void setOnScrollingListener(OnScrollingListener l) {
        mScrollingListener = l;

        //如果当前已经有设置滚动监听，再次设置需要移除原有的监听器
        if (mScrollListener == null) {
            mScrollListener = new ScrollListener();
        }else {
            mRecyclerView.removeOnScrollListener(mScrollListener);
        }
        //用户设置了滚动监听，需要给RecyclerView设置监听
        if (mRecyclerView != null) {
            //添加滚动监听
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
    }

    //自定义滚动监听器
    private ScrollListener mScrollListener;

    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

            if (mScrollingListener == null) return;

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                if (!recyclerView.canScrollVertically(1)) {
                    //是否能向下滚动，false表示已经滚动到底部
                    mScrollingListener.onScrollDown(recyclerView);
                }else if (!recyclerView.canScrollVertically(-1)) {
                    //是否能向上滚动，false表示已经滚动到顶部
                    mScrollingListener.onScrollTop(recyclerView);
                }

            }else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
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
         */
        void onScrollTop(RecyclerView recyclerView);

        /**
         * 列表滚动到最底部
         */
        void onScrollDown(RecyclerView recyclerView);

        /**
         * 列表滚动中
         */
        void onScrolling(RecyclerView recyclerView);
    }

    /**
     * RecyclerView 条目点击监听类
     */
    public interface OnItemClickListener{

        /**
         * 当 RecyclerView 某个条目被点击时回调
         *
         * @param itemView      被点击的条目对象
         * @param position      被点击的条目位置
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
         * @param itemView      被点击的条目对象
         * @param position      被点击的条目位置
         * @return              是否拦截事件
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
         * @param childView        被点击的条目子 View Id
         * @param position      被点击的条目位置
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
         * @param childView        被点击的条目子 View Id
         * @param position      被点击的条目位置
         */
        void onChildLongClick(RecyclerView recyclerView, View childView, int position);
    }
}