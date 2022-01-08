package com.hjq.demo.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseActivity;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.action.StatusAction;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.manager.ThreadPoolManager;
import com.hjq.demo.other.GridSpaceDecoration;
import com.hjq.demo.ui.adapter.VideoSelectAdapter;
import com.hjq.demo.ui.dialog.AlbumDialog;
import com.hjq.demo.widget.StatusLayout;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.FloatActionButton;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/03/01
 *    desc   : 选择视频
 */
public final class VideoSelectActivity extends AppActivity
        implements StatusAction, Runnable,
        BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener,
        BaseAdapter.OnChildClickListener {

    private static final String INTENT_KEY_IN_MAX_SELECT = "maxSelect";

    private static final String INTENT_KEY_OUT_VIDEO_LIST = "videoList";

    public static void start(BaseActivity activity, OnVideoSelectListener listener) {
        start(activity, 1, listener);
    }

    @Log
    @Permissions({Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE})
    public static void start(BaseActivity activity, int maxSelect, OnVideoSelectListener listener) {
        if (maxSelect < 1) {
            // 最少要选择一个视频
            throw new IllegalArgumentException("are you ok?");
        }
        Intent intent = new Intent(activity, VideoSelectActivity.class);
        intent.putExtra(INTENT_KEY_IN_MAX_SELECT, maxSelect);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null) {
                return;
            }

            if (data == null) {
                listener.onCancel();
                return;
            }

            ArrayList<VideoBean> list = data.getParcelableArrayListExtra(INTENT_KEY_OUT_VIDEO_LIST);
            if (list == null || list.isEmpty()) {
                listener.onCancel();
                return;
            }

            Iterator<VideoBean> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (!new File(iterator.next().getVideoPath()).isFile()) {
                    iterator.remove();
                }
            }

            if (resultCode == RESULT_OK && !list.isEmpty()) {
                listener.onSelected(list);
                return;
            }
            listener.onCancel();
        });
    }

    private StatusLayout mStatusLayout;
    private RecyclerView mRecyclerView;
    private FloatActionButton mFloatingView;

    private VideoSelectAdapter mAdapter;

    /** 最大选中 */
    private int mMaxSelect = 1;
    /** 选中列表 */
    private final ArrayList<VideoBean> mSelectVideo = new ArrayList<>();

    /** 全部视频 */
    private final ArrayList<VideoBean> mAllVideo = new ArrayList<>();
    /** 视频专辑 */
    private final HashMap<String, List<VideoBean>> mAllAlbum = new HashMap<>();

    /** 专辑选择对话框 */
    private AlbumDialog.Builder mAlbumDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.video_select_activity;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.hl_video_select_hint);
        mRecyclerView = findViewById(R.id.rv_video_select_list);
        mFloatingView = findViewById(R.id.fab_video_select_floating);
        setOnClickListener(mFloatingView);

        mAdapter = new VideoSelectAdapter(this, mSelectVideo);
        mAdapter.setOnChildClickListener(R.id.fl_video_select_check, this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        // 禁用动画效果
        mRecyclerView.setItemAnimator(null);
        // 添加分割线
        mRecyclerView.addItemDecoration(new GridSpaceDecoration((int) getResources().getDimension(R.dimen.dp_5)));
        // 设置滚动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mFloatingView.hide();
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mFloatingView.show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        // 获取最大的选择数
        mMaxSelect = getInt(INTENT_KEY_IN_MAX_SELECT, mMaxSelect);

        // 显示加载进度条
        showLoading();
        // 加载视频列表
        ThreadPoolManager.getInstance().execute(this);
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @SingleClick
    @Override
    public void onRightClick(View view) {
        if (mAllVideo.isEmpty()) {
            return;
        }

        ArrayList<AlbumDialog.AlbumInfo> data = new ArrayList<>(mAllAlbum.size() + 1);

        int count = 0;
        Set<String> keys = mAllAlbum.keySet();
        for (String key : keys) {
            List<VideoBean> list = mAllAlbum.get(key);
            if (list == null || list.isEmpty()) {
                continue;
            }
            count += list.size();
            data.add(new AlbumDialog.AlbumInfo(list.get(0).getVideoPath(), key, String.format(getString(R.string.video_select_total), list.size()), mAdapter.getData() == list));
        }
        data.add(0, new AlbumDialog.AlbumInfo(mAllVideo.get(0).getVideoPath(), getString(R.string.video_select_all), String.format(getString(R.string.video_select_total), count), mAdapter.getData() == mAllVideo));

        if (mAlbumDialog == null) {
            mAlbumDialog = new AlbumDialog.Builder(this)
                    .setListener((dialog, position, bean) -> {

                        setRightTitle(bean.getName());
                        // 滚动回第一个位置
                        mRecyclerView.scrollToPosition(0);
                        if (position == 0) {
                            mAdapter.setData(mAllVideo);
                        } else {
                            mAdapter.setData(mAllAlbum.get(bean.getName()));
                        }
                        // 执行列表动画
                        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_from_right));
                        mRecyclerView.scheduleLayoutAnimation();
                    });
        }
        mAlbumDialog.setData(data)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Iterator<VideoBean> iterator = mSelectVideo.iterator();
        // 遍历判断选择了的视频是否被删除了
        while (iterator.hasNext()) {
            VideoBean bean = iterator.next();

            File file = new File(bean.getVideoPath());
            if (file.isFile()) {
                continue;
            }

            iterator.remove();
            mAllVideo.remove(bean);

            File parentFile = file.getParentFile();
            if (parentFile == null) {
                continue;
            }

            List<VideoBean> data = mAllAlbum.get(parentFile.getName());
            if (data != null) {
                data.remove(bean);
            }
            mAdapter.notifyDataSetChanged();

            if (mSelectVideo.isEmpty()) {
                mFloatingView.setImageResource(R.drawable.videocam_ic);
            } else {
                mFloatingView.setImageResource(R.drawable.succeed_ic);
            }
        }
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_video_select_floating) {
            if (mSelectVideo.isEmpty()) {
                // 点击拍照
                CameraActivity.start(this, true, new CameraActivity.OnCameraListener() {
                    @Override
                    public void onSelected(File file) {
                        // 当前选中视频的数量必须小于最大选中数
                        if (mSelectVideo.size() < mMaxSelect) {
                            mSelectVideo.add(VideoBean.newInstance(file.getPath()));
                        }

                        // 这里需要延迟刷新，否则可能会找不到拍照的视频
                        postDelayed(() -> {
                            // 重新加载视频列表
                            ThreadPoolManager.getInstance().execute(VideoSelectActivity.this);
                        }, 1000);
                    }

                    @Override
                    public void onError(String details) {
                        toast(details);
                    }
                });
                return;
            }

            // 完成选择
            setResult(RESULT_OK, new Intent().putParcelableArrayListExtra(INTENT_KEY_OUT_VIDEO_LIST, mSelectVideo));
            finish();
        }
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        VideoBean bean = mAdapter.getItem(position);
        new VideoPlayActivity.Builder()
                .setVideoSource(new File(bean.getVideoPath()))
                .setActivityOrientation(bean.getVideoWidth() > bean.getVideoHeight() ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .start(getActivity());
    }

    /**
     * {@link BaseAdapter.OnItemLongClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position) {
        if (mSelectVideo.size() < mMaxSelect) {
            // 长按的时候模拟选中
            return itemView.findViewById(R.id.fl_video_select_check).performClick();
        }
        return false;
    }

    /**
     * {@link BaseAdapter.OnChildClickListener}
     * @param recyclerView      RecyclerView对象
     * @param childView         被点击的条目子 View Id
     * @param position          被点击的条目位置
     */
    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        if (childView.getId() == R.id.fl_video_select_check) {

            VideoBean bean = mAdapter.getItem(position);
            File file = new File(bean.getVideoPath());
            if (!file.isFile()) {
                mAdapter.removeItem(position);
                toast(R.string.video_select_error);
                return;
            }

            if (mSelectVideo.contains(bean)) {
                mSelectVideo.remove(bean);

                if (mSelectVideo.isEmpty()) {
                    mFloatingView.setImageResource(R.drawable.videocam_ic);
                }

                mAdapter.notifyItemChanged(position);
                return;
            }

            if (mMaxSelect == 1 && mSelectVideo.size() == 1) {

                List<VideoBean> data = mAdapter.getData();
                if (data != null) {
                    int index = data.indexOf(mSelectVideo.remove(0));
                    if (index != -1) {
                        mAdapter.notifyItemChanged(index);
                    }
                }
                mSelectVideo.add(bean);

            } else if (mSelectVideo.size() < mMaxSelect) {

                mSelectVideo.add(bean);

                if (mSelectVideo.size() == 1) {
                    mFloatingView.setImageResource(R.drawable.succeed_ic);
                }
            } else {
                toast(String.format(getString(R.string.video_select_max_hint), mMaxSelect));
            }
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void run() {
        mAllAlbum.clear();
        mAllVideo.clear();

        final Uri contentUri = MediaStore.Files.getContentUri("external");
        final String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
        final String selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + MediaStore.MediaColumns.SIZE + ">0";

        ContentResolver contentResolver = getContentResolver();
        String[] projections = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE, MediaStore.Video.Media.DURATION};

        Cursor cursor = null;
        if (XXPermissions.isGranted(this, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)) {
            cursor = contentResolver.query(contentUri, projections, selection, new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)}, sortOrder);
        }
        if (cursor != null && cursor.moveToFirst()) {

            int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            int durationIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DURATION);
            int widthIndex = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
            int heightIndex = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);

            do {
                long duration = cursor.getLong(durationIndex);
                // 视频时长不得小于 1 秒
                if (duration < 1000) {
                    continue;
                }

                long size = cursor.getLong(sizeIndex);
                // 视频大小不得小于 10 KB
                if (size < 1024 * 10) {
                    continue;
                }

                String type = cursor.getString(mimeTypeIndex);
                String path = cursor.getString(pathIndex);
                if (TextUtils.isEmpty(path) || TextUtils.isEmpty(type)) {
                    continue;
                }

                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    continue;
                }

                File parentFile = file.getParentFile();
                if (parentFile == null) {
                    continue;
                }

                // 获取目录名作为专辑名称
                String albumName = parentFile.getName();
                List<VideoBean> data = mAllAlbum.get(albumName);
                if (data == null) {
                    data = new ArrayList<>();
                    mAllAlbum.put(albumName, data);
                }

                int width = cursor.getInt(widthIndex);
                int height = cursor.getInt(heightIndex);

                VideoBean bean = new VideoBean(path, width, height, duration, size);
                data.add(bean);
                mAllVideo.add(bean);

            } while (cursor.moveToNext());

            cursor.close();
        }

        postDelayed(() -> {
            // 滚动回第一个位置
            mRecyclerView.scrollToPosition(0);
            // 设置新的列表数据
            mAdapter.setData(mAllVideo);

            if (mSelectVideo.isEmpty()) {
                mFloatingView.setImageResource(R.drawable.videocam_ic);
            } else {
                mFloatingView.setImageResource(R.drawable.succeed_ic);
            }

            // 执行列表动画
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_fall_down));
            mRecyclerView.scheduleLayoutAnimation();

            if (mAllVideo.isEmpty()) {
                // 显示空布局
                showEmpty();
                // 设置右标题
                setRightTitle(null);
            } else {
                // 显示加载完成
                showComplete();
                // 设置右标题
                setRightTitle(R.string.video_select_all);
            }
        }, 500);
    }

    /**
     * 视频 Bean 类
     */
    public static class VideoBean implements Parcelable {

        private final String mVideoPath;
        private final int mVideoWidth;
        private final int mVideoHeight;
        private final long mVideoDuration;
        private final long mVideoSize;

        public static VideoBean newInstance(String videoPath) {
            int videoWidth = 0;
            int videoHeight = 0;
            long videoDuration = 0;
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);

                String widthMetadata = retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                if (widthMetadata != null && !"".equals(widthMetadata)) {
                    videoWidth = Integer.parseInt(widthMetadata);
                }

                String heightMetadata = retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                if (heightMetadata != null && !"".equals(heightMetadata)) {
                    videoHeight = Integer.parseInt(heightMetadata);
                }

                String durationMetadata = retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (durationMetadata != null && !"".equals(durationMetadata)) {
                    videoDuration = Long.parseLong(durationMetadata);
                }
            } catch (RuntimeException e) {
                // 荣耀 LLD AL20 Android 8.0 出现：java.lang.IllegalArgumentException
                // 荣耀 HLK AL00 Android 10.0 出现：java.lang.RuntimeException：setDataSource failed: status = 0x80000000
                CrashReport.postCatchedException(e);
            }

            long videoSize = new File(videoPath).length();
            return new VideoBean(videoPath, videoWidth, videoHeight, videoDuration, videoSize);
        }

        public VideoBean(String path, int width, int height, long duration, long size) {
            mVideoPath = path;
            mVideoWidth = width;
            mVideoHeight = height;
            mVideoDuration = duration;
            mVideoSize = size;
        }

        public int getVideoWidth() {
            return mVideoWidth;
        }

        public int getVideoHeight() {
            return mVideoHeight;
        }

        public String getVideoPath() {
            return mVideoPath;
        }

        public long getVideoDuration() {
            return mVideoDuration;
        }

        public long getVideoSize() {
            return mVideoSize;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof VideoBean) {
                return mVideoPath.equals(((VideoBean) obj).mVideoPath);
            }
            return false;
        }

        @NonNull
        @Override
        public String toString() {
            return mVideoPath;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mVideoPath);
            dest.writeInt(mVideoWidth);
            dest.writeInt(mVideoHeight);
            dest.writeLong(mVideoDuration);
            dest.writeLong(mVideoSize);
        }

        protected VideoBean(Parcel in) {
            mVideoPath = in.readString();
            mVideoWidth = in.readInt();
            mVideoHeight = in.readInt();
            mVideoDuration = in.readLong();
            mVideoSize = in.readLong();
        }

        public static final Parcelable.Creator<VideoBean> CREATOR = new Parcelable.Creator<VideoBean>() {
            @Override
            public VideoBean createFromParcel(Parcel source) {
                return new VideoBean(source);
            }

            @Override
            public VideoBean[] newArray(int size) {
                return new VideoBean[size];
            }
        };
    }

    /**
     * 视频选择监听
     */
    public interface OnVideoSelectListener {

        /**
         * 选择回调
         *
         * @param data          视频列表
         */
        void onSelected(List<VideoBean> data);

        /**
         * 取消回调
         */
        default void onCancel() {}
    }
}