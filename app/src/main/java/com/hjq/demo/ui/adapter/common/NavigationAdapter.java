package com.hjq.demo.ui.adapter.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.ui.adapter.common.NavigationAdapter.NavigationItem;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/02/28
 *    desc   : 导航栏适配器
 */
public final class NavigationAdapter extends AppAdapter<NavigationItem>
        implements BaseAdapter.OnItemClickListener {

    /** 当前选中条目位置 */
    private int mSelectedPosition = 0;

    /** 导航栏点击监听 */
    @Nullable
    private OnNavigationListener mListener;

    public NavigationAdapter(@NonNull Context context) {
        super(context);
        setOnItemClickListener(this);
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    @NonNull
    @Override
    protected RecyclerView.LayoutManager generateDefaultLayoutManager(@NonNull Context context) {
        return new GridLayoutManager(context, getCount(), RecyclerView.VERTICAL, false);
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 设置导航栏监听
     */
    public void setOnNavigationListener(@Nullable OnNavigationListener listener) {
        mListener = listener;
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     */

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
        if (mSelectedPosition == position) {
            return;
        }

        if (mListener == null || mListener.onNavigationItemSelected(position)) {
            mSelectedPosition = position;
            notifyDataSetChanged();
        }
    }

    private final class ViewHolder extends AppViewHolder {

        private final ImageView mIconView;
        private final TextView mTitleView;

        private ViewHolder() {
            super(R.layout.home_navigation_item);
            mIconView = findViewById(R.id.iv_home_navigation_icon);
            mTitleView = findViewById(R.id.tv_home_navigation_title);
        }

        @Override
        public void onBindView(int position) {
            NavigationItem item = getItem(position);
            mIconView.setImageDrawable(item.drawable);
            mTitleView.setText(item.text);
            mIconView.setSelected(mSelectedPosition == position);
            mTitleView.setSelected(mSelectedPosition == position);
        }
    }

    public static class NavigationItem {

        public final String text;
        public final Drawable drawable;

        public NavigationItem(String text, Drawable drawable) {
            this.text = text;
            this.drawable = drawable;
        }
    }

    public interface OnNavigationListener {

        boolean onNavigationItemSelected(int position);
    }
}