package com.hjq.demo.ui.fragment.home;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.permission.PermissionDescription;
import com.hjq.demo.permission.PermissionInterceptor;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.smallest.width.SmallestWidthAdaptation;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 消息 Fragment
 */
public final class HomeMessageFragment extends TitleBarFragment<HomeActivity> {

    private ImageView mImageView;

    public static HomeMessageFragment newInstance() {
        return new HomeMessageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_message_fragment;
    }

    @Override
    protected void initView() {
        mImageView = findViewById(R.id.iv_home_message_image);
        setOnClickListener(R.id.btn_home_message_image1, R.id.btn_home_message_image2, R.id.btn_home_message_image3,
                R.id.btn_home_message_toast, R.id.btn_home_message_permission, R.id.btn_home_message_setting,
                R.id.btn_home_message_black, R.id.btn_home_message_white, R.id.btn_home_message_tab);
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_home_message_image1) {

            mImageView.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .load("https://www.baidu.com/img/bd_logo.png")
                    .into(mImageView);

        } else if (viewId == R.id.btn_home_message_image2) {

            mImageView.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .load("https://www.baidu.com/img/bd_logo.png")
                    .circleCrop()
                    .into(mImageView);

        } else if (viewId == R.id.btn_home_message_image3) {

            mImageView.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .load("https://www.baidu.com/img/bd_logo.png")
                    .transform(new RoundedCorners((int) SmallestWidthAdaptation.dp2px(this, 20)))
                    .into(mImageView);

        } else if (viewId == R.id.btn_home_message_toast) {

            toast("我是吐司");

        } else if (viewId == R.id.btn_home_message_permission) {

            requestPermission();

        } else if (viewId == R.id.btn_home_message_setting) {

            XXPermissions.startPermissionActivity(this);

        } else if (viewId == R.id.btn_home_message_black) {

            AppActivity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            activity.getStatusBarConfig()
                    .statusBarDarkFont(true)
                    .init();

        } else if (viewId == R.id.btn_home_message_white) {

            AppActivity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            activity.getStatusBarConfig()
                    .statusBarDarkFont(false)
                    .init();

        } else if (viewId == R.id.btn_home_message_tab) {

            HomeActivity.start(view.getContext(), HomeMainFragment.class);
        }
    }

    private void requestPermission() {
        XXPermissions.with(requireContext())
                .permission(PermissionLists.getCameraPermission())
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                .request((grantedList, deniedList) -> {
                    boolean allGranted = deniedList.isEmpty();
                    if (!allGranted) {
                        return;
                    }
                    toast("获取相机权限成功");
                });
    }
}