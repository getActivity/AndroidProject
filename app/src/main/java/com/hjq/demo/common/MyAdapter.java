package com.hjq.demo.common;

import android.content.Context;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.base.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/19
 *    desc   : 项目中 RecyclerView 适配器基类
 */
public abstract class MyAdapter<T> extends BaseAdapter<BaseAdapter.ViewHolder> {

    /** 列表数据 */
    private List<T> mDataSet;
    /** 当前列表的页码，默认为第一页，用于分页加载功能 */
    private int mPageNumber = 1;
    /** 是否是最后一页，默认为false，用于分页加载功能 */
    private boolean mLastPage;
    /** 标记对象 */
    private Object mTag;

    public MyAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    /**
     * 设置新的数据
     */
    public void setData(@Nullable List<T> data) {
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
        if (data == null || data.size() == 0) {
            return;
        }

        if (mDataSet == null || mDataSet.size() == 0) {
            setData(data);
        } else {
            mDataSet.addAll(data);
            notifyItemRangeInserted(mDataSet.size() - data.size(), data.size());
        }
    }

    /**
     * 清空当前数据
     */
    public void clearData() {
        if (mDataSet == null || mDataSet.size() == 0) {
            return;
        }

        mDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取某个位置上的数据
     */
    public T getItem(@IntRange(from = 0) int position) {
        return mDataSet.get(position);
    }

    /**
     * 更新某个位置上的数据
     */
    public void setItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        mDataSet.set(position, item);
        notifyItemChanged(position);
    }

    /**
     * 添加单条数据
     */
    public void addItem(@NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        addItem(mDataSet.size(), item);
    }

    public void addItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        if (position < mDataSet.size()) {
            mDataSet.add(position, item);
        } else {
            mDataSet.add(item);
            position = mDataSet.size() - 1;
        }
        notifyItemInserted(position);
    }

    /**
     * 删除单条数据
     */
    public void removeItem(@NonNull T item) {
        int index = mDataSet.indexOf(item);
        if (index != -1) {
            removeItem(index);
        }
    }

    public void removeItem(@IntRange(from = 0) int position) {
        // 如果是在for循环删除后要记得i--
        mDataSet.remove(position);
        // 告诉适配器删除数据的位置，会有动画效果
        notifyItemRemoved(position);
    }

    /**
     * 获取当前的页码
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    /**
     * 设置当前的页码
     */
    public void setPageNumber(@IntRange(from = 0)int pageNumber) {
        mPageNumber = pageNumber;
    }

    /**
     * 当前是否为最后一页
     */
    public boolean isLastPage() {
        return mLastPage;
    }

    /**
     * 设置是否为最后一页
     */
    public void setLastPage(boolean flag) {
        mLastPage = flag;
    }

    /**
     * 获取标记
     */
    @Nullable
    public Object getTag() {
        return mTag;
    }

    /**
     * 设置标记
     */
    public void setTag(@NonNull Object tag) {
        mTag = tag;
    }

    public final class SimpleHolder extends ViewHolder {

        public SimpleHolder(@LayoutRes int id) {
            super(id);
        }

        public SimpleHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(int position) {}
    }
}