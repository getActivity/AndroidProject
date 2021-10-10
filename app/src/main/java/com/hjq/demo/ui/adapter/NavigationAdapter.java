package com.hjq.demo.ui.adapter;

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

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/02/28
 *    desc   : 导航栏适配器
 */
public final class NavigationAdapter extends AppAdapter<NavigationAdapter.MenuItem>
        implements BaseAdapter.OnItemClickListener {

    /** 当前选中条目位置 */
    private int mSelectedPosition = 0;

    /** 导航栏点击监听 */
    @Nullable
    private OnNavigationListener mListener;

    public NavigationAdapter(Context context) {
        super(context);
        setOnItemClickListener(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    @Override
    protected RecyclerView.LayoutManager generateDefaultLayoutManager(Context context) {
        return new GridLayoutManager(context, getCount(), RecyclerView.VERTICAL, false);
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

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

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        if (mSelectedPosition == position) {
            return;
        }

        if (mListener == null) {
            mSelectedPosition = position;
            notifyDataSetChanged();
            return;
        }

        if (mListener.onNavigationItemSelected(position)) {
            mSelectedPosition = position;
            notifyDataSetChanged();
        }
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView mIconView;
        private final TextView mTitleView;

        private ViewHolder() {
            super(R.layout.home_navigation_item);
            mIconView = findViewById(R.id.iv_home_navigation_icon);
            mTitleView = findViewById(R.id.tv_home_navigation_title);
        }

        @Override
        public void onBindView(int position) {
            MenuItem item = getItem(position);
            mIconView.setImageDrawable(item.getDrawable());
            mTitleView.setText(item.getText());
            mIconView.setSelected(mSelectedPosition == position);
            mTitleView.setSelected(mSelectedPosition == position);
        }
    }

    public static class MenuItem {

        private final String mText;
        private final Drawable mDrawable;

        public MenuItem(String text, Drawable drawable) {
            mText = text;
            mDrawable = drawable;
        }

        public String getText() {
            return mText;
        }

        public Drawable getDrawable() {
            return mDrawable;
        }
    }

    public interface OnNavigationListener {

        boolean onNavigationItemSelected(int position);
    }
}