package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BasePagerAdapter;
import com.hjq.core.manager.ActivityManager;
import com.hjq.core.tools.DoubleClickHelper;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.ui.adapter.common.NavigationAdapter;
import com.hjq.demo.ui.adapter.common.NavigationAdapter.NavigationItem;
import com.hjq.demo.ui.fragment.home.HomeFindFragment;
import com.hjq.demo.ui.fragment.home.HomeMainFragment;
import com.hjq.demo.ui.fragment.home.HomeMessageFragment;
import com.hjq.demo.ui.fragment.home.HomeMineFragment;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 首页界面
 */
public final class HomeActivity extends AppActivity
        implements NavigationAdapter.OnNavigationListener {

    private static final String INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex";
    private static final String INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass";

    private ViewPager mViewPager;
    private RecyclerView mNavigationView;

    private NavigationAdapter mNavigationAdapter;
    private BasePagerAdapter<AppFragment<?>> mPagerAdapter;

    public static void start(@NonNull Context context) {
        start(context, HomeMainFragment.class);
    }

    public static void start(@NonNull Context context, @NonNull Class<? extends AppFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_activity;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_home_pager);
        mNavigationView = findViewById(R.id.rv_home_navigation);

        mNavigationAdapter = new NavigationAdapter(this);
        mNavigationAdapter.addItem(new NavigationItem(getString(R.string.home_nav_main),
                ContextCompat.getDrawable(this, R.drawable.home_main_selector)));
        mNavigationAdapter.addItem(new NavigationItem(getString(R.string.home_nav_found),
                ContextCompat.getDrawable(this, R.drawable.home_found_selector)));
        mNavigationAdapter.addItem(new NavigationItem(getString(R.string.home_nav_message),
                ContextCompat.getDrawable(this, R.drawable.home_message_selector)));
        mNavigationAdapter.addItem(new NavigationItem(getString(R.string.home_nav_mime),
                ContextCompat.getDrawable(this, R.drawable.home_mime_selector)));
        mNavigationAdapter.setOnNavigationListener(this);
        mNavigationView.setAdapter(mNavigationAdapter);
    }

    @Override
    protected void initData() {
        mPagerAdapter = new BasePagerAdapter<>(this);
        mPagerAdapter.addFragment(HomeMainFragment.newInstance());
        mPagerAdapter.addFragment(HomeFindFragment.newInstance());
        mPagerAdapter.addFragment(HomeMessageFragment.newInstance());
        mPagerAdapter.addFragment(HomeMineFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);

        onNewIntent(getIntent());
    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return mNavigationView;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(mPagerAdapter.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX));
    }

    private void switchFragment(int fragmentIndex) {
        if (fragmentIndex == -1) {
            return;
        }

        switch (fragmentIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(fragmentIndex);
                mNavigationAdapter.setSelectedPosition(fragmentIndex);
                break;
            default:
                break;
        }
    }

    /**
     * {@link NavigationAdapter.OnNavigationListener}
     */

    @Override
    public boolean onNavigationItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(position);
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint);
            return;
        }

        // 移动到上一个任务栈
        moveTaskToBack(false);
        postDelayed(() -> {
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities();
        }, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mNavigationView.setAdapter(null);
        mNavigationAdapter.setOnNavigationListener(null);
    }
}