package com.hjq.demo.ui.activity.account;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.custom.widget.layout.SettingBar;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.ui.activity.common.ImagePreviewActivity;
import com.hjq.demo.ui.activity.common.ImageSelectActivity;
import com.hjq.demo.ui.dialog.common.AddressDialog;
import com.hjq.demo.ui.dialog.common.InputDialog;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 个人资料
 */
public final class PersonalDataActivity extends AppActivity {

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mIdView;
    private SettingBar mNameView;
    private SettingBar mAddressView;

    /** 省 */
    private String mProvince = "广东省";
    /** 市 */
    private String mCity = "广州市";
    /** 区 */
    private String mArea = "天河区";

    /** 头像地址 */
    private Uri mAvatarUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.personal_data_activity;
    }

    @Override
    protected void initView() {
        mAvatarLayout = findViewById(R.id.fl_person_data_avatar);
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mIdView = findViewById(R.id.sb_person_data_id);
        mNameView = findViewById(R.id.sb_person_data_name);
        mAddressView = findViewById(R.id.sb_person_data_address);

        // 适配 RTL 特性
        Drawable iconDrawable;
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            iconDrawable = getDrawable(R.drawable.arrows_left_ic);
        } else {
            iconDrawable = getDrawable(R.drawable.arrows_right_ic);
        }
        mAddressView.setEndDrawable(iconDrawable);

        setOnClickListener(mAvatarLayout, mAvatarView, mNameView, mAddressView);
    }

    @Override
    protected void initData() {
        GlideApp.with(this)
                .load(R.drawable.avatar_placeholder_ic)
                .placeholder(R.drawable.avatar_placeholder_ic)
                .error(R.drawable.avatar_placeholder_ic)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mAvatarView);

        mIdView.setEndText("880634");
        mNameView.setEndText("Android 轮子哥");

        String address = mProvince + mCity + mArea;
        mAddressView.setEndText(address);
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        if (view == mAvatarLayout) {
            ImageSelectActivity.start(this, data -> {
                // 裁剪头像
                cropImageFile(data.get(0));
            });
        } else if (view == mAvatarView) {
            if (mAvatarUrl != null) {
                // 查看头像
                ImagePreviewActivity.start(this, mAvatarUrl.toString());
            } else {
                // 选择头像
                onClick(mAvatarLayout);
            }
        } else if (view == mNameView) {
            new InputDialog.Builder(this)
                    .setTitle(getString(R.string.personal_data_name_hint))
                    .setContent(mNameView.getEndText())
                    //.setHint(getString(R.string.personal_data_name_hint))
                    //.setConfirm("确定")
                    // 设置 null 表示不显示取消按钮
                    //.setCancel("取消")
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener((dialog, content) -> {
                        if (!mNameView.getEndText().equals(content)) {
                            mNameView.setEndText(content);
                        }
                    })
                    .show();
        } else if (view == mAddressView) {
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
                        if (!mAddressView.getEndText().equals(address)) {
                            mProvince = province;
                            mCity = city;
                            mArea = area;
                            mAddressView.setEndText(address);
                        }
                    })
                    .show();
        }
    }

    /**
     * 裁剪图片
     */
    private void cropImageFile(String imagePath) {
        mAvatarUrl = Uri.parse(imagePath);
        GlideApp.with(this)
            .load(imagePath)
            .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
            .into(mAvatarView);
    }
}