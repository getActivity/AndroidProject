package com.hjq.demo.ui.activity;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.dialog.AddressDialog;
import com.hjq.dialog.DateDialog;
import com.hjq.dialog.InputDialog;
import com.hjq.dialog.MenuDialog;
import com.hjq.dialog.MessageDialog;
import com.hjq.dialog.PayPasswordDialog;
import com.hjq.dialog.ToastDialog;
import com.hjq.dialog.WaitDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/02
 *    desc   : 对话框使用案例
 */
public class DialogActivity extends MyActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dialog;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_dialog_title;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_dialog_message, R.id.btn_dialog_input, R.id.btn_dialog_bottom_menu, R.id.btn_dialog_center_menu,
            R.id.btn_dialog_succeed_toast, R.id.btn_dialog_fail_toast, R.id.btn_dialog_warn_toast, R.id.btn_dialog_wait,
            R.id.btn_dialog_pay, R.id.btn_dialog_address, R.id.btn_dialog_date, R.id.btn_dialog_custom})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_message: // 消息对话框
                new MessageDialog.Builder(this)
                        .setTitle("我是标题") // 标题可以不用填写
                        .setMessage("我是内容")
                        .setConfirm("确定")
                        .setCancel("取消") // 设置 null 表示不显示取消按钮
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(Dialog dialog) {
                                toast("确定了");
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_input: // 输入对话框
                new InputDialog.Builder(this)
                        .setTitle("我是标题") // 标题可以不用填写
                        .setContent("我是内容")
                        .setHint("我是提示")
                        .setConfirm("确定")
                        .setCancel("取消") // 设置 null 表示不显示取消按钮
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setListener(new InputDialog.OnListener() {

                            @Override
                            public void onConfirm(Dialog dialog, String content) {
                                toast("确定了：" + content);
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_bottom_menu: // 底部选择框
                List<String> data = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    data.add("我是数据" + i);
                }
                new MenuDialog.Builder(this)
                        .setCancel("取消") // 设置 null 表示不显示取消按钮
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setList(data)
                        .setListener(new MenuDialog.OnListener() {

                            @Override
                            public void onSelected(Dialog dialog, int position, String text) {
                                toast("位置：" + position + "，文本：" + text);
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .setGravity(Gravity.BOTTOM)
                        .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                        .show();
                break;
            case R.id.btn_dialog_center_menu: // 居中选择框
                List<String> data1 = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    data1.add("我是数据" + i);
                }
                new MenuDialog.Builder(this)
                        .setCancel(null) // 设置 null 表示不显示取消按钮
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setList(data1)
                        .setListener(new MenuDialog.OnListener() {

                            @Override
                            public void onSelected(Dialog dialog, int position, String text) {
                                toast("位置：" + position + "，文本：" + text);
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .setGravity(Gravity.CENTER)
                        .setAnimStyle(BaseDialog.AnimStyle.SCALE)
                        .show();
                break;
            case R.id.btn_dialog_succeed_toast: // 成功对话框
                new ToastDialog.Builder(this)
                        .setType(ToastDialog.Type.FINISH)
                        .setMessage("完成")
                        .show();
                break;
            case R.id.btn_dialog_fail_toast: // 失败对话框
                new ToastDialog.Builder(this)
                        .setType(ToastDialog.Type.ERROR)
                        .setMessage("错误")
                        .show();
                break;
            case R.id.btn_dialog_warn_toast: // 警告对话框
                new ToastDialog.Builder(this)
                        .setType(ToastDialog.Type.WARN)
                        .setMessage("警告")
                        .show();
                break;
            case R.id.btn_dialog_wait: // 等待对话框
                final BaseDialog dialog = new WaitDialog.Builder(this)
                        .setMessage("加载中...") // 消息文本可以不用填写
                        .show();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 3000);
                break;
            case R.id.btn_dialog_pay: // 支付密码输入对话框
                new PayPasswordDialog.Builder(this)
                        .setTitle("请输入支付密码")
                        .setSubTitle("用于购买一个女盆友")
                        .setMoney("￥ 100.00")
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setListener(new PayPasswordDialog.OnListener() {

                            @Override
                            public void onCompleted(Dialog dialog, String password) {
                                toast(password);
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_address: // 选择地区对话框
                new AddressDialog.Builder(this)
                        .setTitle("选择地区")
                        //.setIgnoreArea() // 不选择县级区域
                        .setListener(new AddressDialog.OnListener() {

                            @Override
                            public void onSelected(Dialog dialog, String province, String city, String area) {
                                toast(province + city + area);
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_date: // 日期选择对话框
                new DateDialog.Builder(this)
                        .setTitle("请选择日期")
                        .setListener(new DateDialog.OnListener() {
                            @Override
                            public void onSelected(Dialog dialog, int year, int month, int day) {
                                toast(year + "年" + month + "月" + day + "日");
                            }

                            @Override
                            public void onCancel(Dialog dialog) {
                                toast("取消了");
                            }
                        })
                        .show();
                break;
            case R.id.btn_dialog_custom: // 自定义对话框
                new BaseDialogFragment.Builder(this)
                        .setContentView(R.layout.dialog_custom)
                        .setAnimStyle(BaseDialog.AnimStyle.SCALE)
                        //.setText(id, "我是预设置的文本")
                        .setOnClickListener(R.id.btn_dialog_custom_ok, new BaseDialog.OnClickListener<ImageView>() {

                            @Override
                            public void onClick(Dialog dialog, ImageView view) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }
}