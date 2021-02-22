package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.request.UpdateImageApi;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.dialog.AddressDialog;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private String mAvatarUrl;

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
        setOnClickListener(mAvatarLayout, mAvatarView, mNameView, mAddressView);
    }

    @Override
    protected void initData() {
        GlideApp.with(getActivity())
                .load(R.drawable.avatar_placeholder_ic)
                .placeholder(R.drawable.avatar_placeholder_ic)
                .error(R.drawable.avatar_placeholder_ic)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mAvatarView);

        mIdView.setRightText("880634");
        mNameView.setRightText("Android 轮子哥");

        String address = mProvince + mCity + mArea;
        mAddressView.setRightText(address);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mAvatarLayout) {
            ImageSelectActivity.start(this, data -> {
                // 裁剪头像
                cropImage(new File(data.get(0)));
            });
        } else if (view == mAvatarView) {
            if (!TextUtils.isEmpty(mAvatarUrl)) {
                // 查看头像
                ImagePreviewActivity.start(getActivity(), mAvatarUrl);
            } else {
                // 选择头像
                onClick(mAvatarLayout);
            }
        } else if (view == mNameView) {
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

    /**
     * 裁剪图片
     */
    private void cropImage(File sourceFile) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getContext(), AppConfig.getPackageName() + ".provider", sourceFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(sourceFile);
        }

        String regex = "^(.+)(\\..+)$";
        String fileName = sourceFile.getName().replaceFirst(regex, "$1_crop_" + new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date())+ "$2");

        File outputFile = new File(sourceFile.getParent(), fileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        intent.setDataAndType(uri, "image/*");
        // 是否进行裁剪
        intent.putExtra("crop", String.valueOf(true));
        // 宽高裁剪比例
        if (Build.MANUFACTURER.toUpperCase().contains("HUAWEI")) {
            // 华为手机特殊处理，否则不会显示正方形裁剪区域，而是显示圆形裁剪区域
            // https://blog.csdn.net/wapchief/article/details/80669647
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        // 是否裁剪成圆形（注：在华为手机上没有任何效果）
        // intent.putExtra("circleCrop", false);
        // 宽高裁剪大小
        // intent.putExtra("outputX", 200);
        // intent.putExtra("outputY", 200);
        // 是否保持比例不变
        intent.putExtra("scale", true);
        // 裁剪区域小于输出大小时，是否放大图像
        intent.putExtra("scaleUpIfNeeded", true);
        // 是否将数据以 Bitmap 的形式保存
        intent.putExtra("return-data", false);
        // 设置裁剪后保存的文件路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        // 设置裁剪后保存的文件格式
        intent.putExtra("outputFormat", getImageFormat(sourceFile).toString());

        // 判断手机是否有裁剪功能
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, (resultCode, data) -> {
                if (resultCode == RESULT_OK) {
                    updateImage(outputFile, true);
                }
            });
            return;
        }

        // 没有的话就不裁剪，直接上传原图片
        // 但是这种情况极其少见，可以忽略不计
        updateImage(sourceFile, false);
    }

    /**
     * 上传图片
     */
    private void updateImage(File file, boolean deleteFile) {
        if (true) {
            mAvatarUrl = file.getPath();
            GlideApp.with(getActivity())
                    .load(mAvatarUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(mAvatarView);
            return;
        }

        EasyHttp.post(this)
                .api(new UpdateImageApi()
                        .setImage(file))
                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        mAvatarUrl = data.getData();
                        GlideApp.with(getActivity())
                                .load(mAvatarUrl)
                                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                                .into(mAvatarView);
                        if (deleteFile) {
                            file.delete();
                        }
                    }
                });
    }

    /**
     * 获取图片文件的格式
     */
    private Bitmap.CompressFormat getImageFormat(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".png")) {
            return Bitmap.CompressFormat.PNG;
        } else if (fileName.endsWith(".webp")) {
            return Bitmap.CompressFormat.WEBP;
        }
        return Bitmap.CompressFormat.JPEG;
    }
}