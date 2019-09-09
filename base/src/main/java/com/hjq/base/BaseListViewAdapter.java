package com.hjq.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : ListView 适配器基类
 */
public abstract class BaseListViewAdapter<VH extends BaseListViewAdapter.ViewHolder> extends BaseAdapter {

    /** 上下文对象 */
    private final Context mContext;

    public BaseListViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    public abstract int getItemCount();

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("all")
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

    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.onBindView(position);
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
    public Resources getResources() {
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
    public int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    /**
     * 获取资源图像
     */
    public Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(mContext, id);
    }

    public abstract class ViewHolder {

        private final View itemView;

        public ViewHolder(ViewGroup parent, @LayoutRes int id) {
            this(LayoutInflater.from(parent.getContext()).inflate(id, parent, false));
        }

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public final View getItemView() {
            return itemView;
        }

        public final <V extends View> V findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }

        public abstract void onBindView(int position);
    }
}