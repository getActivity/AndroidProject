package com.hjq.demo.ui.activity;

import androidx.viewpager.widget.ViewPager;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.ui.adapter.PhotoPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/05
 *    desc   : 查看大图
 */
public final class PhotoActivity extends MyActivity {

    @BindView(R.id.vp_photo_pager)
    ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    protected int getTitleId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        ArrayList<String> data = new ArrayList<>();
        data.add("https://www.baidu.com/img/bd_logo.png");
        data.add("https://www.baidu.com/img/bd_logo.png");
        data.add("https://www.baidu.com/img/bd_logo.png");
        mViewPager.setAdapter(new PhotoPagerAdapter(this, data));
    }

    @Override
    public boolean statusBarDarkFont() {
        return !super.statusBarDarkFont();
    }
}