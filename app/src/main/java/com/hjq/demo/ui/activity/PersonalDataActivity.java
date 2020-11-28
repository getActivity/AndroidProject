package com.hjq.demo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.UpdateImageApi;
import com.hjq.demo.ui.dialog.AddressDialog;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 个人资料
 */
public final class PersonalDataActivity extends MyActivity {

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mIDView;
    private SettingBar mNameView;
    private SettingBar mAddressView;

    /** 省 */
    private String mProvince = "广东省";
    /** 市 */
    private String mCity = "广州市";
    /** 区 */
    private String mArea = "天河区";

    /** 头像地址 */
    private String mAvatarUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.personal_data_activity;
    }

    @Override
    protected void initView() {
        mAvatarLayout = findViewById(R.id.fl_person_data_avatar);
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mIDView = findViewById(R.id.sb_person_data_id);
        mNameView = findViewById(R.id.sb_person_data_name);
        mAddressView = findViewById(R.id.sb_person_data_address);
        setOnClickListener(mAvatarLayout, mAvatarView, mNameView, mAddressView);
    }

    @Override
    protected void initData() {
        GlideApp.with(getActivity())
                .load(R.drawable.avatar_placeholder_ic)
                .placeholder(R.drawable.avatar_placeholder_ic)
                .error(R.drawable.avatar_placeholder_ic)
                .circleCrop()
                .into(mAvatarView);

        String address = mProvince + mCity + mArea;
        mAddressView.setRightText(address);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mAvatarLayout) {
            ImageSelectActivity.start(this, data -> {

                if (true) {
                    mAvatarUrl = data.get(0);
                    GlideApp.with(getActivity())
                            .load(mAvatarUrl)
                            .into(mAvatarView);
                    return;
                }
                // 上传头像
                EasyHttp.post(this)
                        .api(new UpdateImageApi()
                                .setImage(new File(data.get(0))))
                        .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this) {

                            @Override
                            public void onSucceed(HttpData<String> data) {
                                mAvatarUrl = data.getData();
                                GlideApp.with(getActivity())
                                        .load(mAvatarUrl)
                                        .into(mAvatarView);
                            }
                        });
            });
        } else if (v == mAvatarView) {
            if (!TextUtils.isEmpty(mAvatarUrl)) {
                // 查看头像
                ImagePreviewActivity.start(getActivity(), mAvatarUrl);
            } else {
                // 选择头像
                onClick(mAvatarLayout);
            }
        } else if (v == mNameView) {
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
                    .setListener((dialog, content) -> {
                        if (!mNameView.getRightText().equals(content)) {
                            mNameView.setRightText(content);
                        }
                    })
                    .show();
        } else if (v == mAddressView) {
            new AddressDialog.Builder(this)
                    //.setTitle("选择地区")
                    // 设置默认省份
                    .setProvince(mProvince)
                    // 设置默认城市（必须要先设置默认省份）
                    .setCity(mCity)
                    // 不选择县级区域
                    //.setIgnoreArea()
                    .setListener((dialog, province, city, area) -> {
                        String address = province + city + area;
                        if (!mAddressView.getRightText().equals(address)) {
                            mProvince = province;
                            mCity = city;
                            mArea = area;
                            mAddressView.setRightText(address);
                        }
                    })
                    .show();
        }
    }
}