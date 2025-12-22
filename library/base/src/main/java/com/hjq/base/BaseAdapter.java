package com.hjq.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.core.action.ResourcesAction;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : RecyclerView 适配器技术基类
 */
public abstract class BaseAdapter<VH extends BaseAdapter<?>.BaseViewHolder>
        extends RecyclerView.Adapter<VH> implements ResourcesAction {

    /** 上下文对象 */
    @NonNull
    private final Context mContext;

    /** RecyclerView 对象 */
    @Nullable
    private RecyclerView mRecyclerView;

    /** 条目点击监听器 */
    @Nullable
    private OnItemClickListener mItemClickListener;
    /** 条目长按监听器 */
    @Nullable
    private OnItemLongClickListener mItemLongClickListener;

    /** 条目子 View 点击监听器 */
    @Nullable
    private SparseArray<OnChildClickListener> mChildClickListeners;
    /** 条目子 View 长按监听器 */
    @Nullable
    private SparseArray<OnChildLongClickListener> mChildLongClickListeners;

    public BaseAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        holder.onBindView(position);
    }

    /**
     * 获取 RecyclerView 对象
     */
    @Nullable
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @NonNull
    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetached();
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    /**
     * 条目 ViewHolder，需要子类 ViewHolder 继承
     */
    public abstract class BaseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public BaseViewHolder(@LayoutRes int id) {
            this(LayoutInflater.from(getContext()).inflate(id, mRecyclerView, false));
        }

        public BaseViewHolder(View itemView) {
            super(itemView);

            // 设置条目的点击和长按事件
            if (mItemClickListener != null) {
                itemView.setOnClickListener(this);
            }
            if (mItemLongClickListener != null) {
                itemView.setOnLongClickListener(this);
            }

            // 设置条目子 View 点击事件
            if (mChildClickListeners != null) {
                for (int i = 0; i < mChildClickListeners.size(); i++) {
                    View childView = findViewById(mChildClickListeners.keyAt(i));
                    if (childView == null) {
                        continue;
                    }
                    childView.setOnClickListener(this);
                }
            }

            // 设置条目子 View 长按事件
            if (mChildLongClickListeners != null) {
                for (int i = 0; i < mChildLongClickListeners.size(); i++) {
                    View childView = findViewById(mChildLongClickListeners.keyAt(i));
                    if (childView == null) {
                        continue;
                    }
                    childView.setOnLongClickListener(this);
                }
            }
        }

        /**
         * 数据绑定回调
         */
        public abstract void onBindView(int position);

        /**
         * ViewHolder 绑定到窗口回调
         */
        public void onAttached() {
            // default implementation ignored
        }

        /**
         * ViewHolder 从窗口解绑回调
         */
        public void onDetached() {
            // default implementation ignored
        }

        /**
         * ViewHolder 回收回调
         */
        public void onRecycled() {
            // default implementation ignored
        }

        /**
         * 获取 ViewHolder 位置
         */
        protected int getViewHolderPosition() {
            // 这里解释一下为什么用 getLayoutPosition 而不用 getAdapterPosition
            // 如果是使用 getAdapterPosition 会导致一个问题，那就是快速点击删除条目的时候会出现 -1 的情况，因为这个 ViewHolder 已经解绑了
            // 而使用 getLayoutPosition 则不会出现位置为 -1 的情况，因为解绑之后在布局中不会立马消失，所以不用担心在动画执行中获取位置有异常的情况
            return getLayoutPosition();
        }

        /**
         * {@link View.OnClickListener}
         */

        @Override
        public void onClick(@NonNull View view) {
            int position = getViewHolderPosition();
            if (position < 0 || position >= getItemCount()) {
                return;
            }

            if (view == getItemView()) {
                if (mItemClickListener != null && mRecyclerView != null) {
                    mItemClickListener.onItemClick(mRecyclerView, view, position);
                }
                return;
            }

            if (mChildClickListeners != null && mRecyclerView != null) {
                OnChildClickListener listener = mChildClickListeners.get(view.getId());
                if (listener != null) {
                    listener.onChildClick(mRecyclerView, view, position);
                }
            }
        }

        /**
         * {@link View.OnLongClickListener}
         */

        @Override
        public boolean onLongClick(@NonNull View view) {
            int position = getViewHolderPosition();
            if (position < 0 || position >= getItemCount()) {
                return false;
            }

            if (view == getItemView()) {
                if (mItemLongClickListener != null && mRecyclerView != null) {
                    return mItemLongClickListener.onItemLongClick(mRecyclerView, view, position);
                }
                return false;
            }

            if (mChildLongClickListeners != null && mRecyclerView != null) {
                OnChildLongClickListener listener = mChildLongClickListeners.get(view.getId());
                if (listener != null) {
                    return listener.onChildLongClick(mRecyclerView, view, position);
                }
            }
            return false;
        }

        @NonNull
        public final View getItemView() {
            return itemView;
        }

        public final <V extends View> V findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        // 判断当前的布局管理器是否为空，如果为空则设置默认的布局管理器
        if (layoutManager != null) {
            return;
        }
        mRecyclerView.setLayoutManager(generateDefaultLayoutManager(mContext));
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    /**
     * 生成默认的布局摆放器
     */
    @NonNull
    protected RecyclerView.LayoutManager generateDefaultLayoutManager(@NonNull Context context) {
        return new LinearLayoutManager(context);
    }

    /**
     * 设置 RecyclerView 条目点击监听
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        checkListenerEffective();
        mItemClickListener = listener;
    }

    /**
     * 设置 RecyclerView 条目子 View 点击监听
     */
    public void setOnChildClickListener(@IdRes int id, @Nullable OnChildClickListener listener) {
        checkListenerEffective();
        if (mChildClickListeners == null) {
            mChildClickListeners = new SparseArray<>();
        }
        mChildClickListeners.put(id, listener);
    }

    /**
     * 设置 RecyclerView 条目长按监听
     */
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        checkListenerEffective();
        mItemLongClickListener = listener;
    }

    /**
     * 设置 RecyclerView 条目子 View 长按监听
     */
    public void setOnChildLongClickListener(@IdRes int id, @Nullable OnChildLongClickListener listener) {
        checkListenerEffective();
        if (mChildLongClickListeners == null) {
            mChildLongClickListeners = new SparseArray<>();
        }
        mChildLongClickListeners.put(id, listener);
    }

    /**
     * 检查监听器是否有效
     */
    private void checkListenerEffective() {
        if (mRecyclerView == null) {
            return;
        }
        // 必须在 RecyclerView.setAdapter() 之前设置监听
        throw new IllegalStateException("You must set the listener before RecyclerView.setAdapter()");
    }

    /**
     * RecyclerView 条目点击监听类
     */
    public interface OnItemClickListener{

        /**
         * 当 RecyclerView 某个条目被点击时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param itemView          被点击的条目对象
         * @param position          被点击的条目位置
         */
        void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position);
    }

    /**
     * RecyclerView 条目长按监听类
     */
    public interface OnItemLongClickListener {

        /**
         * 当 RecyclerView 某个条目被长按时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param itemView          被点击的条目对象
         * @param position          被点击的条目位置
         * @return                  是否拦截事件
         */
        boolean onItemLongClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position);
    }

    /**
     * RecyclerView 条目子 View 点击监听类
     */
    public interface OnChildClickListener {

        /**
         * 当 RecyclerView 某个条目 子 View 被点击时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param childView         被点击的条目子 View
         * @param position          被点击的条目位置
         */
        void onChildClick(@NonNull RecyclerView recyclerView, @NonNull View childView, int position);
    }

    /**
     * RecyclerView 条目子 View 长按监听类
     */
    public interface OnChildLongClickListener {

        /**
         * 当 RecyclerView 某个条目子 View 被长按时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param childView         被点击的条目子 View
         * @param position          被点击的条目位置
         */
        boolean onChildLongClick(@NonNull RecyclerView recyclerView, @NonNull View childView, int position);
    }
}