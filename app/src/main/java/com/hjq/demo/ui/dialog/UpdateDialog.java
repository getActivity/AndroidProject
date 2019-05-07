package com.hjq.demo.ui.dialog;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.demo.R;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.NumberProgressBar;

import java.io.File;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/20
 *    desc   : 升级对话框
 */
public final class UpdateDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements OnDownloadListener,
            View.OnClickListener, OnPermission {

        private TextView mNameView;
        private TextView mSizeView;
        private TextView mContentView;
        private NumberProgressBar mProgressView;

        private TextView mUpdateView;

        private ViewGroup mCancelLayout;
        private View mCloseView;

        // 下载地址
        private String mDownloadUrl;

        // 当前下载状态
        private int mDownloadStatus = -1;

        // 下载处理对象
        private DownloadHandler mDownloadHandler;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_update);
            setAnimStyle(BaseDialog.AnimStyle.TOAST);
            setGravity(Gravity.CENTER);

            mNameView = findViewById(R.id.tv_dialog_update_name);
            mSizeView = findViewById(R.id.tv_dialog_update_size);
            mContentView = findViewById(R.id.tv_dialog_update_content);
            mProgressView = findViewById(R.id.pb_dialog_update_progress);

            mUpdateView = findViewById(R.id.tv_dialog_update_update);
            mCancelLayout = findViewById(R.id.ll_dialog_update_cancel);
            mCloseView = findViewById(R.id.iv_dialog_update_close);

            mUpdateView.setOnClickListener(this);
            mCloseView.setOnClickListener(this);
        }

        /**
         * 设置版本名
         */
        public Builder setVersionName(CharSequence name) {
            mNameView.setText(name);
            return this;
        }

        /**
         * 设置文件大小
         */
        public Builder setFileSize(long size) {
            return setFileSize(Formatter.formatFileSize(getContext(), size));
        }

        public Builder setFileSize(CharSequence text) {
            mSizeView.setText(text);
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
            mCancelLayout.setVisibility(force ? View.GONE : View.VISIBLE);
            return setCancelable(!force);
        }

        /**
         * 设置下载 url
         */
        public Builder setDownloadUrl(String url) {
            mDownloadUrl = url;
            return this;
        }

        /**
         * {@link OnDownloadListener}
         */

        @Override
        public void downloadProgressChange(int progress) {
            mProgressView.setProgress(progress);
        }

        @Override
        public void downloadStateChange(int state) {
            // 记录本次的下载状态
            mDownloadStatus = state;

            // 判断下载状态
            switch (state) {
                case DownloadManager.STATUS_RUNNING: // 下载中
                    mUpdateView.setText(R.string.dialog_update_status_running);
                    // 显示进度条
                    mProgressView.setVisibility(View.VISIBLE);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL: // 下载成功
                    mUpdateView.setText(R.string.dialog_update_status_successful);
                    // 隐藏进度条
                    mProgressView.setVisibility(View.GONE);
                    // 安装 Apk
                    mDownloadHandler.openDownloadFile();
                    break;
                case DownloadManager.STATUS_FAILED: // 下载失败
                    mUpdateView.setText(R.string.dialog_update_status_failed);
                    // 删除下载的文件
                    mDownloadHandler.deleteDownloadFile();
                    break;
                case DownloadManager.STATUS_PAUSED: // 下载暂停
                    mUpdateView.setText(R.string.dialog_update_status_paused);
                    break;
                case DownloadManager.STATUS_PENDING: // 等待下载
                    mUpdateView.setText(R.string.dialog_update_status_pending);
                    break;
                default:
                    break;
            }
        }

        /**
         * {@link View.OnClickListener,}
         */

        @Override
        public void onClick(View v) {
            if (v == mCloseView) { // 点击了下次再说
                dismiss();
            }else if (v == mUpdateView) { // 点击了更新按钮

                // 判断下载状态
                switch (mDownloadStatus) {
                    case -1: // 没有任何状态
                    case DownloadManager.STATUS_FAILED: // 下载失败
                        // 重新下载
                        requestPermission();
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL: // 下载成功
                        // 安装 Apk
                        mDownloadHandler.openDownloadFile();
                        break;
                    default:
                        break;
                }
            }
        }

        /**
         * 请求权限
         */
        private void requestPermission() {
            XXPermissions.with(getActivity())
                    .constantRequest() // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                    .permission(Permission.REQUEST_INSTALL_PACKAGES) //安装包权限
                    .permission(Permission.Group.STORAGE) // 存储权限
                    .request(this);
        }

        /**
         * {@link OnPermission}
         */
        @Override
        public void hasPermission(List<String> granted, boolean isAll) {
            if (isAll) {

                mDownloadHandler = new DownloadHandler(getActivity());
                mDownloadHandler.setDownloadListener(this);
                if (!mDownloadHandler.createDownload(mDownloadUrl,  getString(R.string.app_name) +
                        " " + mNameView.getText().toString() + ".apk", null)) {
                    mUpdateView.setText(R.string.dialog_update_download_fail);
                } else {
                    // 设置对话框不能被取消
                    setCancelable(false);
                    // 隐藏取消按钮
                    mCancelLayout.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void noPermission(List<String> denied, boolean quick) {
            ToastUtils.show(R.string.dialog_update_permission_hint);
        }
    }

    private static final class DownloadHandler extends Handler {

        private Context mContext;

        private DownloadManager mDownloadManager; // 下载管理器对象
        private DownloadObserver mDownloadObserver; // 下载内容观察者

        private long mDownloadId; // 下载 id

        private OnDownloadListener mListener; // 下载监听

        private File mDownloadFile; // 下载的文件

        private DownloadHandler(Context context) {
            super(Looper.getMainLooper());
            mContext = context;
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        private void setDownloadListener(OnDownloadListener l) {
            this.mListener = l;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListener == null) return;

            // 判断下载状态
            switch (msg.what) {
                case DownloadManager.STATUS_RUNNING: // 下载中
                    // 计算下载百分比
                    int progress = msg.arg2 * 100 / msg.arg1;
                    mListener.downloadProgressChange(progress);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL: // 下载成功
                case DownloadManager.STATUS_FAILED: // 下载失败
                    // 移除内容观察者
                    if (mDownloadObserver != null) {
                        mContext.getContentResolver().unregisterContentObserver(mDownloadObserver);
                    }
                    break;
                default:
                    break;
            }

            mListener.downloadStateChange(msg.what);
        }

        /**
         * 创建下载任务
         *
         * @param downloadUrl           下载地址
         * @param fileName              文件命名
         * @param notificationTitle     通知栏标题
         * @return                      下载 id
         */
        private boolean createDownload(String downloadUrl, String fileName, String notificationTitle) {
            if (fileName == null) {
                throw new IllegalArgumentException("The filename cannot be empty");
            }

            // 记录下载的文件
            mDownloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            // 如果这个文件已经下载过，就先删除这个文件
            if (mDownloadFile.exists()) {
                mDownloadFile.delete();
            }

            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                request.allowScanningByMediaScanner();
                //设置WIFI下进行更新
                //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                if (notificationTitle != null) {
                    request.setTitle(notificationTitle);
                }

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                mDownloadId = mDownloadManager.enqueue(request);

                mDownloadObserver = new DownloadObserver(this, mDownloadManager, new DownloadManager.Query().setFilterById(mDownloadId));
                // 添加内容观察者
                mContext.getContentResolver().registerContentObserver(Uri.parse("content://downloads/"), true, mDownloadObserver);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 打开下载的文件
         */
        private void openDownloadFile() {
            // 这里需要特别说明的是，这个 API 其实不是打开文件的，我也不知道干什么用的
            // 测试前必须要加权限，否则会崩溃：<uses-permission android:name="android.permission.ACCESS_ALL_DOWNLOADS" />
            // mDownloadManager.openDownloadedFile(mDownloadId);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", mDownloadFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(mDownloadFile);
            }

            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

        /**
         * 删除下载的文件
         */
        void deleteDownloadFile() {
            mDownloadManager.remove(mDownloadId);
        }
    }

    private static class DownloadObserver extends ContentObserver {

        private Handler mHandler;
        private DownloadManager mDownloadManager;
        private DownloadManager.Query mQuery;

        DownloadObserver(Handler handler, DownloadManager manager, DownloadManager.Query query) {
            super(handler);
            mHandler = handler;
            mDownloadManager = manager;
            mQuery = query;
        }

        /**
         * 每当 /data/data/com.android.providers.download/database/database.db 变化后就会触发onChange方法
         *
         * @param selfChange        是否是当前应用自己操作了数据库
         */
        @Override
        public void onChange(boolean selfChange) {
            // 查询数据库
            Cursor cursor = mDownloadManager.query(mQuery);
            // 游标定位到第一个，因为 Cursor 总数只有一个
            cursor.moveToFirst();

            // 总需下载的字节数
            int totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            // 已经下载的字节数
            int downloadedBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            // 下载状态
            int downloadStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

            // 关闭游标
            cursor.close();

            // 发送更新消息
            Message msg = mHandler.obtainMessage();
            msg.arg1 = totalBytes;
            msg.arg2 = downloadedBytes;
            msg.what = downloadStatus;
            mHandler.sendMessage(msg);
        }
    }

    private interface OnDownloadListener {
        /**
         * 下载进度改变
         */
        void downloadProgressChange(int progress);

        /**
         * 下载状态改变
         */
        void downloadStateChange(int state);
    }
}