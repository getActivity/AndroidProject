package com.hjq.base;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : FragmentPagerAdapter 封装
 */
public final class FragmentPagerAdapter<F extends Fragment> extends androidx.fragment.app.FragmentPagerAdapter {

    /** Fragment 集合 */
    private final List<F> mFragmentSet = new ArrayList<>();
    /** Fragment 标题 */
    private final List<CharSequence> mFragmentTitle = new ArrayList<>();

    /** 当前显示的Fragment */
    private F mShowFragment;

    /** 当前 ViewPager */
    private ViewPager mViewPager;

    /** 设置成懒加载模式 */
    private boolean mLazyMode = true;

    public FragmentPagerAdapter(FragmentActivity activity) {
        this(activity.getSupportFragmentManager());
    }

    public FragmentPagerAdapter(Fragment fragment) {
        this(fragment.getChildFragmentManager());
    }

    public FragmentPagerAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public F getItem(int position) {
        return mFragmentSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getCount() {
        return mFragmentSet.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitle.get(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        if (getShowFragment() != object) {
            // 记录当前的Fragment对象
            mShowFragment = (F) object;
        }
    }

    /**
     * 添加 Fragment
     */
    public void addFragment(F fragment) {
        addFragment(fragment, null);
    }

    public void addFragment(F fragment, CharSequence title) {
        mFragmentSet.add(fragment);
        mFragmentTitle.add(title);
        if (mViewPager != null) {
            notifyDataSetChanged();
            if (mLazyMode) {
                mViewPager.setOffscreenPageLimit(getCount());
            } else {
                mViewPager.setOffscreenPageLimit(1);
            }
        }
    }

    /**
     * 获取当前的Fragment
     */
    public F getShowFragment() {
        return mShowFragment;
    }

    /**
     * 获取某个 Fragment 的索引（没有就返回 -1）
     */
    public int getFragmentIndex(Class<? extends Fragment> clazz) {
        if (clazz != null) {
            for (int i = 0; i < mFragmentSet.size(); i++) {
                if (clazz.getName().equals(mFragmentSet.get(i).getClass().getName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
        if (container instanceof ViewPager) {
            // 记录绑定 ViewPager
            mViewPager = (ViewPager) container;
            refreshLazyMode();
        }
    }

    /**
     * 设置懒加载模式
     */
    public void setLazyMode(boolean lazy) {
        mLazyMode = lazy;
        refreshLazyMode();
    }

    /**
     * 刷新加载模式
     */
    private void refreshLazyMode() {
        if (mViewPager == null) {
            return;
        }

        if (mLazyMode) {
            // 设置成懒加载模式（也就是不限制 Fragment 展示的数量）
            mViewPager.setOffscreenPageLimit(getCount());
        } else {
            mViewPager.setOffscreenPageLimit(1);
        }
    }
}