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
 *    desc   : FragmentPagerAdapter 封装
 */
@SuppressWarnings({"SuspiciousMethodCalls", "deprecation"})
public final class BasePagerAdapter<F extends Fragment> extends FragmentPagerAdapter {

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

    public BasePagerAdapter(FragmentActivity activity) {
        this(activity.getSupportFragmentManager());
    }

    public BasePagerAdapter(Fragment fragment) {
        this(fragment.getChildFragmentManager());
    }

    public BasePagerAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public F getItem(int position) {
        return mFragmentSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position >= getCount()) {
            return super.getItemId(position);
        }
        return getItem(position).hashCode();
    }

    @Override
    public int getCount() {
        return mFragmentSet.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof Fragment && !mFragmentSet.contains(object)) {
            return POSITION_NONE;
        }

        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitle.get(position);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
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
        addFragment(fragment, "");
    }

    public void addFragment(F fragment, CharSequence title) {
        addFragment(fragment, mFragmentSet.size(), title);
    }

    public void addFragment(F fragment, int fragmentIndex, CharSequence title) {
        // 避免集合角标越界
        if (fragmentIndex > mFragmentSet.size()) {
            return;
        }

        mFragmentSet.add(fragmentIndex, fragment);
        mFragmentTitle.add(fragmentIndex, title);

        if (mViewPager == null) {
            return;
        }

        notifyDataSetChanged();
        if (mLazyMode) {
            mViewPager.setOffscreenPageLimit(getCount());
        } else {
            mViewPager.setOffscreenPageLimit(1);
        }
    }

    public void removeFragment(Class<? extends Fragment> clazz) {
        int fragmentIndex = getFragmentIndex(clazz);
        if (fragmentIndex <= 0) {
            return;
        }
        removeFragment(fragmentIndex);
    }

    public void removeFragment(Fragment fragment) {
        int fragmentIndex = mFragmentSet.indexOf(fragment);
        if (fragmentIndex <= 0) {
            return;
        }
        removeFragment(fragmentIndex);
    }

    public void removeFragment(int fragmentIndex) {
        mFragmentSet.remove(fragmentIndex);
        mFragmentTitle.remove(fragmentIndex);

        if (mViewPager == null) {
            return;
        }

        int currentItem = mViewPager.getCurrentItem();
        if (currentItem > 0 && fragmentIndex == currentItem && fragmentIndex == mFragmentSet.size() - 1) {
            // 则先切换到上一个 Fragment 再进行删除，避免出现角标异常
            mViewPager.setCurrentItem(currentItem - 1, false);
        }

        notifyDataSetChanged();
        if (mLazyMode) {
            mViewPager.setOffscreenPageLimit(getCount());
        } else {
            mViewPager.setOffscreenPageLimit(1);
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
        if (clazz == null) {
            return -1;
        }
        for (int i = 0; i < mFragmentSet.size(); i++) {
            if (clazz.getName().equals(mFragmentSet.get(i).getClass().getName())) {
                return i;
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