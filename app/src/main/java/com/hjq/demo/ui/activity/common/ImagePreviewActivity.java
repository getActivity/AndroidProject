package com.hjq.demo.ui.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.ui.adapter.common.ImagePreviewAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.relex.circleindicator.CircleIndicator3;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/05
 *    desc   : 查看大图
 */
public final class ImagePreviewActivity extends AppActivity
        implements BaseAdapter.OnItemClickListener {

    private static final String INTENT_KEY_IN_IMAGE_LIST = "imageList";
    private static final String INTENT_KEY_IN_IMAGE_INDEX = "imageIndex";

    public static void start(@NonNull Context context, @NonNull String url) {
        ArrayList<String> images = new ArrayList<>(1);
        images.add(url);
        start(context, images);
    }

    public static void start(@NonNull Context context, @NonNull List<String> urls) {
        start(context, urls, 0);
    }

    @Log
    public static void start(@NonNull Context context, @NonNull List<String> urls, int index) {
        if (urls.isEmpty()) {
            return;
        }
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        if (urls.size() > 2000) {
            // 请注意：如果传输的数据量过大，会抛出此异常，并且这种异常是不能被捕获的
            // 所以当图片数量过多的时候，我们应当只显示一张，这种一般是手机图片过多导致的
            // 经过测试，传入 3121 张图片集合的时候会抛出此异常，所以保险值应当是 2000
            // android.os.TransactionTooLargeException: data parcel size 521984 bytes
            urls = Collections.singletonList(urls.get(index));
        }

        if (urls instanceof ArrayList) {
            intent.putExtra(INTENT_KEY_IN_IMAGE_LIST, (ArrayList<String>) urls);
        } else {
            intent.putExtra(INTENT_KEY_IN_IMAGE_LIST, new ArrayList<>(urls));
        }
        intent.putExtra(INTENT_KEY_IN_IMAGE_INDEX, index);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private ViewPager2 mViewPager2;

    @NonNull
    private final ImagePreviewAdapter mAdapter = new ImagePreviewAdapter(this);

    /** 圆圈指示器 */
    private CircleIndicator3 mCircleIndicatorView;
    /** 文本指示器 */
    private TextView mTextIndicatorView;

    /** ViewPager2 页面改变监听器 */
    private final ViewPager2.OnPageChangeCallback mPageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int position) {
            // 适配 RTL 特性
            String text;
            if (mAdapter.getContext().getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                text = mAdapter.getCount() + "/" + (position + 1);
            } else {
                text = (position + 1) + "/" + mAdapter.getCount();
            }
            mTextIndicatorView.setText(text);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.image_preview_activity;
    }

    @Override
    protected void initView() {
        mViewPager2 = findViewById(R.id.vp_image_preview_pager);
        mViewPager2.setOffscreenPageLimit(3);
        mCircleIndicatorView = findViewById(R.id.ci_image_preview_indicator);
        mTextIndicatorView = findViewById(R.id.tv_image_preview_indicator);
    }

    @Override
    protected void initData() {
        ArrayList<String> images = getStringArrayList(INTENT_KEY_IN_IMAGE_LIST);
        if (images == null || images.isEmpty()) {
            finish();
            return;
        }
        mAdapter.setData(images);
        mAdapter.setOnItemClickListener(this);
        mViewPager2.setAdapter(mAdapter);

        if (images.size() != 1) {
            if (images.size() < 10) {
                // 如果是 10 张以内的图片，那么就显示圆圈指示器
                mCircleIndicatorView.setVisibility(View.VISIBLE);
                mCircleIndicatorView.setViewPager(mViewPager2);
            } else {
                // 如果超过 10 张图片，那么就显示文字指示器
                mTextIndicatorView.setVisibility(View.VISIBLE);
                mPageChangeCallback.onPageSelected(0);
                mViewPager2.registerOnPageChangeCallback(mPageChangeCallback);
            }

            int index = getInt(INTENT_KEY_IN_IMAGE_INDEX);
            if (index < images.size()) {
                mViewPager2.setCurrentItem(index, false);
            }
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }

    @Override
    public boolean isStatusBarDarkFont() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager2.unregisterOnPageChangeCallback(mPageChangeCallback);
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     * @param recyclerView      RecyclerView 对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        // 单击图片退出当前的 Activity
        finish();
    }
}