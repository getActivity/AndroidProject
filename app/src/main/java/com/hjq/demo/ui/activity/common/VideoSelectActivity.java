package com.hjq.demo.ui.activity.common;

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
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.PickVisualMediaRequest.Builder;
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseAdapter;
import com.hjq.core.manager.ThreadPoolManager;
import com.hjq.core.recycler.GridSpaceDecoration;
import com.hjq.custom.widget.view.FloatActionButton;
import com.hjq.demo.R;
import com.hjq.demo.action.StatusAction;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.permission.PermissionDescription;
import com.hjq.demo.permission.PermissionInterceptor;
import com.hjq.demo.ui.adapter.common.VideoSelectAdapter;
import com.hjq.demo.ui.dialog.common.AlbumDialog;
import com.hjq.demo.widget.StatusLayout;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.smallest.width.SmallestWidthAdaptation;
import com.tencent.bugly.library.Bugly;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

    public static void start(@NonNull BaseActivity activity, @Nullable OnVideoSelectListener listener) {
        start(activity, 1, listener);
    }

    @Log
    public static void start(@NonNull BaseActivity activity, int maxSelect, @Nullable OnVideoSelectListener listener) {
        if (maxSelect < 1) {
            // 最少要选择一个视频
            throw new IllegalArgumentException("are you ok?");
        }

        if (PickVisualMedia.isPhotoPickerAvailable(activity)) {
            PickVisualMediaRequest visualMediaRequest = new Builder()
                // 只选择视频
                .setMediaType(PickVisualMedia.VideoOnly.INSTANCE)
                .build();

            if (maxSelect > 1) {
                PickMultipleVisualMedia multipleVisualMedia = new PickMultipleVisualMedia(maxSelect);
                Intent intent = multipleVisualMedia.createIntent(activity, visualMediaRequest);
                activity.startActivityForResult(intent, (resultCode, data) -> {
                    List<Uri> uris = multipleVisualMedia.parseResult(resultCode, data);
                    if (uris.isEmpty()) {
                        return;
                    }
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < uris.size(); i++) {
                        list.add(uris.get(i).toString());
                    }
                    if (listener == null) {
                        return;
                    }
                    listener.onSelected(list);
                });
            } else {
                PickVisualMedia pickVisualMedia = new PickVisualMedia();
                Intent intent = pickVisualMedia.createIntent(activity, visualMediaRequest);
                activity.startActivityForResult(intent, (resultCode, data) -> {
                    Uri uri = pickVisualMedia.parseResult(resultCode, data);
                    if (uri == null) {
                        return;
                    }
                    List<String> list = new ArrayList<>();
                    list.add(uri.toString());
                    if (listener == null) {
                        return;
                    }
                    listener.onSelected(list);
                });
            }
            return;
        }

        XXPermissions.with(activity)
                .permission(PermissionLists.getReadExternalStoragePermission())
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                // 设置不触发错误检测机制
                .unchecked()
                .request((grantedList, deniedList) -> {
                    boolean allGranted = deniedList.isEmpty();
                    if (!allGranted) {
                        return;
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

                        ArrayList<VideoBean> videoBeans = data.getParcelableArrayListExtra(INTENT_KEY_OUT_VIDEO_LIST);
                        if (videoBeans == null || videoBeans.isEmpty()) {
                            listener.onCancel();
                            return;
                        }

                        Iterator<VideoBean> iterator = videoBeans.iterator();
                        while (iterator.hasNext()) {
                            if (!new File(iterator.next().getVideoPath()).isFile()) {
                                iterator.remove();
                            }
                        }

                        List<String> list = new ArrayList<>();
                        for (VideoBean videoBean : videoBeans) {
                            list.add(videoBean.getVideoPath());
                        }

                        if (resultCode == RESULT_OK && !list.isEmpty()) {
                            listener.onSelected(list);
                            return;
                        }
                        listener.onCancel();
                    });
                });
    }

    private StatusLayout mStatusLayout;
    private RecyclerView mRecyclerView;
    private FloatActionButton mFloatingView;

    /** 最大选中 */
    private int mMaxSelect = 1;
    /** 选中列表 */
    private final ArrayList<VideoBean> mSelectVideo = new ArrayList<>();

    /** 全部视频 */
    private final ArrayList<VideoBean> mAllVideo = new ArrayList<>();
    /** 视频专辑 */
    private final HashMap<String, List<VideoBean>> mAllAlbum = new HashMap<>();

    /** 列表适配器 */
    private final VideoSelectAdapter mAdapter = new VideoSelectAdapter(this, mSelectVideo);

    /** 专辑选择对话框 */
    private AlbumDialog.Builder mAlbumDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.video_select_activity;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.sl_video_select_status);
        mRecyclerView = findViewById(R.id.rv_video_select_list);
        mFloatingView = findViewById(R.id.fab_video_select_floating);
        setOnClickListener(mFloatingView);

        mAdapter.setOnChildClickListener(R.id.fl_video_select_check, this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        // 禁用动画效果
        mRecyclerView.setItemAnimator(null);
        // 添加分割线
        mRecyclerView.addItemDecoration(new GridSpaceDecoration((int) SmallestWidthAdaptation.dp2px(this, 5)));
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
    public StatusLayout acquireStatusLayout() {
        return mStatusLayout;
    }

    @SingleClick
    @Override
    public void onRightClick(@NonNull TitleBar titleBar) {
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
            data.add(new AlbumDialog.AlbumInfo(list.get(0).getVideoPath(), key, String.format(getString(R.string.video_select_total), list.size()),
                    mAdapter.getData() != mAllVideo && mAdapter.getData().equals(list)));
        }
        data.add(0, new AlbumDialog.AlbumInfo(mAllVideo.get(0).getVideoPath(), getString(R.string.video_select_all),
                String.format(getString(R.string.video_select_total), count), mAdapter.getData() == mAllVideo));

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
                        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                                VideoSelectActivity.this, R.anim.layout_from_right));
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
                if (data == mAdapter.getData()) {
                    mAdapter.removeItem(bean);
                }
            }

            if (mSelectVideo.isEmpty()) {
                mFloatingView.setImageResource(R.drawable.videocam_ic);
            } else {
                mFloatingView.setImageResource(R.drawable.succeed_ic);
            }
        }
        refreshLayout();
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.fab_video_select_floating) {
            if (mSelectVideo.isEmpty()) {
                // 点击拍照
                CameraActivity.start(this, true, new CameraActivity.OnCameraListener() {
                    @Override
                    public void onSelected(@NonNull File file) {
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
                    public void onError(@NonNull String details) {
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
    public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
        VideoBean bean = mAdapter.getItem(position);
        new VideoPlayActivity.Builder()
                .setVideoSource(new File(bean.getVideoPath()))
                .setActivityOrientation(bean.getVideoWidth() > bean.getVideoHeight() ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .start(this);
    }

    /**
     * {@link BaseAdapter.OnItemLongClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public boolean onItemLongClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
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
    public void onChildClick(@NonNull RecyclerView recyclerView, @NonNull View childView, int position) {
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
                int index = data.indexOf(mSelectVideo.remove(0));
                if (index != -1) {
                    mAdapter.notifyItemChanged(index);
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
        if (XXPermissions.isGrantedPermission(this, PermissionLists.getReadMediaVisualUserSelectedPermission())) {
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

        post(() -> {
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
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                    VideoSelectActivity.this, R.anim.layout_fall_down));
            mRecyclerView.scheduleLayoutAnimation();

            refreshLayout();
        });
    }

    /**
     * 刷新布局
     */
    private void refreshLayout() {
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
                if (widthMetadata != null && !widthMetadata.isEmpty()) {
                    videoWidth = Integer.parseInt(widthMetadata);
                }

                String heightMetadata = retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                if (heightMetadata != null && !heightMetadata.isEmpty()) {
                    videoHeight = Integer.parseInt(heightMetadata);
                }

                String durationMetadata = retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (durationMetadata != null && !durationMetadata.isEmpty()) {
                    videoDuration = Long.parseLong(durationMetadata);
                }
            } catch (RuntimeException e) {
                // 荣耀 LLD AL20 Android 8.0 出现：java.lang.IllegalArgumentException
                // 荣耀 HLK AL00 Android 10.0 出现：java.lang.RuntimeException：setDataSource failed: status = 0x80000000
                e.printStackTrace();
                Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
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

        @Override
        public int hashCode() {
            return Objects.hash(mVideoPath, mVideoWidth, mVideoHeight, mVideoDuration, mVideoSize);
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
        void onSelected(@NonNull List<String> data);

        /**
         * 取消回调
         */
        default void onCancel() {
            // default implementation ignored
        }
    }
}