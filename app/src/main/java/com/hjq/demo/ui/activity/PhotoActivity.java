package com.hjq.demo.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.base.BaseRecyclerViewAdapter;
import com.hjq.demo.R;
import com.hjq.demo.common.MyActivity;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.other.IntentKey;
import com.hjq.demo.other.PhotoSpaceDecoration;
import com.hjq.demo.ui.adapter.PhotoAdapter;
import com.hjq.demo.ui.dialog.AlbumDialog;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/24
 *    desc   : 图片选择
 */
public final class PhotoActivity extends MyActivity
        implements BaseRecyclerViewAdapter.OnItemClickListener,
        BaseRecyclerViewAdapter.OnItemLongClickListener,
        BaseRecyclerViewAdapter.OnChildClickListener, Runnable {

    public static void start(BaseActivity activity, OnPhotoSelectListener listener) {
        start(activity, 1, listener);
    }

    public static void start(BaseActivity activity, int maxSelect, OnPhotoSelectListener listener) {
        if (maxSelect < 1) {
            // 最少要选择一个图片
            throw new IllegalArgumentException("are you ok?");
        }
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putExtra(IntentKey.AMOUNT, maxSelect);
        activity.startActivityForResult(intent, new ActivityCallback() {

            @Override
            public void onActivityResult(int resultCode, @Nullable Intent data) {
                if (listener == null || data == null) {
                    return;
                }

                if (resultCode == RESULT_OK) {
                    listener.onSelect(data.getStringArrayListExtra(IntentKey.PICTURE));
                } else {
                    listener.onCancel();
                }
            }
        });
    }

    @BindView(R.id.rv_photo_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab_photo_floating)
    FloatingActionButton mFloatingView;

    private PhotoAdapter mAdapter;

    /** 最大选中 */
    private int mMaxSelect = 1;
    /** 选中列表 */
    private final ArrayList<String> mSelectPhoto = new ArrayList<>();

    /** 全部图片 */
    private final ArrayList<String> mAllPhoto = new ArrayList<>();
    /** 图片专辑 */
    private final HashMap<String, List<String>> mAllAlbum = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo;
    }

    @Override
    protected void initView() {
        mAdapter = new PhotoAdapter(this, mSelectPhoto);
        mAdapter.setOnChildClickListener(R.id.fl_photo_check, this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        // 禁用动画效果
        mRecyclerView.setItemAnimator(null);
        // 添加分割线
        mRecyclerView.addItemDecoration(new PhotoSpaceDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics())));
    }

    @Override
    protected void initData() {
        // 获取最大的选择数
        mMaxSelect = getIntent().getIntExtra(IntentKey.AMOUNT, mMaxSelect);

        // 申请存储权限
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .constantRequest()
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        // 显示加载进度条
                        showLoading();
                        // 加载图片列表
                        new Thread(PhotoActivity.this).start();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            toast(R.string.common_permission_fail);
                            XXPermissions.gotoPermissionSettings(PhotoActivity.this, false);
                        } else {
                            toast(R.string.common_permission_hint);
                        }
                    }
                });
    }

    @Override
    public void onRightClick(View v) {
        if (mAllPhoto.isEmpty()) {
            return;
        }

        ArrayList<AlbumDialog.AlbumBean> data = new ArrayList<>(mAllAlbum.size() + 1);
        data.add(new AlbumDialog.AlbumBean(mAllPhoto.get(0), getString(R.string.photo_all), mAllPhoto.size(), mAdapter.getData() == mAllPhoto));
        Set<String> keys = mAllAlbum.keySet();
        for (String key : keys) {
            List<String> temp = mAllAlbum.get(key);
            if (temp != null && !temp.isEmpty()) {
                data.add(new AlbumDialog.AlbumBean(temp.get(0), key, temp.size(), mAdapter.getData() == temp));
            }
        }

        new AlbumDialog.Builder(this)
                .setData(data)
                .setListener(new AlbumDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, int position, AlbumDialog.AlbumBean bean) {
                        setRightTitle(bean.getName());
                        // 滚动回第一个位置
                        mRecyclerView.scrollToPosition(0);
                        if (position == 0) {
                            mAdapter.setData(mAllPhoto);
                        } else {
                            mAdapter.setData(mAllAlbum.get(bean.getName()));
                        }
                        // 执行列表动画
                        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_right));
                        mRecyclerView.scheduleLayoutAnimation();
                    }
                })
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 遍历判断选择了的图片是否被删除了
        for (String path : mSelectPhoto) {
            File file = new File(path);
            if (!file.isFile()) {

                mSelectPhoto.remove(path);
                mAllPhoto.remove(path);
                List<String> data = mAllAlbum.get(file.getParentFile().getName());
                if (data != null) {
                    data.remove(path);
                }
                mAdapter.notifyDataSetChanged();

                if (mSelectPhoto.isEmpty()) {
                    mFloatingView.setImageResource(R.drawable.ic_photo_camera);
                } else {
                    mFloatingView.setImageResource(R.drawable.ic_photo_succeed);
                }
            }
        }
    }

    @OnClick(R.id.fab_photo_floating)
    public void onClick(View v) {
        if (v.getId() == R.id.fab_photo_floating) {
            if (mSelectPhoto.isEmpty()) {
                XXPermissions.with(this)
                        .permission(Permission.CAMERA)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                // 点击拍照
                                launchCamera();
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                if (quick) {
                                    toast(R.string.common_permission_fail);
                                    XXPermissions.gotoPermissionSettings(PhotoActivity.this, true);
                                } else {
                                    toast(R.string.common_permission_hint);
                                }
                            }
                        });
            } else {
                // 完成选择
                setResult(RESULT_OK, new Intent().putStringArrayListExtra(IntentKey.PICTURE, mSelectPhoto));
                finish();
            }
        }
    }

    /**
     * {@link BaseRecyclerViewAdapter.OnItemClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        if (mSelectPhoto.contains(mAdapter.getItem(position))) {
            ImageActivity.start(getActivity(), mSelectPhoto, mSelectPhoto.indexOf(mAdapter.getItem(position)));
        } else {
            ImageActivity.start(getActivity(), mAdapter.getItem(position));
        }
    }

    /**
     * {@link BaseRecyclerViewAdapter.OnItemLongClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position) {
        if (mSelectPhoto.size() < mMaxSelect) {
            // 长按的时候模拟选中
            return itemView.findViewById(R.id.fl_photo_check).performClick();
        } else {
            return false;
        }
    }

    /**
     * {@link BaseRecyclerViewAdapter.OnChildClickListener}
     * @param recyclerView      RecyclerView对象
     * @param childView         被点击的条目子 View Id
     * @param position          被点击的条目位置
     */
    @Override
    public void onChildClick(RecyclerView recyclerView, View childView, int position) {
        switch (childView.getId()) {
            case R.id.fl_photo_check:
                if (mSelectPhoto.contains(mAdapter.getItem(position))) {
                    mSelectPhoto.remove(mAdapter.getItem(position));

                    if (mSelectPhoto.isEmpty()) {
                        mFloatingView.hide();
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mFloatingView.setImageResource(R.drawable.ic_photo_camera);
                                mFloatingView.show();
                            }
                        }, 200);
                    }

                } else {

                    if (mSelectPhoto.size() < mMaxSelect) {
                        mSelectPhoto.add(mAdapter.getItem(position));

                        if (mSelectPhoto.size() == 1) {
                            mFloatingView.hide();
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mFloatingView.setImageResource(R.drawable.ic_photo_succeed);
                                    mFloatingView.show();
                                }
                            }, 200);
                        }
                    } else {
                        toast(String.format(getString(R.string.photo_max_hint), mMaxSelect));
                    }
                }
                mAdapter.notifyItemChanged(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        mAllAlbum.clear();
        mAllPhoto.clear();

        final Uri contentUri = MediaStore.Files.getContentUri("external");
        final String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
        final String selection =
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND "
                        + MediaStore.MediaColumns.SIZE + ">0";

        final String[] selectionAllArgs = {String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};

        ContentResolver contentResolver = getContentResolver();
        String[] projections;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projections = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                    MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.WIDTH, MediaStore
                    .MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE, MediaStore.Video.Media.DURATION};
        } else {
            projections = new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                    MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE, MediaStore.Video.Media.DURATION};
        }

        Cursor cursor = contentResolver.query(contentUri, projections, selection, selectionAllArgs, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {

            int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            int widthIndex = 0;
            int heightIndex = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                widthIndex = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
                heightIndex = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);
            }

            do {
                long size = cursor.getLong(sizeIndex);
                if (size < 1) {
                    continue;
                }

                String type = cursor.getString(mimeTypeIndex);
                String path = cursor.getString(pathIndex);
                if (TextUtils.isEmpty(path) || TextUtils.isEmpty(type)) {
                    continue;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    int width = cursor.getInt(widthIndex);
                    int height = cursor.getInt(heightIndex);
                    if (width < 1 || height < 1) {
                        continue;
                    }
                }

                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    continue;
                }

                // 专辑名称
                String albumName = file.getParentFile().getName();
                List<String> files = mAllAlbum.get(albumName);
                if (files == null) {
                    files = new ArrayList<>();
                    mAllAlbum.put(albumName, files);
                }
                files.add(path);
                mAllPhoto.add(path);

            } while (cursor.moveToNext());

            cursor.close();
        }

        postDelayed(new Runnable() {

            @Override
            public void run() {
                // 滚动回第一个位置
                mRecyclerView.scrollToPosition(0);
                // 设置新的列表数据
                mAdapter.setData(mAllPhoto);

                if (mSelectPhoto.isEmpty()) {
                    mFloatingView.setImageResource(R.drawable.ic_photo_camera);
                } else {
                    mFloatingView.setImageResource(R.drawable.ic_photo_succeed);
                }

                // 执行列表动画
                mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down));
                mRecyclerView.scheduleLayoutAnimation();

                // 显示加载完成
                showComplete();
                // 设置右标提
                setRightTitle(R.string.photo_all);
            }
        }, 1000);
    }

    /**
     * 启动系统相机
     */
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File file = createCameraFile();
            if (file != null && file.exists()) {

                Uri imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 通过 FileProvider 创建一个 Content 类型的 Uri 文件
                    imageUri = FileProvider.getUriForFile(this, AppConfig.getPackageName() + ".provider", file);
                } else {
                    imageUri = Uri.fromFile(file);
                }
                // 对目标应用临时授权该 Uri 所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 将拍取的照片保存到指定 Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, new ActivityCallback() {

                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    public void onActivityResult(int resultCode, @Nullable Intent data) {
                        switch (resultCode) {
                            case RESULT_OK:
                                if (file.exists() && file.isFile()) {
                                    // 重新扫描多媒体（否则可能扫描不到）
                                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getPath()}, null,null);

                                    // 当前选中图片的数量必须小于最大选中数
                                    if (mSelectPhoto.size() < mMaxSelect) {
                                        mSelectPhoto.add(file.getPath());
                                    }

                                    // 这里需要延迟刷新，否则可能会找不到拍照的图片
                                    postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 重新加载图片列表
                                            new Thread(PhotoActivity.this).start();
                                        }
                                    }, 1000);
                                }
                                break;
                            case RESULT_CANCELED:
                                file.delete();
                                break;
                            default:
                                break;
                        }
                    }
                });
            } else {
                toast(R.string.photo_picture_error);
            }
        } else {
            toast(R.string.photo_launch_fail);
        }
    }

    /**
     * 创建一个拍照图片文件对象
     */
    private File createCameraFile() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdirs()) {
                folder = Environment.getExternalStorageDirectory();
            }
        }

        try {
            return File.createTempFile("IMG", ".jpg", folder);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 图片选择监听
     */
    public interface OnPhotoSelectListener {
        /**
         * 选择结果回调
         * @param data          图片列表
         */
        void onSelect(List<String> data);

        /**
         * 取消回调
         */
        void onCancel();
    }
}