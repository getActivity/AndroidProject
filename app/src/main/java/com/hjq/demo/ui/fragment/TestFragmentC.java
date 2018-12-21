package com.hjq.demo.ui.fragment;

import android.view.View;
import android.widget.Button;

import com.hjq.demo.R;
import com.hjq.demo.common.MyLazyFragment;
import com.hjq.demo.common.UIActivity;
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
public class TestFragmentC extends MyLazyFragment
        implements View.OnClickListener {

    @BindView(R.id.btn_test_toast)
    Button mToastView;
    @BindView(R.id.btn_test_permission)
    Button mPermissionView;
    @BindView(R.id.btn_test_state_black)
    Button mStateBlackView;
    @BindView(R.id.btn_test_state_white)
    Button mStateWhiteView;
    @BindView(R.id.btn_test_swipe_enabled)
    Button mSwipeEnabledView;
    @BindView(R.id.btn_test_swipe_disable)
    Button mSwipeDisableView;

    public static TestFragmentC newInstance() {
        return new TestFragmentC();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_c;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_test_c_title;
    }

    @Override
    protected void initView() {
        mToastView.setOnClickListener(this);
        mPermissionView.setOnClickListener(this);
        mStateBlackView.setOnClickListener(this);
        mStateWhiteView.setOnClickListener(this);
        mSwipeEnabledView.setOnClickListener(this);
        mSwipeDisableView.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    /**
     * {@link View.OnClickListener}
     */
    @Override
    public void onClick(View v) {
        if (v == mToastView) {
            toast("我是吐司");
        }else if (v == mPermissionView) {
            XXPermissions.with(getFragmentActivity())
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
                                XXPermissions.gotoPermissionSettings(getFragmentActivity());
                            }else {
                                toast("获取权限失败");
                            }
                        }
                    });
        }else if (v == mStateBlackView) {
            UIActivity activity = (UIActivity) getFragmentActivity();
            activity.getStatusBarConfig().statusBarDarkFont(true).init();
        }else if (v == mStateWhiteView) {
            UIActivity activity = (UIActivity) getFragmentActivity();
            activity.getStatusBarConfig().statusBarDarkFont(false).init();
        }else if (v == mSwipeEnabledView) {
            UIActivity activity = (UIActivity) getFragmentActivity();
            activity.getSwipeBackHelper().setSwipeBackEnable(true);
            toast("当前界面不会生效，其他界面调用才会有效果");
        }else if (v == mSwipeDisableView) {
            UIActivity activity = (UIActivity) getFragmentActivity();
            activity.getSwipeBackHelper().setSwipeBackEnable(false);
            toast("当前界面不会生效，其他界面调用才会有效果");
        }
    }
}