package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/05
 *    desc   : 可进行拷贝的副本
 */
public final class CopyAdapter extends AppAdapter<String> {

    public CopyAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppViewHolder {

        private ViewHolder() {
            super(R.layout.copy_item);
        }

        @Override
        public void onBindView(int position) {

        }
    }
}