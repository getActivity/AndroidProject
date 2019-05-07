package com.hjq.demo.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.hjq.demo.R;
import com.hjq.demo.common.MyLazyFragment;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.image.ImageLoader;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目框架使用示例
 */
public final class TestFragmentC extends MyLazyFragment<HomeActivity> {

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
    protected int getTitleId() {
        return R.id.tb_test_c_title;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @OnClick({R.id.btn_test_image1, R.id.btn_test_image2, R.id.btn_test_image3,
            R.id.btn_test_toast, R.id.btn_test_permission,
            R.id.btn_test_state_black, R.id.btn_test_state_white,
            R.id.btn_test_swipe_enabled, R.id.btn_test_swipe_disable})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_image1:
                ImageLoader.loadImage(mImageView, "https://www.baidu.com/img/bd_logo.png");
                break;
            case R.id.btn_test_image2:
                ImageLoader.loadCircleImage(mImageView, "https://www.baidu.com/img/bd_logo.png");
                break;
            case R.id.btn_test_image3:
                ImageLoader.loadRoundImage(mImageView, "https://www.baidu.com/img/bd_logo.png", 20);
                break;
            case R.id.btn_test_toast:
                toast("我是吐司");
                break;
            case R.id.btn_test_permission:
                XXPermissions.with(getBindingActivity())
                        //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                        //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                        .permission(Permission.CAMERA) //不指定权限则自动获取清单中的危险权限
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                if (isAll) {
                                    toast("获取权限成功");
                                }else {
                                    toast("获取权限成功，部分权限未正常授予");
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                if(quick) {
                                    toast("被永久拒绝授权，请手动授予权限");
                                    //如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.gotoPermissionSettings(getBindingActivity());
                                }else {
                                    toast("获取权限失败");
                                }
                            }
                        });
                break;
            case R.id.btn_test_state_black:
                getBindingActivity().getStatusBarConfig().statusBarDarkFont(true).init();
                break;
            case R.id.btn_test_state_white:
                getBindingActivity().getStatusBarConfig().statusBarDarkFont(false).init();
                break;
            case R.id.btn_test_swipe_enabled:
                getBindingActivity().getSwipeBackHelper().setSwipeBackEnable(true);
                toast("当前界面不会生效，其他界面调用才会有效果");
                break;
            case R.id.btn_test_swipe_disable:
                getBindingActivity().getSwipeBackHelper().setSwipeBackEnable(false);
                toast("当前界面不会生效，其他界面调用才会有效果");
                break;
            default:
                break;
        }
    }
}