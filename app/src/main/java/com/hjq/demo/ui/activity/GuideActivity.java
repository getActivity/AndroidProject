package com.hjq.demo.ui.activity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.ui.adapter.GuideAdapter;
import me.relex.circleindicator.CircleIndicator3;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 应用引导页
 */
public final class GuideActivity extends AppActivity {

    private ViewPager2 mViewPager;
    private CircleIndicator3 mIndicatorView;
    private View mCompleteView;

    @NonNull
    private final GuideAdapter mAdapter = new GuideAdapter(this);

    private final ViewPager2.OnPageChangeCallback mCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewPager.getCurrentItem() != mAdapter.getCount() - 1 || positionOffsetPixels <= 0) {
                return;
            }

            mIndicatorView.setVisibility(View.VISIBLE);
            mCompleteView.setVisibility(View.INVISIBLE);
            mCompleteView.clearAnimation();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return;
            }

            boolean lastItem = mViewPager.getCurrentItem() == mAdapter.getCount() - 1;
            mIndicatorView.setVisibility(lastItem ? View.INVISIBLE : View.VISIBLE);
            mCompleteView.setVisibility(lastItem ? View.VISIBLE : View.INVISIBLE);
            if (lastItem) {
                // 按钮呼吸动效
                ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(350);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(Animation.INFINITE);
                mCompleteView.startAnimation(animation);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.guide_activity;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_guide_pager);
        mIndicatorView = findViewById(R.id.cv_guide_indicator);
        mCompleteView = findViewById(R.id.btn_guide_complete);
        setOnClickListener(mCompleteView);
    }

    @Override
    protected void initData() {
        mAdapter.addItem(R.drawable.guide_1_bg);
        mAdapter.addItem(R.drawable.guide_2_bg);
        mAdapter.addItem(R.drawable.guide_3_bg);

        mViewPager.setAdapter(mAdapter);
        mViewPager.registerOnPageChangeCallback(mCallback);
        mIndicatorView.setViewPager(mViewPager);
    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return findViewById(R.id.fl_guide_control);
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        if (view == mCompleteView) {
            HomeActivity.start(getContext());
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.unregisterOnPageChangeCallback(mCallback);
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }
}