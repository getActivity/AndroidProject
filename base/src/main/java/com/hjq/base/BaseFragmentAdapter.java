package com.hjq.base;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : FragmentPagerAdapter 基类
 */
public class BaseFragmentAdapter<F extends BaseFragment> extends FragmentPagerAdapter {

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

    public BaseFragmentAdapter(FragmentActivity activity) {
        this(activity.getSupportFragmentManager());
    }

    public BaseFragmentAdapter(Fragment fragment) {
        this(fragment.getChildFragmentManager());
    }

    public BaseFragmentAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public F getItem(int position) {
        return mFragmentSet.get(position);
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
        if (getShowFragment() != object) {
            // 记录当前的Fragment对象
            mShowFragment = (F) object;
        }
        super.setPrimaryItem(container, position, object);
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
            }
        }
    }

    /**
     * 获取当前的Fragment
     */
    public F getShowFragment() {
        return mShowFragment;
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