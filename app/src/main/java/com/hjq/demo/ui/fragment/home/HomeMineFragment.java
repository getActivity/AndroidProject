package com.hjq.demo.ui.fragment.home;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.ui.activity.AboutActivity;
import com.hjq.demo.ui.activity.DialogActivity;
import com.hjq.demo.ui.activity.GuideActivity;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.SettingActivity;
import com.hjq.demo.ui.activity.StatusActivity;
import com.hjq.demo.ui.activity.account.LoginActivity;
import com.hjq.demo.ui.activity.account.PasswordForgetActivity;
import com.hjq.demo.ui.activity.account.PasswordResetActivity;
import com.hjq.demo.ui.activity.account.PersonalDataActivity;
import com.hjq.demo.ui.activity.account.PhoneResetActivity;
import com.hjq.demo.ui.activity.account.RegisterActivity;
import com.hjq.demo.ui.activity.common.BrowserActivity;
import com.hjq.demo.ui.activity.common.ImagePreviewActivity;
import com.hjq.demo.ui.activity.common.ImageSelectActivity;
import com.hjq.demo.ui.activity.common.ImageSelectActivity.OnImageSelectListener;
import com.hjq.demo.ui.activity.common.VideoPlayActivity;
import com.hjq.demo.ui.activity.common.VideoSelectActivity;
import com.hjq.demo.ui.dialog.common.InputDialog;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import com.tencent.bugly.library.Bugly;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 我的 Fragment
 */
public final class HomeMineFragment extends TitleBarFragment<HomeActivity> {

    public static HomeMineFragment newInstance() {
        return new HomeMineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_mine_fragment;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.btn_home_mine_dialog, R.id.btn_home_mine_hint, R.id.btn_home_mine_login, R.id.btn_home_mine_register, R.id.btn_home_mine_forget,
                R.id.btn_home_mine_reset, R.id.btn_home_mine_change, R.id.btn_home_mine_personal, R.id.btn_home_mine_setting, R.id.btn_home_mine_about,
                R.id.btn_home_mine_guide, R.id.btn_home_mine_browser, R.id.btn_home_mine_image_select, R.id.btn_home_mine_image_preview,
                R.id.btn_home_mine_video_select, R.id.btn_home_mine_video_play, R.id.btn_home_mine_crash, R.id.btn_home_mine_donate);
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_home_mine_dialog) {

            startActivity(DialogActivity.class);

        } else if (viewId == R.id.btn_home_mine_hint) {

            startActivity(StatusActivity.class);

        } else if (viewId == R.id.btn_home_mine_login) {

            startActivity(LoginActivity.class);

        } else if (viewId == R.id.btn_home_mine_register) {

            startActivity(RegisterActivity.class);

        } else if (viewId == R.id.btn_home_mine_forget) {

            startActivity(PasswordForgetActivity.class);

        } else if (viewId == R.id.btn_home_mine_reset) {

            startActivity(PasswordResetActivity.class);

        } else if (viewId == R.id.btn_home_mine_change) {

            startActivity(PhoneResetActivity.class);

        } else if (viewId == R.id.btn_home_mine_personal) {

            startActivity(PersonalDataActivity.class);

        } else if (viewId == R.id.btn_home_mine_setting) {

            startActivity(SettingActivity.class);

        } else if (viewId == R.id.btn_home_mine_about) {

            startActivity(AboutActivity.class);

        } else if (viewId == R.id.btn_home_mine_guide) {

            startActivity(GuideActivity.class);

        } else if (viewId == R.id.btn_home_mine_browser) {

            Activity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            new InputDialog.Builder(activity)
                    .setTitle("跳转到网页")
                    .setContent("https://juejin.cn/user/712139265815144/posts")
                    .setHint("请输入网页地址")
                    .setConfirm(getString(R.string.common_confirm))
                    .setCancel(getString(R.string.common_cancel))
                    .setListener((dialog, content) -> BrowserActivity.start(activity, content))
                    .show();

        } else if (viewId == R.id.btn_home_mine_image_select) {

            BaseActivity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            ImageSelectActivity.start(activity, new OnImageSelectListener() {

                @Override
                public void onSelected(@NonNull List<String> data) {
                    toast("选择了" + data);
                }

                @Override
                public void onCancel() {
                    toast("取消了");
                }
            });

        } else if (viewId == R.id.btn_home_mine_image_preview) {

            Activity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            List<String> images = new ArrayList<>();
            images.add("https://www.baidu.com/img/bd_logo.png");
            images.add("https://avatars1.githubusercontent.com/u/28616817");
            ImagePreviewActivity.start(activity, images, images.size() - 1);

        } else if (viewId == R.id.btn_home_mine_video_select) {

            BaseActivity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            VideoSelectActivity.start(activity, new VideoSelectActivity.OnVideoSelectListener() {

                @Override
                public void onSelected(@NonNull List<String> data) {
                    toast("选择了" + data);
                }

                @Override
                public void onCancel() {
                    toast("取消了");
                }
            });

        } else if (viewId == R.id.btn_home_mine_video_play) {

            Activity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            new VideoPlayActivity.Builder()
                    .setVideoTitle("速度与激情特别行动")
                    .setVideoSource("http://vfx.mtime.cn/Video/2019/06/29/mp4/190629004821240734.mp4")
                    .setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    .start(activity);

        } else if (viewId == R.id.btn_home_mine_crash) {

            IllegalStateException e = new IllegalStateException("are you ok?");
            // 上报错误到 Bugly 上
            Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
            // 关闭 Bugly 异常捕捉
            Bugly.setCrashMonitorAble(Bugly.JAVA_CRASH, false);
            throw e;

        } else if (viewId == R.id.btn_home_mine_donate) {

            Activity activity = getAttachActivity();
            if (activity == null) {
                return;
            }

            new MessageDialog.Builder(activity)
                    .setTitle("捐赠")
                    .setMessage("如果你觉得这个开源项目很棒，希望它能更好地坚持开发下去，可否愿意花一点点钱（推荐 10.24 元）作为对于开发者的激励")
                    .setConfirm("支付宝")
                    .setCancel(null)
                    //.setAutoDismiss(false)
                    .setListener(dialog -> {
                        BrowserActivity.start(activity, "https://github.com/getActivity/Donate");
                        toast("AndroidProject 因为有你的支持而能够不断更新、完善，非常感谢支持！");
                        postDelayed(() -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2FFKX04202G4K6AVCF5GIY66%3F_s%3Dweb-other"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                toast("打开支付宝失败，你可能还没有安装支付宝客户端");
                            }
                        }, 2000);
                    })
                    .show();
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}