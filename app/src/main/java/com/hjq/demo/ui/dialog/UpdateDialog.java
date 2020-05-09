package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.hjq.base.BaseDialog;
import com.hjq.base.action.AnimAction;
import com.hjq.demo.R;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.other.AppConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.DownloadInfo;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.Permission;

import java.io.File;

import okhttp3.Call;

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
        private String mFileMD5;
        /** 当前是否下载中 */
        private boolean mDownloading;
        /** 当前是否下载完毕 */
        private boolean mDownloadComplete;

        public Builder(Context context) {
            super(context);

            setContentView(R.layout.dialog_update);
            setAnimStyle(AnimAction.BOTTOM);
            setCancelable(false);

            mNameView = findViewById(R.id.tv_update_name);
            mContentView = findViewById(R.id.tv_update_content);
            mProgressView = findViewById(R.id.pb_update_progress);

            mUpdateView = findViewById(R.id.tv_update_update);
            mCloseView = findViewById(R.id.tv_update_close);

            setOnClickListener(R.id.tv_update_update, R.id.tv_update_close);
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
        public Builder setFileMD5(String md5) {
            mFileMD5 = md5;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(View v) {
            if (v == mCloseView) {
                dismiss();
            } else if (v == mUpdateView) {
                // 判断下载状态
                if (mDownloadComplete) {
                    // 下载完毕，安装 Apk
                    installApk();
                } else if (!mDownloading) {
                    // 没有下载，开启下载
                    downloadApk();
                }
            }
        }

        /**
         * 下载 Apk
         */
        @Permissions({Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE})
        private void downloadApk() {
            // 创建要下载的文件对象
            mApkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name) + "_v" + mNameView.getText().toString() + ".apk");
            // 设置对话框不能被取消
            setCancelable(false);

            EasyHttp.download(getActivity())
                    .method(HttpMethod.GET)
                    .file(mApkFile)
                    .url(mDownloadUrl)
                    .md5(mFileMD5)
                    .listener(new OnDownloadListener() {

                        @Override
                        public void onStart(Call call) {
                            // 标记为下载中
                            mDownloading = true;
                            // 标记成未下载完成
                            mDownloadComplete = false;
                            // 后台更新
                            mCloseView.setText(R.string.update_background);
                            // 显示进度条
                            mProgressView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onProgress(DownloadInfo info) {
                            mUpdateView.setText(String.format(getString(R.string.update_status_running), info.getDownloadProgress()));
                            mProgressView.setProgress(info.getDownloadProgress());
                        }

                        @Override
                        public void onComplete(DownloadInfo info) {
                            mCloseView.setText(R.string.update_no);
                            mUpdateView.setText(R.string.update_status_successful);
                            // 标记成下载完成
                            mDownloadComplete = true;
                            // 安装 Apk
                            installApk();
                        }

                        @SuppressWarnings("ResultOfMethodCallIgnored")
                        @Override
                        public void onError(DownloadInfo info, Exception e) {
                            mUpdateView.setText(R.string.update_status_failed);
                            // 删除下载的文件
                            info.getFile().delete();
                        }

                        @Override
                        public void onEnd(Call call) {
                            // 隐藏进度条
                            mProgressView.setVisibility(View.GONE);
                            // 标记当前不是下载
                            mDownloading = false;
                        }
                    }).start();
        }

        /**
         * 安装 Apk
         */
        @Permissions({Permission.REQUEST_INSTALL_PACKAGES})
        private void installApk() {
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
            getContext().startActivity(intent);
        }
    }
}