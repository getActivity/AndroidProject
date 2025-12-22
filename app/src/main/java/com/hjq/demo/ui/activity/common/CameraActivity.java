package com.hjq.demo.ui.activity.common;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import com.hjq.base.BaseActivity;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.permission.PermissionDescription;
import com.hjq.demo.permission.PermissionInterceptor;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/18
 *    desc   : 拍摄图片、视频
 */
public final class CameraActivity extends AppActivity {

    public static final String INTENT_KEY_IN_FILE = "file";
    public static final String INTENT_KEY_IN_VIDEO = "video";

    public static final String INTENT_KEY_OUT_ERROR = "error";

    public static void start(@NonNull BaseActivity activity, @Nullable OnCameraListener listener) {
        start(activity, false, listener);
    }

    @Log
    public static void start(@NonNull BaseActivity activity, boolean video, @Nullable OnCameraListener listener) {
        List<IPermission> permissions = new ArrayList<>();
        permissions.add(PermissionLists.getCameraPermission());
        if (!AndroidVersion.isAndroid11()) {
            permissions.add(PermissionLists.getReadExternalStoragePermission());
            permissions.add(PermissionLists.getWriteExternalStoragePermission());
        }
        XXPermissions.with(activity)
                .permissions(permissions)
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                // 设置不触发错误检测机制
                .unchecked()
                .request((grantedList, deniedList) -> {
                    boolean allGranted = deniedList.isEmpty();
                    if (!allGranted) {
                        return;
                    }

                    File file = createCameraFile(video);
                    Intent intent = new Intent(activity, CameraActivity.class);
                    intent.putExtra(INTENT_KEY_IN_FILE, file);
                    intent.putExtra(INTENT_KEY_IN_VIDEO, video);
                    activity.startActivityForResult(intent, (resultCode, data) -> {
                        if (listener == null) {
                            return;
                        }

                        switch (resultCode) {
                            case RESULT_OK:
                                if (file.isFile()) {
                                    listener.onSelected(file);
                                } else {
                                    listener.onCancel();
                                }
                                break;
                            case RESULT_ERROR:
                                String details = null;
                                if (data != null) {
                                    details = data.getStringExtra(INTENT_KEY_OUT_ERROR);
                                }
                                if (details == null) {
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
                });
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = new Intent();
        // 启动系统相机
        if (getBoolean(INTENT_KEY_IN_VIDEO)) {
            // 录制视频
            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        } else {
            // 拍摄照片
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        List<IPermission> permissions = new ArrayList<>();
        permissions.add(PermissionLists.getCameraPermission());
        if (!AndroidVersion.isAndroid11()) {
            permissions.add(PermissionLists.getReadExternalStoragePermission());
            permissions.add(PermissionLists.getWriteExternalStoragePermission());
        }
        if (intent.resolveActivity(getPackageManager()) == null ||
                !XXPermissions.isGrantedPermissions(this, permissions)) {
            setResult(RESULT_ERROR, new Intent().putExtra(INTENT_KEY_OUT_ERROR, getString(R.string.camera_launch_fail)));
            finish();
            return;
        }

        File file = getSerializable(INTENT_KEY_IN_FILE);
        if (file == null) {
            setResult(RESULT_ERROR, new Intent().putExtra(INTENT_KEY_OUT_ERROR, getString(R.string.camera_image_error)));
            finish();
            return;
        }

        Uri imageUri;
        if (AndroidVersion.isAndroid7()) {
            // 通过 FileProvider 创建一个 Content 类型的 Uri 文件
            imageUri = FileProvider.getUriForFile(this, AppConfig.getPackageName() + ".provider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        // 对目标应用临时授权该 Uri 读写权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 将拍取的照片保存到指定 Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, (resultCode, data) -> {
            if (resultCode == RESULT_OK) {
                // 通知系统多媒体扫描该文件，否则会导致拍摄出来的图片或者视频没有及时显示到相册中，而需要通过重启手机才能看到
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getPath()}, null, null);
            }
            setResult(resultCode);
            finish();
        });
    }

    /**
     * 创建一个拍照图片文件路径
     */
    @SuppressWarnings("deprecation")
    @NonNull
    private static File createCameraFile(boolean video) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdirs()) {
                folder = Environment.getExternalStorageDirectory();
            }
        }

        return new File(folder, (video ? "VID" : "IMG") + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) +
                (video ? ".mp4" : ".jpg"));
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
        void onSelected(@NonNull File file);

        /**
         * 错误回调
         *
         * @param details       错误详情
         */
        void onError(@NonNull String details);

        /**
         * 取消回调
         */
        default void onCancel() {
            // default implementation ignored
        }
    }
}