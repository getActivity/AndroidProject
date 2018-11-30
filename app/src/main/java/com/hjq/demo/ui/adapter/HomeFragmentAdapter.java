package com.hjq.demo.ui.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.hjq.base.BaseFragmentPagerAdapter;
import com.hjq.demo.base.MyLazyFragment;
import com.hjq.demo.ui.fragment.TestFragmentA;
import com.hjq.demo.ui.fragment.TestFragmentB;
import com.hjq.demo.ui.fragment.TestFragmentC;
import com.hjq.demo.ui.fragment.TestFragmentD;

import java.util.List;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 主页界面 ViewPager + Fragment 适配器
 */
public final class HomeFragmentAdapter extends BaseFragmentPagerAdapter<MyLazyFragment> {

    public HomeFragmentAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    protected void init(FragmentManager fm, List<MyLazyFragment> list) {
        list.add(TestFragmentA.newInstance());
        list.add(TestFragmentB.newInstance());
        list.add(TestFragmentC.newInstance());
        list.add(TestFragmentD.newInstance());
    }
}