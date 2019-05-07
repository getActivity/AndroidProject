package com.hjq.demo.ui.activity;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;

import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.dialog.AddressDialog;
import com.hjq.dialog.InputDialog;
import com.hjq.widget.SettingBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 个人资料
 */
public final class PersonalDataActivity extends MyActivity {

    @BindView(R.id.iv_person_data_head)
    ImageView mHeadView;
    @BindView(R.id.sb_person_data_id)
    SettingBar mIDView;
    @BindView(R.id.sb_person_data_name)
    SettingBar mNameView;
    @BindView(R.id.sb_person_data_address)
    SettingBar mAddressView;
    @BindView(R.id.sb_person_data_phone)
    SettingBar mPhoneView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_data;
    }

    @Override
    protected int getTitleId() {
        return R.id.tb_personal_data_title;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.fl_person_data_head, R.id.sb_person_data_name, R.id.sb_person_data_address, R.id.sb_person_data_phone})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_person_data_head:

                break;
            case R.id.sb_person_data_name:
                new InputDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.personal_data_name_hint)) // 标题可以不用填写
                        .setContent(mNameView.getRightText())
                        //.setHint(getResources().getString(R.string.personal_data_name_hint))
                        //.setConfirm("确定")
                        //.setCancel("取消") // 设置 null 表示不显示取消按钮
                        //.setAutoDismiss(false) // 设置点击按钮后不关闭对话框
                        .setListener(new InputDialog.OnListener() {

                            @Override
                            public void onConfirm(Dialog dialog, String content) {
                                if (!mNameView.getRightText().equals(content)) {
                                    mNameView.setRightText(content);
                                }
                            }

                            @Override
                            public void onCancel(Dialog dialog) {}
                        })
                        .show();
                break;
            case R.id.sb_person_data_address:
                new AddressDialog.Builder(this)
                        //.setTitle("选择地区")
                        //.setProvince("广东省") // 设置默认省份
                        //.setCity("广州市") // 设置默认城市（必须要先设置默认省份）
                        .setIgnoreArea() // 不选择县级区域
                        .setListener(new AddressDialog.OnListener() {

                            @Override
                            public void onSelected(Dialog dialog, String province, String city, String area) {
                                if (!mAddressView.getRightText().equals(province + city)) {
                                    mAddressView.setRightText(province + city);
                                }
                            }

                            @Override
                            public void onCancel(Dialog dialog) {}
                        })
                        .show();
                break;
            case R.id.sb_person_data_phone:
                // 先判断有没有设置过手机号
                if (true) {
                    startActivity(PhoneVerifyActivity.class);
                } else {
                    startActivity(PhoneResetActivity.class);
                }
                break;
            default:
                break;
        }
    }
}