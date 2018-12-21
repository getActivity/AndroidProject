package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.hjq.demo.R;
import com.hjq.demo.common.MyRecyclerViewAdapter;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/05
 *    desc   : 可进行拷贝的副本
 */
public class CopyAdapter extends MyRecyclerViewAdapter<String, CopyAdapter.ViewHolder> {

    public CopyAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @NonNull
    @Override
    public CopyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_copy);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    class ViewHolder extends MyRecyclerViewAdapter.ViewHolder {

        ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }
    }
}