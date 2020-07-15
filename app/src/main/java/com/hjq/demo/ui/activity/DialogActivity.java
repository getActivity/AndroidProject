package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.ui.dialog.AddressDialog;
import com.hjq.demo.ui.dialog.DateDialog;
import com.hjq.demo.ui.dialog.HintDialog;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.ui.dialog.PayPasswordDialog;
import com.hjq.demo.ui.dialog.SafeDialog;
import com.hjq.demo.ui.dialog.SelectDialog;
import com.hjq.demo.ui.dialog.ShareDialog;
import com.hjq.demo.ui.dialog.TimeDialog;
import com.hjq.demo.ui.dialog.UpdateDialog;
import com.hjq.demo.ui.dialog.WaitDialog;
import com.hjq.demo.ui.popup.ListPopup;
import com.hjq.demo.wxapi.WXEntryActivity;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengClient;
import com.hjq.umeng.UmengShare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/02
 *    desc   : 对话框使用案例
 */
public final class DialogActivity extends MyActivity {

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
                R.id.btn_dialog_safe, R.id.btn_dialog_custom);
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_message:
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
                            public void onConfirm(BaseDialog dialog) {
                                toast("确定了");
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_input:
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
                            public void onConfirm(BaseDialog dialog, String content) {
                                toast("确定了：" + content);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_bottom_menu:
                List<String> data = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
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
                            public void onSelected(BaseDialog dialog, int position, String string) {
                                toast("位置：" + position + "，文本：" + string);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_center_menu:
                List<String> data1 = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    data1.add("我是数据" + (i + 1));
                }
                // 居中选择框
                new MenuDialog.Builder(this)
                        .setGravity(Gravity.CENTER)
                        // 设置 null 表示不显示取消按钮
                        //.setCancel(null)
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setList(data1)
                        .setListener(new MenuDialog.OnListener<String>() {

                            @Override
                            public void onSelected(BaseDialog dialog, int position, String string) {
                                toast("位置：" + position + "，文本：" + string);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_single_select:
                // 单选对话框
                new SelectDialog.Builder(this)
                        .setTitle("请选择你的性别")
                        .setList("男", "女")
                        // 设置单选模式
                        .setSingleSelect()
                        // 设置默认选中
                        .setSelect(0)
                        .setListener(new SelectDialog.OnListener<String>() {

                            @Override
                            public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                toast("确定了：" + data.toString());
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_more_select:
                // 多选对话框
                new SelectDialog.Builder(this)
                        .setTitle("请选择工作日")
                        .setList("星期一", "星期二", "星期三", "星期四", "星期五")
                        // 设置最大选择数
                        .setMaxSelect(3)
                        // 设置默认选中
                        .setSelect(2, 3, 4)
                        .setListener(new SelectDialog.OnListener<String>() {

                            @Override
                            public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
                                toast("确定了：" + data.toString());
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_succeed_toast:
                // 成功对话框
                new HintDialog.Builder(this)
                        .setIcon(HintDialog.ICON_FINISH)
                        .setMessage("完成")
                        .show();
                break;
            case R.id.btn_dialog_fail_toast:
                // 失败对话框
                new HintDialog.Builder(this)
                        .setIcon(HintDialog.ICON_ERROR)
                        .setMessage("错误")
                        .show();
                break;
            case R.id.btn_dialog_warn_toast:
                // 警告对话框
                new HintDialog.Builder(this)
                        .setIcon(HintDialog.ICON_WARNING)
                        .setMessage("警告")
                        .show();
                break;
            case R.id.btn_dialog_wait:
                // 等待对话框
                final BaseDialog waitDialog = new WaitDialog.Builder(this)
                        // 消息文本可以不用填写
                        .setMessage(getString(R.string.common_loading))
                        .show();
                postDelayed(waitDialog::dismiss, 2000);
                break;
            case R.id.btn_dialog_pay:
                // 支付密码输入对话框
                new PayPasswordDialog.Builder(this)
                        .setTitle(getString(R.string.pay_title))
                        .setSubTitle("用于购买一个女盆友")
                        .setMoney("￥ 100.00")
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setListener(new PayPasswordDialog.OnListener() {

                            @Override
                            public void onCompleted(BaseDialog dialog, String password) {
                                toast(password);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_address:
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
                            public void onSelected(BaseDialog dialog, String province, String city, String area) {
                                toast(province + city + area);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_date:
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
                            public void onSelected(BaseDialog dialog, int year, int month, int day) {
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
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_time:
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
                            public void onSelected(BaseDialog dialog, int hour, int minute, int second) {
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
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_share:
                toast("记得改好第三方 AppID 和 AppKey，否则会调不起来哦");
                toast("也别忘了改微信 " + WXEntryActivity.class.getSimpleName() + " 类所在的包名哦");
                // 分享对话框
                new ShareDialog.Builder(this)
                        // 分享标题
                        .setShareTitle("Github")
                        // 分享描述
                        .setShareDescription("AndroidProject")
                        // 分享缩略图
                        .setShareLogo(R.mipmap.launcher_ic)
                        // 分享链接
                        .setShareUrl("https://github.com/getActivity/AndroidProject")
                        .setListener(new UmengShare.OnShareListener() {

                            @Override
                            public void onSucceed(Platform platform) {
                                toast("分享成功");
                            }

                            @Override
                            public void onError(Platform platform, Throwable t) {
                                toast("分享出错");
                            }

                            @Override
                            public void onCancel(Platform platform) {
                                toast("分享取消");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_update:
                // 升级对话框
                new UpdateDialog.Builder(this)
                        // 版本名
                        .setVersionName("5.2.0")
                        // 是否强制更新
                        .setForceUpdate(false)
                        // 更新日志
                        .setUpdateLog("到底更新了啥\n到底更新了啥\n到底更新了啥\n到底更新了啥\n到底更新了啥")
                        // 下载 URL
                        .setDownloadUrl("https://dldir1.qq.com/weixin/android/weixin7014android1660.apk")
                        // 文件 MD5
                        .setFileMd5("6ec99cb762ffd9158e8b27dc33d9680d")
                        .show();
                break;
            case R.id.btn_dialog_safe:
                new SafeDialog.Builder(this)
                        .setListener(new SafeDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String phone, String code) {
                                toast("手机号：" + phone + "\n验证码：" + code);
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_custom:
                // 自定义对话框
                new BaseDialog.Builder(this)
                        .setContentView(R.layout.custom_dialog)
                        .setAnimStyle(BaseDialog.ANIM_SCALE)
                        //.setText(id, "我是预设置的文本")
                        .setOnClickListener(R.id.btn_dialog_custom_ok, (BaseDialog.OnClickListener<Button>) (dialog, view) -> dialog.dismiss())
                        .addOnShowListener(dialog -> toast("Dialog  显示了"))
                        .addOnCancelListener(dialog -> toast("Dialog 取消了"))
                        .addOnDismissListener(dialog -> toast("Dialog 销毁了"))
                        .setOnKeyListener((dialog, event) -> {
                            toast("按键代码：" + event.getKeyCode());
                            return false;
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟分享回调
        UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRightClick(View v) {
        // 菜单弹窗
        new ListPopup.Builder(this)
                .setList("选择拍照", "选取相册")
                .addOnShowListener(popupWindow -> toast("PopupWindow 显示了"))
                .addOnDismissListener(popupWindow -> toast("PopupWindow 销毁了"))
                .setListener((ListPopup.OnListener<String>) (popupWindow, position, s) -> toast("点击了：" + s))
                .showAsDropDown(v);
    }
}