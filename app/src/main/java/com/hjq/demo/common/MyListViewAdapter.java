package com.hjq.demo.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hjq.base.BaseListViewAdapter;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中 ListView 适配器基类
 */
public abstract class MyListViewAdapter<T> extends BaseListViewAdapter<MyListViewAdapter.ViewHolder> {

    /** 列表数据 */
    private List<T> mDataSet;
    /** 当前列表的页码，默认为第一页，用于分页加载功能 */
    private int mPageNumber = 1;
    /** 是否是最后一页，默认为false，用于分页加载功能 */
    private boolean mLastPage;
    /** 标记对象 */
    private Object mTag;

    public MyListViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
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
        if (mDataSet == null || mDataSet.size() == 0) {
            return;
        }

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
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        mDataSet.set(position, item);
        notifyDataSetChanged();
    }

    /**
     * 添加单条数据
     */
    public void addItem(T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        addItem(mDataSet.size(), item);
    }

    /**
     * 添加单条数据
     */
    public void addItem(int position, T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

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
     * 获取当前的页码
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    /**
     * 设置当前的页码
     */
    public void setPageNumber(int pageNumber) {
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
    public void setLastPage(boolean lastPage) {
        mLastPage = lastPage;
    }

    /**
     * 获取标记
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * 设置标记
     */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * 显示吐司
     */
    public void toast(CharSequence text) {
        ToastUtils.show(text);
    }

    public void toast(@StringRes int id) {
        ToastUtils.show(id);
    }

    public void toast(Object object) {
        ToastUtils.show(object);
    }

    public abstract class ViewHolder extends BaseListViewAdapter.ViewHolder {

        public ViewHolder(ViewGroup parent, @LayoutRes int id) {
            super(parent, id);
            ButterKnife.bind(getItemView());
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }

    public class SimpleHolder extends ViewHolder {

        public SimpleHolder(ViewGroup parent, @LayoutRes int id) {
            super(parent, id);
        }

        public SimpleHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(int position) {}
    }
}