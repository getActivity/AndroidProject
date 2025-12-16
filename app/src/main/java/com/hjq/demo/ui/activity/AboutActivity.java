package com.hjq.demo.ui.activity;

import android.view.View;
import androidx.annotation.Nullable;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 关于界面
 */
public final class AboutActivity extends AppActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.about_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return findViewById(R.id.tv_about_copyright);
    }
}