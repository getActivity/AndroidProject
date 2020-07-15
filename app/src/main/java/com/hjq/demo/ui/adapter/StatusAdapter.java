package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.demo.R;
import com.hjq.demo.common.MyAdapter;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/22
 *    desc   : 状态数据列表
 */
public final class StatusAdapter extends MyAdapter<String> {

    public StatusAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends MyAdapter.ViewHolder {

        private TextView mTextView;

        private ViewHolder() {
            super(R.layout.status_item);
            mTextView = (TextView) findViewById(R.id.tv_status_text);
        }

        @Override
        public void onBindView(int position) {
            mTextView.setText(getItem(position));
        }
    }
}