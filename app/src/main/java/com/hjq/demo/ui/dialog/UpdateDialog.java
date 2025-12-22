package com.hjq.demo.ui.dialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import com.hjq.base.BaseDialog;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.demo.aop.CheckNet;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.permission.PermissionDescription;
import com.hjq.demo.permission.PermissionInterceptor;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/20
 *    desc   : 升级对话框
 */
public final class UpdateDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder> {

        @NonNull
        private final TextView mNameView;
        @NonNull
        private final TextView mDetailsView;
        @NonNull
        private final ProgressBar mProgressView;

        @NonNull
        private final TextView mUpdateView;
        @NonNull
        private final TextView mCloseView;

        /** Apk 文件 */
        private File mApkFile;
        /** 下载地址 */
        private String mDownloadUrl;
        /** 文件 MD5 */
        private String mFileMd5;
        /** 是否强制更新 */
        private boolean mForceUpdate;

        /** 当前是否下载中 */
        private boolean mDownloading;
        /** 当前是否下载完毕 */
        private boolean mDownloadComplete;

        public Builder(@NonNull Context context) {
            super(context);

            setContentView(R.layout.update_dialog);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);
            setCancelable(false);

            mNameView = findViewById(R.id.tv_update_name);
            mDetailsView = findViewById(R.id.tv_update_details);
            mProgressView = findViewById(R.id.pb_update_progress);
            mUpdateView = findViewById(R.id.tv_update_update);
            mCloseView = findViewById(R.id.tv_update_close);
            setOnClickListener(mUpdateView, mCloseView);

            // 让 TextView 支持滚动
            mDetailsView.setMovementMethod(new ScrollingMovementMethod());
        }

        /**
         * 设置版本名
         */
        public Builder setVersionName(CharSequence name) {
            mNameView.setText(name);
            return this;
        }

        /**
         * 设置更新日志
         */
        public Builder setUpdateLog(CharSequence text) {
            mDetailsView.setText(text);
            mDetailsView.setVisibility(text == null ? View.GONE : View.VISIBLE);
            return this;
        }

        /**
         * 设置强制更新
         */
        public Builder setForceUpdate(boolean force) {
            mForceUpdate = force;
            mCloseView.setVisibility(force ? View.GONE : View.VISIBLE);
            setCancelable(!force);
            return this;
        }

        /**
         * 设置下载 url
         */
        public Builder setDownloadUrl(String url) {
            mDownloadUrl = url;
            return this;
        }

        /**
         * 设置文件 md5
         */
        public Builder setFileMd5(String md5) {
            mFileMd5 = md5;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(@NonNull View view) {
            if (view == mCloseView) {
                dismiss();
                return;
            }

            if (view == mUpdateView) {
                // 判断下载状态
                if (mDownloadComplete) {
                    if (mApkFile.isFile()) {
                        // 下载完毕，安装 Apk
                        startInstall();
                    } else {
                        // 下载失败，重新下载
                        startDownload();
                    }
                } else if (!mDownloading) {
                    // 没有下载，开启下载
                    startDownload();
                }
            }
        }

        @CheckNet
        private void startDownload() {
            XXPermissions.with(getActivity())
                    .permission(PermissionLists.getRequestInstallPackagesPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request((grantedList, deniedList) -> {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            return;
                        }
                        downloadApk();
                    });
        }

        private void downloadApk() {
            // 设置对话框不能被取消
            setCancelable(false);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            int notificationId = getContext().getApplicationInfo().uid;
            String channelId = "";
            // 适配 Android 8.0 通知渠道新特性
            if (notificationManager != null && AndroidVersion.isAndroid8()) {
                NotificationChannel channel = new NotificationChannel(getString(R.string.update_notification_channel_id), getString(R.string.update_notification_channel_name), NotificationManager.IMPORTANCE_LOW);
                channel.enableLights(false);
                channel.enableVibration(false);
                channel.setVibrationPattern(new long[]{0});
                channel.setSound(null, null);
                notificationManager.createNotificationChannel(channel);
                channelId = channel.getId();
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), channelId)
                    // 设置通知时间
                    .setWhen(System.currentTimeMillis())
                    // 设置通知标题
                    .setContentTitle(getString(R.string.app_name))
                    // 设置通知小图标
                    .setSmallIcon(R.mipmap.launcher_ic)
                    // 设置通知大图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.launcher_ic))
                    // 设置通知静音
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    // 设置震动频率
                    .setVibrate(new long[]{0})
                    // 设置声音文件
                    .setSound(null)
                    // 设置通知的优先级
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // 创建要下载的文件对象
            mApkFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    getString(R.string.app_name) + "_v" + mNameView.getText().toString() + ".apk");
            EasyHttp.download(getDialog())
                    .method(HttpMethod.GET)
                    .file(mApkFile)
                    .url(mDownloadUrl)
                    .md5(mFileMd5)
                    // 设置断点续传（默认不开启）
                    .resumableTransfer(true)
                    .listener(new OnDownloadListener() {

                        @Override
                        public void onDownloadStart(@NonNull File file) {
                            // 标记为下载中
                            mDownloading = true;
                            // 标记成未下载完成
                            mDownloadComplete = false;
                            // 后台更新
                            mCloseView.setVisibility(View.GONE);
                            // 显示进度条
                            mProgressView.setVisibility(View.VISIBLE);
                            mUpdateView.setText(R.string.update_status_start);
                        }

                        @Override
                        public void onDownloadProgressChange(@NonNull File file, int progress) {
                            mUpdateView.setText(String.format(getString(R.string.update_status_running), progress));
                            mProgressView.setProgress(progress);
                            if (notificationManager == null) {
                                return;
                            }
                            // 更新下载通知
                            notificationManager.notify(notificationId, notificationBuilder
                                    // 设置通知的文本
                                    .setContentText(String.format(getString(R.string.update_status_running), progress))
                                    // 设置下载的进度
                                    .setProgress(100, progress, false)
                                    // 设置点击通知后是否自动消失
                                    .setAutoCancel(false)
                                    // 是否正在交互中
                                    .setOngoing(true)
                                    // 重新创建新的通知对象
                                    .build());
                        }

                        @Override
                        public void onDownloadSuccess(@NonNull File file) {
                            mUpdateView.setText(R.string.update_status_successful);
                            // 标记成下载完成
                            mDownloadComplete = true;
                            // 安装 Apk
                            startInstall();
                            if (notificationManager == null) {
                                return;
                            }
                            int pendingIntentFlag;
                            if (AndroidVersion.isAndroid12()) {
                                // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
                                // Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable, e.g.
                                // if it needs to be used with inline replies or bubbles.
                                pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE;
                            } else {
                                pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
                            }
                            // 显示下载成功通知
                            notificationManager.notify(notificationId, notificationBuilder
                                // 设置通知的文本
                                .setContentText(String.format(getString(R.string.update_status_successful), 100))
                                // 设置下载的进度
                                .setProgress(100, 100, false)
                                // 设置通知点击之后的意图
                                .setContentIntent(PendingIntent.getActivity(getContext(), 1, getInstallIntent(), pendingIntentFlag))
                                // 设置点击通知后是否自动消失
                                .setAutoCancel(true)
                                // 是否正在交互中
                                .setOngoing(false)
                                .build());
                        }

                        @SuppressWarnings("ResultOfMethodCallIgnored")
                        @Override
                        public void onDownloadFail(@NonNull File file, @NonNull Throwable throwable) {
                            // 清除通知
                            notificationManager.cancel(notificationId);
                            mUpdateView.setText(R.string.update_status_failed);
                            // 删除下载的文件
                            file.delete();
                        }

                        @Override
                        public void onDownloadEnd(@NonNull File file) {
                            // 更新进度条
                            mProgressView.setProgress(0);
                            mProgressView.setVisibility(View.GONE);
                            // 标记当前不是下载中
                            mDownloading = false;
                            // 如果当前不是强制更新，对话框就恢复成可取消状态
                            if (mForceUpdate) {
                                return;
                            }
                            setCancelable(true);
                        }
                    }).start();
        }

        private void startInstall() {
            XXPermissions.with(getContext())
                    .permission(PermissionLists.getRequestInstallPackagesPermission())
                    .interceptor(new PermissionInterceptor())
                    .description(new PermissionDescription())
                    .request((grantedList, deniedList) -> {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            return;
                        }
                        getContext().startActivity(getInstallIntent());
                    });
        }

        /**
         * 获取安装意图
         */
        private Intent getInstallIntent() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (AndroidVersion.isAndroid7()) {
                uri = FileProvider.getUriForFile(getContext(), AppConfig.getPackageName() + ".provider", mApkFile);
            } else {
                uri = Uri.fromFile(mApkFile);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 对目标应用临时授权该 Uri 读写权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return intent;
        }
    }
}