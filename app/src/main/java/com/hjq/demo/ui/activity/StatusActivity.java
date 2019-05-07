package com.hjq.demo.ui.activity;

import android.app.Dialog;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.dialog.MenuDialog;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/17
 *    desc   : 加载使用案例
 */
public final class StatusActivity extends MyActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_status;
    }

    @Override
    protected int getTitleId() {
        return R.id.tb_status_title;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        showLoading();
        postDelayed(new Runnable() {
            @Override
            public void run() {

                new MenuDialog.Builder(StatusActivity.this)
                        .setCancelable(false)
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setList("请求错误", "空数据提示", "自定义提示")
                        .setListener(new MenuDialog.OnListener() {

                            @Override
                            public void onSelected(Dialog dialog, int position, String text) {
                                switch (position) {
                                    case 0:
                                        showError();
                                        break;
                                    case 1:
                                        showEmpty();
                                        break;
                                    case 2:
                                        showLayout(getResources().getDrawable(R.mipmap.icon_hint_address), "还没有添加地址");
                                        break;
                                    default:
                                        break;
                                }
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                showComplete();
                            }
                        })
                        .show();

            }
        }, 3000);
    }
}