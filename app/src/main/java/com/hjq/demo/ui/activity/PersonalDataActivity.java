package com.hjq.demo.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.ui.dialog.AddressDialog;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.image.ImageLoader;
import com.hjq.widget.layout.SettingBar;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 个人资料
 */
public final class PersonalDataActivity extends MyActivity {

    @BindView(R.id.iv_person_data_avatar)
    ImageView mAvatarView;
    @BindView(R.id.sb_person_data_id)
    SettingBar mIDView;
    @BindView(R.id.sb_person_data_name)
    SettingBar mNameView;
    @BindView(R.id.sb_person_data_address)
    SettingBar mAddressView;
    @BindView(R.id.sb_person_data_phone)
    SettingBar mPhoneView;

    private String mAvatarUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_data;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_person_data_avatar, R.id.fl_person_data_head, R.id.sb_person_data_name, R.id.sb_person_data_address, R.id.sb_person_data_phone})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_person_data_avatar:
                if (mAvatarUrl != null && !"".equals(mAvatarUrl)) {
                    // 查看头像
                    ImageActivity.start(getActivity(), mAvatarUrl);
                } else {
                    // 选择头像
                    onClick(findViewById(R.id.fl_person_data_head));
                }
                break;
            case R.id.fl_person_data_head:
                PhotoActivity.start(getActivity(), new PhotoActivity.OnPhotoSelectListener() {

                    @Override
                    public void onSelect(List<String> data) {
                        mAvatarUrl = data.get(0);
                        ImageLoader.with(getActivity())
                                .load(mAvatarUrl)
                                .into(mAvatarView);
                    }

                    @Override
                    public void onCancel() {}
                });
                break;
            case R.id.sb_person_data_name:
                new InputDialog.Builder(this)
                        // 标题可以不用填写
                        .setTitle(getString(R.string.personal_data_name_hint))
                        .setContent(mNameView.getRightText())
                        //.setHint(getString(R.string.personal_data_name_hint))
                        //.setConfirm("确定")
                        // 设置 null 表示不显示取消按钮
                        //.setCancel("取消")
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setListener(new InputDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (!mNameView.getRightText().equals(content)) {
                                    mNameView.setRightText(content);
                                }
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {}
                        })
                        .show();
                break;
            case R.id.sb_person_data_address:
                new AddressDialog.Builder(this)
                        //.setTitle("选择地区")
                        // 设置默认省份
                        .setProvince("广东省")
                        // 设置默认城市（必须要先设置默认省份）
                        .setCity("广州市")
                        // 不选择县级区域
                        //.setIgnoreArea()
                        .setListener(new AddressDialog.OnListener() {

                            @Override
                            public void onSelected(BaseDialog dialog, String province, String city, String area) {
                                String address = province + city + area;
                                if (!mAddressView.getRightText().equals(address)) {
                                    mAddressView.setRightText(address);
                                }
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {}
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