package com.hjq.demo.ui.dialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.CheckNet;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.other.AppConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.Permission;

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

        private final TextView mNameView;
        private final TextView mContentView;
        private final ProgressBar mProgressView;

        private final TextView mUpdateView;
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

        public Builder(Context context) {
            super(context);

            setContentView(R.layout.update_dialog);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);
            setCancelable(false);

            mNameView = findViewById(R.id.tv_update_name);
            mContentView = findViewById(R.id.tv_update_content);
            mProgressView = findViewById(R.id.pb_update_progress);
            mUpdateView = findViewById(R.id.tv_update_update);
            mCloseView = findViewById(R.id.tv_update_close);
            setOnClickListener(mUpdateView, mCloseView);
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
            mContentView.setText(text);
            mContentView.setVisibility(text == null ? View.GONE : View.VISIBLE);
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
        public void onClick(View view) {
            if (view == mCloseView) {
                dismiss();
            } else if (view == mUpdateView) {
                // 判断下载状态
                if (mDownloadComplete) {
                    if (mApkFile.isFile()) {
                        // 下载完毕，安装 Apk
                        installApk();
                    } else {
                        // 下载失败，重新下载
                        downloadApk();
                    }
                } else if (!mDownloading) {
                    // 没有下载，开启下载
                    downloadApk();
                }
            }
        }

        /**
         * 下载 Apk
         */
        @CheckNet
        @Permissions({Permission.MANAGE_EXTERNAL_STORAGE})
        private void downloadApk() {
            // 设置对话框不能被取消
            setCancelable(false);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            int notificationId = getContext().getApplicationInfo().uid;
            String channelId = "";
            // 适配 Android 8.0 通知渠道新特性
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
            mApkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    getString(R.string.app_name) + "_v" + mNameView.getText().toString() + ".apk");
            EasyHttp.download(getDialog())
                    .method(HttpMethod.GET)
                    .file(mApkFile)
                    .url(mDownloadUrl)
                    .md5(mFileMd5)
                    .listener(new OnDownloadListener() {

                        @Override
                        public void onStart(File file) {
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
                        public void onProgress(File file, int progress) {
                            mUpdateView.setText(String.format(getString(R.string.update_status_running), progress));
                            mProgressView.setProgress(progress);
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
                        public void onComplete(File file) {
                            // 显示下载成功通知
                            notificationManager.notify(notificationId, notificationBuilder
                                    // 设置通知的文本
                                    .setContentText(String.format(getString(R.string.update_status_successful), 100))
                                    // 设置下载的进度
                                    .setProgress(100, 100, false)
                                    // 设置通知点击之后的意图
                                    .setContentIntent(PendingIntent.getActivity(getContext(), 1, getInstallIntent(), Intent.FILL_IN_ACTION))
                                    // 设置点击通知后是否自动消失
                                    .setAutoCancel(true)
                                    // 是否正在交互中
                                    .setOngoing(false)
                                    .build());
                            mUpdateView.setText(R.string.update_status_successful);
                            // 标记成下载完成
                            mDownloadComplete = true;
                            // 安装 Apk
                            installApk();
                        }

                        @SuppressWarnings("ResultOfMethodCallIgnored")
                        @Override
                        public void onError(File file, Exception e) {
                            // 清除通知
                            notificationManager.cancel(notificationId);
                            mUpdateView.setText(R.string.update_status_failed);
                            // 删除下载的文件
                            file.delete();
                        }

                        @Override
                        public void onEnd(File file) {
                            // 更新进度条
                            mProgressView.setProgress(0);
                            mProgressView.setVisibility(View.GONE);
                            // 标记当前不是下载中
                            mDownloading = false;
                            // 如果当前不是强制更新，对话框就恢复成可取消状态
                            if (!mForceUpdate) {
                                setCancelable(true);
                            }
                        }

                    }).start();
        }

        /**
         * 安装 Apk
         */
        @Permissions({Permission.REQUEST_INSTALL_PACKAGES})
        private void installApk() {
            getContext().startActivity(getInstallIntent());
        }

        /**
         * 获取安装意图
         */
        private Intent getInstallIntent() {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getContext(), AppConfig.getPackageName() + ".provider", mApkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(mApkFile);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }
    }
}