package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hjq.base.BaseRecyclerViewAdapter;
import com.hjq.demo.R;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/05
 *    desc   : 可进行拷贝的副本
 */
public class CopyAdapter extends BaseRecyclerViewAdapter<String, CopyAdapter.ViewHolder> {

    public CopyAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public CopyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_copy);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }
    }
}