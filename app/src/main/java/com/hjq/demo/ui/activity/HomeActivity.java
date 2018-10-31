package com.hjq.demo.ui.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hjq.baselibrary.utils.OnClickUtils;
import com.hjq.demo.R;
import com.hjq.demo.common.CommonActivity;
import com.hjq.demo.ui.adapter.HomeViewPagerAdapter;
import com.hjq.toast.ToastUtils;

import butterknife.BindView;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 主页界面
 */
public class HomeActivity extends CommonActivity implements
        ViewPager.OnPageChangeListener, Runnable,
        BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.vp_home_pager)
    ViewPager mViewPager;
    @BindView(R.id.bv_home_navigation)
    BottomNavigationView mBottomNavigationView;

    private HomeViewPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {
        mViewPager.addOnPageChangeListener(this);

        // 不使用图标默认变色
        mBottomNavigationView.setItemIconTintList(null);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // 修复在 ViewPager 中点击 EditText 弹出软键盘导致 BottomNavigationView 还显示在 ViewPager 下面的问题
        postDelayed(this, 1000);
    }

    @Override
    protected void initData() {
        mAdapter = new HomeViewPagerAdapter(this);
        mViewPager.setAdapter(mAdapter);

        // 限制页面数量
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
    }

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
        /*
         父布局为LinearLayout，因为 ViewPager 使用了权重的问题
         软键盘在弹出的时候会把布局进行收缩，ViewPager 的高度缩小了
         所以 BottomNavigationView 会显示在ViewPager 下面
         解决方法是在 ViewPager 初始化高度后手动进行设置 ViewPager 高度并将权重设置为0
         */
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.height = mViewPager.getHeight();
        layoutParams.weight = 0;
        mViewPager.setLayoutParams(layoutParams);
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mBottomNavigationView.setSelectedItemId(R.id.menu_home);
                break;
            case 1:
                mBottomNavigationView.setSelectedItemId(R.id.home_found);
                break;
            case 2:
                mBottomNavigationView.setSelectedItemId(R.id.home_message);
                break;
            case 3:
                mBottomNavigationView.setSelectedItemId(R.id.home_me);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    /**
     * {@link BottomNavigationView.OnNavigationItemSelectedListener}
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.home_found:
                mViewPager.setCurrentItem(1);
                return true;
            case R.id.home_message:
                mViewPager.setCurrentItem(2);
                return true;
            case R.id.home_me:
                mViewPager.setCurrentItem(3);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (OnClickUtils.isOnDoubleClick()) {
            //移动到上一个任务栈，避免侧滑引起的不良反应
            moveTaskToBack(false);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //销毁掉当前界面
                    finish();
                }
            }, 300);
        } else {
            ToastUtils.show(getResources().getString(R.string.home_exit_hint));
        }
    }

    @Override
    protected void onDestroy() {
        mViewPager.removeOnPageChangeListener(this);
        mViewPager.setAdapter(null);
        mBottomNavigationView.setOnNavigationItemSelectedListener(null);
        super.onDestroy();
    }

    @Override
    public boolean isSupportSwipeBack() {
        // 不使用侧滑功能
        return !super.isSupportSwipeBack();
    }
}