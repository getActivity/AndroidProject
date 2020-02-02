package com.hjq.demo.ui.fragment;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyFragment;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.PhotoActivity;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import butterknife.BindView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目框架使用示例
 */
public final class TestFragmentC extends MyFragment<HomeActivity> {

    @BindView(R.id.iv_test_image)
    ImageView mImageView;

    public static TestFragmentC newInstance() {
        return new TestFragmentC();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_c;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.btn_test_image1, R.id.btn_test_image2, R.id.btn_test_image3, R.id.btn_test_image4, R.id.btn_test_toast,
                R.id.btn_test_permission, R.id.btn_test_setting, R.id.btn_test_state_black, R.id.btn_test_state_white);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_image1:
                mImageView.setVisibility(View.VISIBLE);
                GlideApp.with(this)
                        .load("https://www.baidu.com/img/bd_logo.png")
                        .into(mImageView);
                break;
            case R.id.btn_test_image2:
                mImageView.setVisibility(View.VISIBLE);
                GlideApp.with(this)
                        .load("https://www.baidu.com/img/bd_logo.png")
                        .circleCrop()
                        .into(mImageView);
                break;
            case R.id.btn_test_image3:
                mImageView.setVisibility(View.VISIBLE);
                GlideApp.with(this)
                        .load("https://www.baidu.com/img/bd_logo.png")
                        .transform(new RoundedCorners((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, this.getResources().getDisplayMetrics())))
                        .into(mImageView);
                break;
            case R.id.btn_test_image4:
                PhotoActivity.start(getAttachActivity(), new PhotoActivity.OnPhotoSelectListener() {

                    @Override
                    public void onSelected(List<String> data) {
                        mImageView.setVisibility(View.VISIBLE);
                        GlideApp.with(getAttachActivity())
                                .load(data.get(0))
                                .into(mImageView);
                    }

                    @Override
                    public void onCancel() {
                        toast("取消了");
                    }
                });
                break;
            case R.id.btn_test_toast:
                toast("我是吐司");
                break;
            case R.id.btn_test_permission:
                XXPermissions.with(getAttachActivity())
                        // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                        //.constantRequest()
                        // 支持请求6.0悬浮窗权限8.0请求安装权限
                        //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES)
                        // 不指定权限则自动获取清单中的危险权限
                        .permission(Permission.CAMERA)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                if (isAll) {
                                    toast("获取权限成功");
                                } else {
                                    toast("获取权限成功，部分权限未正常授予");
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                if(quick) {
                                    toast("被永久拒绝授权，请手动授予权限");
                                    //如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.gotoPermissionSettings(getAttachActivity());
                                } else {
                                    toast("获取权限失败");
                                }
                            }
                        });
                break;
            case R.id.btn_test_setting:
                XXPermissions.gotoPermissionSettings(getAttachActivity());
                break;
            case R.id.btn_test_state_black:
                if (getAttachActivity().getStatusBarConfig() != null) {
                    getAttachActivity().getStatusBarConfig().statusBarDarkFont(true).init();
                }
                break;
            case R.id.btn_test_state_white:
                if (getAttachActivity().getStatusBarConfig() != null) {
                    getAttachActivity().getStatusBarConfig().statusBarDarkFont(false).init();
                }
                break;
            default:
                break;
        }
    }
}