package com.hjq.baselibrary.base;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : ListView适配器基类
 */
public abstract class BaseListViewAdapter<T, VH extends BaseListViewAdapter.ViewHolder> extends BaseAdapter {

    private List<T> mDataSet;
    private Context mContext;

    public BaseListViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            convertView = holder.getItemView();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        return holder.getItemView();
    }

    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(ViewHolder holder, int position);

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
        addItem(mDataSet.size() - 1, item);
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

    public Context getContext() {
        return mContext;
    }

    public static class ViewHolder {

        public final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public final View getItemView() {
            return itemView;
        }

        public final <T extends View> T findView(@IdRes int id) {
            return (T) itemView.findViewById(id);
        }

        public final ViewHolder setText(@IdRes int id, String text) {
            if (text == null) text = "";
            View view = findView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public final ViewHolder setVisibility(@IdRes int id, int visibility) {
            View view = findView(id);
            if (view != null) {
                view.setVisibility(visibility);
            }
            return this;
        }

        public final ViewHolder setColor(@IdRes int id, @ColorInt int color) {
            View view = findView(id);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
            return this;
        }

        public final ViewHolder setImage(@IdRes int id, @DrawableRes int resID) {
            View view = findView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resID);
            }
            return this;
        }
    }
}