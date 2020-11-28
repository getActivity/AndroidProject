package com.hjq.demo.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.action.StatusAction;
import com.hjq.demo.aop.DebugLog;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.other.GridSpaceDecoration;
import com.hjq.demo.other.IntentKey;
import com.hjq.demo.ui.adapter.VideoSelectAdapter;
import com.hjq.demo.ui.dialog.AlbumDialog;
import com.hjq.demo.widget.HintLayout;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/03/01
 *    desc   : 选择视频
 */
public final class VideoSelectActivity extends MyActivity
        implements StatusAction, Runnable,
        BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener,
        BaseAdapter.OnChildClickListener {

    public static void start(BaseActivity activity, OnVideoSelectListener listener) {
        start(activity, 1, listener);
    }

    @DebugLog
    @Permissions({Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE})
    public static void start(BaseActivity activity, int maxSelect, OnVideoSelectListener listener) {
        if (maxSelect < 1) {
            // 最少要选择一个视频
            throw new IllegalArgumentException("are you ok?");
        }
        Intent intent = new Intent(activity, VideoSelectActivity.class);
        intent.putExtra(IntentKey.AMOUNT, maxSelect);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSelected(data.getParcelableArrayListExtra(IntentKey.IMAGE));
            } else {
                listener.onCancel();
            }
        });
    }

    private HintLayout mHintLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingView;

    private VideoSelectAdapter mAdapter;

    /** 最大选中 */
    private int mMaxSelect = 1;
    /** 选中列表 */
    private final ArrayList<VideoBean> mSelectVideo = new ArrayList<>();

    /** 全部视频 */
    private final ArrayList<VideoBean> mAllVideo = new ArrayList<>();
    /** 视频专辑 */
    private final HashMap<String, List<VideoBean>> mAllAlbum = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.video_select_activity;
    }

    @Override
    protected void initView() {
        mHintLayout = findViewById(R.id.hl_video_select_hint);
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
        mRecyclerView.addItemDecoration(new GridSpaceDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics())));
    }

    @SuppressWarnings("all")
    @Override
    protected void initData() {
        // 获取最大的选择数
        mMaxSelect = getInt(IntentKey.AMOUNT, mMaxSelect);

        // 显示加载进度条
        showLoading();
        // 加载视频列表
        new Thread(VideoSelectActivity.this).start();
    }

    @Override
    public HintLayout getHintLayout() {
        return mHintLayout;
    }

    @SingleClick
    @Override
    public void onRightClick(View v) {
        if (mAllVideo.isEmpty()) {
            return;
        }

        ArrayList<AlbumDialog.AlbumInfo> data = new ArrayList<>(mAllAlbum.size() + 1);
        data.add(new AlbumDialog.AlbumInfo(mAllVideo.get(0).getVideoPath(), getString(R.string.video_select_all), String.format(getString(R.string.video_select_total), mAllAlbum.size()), mAdapter.getData() == mAllVideo));
        Set<String> keys = mAllAlbum.keySet();
        for (String key : keys) {
            List<VideoBean> temp = mAllAlbum.get(key);
            if (temp != null && !temp.isEmpty()) {
                data.add(new AlbumDialog.AlbumInfo(temp.get(0).getVideoPath(), key, String.format(getString(R.string.video_select_total), temp.size()), mAdapter.getData() == temp));
            }
        }

        new AlbumDialog.Builder(this)
                .setData(data)
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
                    mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.from_right_layout));
                    mRecyclerView.scheduleLayoutAnimation();
                })
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 遍历判断选择了的视频是否被删除了
        for (VideoBean bean : mSelectVideo) {
            File file = new File(bean.getVideoPath());
            if (!file.isFile()) {

                mSelectVideo.remove(bean);
                mAllVideo.remove(bean);

                File parentFile = file.getParentFile();
                if (parentFile != null) {
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
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_video_select_floating) {
            if (mSelectVideo.isEmpty()) {
                // 点击拍照
                CameraActivity.start(this, true, file -> {

                    // 当前选中视频的数量必须小于最大选中数
                    if (mSelectVideo.size() < mMaxSelect) {
                        mSelectVideo.add(VideoBean.getInstance(file.getPath()));
                    }

                    // 这里需要延迟刷新，否则可能会找不到拍照的视频
                    postDelayed(() -> {
                        // 重新加载视频列表
                        new Thread(VideoSelectActivity.this).start();
                    }, 1000);
                });
            } else {
                // 完成选择
                setResult(RESULT_OK, new Intent().putParcelableArrayListExtra(IntentKey.IMAGE, mSelectVideo));
                finish();
            }
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
        VideoPlayActivity.start(getActivity(), new File(mAdapter.getItem(position).getVideoPath()));
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
        } else {
            return false;
        }
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
            if (mSelectVideo.contains(mAdapter.getItem(position))) {
                mSelectVideo.remove(mAdapter.getItem(position));

                if (mSelectVideo.isEmpty()) {
                    mFloatingView.hide();
                    postDelayed(() -> {
                        mFloatingView.setImageResource(R.drawable.videocam_ic);
                        mFloatingView.show();
                    }, 200);
                }

            } else {

                if (mMaxSelect == 1 && mSelectVideo.size() == 1) {

                    List<VideoBean> data = mAdapter.getData();
                    if (data != null) {
                        int index = data.indexOf(mSelectVideo.get(0));
                        if (index != -1) {
                            mSelectVideo.remove(0);
                            mAdapter.notifyItemChanged(index);
                        }
                    }
                    mSelectVideo.add(mAdapter.getItem(position));

                } else if (mSelectVideo.size() < mMaxSelect) {

                    mSelectVideo.add(mAdapter.getItem(position));

                    if (mSelectVideo.size() == 1) {
                        mFloatingView.hide();
                        postDelayed(() -> {
                            mFloatingView.setImageResource(R.drawable.succeed_ic);
                            mFloatingView.show();
                        }, 200);
                    }
                } else {
                    toast(String.format(getString(R.string.video_select_max_hint), mMaxSelect));
                }
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
        if (XXPermissions.hasPermission(this, Permission.READ_EXTERNAL_STORAGE)) {
            cursor = contentResolver.query(contentUri, projections, selection, new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)}, sortOrder);
        }
        if (cursor != null && cursor.moveToFirst()) {

            int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            int durationIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);

            do {
                long duration = cursor.getLong(durationIndex);
                // 视频时长不得小于 2 秒
                if (duration < 1000 * 2) {
                    continue;
                }

                long size = cursor.getLong(sizeIndex);
                // 视频大小不得小于 100 KB
                if (size < 1024 * 100) {
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
                if (parentFile != null) {
                    // 获取目录名作为专辑名称
                    String albumName = parentFile.getName();
                    List<VideoBean> data = mAllAlbum.get(albumName);
                    if (data == null) {
                        data = new ArrayList<>();
                        mAllAlbum.put(albumName, data);
                    }

                    VideoBean bean = new VideoBean(path, duration, size);
                    data.add(bean);
                    mAllVideo.add(bean);
                }

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
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.fall_down_layout));
            mRecyclerView.scheduleLayoutAnimation();

            // 设置右标提
            setRightTitle(R.string.video_select_all);

            if (mAllVideo.isEmpty()) {
                // 显示空布局
                showEmpty();
            } else {
                // 显示加载完成
                showComplete();
            }
        }, 500);
    }

    /**
     * 视频 Bean 类
     */
    public static class VideoBean implements Parcelable {

        private final String videoPath;
        private final long duration;
        private final long size;

        public static VideoBean getInstance(String videoPath) {
            int duration = 0;
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                // 荣耀 LLD AL20 Android 8.0 出现：java.lang.IllegalArgumentException
                // 荣耀 HLK AL00 Android 10.0 出现：java.lang.RuntimeException：setDataSource failed: status = 0x80000000
                retriever.setDataSource(videoPath);
                duration = Integer.parseInt(retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_DURATION));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            long size = new File(videoPath).length();
            return new VideoBean(videoPath, duration, size);
        }

        public VideoBean(String videoPath, long videoDuration, long videoSize) {
            this.videoPath = videoPath;
            this.duration = videoDuration;
            this.size = videoSize;
        }

        public String getVideoPath() {
            return videoPath;
        }

        public long getVideoDuration() {
            return duration;
        }

        public long getVideoSize() {
            return size;
        }

        @NonNull
        @Override
        public String toString() {
            return videoPath;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.videoPath);
            dest.writeLong(this.duration);
            dest.writeLong(this.size);
        }

        protected VideoBean(Parcel in) {
            this.videoPath = in.readString();
            this.duration = in.readLong();
            this.size = in.readLong();
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