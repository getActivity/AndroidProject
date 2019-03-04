package com.hjq.demo.common;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.base.BaseListViewAdapter;
import com.hjq.image.ImageLoader;

import butterknife.ButterKnife;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中 ListView 适配器基类
 */
public abstract class MyListViewAdapter<T, VH extends MyListViewAdapter.ViewHolder> extends BaseListViewAdapter<T, VH> {

    //当前列表的页码，默认为第一页，用于分页加载功能
    private int mPageNumber = 1;
    //是否是最后一页，默认为false，用于分页加载功能
    private boolean mLastPage;
    //标记对象
    private Object mTag;

    public MyListViewAdapter(Context context) {
        super(context);
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

    public class ViewHolder extends BaseListViewAdapter.ViewHolder {

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }

        public final ViewHolder setText(@IdRes int viewId, @StringRes int resId) {
            return setText(viewId, getItemView().getResources().getString(resId));
        }

        public final ViewHolder setText(@IdRes int viewId, String text) {
            if (text == null) text = "";
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public final ViewHolder setVisibility(@IdRes int viewId, int visibility) {
            View view = findViewById(viewId);
            if (view != null) {
                view.setVisibility(visibility);
            }
            return this;
        }

        public final ViewHolder setColor(@IdRes int viewId, @ColorInt int color) {
            View view = findViewById(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
            return this;
        }

        public final ViewHolder setImage(@IdRes int viewId, @DrawableRes int resId) {
            View view = findViewById(viewId);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public final ViewHolder setImage(@IdRes int viewId, String url) {
            View view = findViewById(viewId);
            if (view instanceof ImageView) {
                ImageLoader.loadImage((ImageView) view, url);
            }
            return this;
        }

        public final ViewHolder setChecked(@IdRes int viewId, boolean checked) {
            View view = findViewById(viewId);
            if (view instanceof CompoundButton) {
                ((CompoundButton) view).setChecked(checked);
            }
            return this;
        }
    }
}