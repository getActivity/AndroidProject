package com.hjq.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : ListView 适配器基类
 */
public abstract class BaseListViewAdapter
        <T, VH extends BaseListViewAdapter.ViewHolder>
                        extends BaseAdapter {

    // 列表数据
    private List<T> mDataSet;

    // 上下文对象
    private Context mContext;

    public BaseListViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VH holder;
        if (convertView == null) {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            holder.getItemView().setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        return holder.getItemView();
    }

    @NonNull
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(@NonNull VH holder, int position);

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
    @Nullable
    public List<T> getData() {
        return mDataSet;
    }

    /**
     * 追加一些数据
     */
    public void addData(List<T> data) {
        if (mDataSet != null) {
            mDataSet.addAll(data);
        } else {
            mDataSet = data;
        }
        notifyDataSetChanged();
    }

    /**
     * 清空当前数据
     */
    public void clearData() {
        //当前的数据不能为空
        if (mDataSet == null || mDataSet.size() == 0) return;

        mDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取某个位置上的数据
     */
    @Override
    public T getItem(int position) {
        return mDataSet.get(position);
    }

    /**
     * 更新某个位置上的数据
     */
    public void setItem(int position, T item) {
        if (mDataSet == null) mDataSet = new ArrayList<>();
        mDataSet.set(position, item);
        notifyDataSetChanged();
    }

    /**
     * 添加单条数据
     */
    public void addItem(T item) {
        if (mDataSet == null) mDataSet = new ArrayList<>();

        addItem(mDataSet.size(), item);
    }

    /**
     * 添加单条数据
     */
    public void addItem(int position, T item) {
        if (mDataSet == null) mDataSet = new ArrayList<>();

        //如果是在for循环添加后要记得position++
        if (position < mDataSet.size()) {
            mDataSet.add(position, item);
        } else {
            mDataSet.add(item);
        }
        notifyDataSetChanged();
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
        notifyDataSetChanged();
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
    public Resources getResources() {
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
    public int getColor(@ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.getColor(id);
        }else {
            return mContext.getResources().getColor(id);
        }
    }

    /**
     * 获取资源图像
     */
    public Drawable getDrawable(@DrawableRes int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mContext.getDrawable(id);
        }else {
            return mContext.getResources().getDrawable(id);
        }
    }

    public class ViewHolder {

        private final View itemView;

        // 内存优化和防止泄露
        private SparseArray<WeakReference<View>> mViews = new SparseArray<>();

        public ViewHolder(ViewGroup parent, int layoutId) {
            this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        }

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public final View getItemView() {
            return itemView;
        }

        @SuppressWarnings("unchecked")
        public final <V extends View> V findViewById(@IdRes int id) {
            WeakReference<View> reference = mViews.get(id);
            if (reference != null && reference.get() != null) {
                return (V) reference.get();
            }else {
                View view = itemView.findViewById(id);
                mViews.put(id, new WeakReference<>(view));
                return (V) view;
            }
        }
    }
}