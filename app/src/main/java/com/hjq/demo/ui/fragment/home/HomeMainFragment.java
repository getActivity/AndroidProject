package com.hjq.demo.ui.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BasePagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.adapter.common.TabAdapter;
import com.hjq.demo.ui.fragment.StatusFragment;
import com.hjq.demo.ui.fragment.common.BrowserFragment;
import com.hjq.demo.widget.XCollapsingToolbarLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 首页 Fragment
 */
public final class HomeMainFragment extends TitleBarFragment<HomeActivity>
        implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener,
        XCollapsingToolbarLayout.OnScrimsListener {

    private XCollapsingToolbarLayout mCollapsingToolbarLayout;

    private TextView mAddressView;
    private TextView mHintView;
    private AppCompatImageView mSearchView;

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private BasePagerAdapter<AppFragment<?>> mPagerAdapter;

    public static HomeMainFragment newInstance() {
        return new HomeMainFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_main_fragment;
    }

    @Override
    protected void initView() {
        mCollapsingToolbarLayout = findViewById(R.id.ctl_home_main_bar);

        mAddressView = findViewById(R.id.tv_home_main_address);
        mHintView = findViewById(R.id.tv_home_main_hint);
        mSearchView = findViewById(R.id.iv_home_main_search);

        mTabView = findViewById(R.id.rv_home_main_tab);
        mViewPager = findViewById(R.id.vp_home_main_pager);

        mPagerAdapter = new BasePagerAdapter<>(this);
        mPagerAdapter.addFragment(StatusFragment.newInstance(), "列表演示");
        mPagerAdapter.addFragment(BrowserFragment.newInstance("https://github.com/getActivity"), "网页演示");
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(mTabView.getContext());
        mTabView.setAdapter(mTabAdapter);

        Activity activity = getAttachActivity();
        if (activity != null) {
            ImmersionBar.setTitleBarMarginTop(activity, findViewById(R.id.tb_home_main_title));
        }

        // 设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
    }

    @Override
    protected void initData() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            mTabAdapter.addItem(String.valueOf(mPagerAdapter.getPageTitle(i)));
        }
        mTabAdapter.setOnTabListener(this);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(@NonNull RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // default implementation ignored
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // default implementation ignored
    }

    /**
     * CollapsingToolbarLayout 渐变回调
     *
     * {@link XCollapsingToolbarLayout.OnScrimsListener}
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onScrimsStateChange(@NonNull XCollapsingToolbarLayout layout, boolean shown) {
        getStatusBarConfig().statusBarDarkFont(shown).init();
        Activity activity = getAttachActivity();
        if (activity == null) {
            return;
        }
        mAddressView.setTextColor(ContextCompat.getColor(activity, shown ? R.color.black : R.color.white));
        mHintView.setBackgroundResource(shown ? R.drawable.home_search_bar_gray_bg : R.drawable.home_search_bar_transparent_bg);
        mHintView.setTextColor(ContextCompat.getColor(activity, shown ? R.color.black60 : R.color.white60));
        mSearchView.setSupportImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, shown ? R.color.common_icon_color : R.color.white)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }
}