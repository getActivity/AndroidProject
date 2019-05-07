package com.hjq.demo.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.hjq.image.ImageLoader;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/05
 *    desc   : 图片加载适配器
 */
public final class PhotoPagerAdapter extends PagerAdapter implements View.OnClickListener {

    private Activity mActivity;
    private List<String> mData;

    public PhotoPagerAdapter(Activity activity, List<String> data) {
        mActivity = activity;
        mData = data;
    }

    // 加载数量，自动回调
    @Override
    public int getCount() {
        return mData.size();
    }

    // 返回真表示不会重新创建，使用缓存加载。返回假则重新创建
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 实例化条目
     * ViewPager预加载机制：最多保存3个page，超过的将需要被销毁掉
     * 由于最多3个page，所以不需要设置ViewHolder
     * 用于将数据设置给ViewItem
     */

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView view = new PhotoView(mActivity);
        view.setOnClickListener(this);
        ImageLoader.loadImage(view, mData.get(position));
        // 将View添加到ViewPager
        container.addView(view);
        return view;
    }

    // 销毁条目
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onClick(View v) {
        // 点击退出当前的 Activity
        mActivity.finish();
    }
}