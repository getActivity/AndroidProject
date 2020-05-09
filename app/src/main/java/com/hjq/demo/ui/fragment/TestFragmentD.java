package com.hjq.demo.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyFragment;
import com.hjq.demo.ui.activity.AboutActivity;
import com.hjq.demo.ui.activity.BrowserActivity;
import com.hjq.demo.ui.activity.DialogActivity;
import com.hjq.demo.ui.activity.GuideActivity;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.ImageActivity;
import com.hjq.demo.ui.activity.LoginActivity;
import com.hjq.demo.ui.activity.PasswordForgetActivity;
import com.hjq.demo.ui.activity.PasswordResetActivity;
import com.hjq.demo.ui.activity.PersonalDataActivity;
import com.hjq.demo.ui.activity.PhoneResetActivity;
import com.hjq.demo.ui.activity.PhoneVerifyActivity;
import com.hjq.demo.ui.activity.RegisterActivity;
import com.hjq.demo.ui.activity.SettingActivity;
import com.hjq.demo.ui.activity.StatusActivity;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目界面跳转示例
 */
public final class TestFragmentD extends MyFragment<HomeActivity> {

    public static TestFragmentD newInstance() {
        return new TestFragmentD();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_d;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.btn_test_dialog, R.id.btn_test_hint, R.id.btn_test_login, R.id.btn_test_register, R.id.btn_test_forget,
                R.id.btn_test_reset,R.id.btn_test_verify, R.id.btn_test_change, R.id.btn_test_personal, R.id.btn_test_setting,
                R.id.btn_test_about, R.id.btn_test_guide, R.id.btn_test_browser, R.id.btn_test_image, R.id.btn_test_crash, R.id.btn_test_pay);
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_dialog:
                startActivity(DialogActivity.class);
                break;
            case R.id.btn_test_hint:
                startActivity(StatusActivity.class);
                break;
            case R.id.btn_test_login:
                startActivity(LoginActivity.class);
                break;
            case R.id.btn_test_register:
                startActivity(RegisterActivity.class);
                break;
            case R.id.btn_test_forget:
                startActivity(PasswordForgetActivity.class);
                break;
            case R.id.btn_test_reset:
                startActivity(PasswordResetActivity.class);
                break;
            case R.id.btn_test_verify:
                startActivity(PhoneVerifyActivity.class);
                break;
            case R.id.btn_test_change:
                startActivity(PhoneResetActivity.class);
                break;
            case R.id.btn_test_personal:
                startActivity(PersonalDataActivity.class);
                break;
            case R.id.btn_test_setting:
                startActivity(SettingActivity.class);
                break;
            case R.id.btn_test_about:
                startActivity(AboutActivity.class);
                break;
            case R.id.btn_test_guide:
                startActivity(GuideActivity.class);
                break;
            case R.id.btn_test_browser:
                BrowserActivity.start(getAttachActivity(), "https://github.com/getActivity");
                break;
            case R.id.btn_test_image:
                ArrayList<String> images = new ArrayList<>();
                images.add("https://www.baidu.com/img/bd_logo.png");
                images.add("https://avatars1.githubusercontent.com/u/28616817?s=460&v=4");
                ImageActivity.start(getAttachActivity(), images, images.size() - 1);
                break;
            case R.id.btn_test_crash:
                // 关闭 Bugly 异常捕捉
                CrashReport.closeBugly();
                throw new IllegalStateException("are you ok?");
            case R.id.btn_test_pay:
                new MessageDialog.Builder(getAttachActivity())
                        .setTitle("捐赠")
                        .setMessage("如果你觉得这个开源项目很棒，希望它能更好地坚持开发下去，可否愿意花一点点钱（推荐 10.24 元）作为对于开发者的激励")
                        .setConfirm("支付宝")
                        .setCancel(null)
                        //.setAutoDismiss(false)
                        .setListener(dialog -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2FFKX04202G4K6AVCF5GIY66%3F_s%3Dweb-other"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                toast("这个开源项目因为你的支持而能够不断更新、完善，非常感谢你的支持");
                            } catch (Exception e) {
                                toast("打开支付宝失败");
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}