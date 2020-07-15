package com.hjq.demo.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.common.MyFragment;
import com.hjq.demo.ui.adapter.StatusAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/07/10
 *    desc   : 加载使用案例
 */
public final class StatusFragment extends MyFragment<MyActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        TextView headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        headerView.setText("我是头部");
        headerView.setOnClickListener(v -> toast("点击了头部"));

        TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
        footerView.setText("我是尾部");
        footerView.setOnClickListener(v -> toast("点击了尾部"));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        mAdapter.setData(analogData());
    }

    /**
     * 模拟数据
     */
    private List<String> analogData() {
        List<String> data = new ArrayList<>();
        for (int i = mAdapter.getItemCount(); i < mAdapter.getItemCount() + 20; i++) {
            data.add("我是第" + i + "条目");
        }
        return data;
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        toast(mAdapter.getItem(position));
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(analogData());
            mRefreshLayout.finishRefresh();
            toast("刷新完成");
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.addData(analogData());
            mRefreshLayout.finishLoadMore();
            toast("加载完成");
        }, 1000);
    }
}