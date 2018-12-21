package com.hjq.demo.ui.activity;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.dialog.MenuDialog;
import com.hjq.dialog.MessageDialog;
import com.hjq.dialog.PayPasswordDialog;
import com.hjq.dialog.ToastDialog;
import com.hjq.dialog.WaitDialog;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/02
 *    desc   : 对话框使用案例
 */
public class DialogActivity extends MyActivity implements View.OnClickListener {

    @BindView(R.id.btn_dialog_message)
    Button mMessageView;
    @BindView(R.id.btn_dialog_bottom_menu)
    Button mBottomMenuView;
    @BindView(R.id.btn_dialog_center_menu)
    Button mCenterMenuView;

    @BindView(R.id.btn_dialog_succeed_toast)
    Button mSucceedToastView;
    @BindView(R.id.btn_dialog_fail_toast)
    Button mFailToastView;
    @BindView(R.id.btn_dialog_warn_toast)
    Button mWarnToastView;

    @BindView(R.id.btn_dialog_wait)
    Button mWaitView;

    @BindView(R.id.btn_dialog_pay)
    Button mPayView;

    @BindView(R.id.btn_dialog_custom)
    Button mCustomView;

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
        mMessageView.setOnClickListener(this);
        mBottomMenuView.setOnClickListener(this);
        mCenterMenuView.setOnClickListener(this);

        mSucceedToastView.setOnClickListener(this);
        mFailToastView.setOnClickListener(this);
        mWarnToastView.setOnClickListener(this);

        mWaitView.setOnClickListener(this);

        mPayView.setOnClickListener(this);

        mCustomView.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    /**
     * {@link View.OnClickListener}
     */
    @Override
    public void onClick(View v) {
        if (v == mMessageView) { // 消息对话框

            new MessageDialog.Builder(this)
                    .setTitle("我是标题") // 标题可以不用填写
                    .setMessage("我是内容")
                    .setConfirm("确定")
                    .setCancel("取消") // 设置 null 表示不显示取消按钮
                    //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void confirm(Dialog dialog) {
                            ToastUtils.show("确定了");
                        }

                        @Override
                        public void cancel(Dialog dialog) {
                            ToastUtils.show("取消了");
                        }
                    })
                    .show();

        } else if (v == mBottomMenuView) { // 底部选择框

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
                        public void select(Dialog dialog, int position, String text) {
                            ToastUtils.show("位置：" + position + "，文本：" + text);
                        }

                        @Override
                        public void cancel(Dialog dialog) {
                            ToastUtils.show("取消了");
                        }
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                    .show();

        } else if (v == mCenterMenuView) { // 居中选择框

            List<String> data = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                data.add("我是数据" + i);
            }
            new MenuDialog.Builder(this)
                    .setCancel(null) // 设置 null 表示不显示取消按钮
                    //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                    .setList(data)
                    .setListener(new MenuDialog.OnListener() {

                        @Override
                        public void select(Dialog dialog, int position, String text) {
                            ToastUtils.show("位置：" + position + "，文本：" + text);
                        }

                        @Override
                        public void cancel(Dialog dialog) {
                            ToastUtils.show("取消了");
                        }
                    })
                    .setGravity(Gravity.CENTER)
                    .setAnimStyle(BaseDialog.AnimStyle.SCALE)
                    .show();

        } else if (v == mSucceedToastView) { // 成功对话框

            new ToastDialog.Builder(this)
                    .setType(ToastDialog.Type.FINISH)
                    .setMessage("完成")
                    .show();

        } else if (v == mFailToastView) { // 失败对话框

            new ToastDialog.Builder(this)
                    .setType(ToastDialog.Type.ERROR)
                    .setMessage("错误")
                    .show();

        } else if (v == mWarnToastView) { // 警告对话框

            new ToastDialog.Builder(this)
                    .setType(ToastDialog.Type.WARN)
                    .setMessage("警告")
                    .show();

        } else if (v == mWaitView) { // 等待对话框

            final BaseDialog dialog = new WaitDialog.Builder(this)
                    .setMessage("加载中...") // 消息文本可以不用填写
                    .show();
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 3000);

        } else if (v == mPayView) { // 支付密码输入对话框

            new PayPasswordDialog.Builder(this)
                    .setTitle("请输入支付密码")
                    .setSubTitle("用于购买一个女盆友")
                    .setMoney("￥ 100.00")
                    //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                    .setListener(new PayPasswordDialog.OnListener() {

                        @Override
                        public void complete(Dialog dialog, String password) {
                            ToastUtils.show(password);
                        }

                        @Override
                        public void cancel(Dialog dialog) {
                            ToastUtils.show("取消了");
                        }
                    })
                    .show();

        } else if (v == mCustomView) { // 自定义对话框

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
        }
    }
}