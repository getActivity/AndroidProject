package com.hjq.demo.ui.fragment.home;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.custom.widget.view.CountdownView;
import com.hjq.custom.widget.view.SimpleRatingBar;
import com.hjq.custom.widget.view.SwitchButton;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 发现 Fragment
 */
public final class HomeFindFragment extends TitleBarFragment<HomeActivity>
        implements SwitchButton.OnCheckedChangeListener,
        SimpleRatingBar.OnRatingChangeListener {

    private ImageView mCircleView;
    private ImageView mCornerView;
    private SwitchButton mSwitchButton;
    private CountdownView mCountdownView;

    public static HomeFindFragment newInstance() {
        return new HomeFindFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_find_fragment;
    }

    @Override
    protected void initView() {
        mCircleView = findViewById(R.id.iv_home_find_circle);
        mCornerView = findViewById(R.id.iv_home_find_corner);
        mSwitchButton = findViewById(R.id.sb_home_find_switch);
        mCountdownView = findViewById(R.id.cv_home_find_countdown);
        setOnClickListener(mCountdownView);

        mSwitchButton.setOnCheckedChangeListener(this);

        SimpleRatingBar simpleRatingBar1 = findViewById(R.id.srb_home_find_rating_bar_1);
        simpleRatingBar1.setOnRatingBarChangeListener(this);
        SimpleRatingBar simpleRatingBar2 = findViewById(R.id.srb_home_find_rating_bar_2);
        simpleRatingBar2.setOnRatingBarChangeListener(this);
    }

    @Override
    protected void initData() {
        // 显示圆形的 ImageView
        GlideApp.with(this)
                .load(R.drawable.update_app_top_bg)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mCircleView);

        // 显示圆角的 ImageView
        GlideApp.with(this)
                .load(R.drawable.update_app_top_bg)
                .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) SmallestWidthAdaptation.dp2px(this, 10))))
                .into(mCornerView);
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        if (view == mCountdownView) {
            toast(R.string.common_code_send_hint);
            mCountdownView.start();
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(@NonNull SwitchButton button, boolean checked) {
        toast(checked);
    }

    /**
     * {@link SimpleRatingBar.OnRatingChangeListener}
     */
    @Override
    public void onRatingChanged(@NonNull SimpleRatingBar ratingBar, float grade, boolean touch) {
        toast(grade);
    }
}