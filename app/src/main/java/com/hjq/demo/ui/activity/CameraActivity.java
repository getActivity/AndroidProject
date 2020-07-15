package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.aop.DebugLog;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.other.IntentKey;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/18
 *    desc   : 拍照选择（拍摄图片、视频）
 */
public final class CameraActivity extends MyActivity {

    private static final int CAMERA_REQUEST_CODE = 1024;

    public static void start(BaseActivity activity, OnCameraListener listener) {
        start(activity, false, listener);
    }

    @DebugLog
    @Permissions({Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA})
    public static void start(BaseActivity activity, boolean video, OnCameraListener listener) {
        File file = createCameraFile(video);
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(IntentKey.FILE, file);
        intent.putExtra(IntentKey.VIDEO, video);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSelected(file);
            } else {
                listener.onCancel();
            }
        });
    }

    private File mFile;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent;
        // 启动系统相机
        if (getBoolean(IntentKey.VIDEO)) {
            // 录制视频
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        } else {
            // 拍摄照片
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        if (XXPermissions.hasPermission(this, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA)
                && intent.resolveActivity(getPackageManager()) != null) {
            mFile = getSerializable(IntentKey.FILE);
            if (mFile != null && mFile.exists()) {

                Uri imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 通过 FileProvider 创建一个 Content 类型的 Uri 文件
                    imageUri = FileProvider.getUriForFile(this, AppConfig.getPackageName() + ".provider", mFile);
                } else {
                    imageUri = Uri.fromFile(mFile);
                }
                // 对目标应用临时授权该 Uri 所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 将拍取的照片保存到指定 Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                toast(R.string.camera_image_error);
                finish();
            }
        } else {
            toast(R.string.camera_launch_fail);
            finish();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    // 重新扫描多媒体（否则可能扫描不到）
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{mFile.getPath()}, null,null);
                    break;
                case RESULT_CANCELED:
                    // 删除这个文件
                    mFile.delete();
                    break;
                default:
                    break;
            }
            setResult(resultCode);
            finish();
        }
    }

    /**
     * 创建一个拍照图片文件对象
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File createCameraFile(boolean video) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdirs()) {
                folder = Environment.getExternalStorageDirectory();
            }
        }

        try {
            File file = new File(folder, (video ? "IMG_" : "VID") + new SimpleDateFormat("_yyyyMMdd_HHmmss.", Locale.getDefault()).format(new Date()) + (video ? "mp4" : "jpg"));
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拍照选择监听
     */
    public interface OnCameraListener {

        /**
         * 选择回调
         *
         * @param file          文件
         */
        void onSelected(File file);

        /**
         * 取消回调
         */
        default void onCancel() {}
    }
}