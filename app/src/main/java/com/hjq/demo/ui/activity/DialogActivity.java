package com.hjq.demo.ui.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.base.BasePopupWindow;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.manager.DialogManager;
import com.hjq.demo.ui.dialog.PayPasswordDialog;
import com.hjq.demo.ui.dialog.SafeDialog;
import com.hjq.demo.ui.dialog.UpdateDialog;
import com.hjq.demo.ui.dialog.common.AddressDialog;
import com.hjq.demo.ui.dialog.common.DateDialog;
import com.hjq.demo.ui.dialog.common.InputDialog;
import com.hjq.demo.ui.dialog.common.MenuDialog;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import com.hjq.demo.ui.dialog.common.SelectDialog;
import com.hjq.demo.ui.dialog.common.ShareDialog;
import com.hjq.demo.ui.dialog.common.TimeDialog;
import com.hjq.demo.ui.dialog.common.TipsDialog;
import com.hjq.demo.ui.dialog.common.WaitDialog;
import com.hjq.demo.ui.popup.ListPopup;
import com.hjq.umeng.sdk.Platform;
import com.hjq.umeng.sdk.UmengShare;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/02
 *    desc   : 对话框使用案例
 */
public final class DialogActivity extends AppActivity {

    /** 等待对话框 */
    private BaseDialog mWaitDialog;

    /** 菜单弹窗 */
    private BasePopupWindow mListPopup;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_activity;
    }

    @Override
    protected void initView() {
        setOnClickListener(R.id.btn_dialog_message, R.id.btn_dialog_input,
                R.id.btn_dialog_bottom_menu, R.id.btn_dialog_center_menu,
                R.id.btn_dialog_single_select, R.id.btn_dialog_more_select,
                R.id.btn_dialog_succeed_toast, R.id.btn_dialog_fail_toast,
                R.id.btn_dialog_warn_toast, R.id.btn_dialog_wait,
                R.id.btn_dialog_pay, R.id.btn_dialog_address,
                R.id.btn_dialog_date, R.id.btn_dialog_time,
                R.id.btn_dialog_update, R.id.btn_dialog_share,
                R.id.btn_dialog_safe, R.id.btn_dialog_custom,
                R.id.btn_dialog_multi);
    }

    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    public View getImmersionBottomView() {
        return findViewById(R.id.ll_dialog_content);
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_dialog_message) {

            // 消息对话框
            new MessageDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle("我是标题")
                    // 内容必须要填写
                    .setMessage("我是内容")
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(@NonNull BaseDialog dialog) {
                            toast("确定了");
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_input) {

            // 输入对话框
            new InputDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle("我是标题")
                    // 内容可以不用填写
                    .setContent("我是内容")
                    // 提示可以不用填写
                    .setHint("我是提示")
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new InputDialog.OnListener() {

                        @Override
                        public void onConfirm(@NonNull BaseDialog dialog, String content) {
                            toast("确定了：" + content);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_bottom_menu) {

            List<String> data = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                data.add("我是数据" + (i + 1));
            }
            // 底部选择框
            new MenuDialog.Builder(this)
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(data)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, int position, String data) {
                            toast("位置：" + position + "，文本：" + data);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_center_menu) {

            List<String> data = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                data.add("我是数据" + (i + 1));
            }
            // 居中选择框
            new MenuDialog.Builder(this)
                    .setGravity(Gravity.CENTER)
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(null)
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(data)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, int position, String data) {
                            toast("位置：" + position + "，文本：" + data);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_single_select) {

            // 单选对话框
            new SelectDialog.Builder(this)
                    .setTitle("请选择你的性别")
                    .setList("男", "女")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(0)
                    .setSingleListener(new SelectDialog.OnSingleListener<String>() {

                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, int position, String data) {
                            toast("位置：" + position + "，数据：" + data);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_more_select) {

            // 多选对话框
            new SelectDialog.Builder(this)
                    .setTitle("请选择工作日")
                    .setList("星期一", "星期二", "星期三", "星期四", "星期五")
                    // 设置最大选择数
                    .setMaxSelect(3)
                    // 设置默认选中
                    .setSelect(2, 3, 4)
                    .setMultiListener(new SelectDialog.OnMultiListener<String>() {

                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, Map<Integer, String> data) {
                            toast("确定了：" + data.toString());
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_succeed_toast) {

            // 成功对话框
            new TipsDialog.Builder(this)
                    .setIcon(TipsDialog.ICON_FINISH)
                    .setMessage("完成")
                    .show();

        } else if (viewId == R.id.btn_dialog_fail_toast) {

            // 失败对话框
            new TipsDialog.Builder(this)
                    .setIcon(TipsDialog.ICON_ERROR)
                    .setMessage("错误")
                    .show();

        } else if (viewId == R.id.btn_dialog_warn_toast) {

            // 警告对话框
            new TipsDialog.Builder(this)
                    .setIcon(TipsDialog.ICON_WARNING)
                    .setMessage("警告")
                    .show();

        } else if (viewId == R.id.btn_dialog_wait) {

            if (mWaitDialog == null) {
                mWaitDialog = new WaitDialog.Builder(this)
                        // 消息文本可以不用填写
                        .setMessage(getString(R.string.common_loading))
                        .create();
            }
            if (!mWaitDialog.isShowing()) {
                mWaitDialog.show();
                postDelayed(mWaitDialog::dismiss, 2000);
            }

        } else if (viewId == R.id.btn_dialog_pay) {

            // 支付密码输入对话框
            new PayPasswordDialog.Builder(this)
                    .setTitle(getString(R.string.pay_title))
                    .setSubTitle("用于购买一个女盆友")
                    .setMoney("￥ 100.00")
                    //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                    .setListener(new PayPasswordDialog.OnListener() {

                        @Override
                        public void onCompleted(@NonNull BaseDialog dialog, String password) {
                            toast(password);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_address) {

            // 选择地区对话框
            new AddressDialog.Builder(this)
                    .setTitle(getString(R.string.address_title))
                    // 设置默认省份
                    //.setProvince("广东省")
                    // 设置默认城市（必须要先设置默认省份）
                    //.setCity("广州市")
                    // 不选择县级区域
                    //.setIgnoreArea()
                    .setListener(new AddressDialog.OnListener() {

                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, @NonNull String province, @NonNull String city, @NonNull String area) {
                            toast(province + city + area);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_date) {

            // 日期选择对话框
            new DateDialog.Builder(this)
                    .setTitle(getString(R.string.date_title))
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置日期
                    //.setDate("2018-12-31")
                    //.setDate("20181231")
                    //.setDate(1546263036137)
                    // 设置年份
                    //.setYear(2018)
                    // 设置月份
                    //.setMonth(2)
                    // 设置天数
                    //.setDay(20)
                    // 不选择天数
                    //.setIgnoreDay()
                    .setListener(new DateDialog.OnListener() {
                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, int year, int month, int day) {
                            toast(year + getString(R.string.common_year) + month + getString(R.string.common_month) + day + getString(R.string.common_day));

                            // 如果不指定时分秒则默认为现在的时间
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            // 月份从零开始，所以需要减 1
                            calendar.set(Calendar.MONTH, month - 1);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            toast("时间戳：" + calendar.getTimeInMillis());
                            //toast(new SimpleDateFormat("yyyy年MM月dd日 kk:mm:ss").format(calendar.getTime()));
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_time) {

            // 时间选择对话框
            new TimeDialog.Builder(this)
                    .setTitle(getString(R.string.time_title))
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置时间
                    //.setTime("23:59:59")
                    //.setTime("235959")
                    // 设置小时
                    //.setHour(23)
                    // 设置分钟
                    //.setMinute(59)
                    // 设置秒数
                    //.setSecond(59)
                    // 不选择秒数
                    //.setIgnoreSecond()
                    .setListener(new TimeDialog.OnListener() {

                        @Override
                        public void onSelected(@NonNull BaseDialog dialog, int hour, int minute, int second) {
                            toast(hour + getString(R.string.common_hour) + minute + getString(R.string.common_minute) + second + getString(R.string.common_second));

                            // 如果不指定年月日则默认为今天的日期
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, second);
                            toast("时间戳：" + calendar.getTimeInMillis());
                            //toast(new SimpleDateFormat("yyyy年MM月dd日 kk:mm:ss").format(calendar.getTime()));
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_share) {

            toast("记得改好第三方 AppID 和 Secret，否则会调不起来哦");

            UMWeb umWeb = new UMWeb("https://github.com/getActivity/AndroidProject");
            umWeb.setTitle("Github");
            umWeb.setThumb(new UMImage(this, R.mipmap.launcher_ic));
            umWeb.setDescription(getString(R.string.app_name));

            /* UMImage umImage = new UMImage(this, R.mipmap.launcher_ic); */

            // 分享对话框
            new ShareDialog.Builder(this)
                    .setShareLink(umWeb)
                    .setListener(new UmengShare.OnShareListener() {

                        @Override
                        public void onShareSuccess(@NonNull Platform platform) {
                            toast("分享成功");
                        }

                        @Override
                        public void onShareFail(@NonNull Platform platform, @NonNull Throwable throwable) {
                            toast(throwable.getMessage());
                        }

                        @Override
                        public void onShareCancel(@NonNull Platform platform) {
                            toast("分享取消");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_update) {

            // 升级对话框
            new UpdateDialog.Builder(this)
                    // 版本名
                    .setVersionName("5.2.0")
                    // 是否强制更新
                    .setForceUpdate(false)
                    // 更新日志
                    .setUpdateLog("到底更新了啥\n到底更新了啥\n到底更新了啥\n到底更新了啥\n到底更新了啥\n到底更新了啥")
                    // 下载 URL
                    .setDownloadUrl("https://dldir1.qq.com/weixin/android/weixin8015android2020_arm64.apk")
                    // 文件 MD5
                    .setFileMd5("b05b25d4738ea31091dd9f80f4416469")
                    .show();

        } else if (viewId == R.id.btn_dialog_safe) {

            // 身份校验对话框
            new SafeDialog.Builder(this)
                    .setListener(new SafeDialog.OnListener() {

                        @Override
                        public void onConfirm(@NonNull BaseDialog dialog, @NonNull String phone, @NonNull String code) {
                            toast("手机号：" + phone + "\n验证码：" + code);
                        }

                        @Override
                        public void onCancel(@NonNull BaseDialog dialog) {
                            toast("取消了");
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_dialog_custom) {

            // 自定义对话框
            new BaseDialog.Builder<>(this)
                    .setContentView(R.layout.custom_dialog)
                    .setAnimStyle(BaseDialog.ANIM_SCALE)
                    .setOnCreateListener(dialog -> toast("Dialog 创建了"))
                    .addOnShowListener(dialog -> toast("Dialog 显示了"))
                    .addOnCancelListener(dialog -> toast("Dialog 取消了"))
                    .addOnDismissListener(dialog -> toast("Dialog 销毁了"))
                    .setOnKeyListener((dialog, event) -> {
                        toast("按键代码：" + event.getKeyCode());
                        return false;
                    })
                    .setOnClickListenerByView(R.id.btn_dialog_custom_ok, (BaseDialog.OnClickListener<Button>) (dialog, button) -> dialog.dismiss())
                    .show();

        } else if (viewId == R.id.btn_dialog_multi) {

            BaseDialog dialog1 = new MessageDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("我是第一个弹出的对话框")
                    .setConfirm(getString(R.string.common_confirm))
                    .setCancel(getString(R.string.common_cancel))
                    .create();

            BaseDialog dialog2 = new MessageDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("我是第二个弹出的对话框")
                    .setConfirm(getString(R.string.common_confirm))
                    .setCancel(getString(R.string.common_cancel))
                    .create();

            BaseDialog dialog3 = new MessageDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("我是第三个弹出的对话框")
                    .setConfirm(getString(R.string.common_confirm))
                    .setCancel(getString(R.string.common_cancel))
                    .create();

            DialogManager.getInstance(this).addDialog(dialog1);
            DialogManager.getInstance(this).addDialog(dialog2);
            DialogManager.getInstance(this).addDialog(dialog3);
            DialogManager.getInstance(this).startShow();
        }
    }

    @Override
    public void onRightClick(TitleBar titleBar) {
        if (mListPopup == null) {
            mListPopup = new ListPopup.Builder(this)
                .setList("选择拍照", "选取相册")
                .addOnShowListener(popupWindow -> toast("PopupWindow 显示了"))
                .addOnDismissListener(popupWindow -> toast("PopupWindow 销毁了"))
                .setListener((ListPopup.OnListener<String>) (popupWindow, position, s) -> toast("点击了：" + s))
                .create();
        }
        if (!mListPopup.isShowing()) {
            mListPopup.showAsDropDown(titleBar.getRightView());
        }
    }
}