package com.hjq.demo.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.other.AppConfig;
import com.hjq.permissions.Permission;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/09/19
 *    desc   : 图片裁剪
 */
public final class ImageCropActivity extends AppActivity {

    private static final String INTENT_KEY_IN_SOURCE_IMAGE_PATH = "imagePath";
    private static final String INTENT_KEY_IN_CROP_RATIO_X = "cropRatioX";
    private static final String INTENT_KEY_IN_CROP_RATIO_Y = "cropRatioY";

    public static final String INTENT_KEY_OUT_FILE_URI = "fileUri";
    public static final String INTENT_KEY_OUT_FILE_NAME = "fileName";
    public static final String INTENT_KEY_OUT_ERROR = "error";

    public static void start(BaseActivity activity, File file, OnCropListener listener) {
        start(activity, file, 0, 0, listener);
    }

    @Log
    @Permissions({Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE})
    public static void start(BaseActivity activity, File file, int cropRatioX, int cropRatioY, OnCropListener listener) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(INTENT_KEY_IN_SOURCE_IMAGE_PATH, file.toString());
        intent.putExtra(INTENT_KEY_IN_CROP_RATIO_X, cropRatioX);
        intent.putExtra(INTENT_KEY_IN_CROP_RATIO_Y, cropRatioY);
        activity.startActivityForResult(intent, (resultCode, data) -> {
            if (listener == null) {
                return;
            }

            switch (resultCode) {
                case RESULT_OK:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getParcelableExtra(INTENT_KEY_OUT_FILE_URI);
                    }
                    if (uri != null) {
                        listener.onSucceed(uri, data.getStringExtra(INTENT_KEY_OUT_FILE_NAME));
                    } else {
                        listener.onCancel();
                    }
                    break;
                case RESULT_ERROR:
                    String details;
                    if (data == null || (details = data.getStringExtra(INTENT_KEY_OUT_ERROR)) == null) {
                        details = activity.getString(R.string.common_unknown_error);
                    }
                    listener.onError(details);
                    break;
                case RESULT_CANCELED:
                default:
                    listener.onCancel();
                    break;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {}

    @Override
    protected void initData() {
        File sourceFile = new File(getString(INTENT_KEY_IN_SOURCE_IMAGE_PATH));
        int cropRatioX = getInt(INTENT_KEY_IN_CROP_RATIO_X);
        int cropRatioY = getInt(INTENT_KEY_IN_CROP_RATIO_Y);

        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri sourceUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sourceUri = FileProvider.getUriForFile(getContext(), AppConfig.getPackageName() + ".provider", sourceFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            sourceUri = Uri.fromFile(sourceFile);
        }

        String fileName = "CROP_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) +
                "." + getImageFormat(sourceFile).toString().toLowerCase();
        String subFolderName = "CropImage";

        intent.setDataAndType(sourceUri, "image/*");
        // 是否进行裁剪
        intent.putExtra("crop", String.valueOf(true));
        // 是否裁剪成圆形（注意：在某些手机上没有任何效果，例如华为机）
        //intent.putExtra("circleCrop", true);
        // 宽高裁剪大小
        //intent.putExtra("outputX", builder.getCropWidth());
        //intent.putExtra("outputY", builder.getCropHeight());

        if (cropRatioX != 0 && cropRatioY != 0) {
            // 宽高裁剪比例
            if (cropRatioX == cropRatioY &&
                    Build.MANUFACTURER.toUpperCase().contains("HUAWEI")) {
                // 华为手机特殊处理，否则不会显示正方形裁剪区域，而是显示圆形裁剪区域
                // https://blog.csdn.net/wapchief/article/details/80669647
                intent.putExtra("aspectX", 9998);
                intent.putExtra("aspectY", 9999);
            } else {
                intent.putExtra("aspectX", cropRatioX);
                intent.putExtra("aspectY", cropRatioY);
            }
        }

        // 是否保持比例不变
        intent.putExtra("scale", true);

        // 裁剪区域小于输出大小时，是否放大图像
        intent.putExtra("scaleUpIfNeeded", true);
        // 是否将数据以 Bitmap 的形式保存
        intent.putExtra("return-data", false);
        // 设置裁剪后保存的文件路径
        Uri outputUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 适配 Android 10 分区存储特性
            ContentValues values = new ContentValues();
            // 设置显示的文件名
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            // 设置输出的路径信息
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + subFolderName);
            // 生成一个新的 uri 路径
            outputUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            File folderFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + subFolderName);
            if (!folderFile.isDirectory()) {
                folderFile.delete();
            }
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            outputUri = Uri.fromFile(new File(folderFile, fileName));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        // 设置裁剪后保存的文件格式
        intent.putExtra("outputFormat", getImageFormat(sourceFile).toString());

        try {
            // 因为有人反馈 Intent.resolveActivity 在某些手机上判断不准确
            // 手机明明有裁剪界面，但是系统偏偏就告诉应用没有
            // 出现这个问题的机型信息：一加手机 8，Android 11
            startActivityForResult(intent, (resultCode, data) -> {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, new Intent()
                            .putExtra(INTENT_KEY_OUT_FILE_URI, outputUri)
                            .putExtra(INTENT_KEY_OUT_FILE_NAME, fileName));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // 删除这个 uri，避免重复占用
                        getContentResolver().delete(outputUri, null, null);
                    }
                    setResult(RESULT_CANCELED);
                }
                finish();
            });
        } catch (ActivityNotFoundException e) {
            CrashReport.postCatchedException(e);
            setResult(RESULT_ERROR, new Intent().putExtra(INTENT_KEY_OUT_ERROR, getString(R.string.image_crop_error_not_support)));
            finish();
        }
    }

    /**
     * 获取图片文件的格式
     */
    private static Bitmap.CompressFormat getImageFormat(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".png")) {
            return Bitmap.CompressFormat.PNG;
        } else if (fileName.endsWith(".webp")) {
            return Bitmap.CompressFormat.WEBP;
        }
        return Bitmap.CompressFormat.JPEG;
    }

    /**
     * 裁剪图片监听
     */
    public interface OnCropListener {

        /**
         * 裁剪成功回调
         *
         * @param fileUri          文件路径
         * @param fileName         文件名称
         */
        void onSucceed(Uri fileUri, String fileName);

        /**
         * 错误回调
         *
         * @param details       错误详情
         */
        void onError(String details);

        /**
         * 取消回调
         */
        default void onCancel() {}
    }
}