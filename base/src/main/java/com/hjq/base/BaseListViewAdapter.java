package com.hjq.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : ListView 适配器基类
 */
public abstract class BaseListViewAdapter<VH extends BaseListViewAdapter.ViewHolder> extends BaseAdapter {

    // 上下文对象
    private Context mContext;

    public BaseListViewAdapter(Context context) {
        mContext = context;
    }

    /**
     * 获取上下文对象，注意不要在构造方法中调用
     */
    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return getItemCount();
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

    public abstract int getItemCount();

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