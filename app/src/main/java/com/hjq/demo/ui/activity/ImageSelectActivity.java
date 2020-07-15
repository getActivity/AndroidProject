package com.hjq.demo.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;

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
import com.hjq.demo.ui.adapter.ImageSelectAdapter;
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
 *    time   : 2019/07/24
 *    desc   : 选择图片
 */
public final class ImageSelectActivity extends MyActivity
        implements StatusAction, Runnable,
        BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener,
        BaseAdapter.OnChildClickListener {

    public static void start(BaseActivity activity, OnPhotoSelectListener listener) {
        start(activity, 1, listener);
    }

    @DebugLog
    @Permissions({Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE})
    public static void start(BaseActivity activity, int maxSelect, OnPhotoSelectListener listener) {
        if (maxSelect < 1) {
            // 最少要选择一个图片
            throw new IllegalArgumentException("are you ok?");
        }
        Intent intent = new Intent(activity, ImageSelectActivity.class);
        intent.putExtra(IntentKey.AMOUNT, maxSelect);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSelected(data.getStringArrayListExtra(IntentKey.IMAGE));
            } else {
                listener.onCancel();
            }
        });
    }

    private HintLayout mHintLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingView;

    private ImageSelectAdapter mAdapter;

    /** 最大选中 */
    private int mMaxSelect = 1;
    /** 选中列表 */
    private final ArrayList<String> mSelectImage = new ArrayList<>();

    /** 全部图片 */
    private final ArrayList<String> mAllImage = new ArrayList<>();
    /** 图片专辑 */
    private final HashMap<String, List<String>> mAllAlbum = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.image_select_activity;
    }

    @Override
    protected void initView() {
        mHintLayout = findViewById(R.id.hl_image_select_hint);
        mRecyclerView = findViewById(R.id.rv_image_select_list);
        mFloatingView = findViewById(R.id.fab_image_select_floating);
        setOnClickListener(mFloatingView);

        mAdapter = new ImageSelectAdapter(this, mSelectImage);
        mAdapter.setOnChildClickListener(R.id.fl_image_select_check, this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        // 禁用动画效果
        mRecyclerView.setItemAnimator(null);
        // 添加分割线
        mRecyclerView.addItemDecoration(new GridSpaceDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics())));
    }

    @SuppressWarnings("all")
    @Override
    protected void initData() {
        // 获取最大的选择数
        mMaxSelect = getInt(IntentKey.AMOUNT, mMaxSelect);

        // 显示加载进度条
        showLoading();
        // 加载图片列表
        new Thread(ImageSelectActivity.this).start();
    }

    @Override
    public HintLayout getHintLayout() {
        return mHintLayout;
    }

    @SingleClick
    @Override
    public void onRightClick(View v) {
        if (mAllImage.isEmpty()) {
            return;
        }

        ArrayList<AlbumDialog.AlbumInfo> data = new ArrayList<>(mAllAlbum.size() + 1);
        data.add(new AlbumDialog.AlbumInfo(mAllImage.get(0), getString(R.string.image_select_all), String.format(getString(R.string.image_select_total), mAllAlbum.size()), mAdapter.getData() == mAllImage));
        Set<String> keys = mAllAlbum.keySet();
        for (String key : keys) {
            List<String> temp = mAllAlbum.get(key);
            if (temp != null && !temp.isEmpty()) {
                data.add(new AlbumDialog.AlbumInfo(temp.get(0), key, String.format(getString(R.string.image_select_total), temp.size()), mAdapter.getData() == temp));
            }
        }

        new AlbumDialog.Builder(this)
                .setData(data)
                .setListener((dialog, position, bean) -> {

                    setRightTitle(bean.getName());
                    // 滚动回第一个位置
                    mRecyclerView.scrollToPosition(0);
                    if (position == 0) {
                        mAdapter.setData(mAllImage);
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
        // 遍历判断选择了的图片是否被删除了
        for (String path : mSelectImage) {
            File file = new File(path);
            if (!file.isFile()) {

                mSelectImage.remove(path);
                mAllImage.remove(path);

                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    List<String> data = mAllAlbum.get(parentFile.getName());
                    if (data != null) {
                        data.remove(path);
                    }
                    mAdapter.notifyDataSetChanged();

                    if (mSelectImage.isEmpty()) {
                        mFloatingView.setImageResource(R.drawable.camera_ic);
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
        if (v.getId() == R.id.fab_image_select_floating) {
            if (mSelectImage.isEmpty()) {
                // 点击拍照
                CameraActivity.start(this, file -> {

                    // 当前选中图片的数量必须小于最大选中数
                    if (mSelectImage.size() < mMaxSelect) {
                        mSelectImage.add(file.getPath());
                    }

                    // 这里需要延迟刷新，否则可能会找不到拍照的图片
                    postDelayed(() -> {
                        // 重新加载图片列表
                        new Thread(ImageSelectActivity.this).start();
                    }, 1000);
                });
            } else {
                // 完成选择
                setResult(RESULT_OK, new Intent().putStringArrayListExtra(IntentKey.IMAGE, mSelectImage));
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
        if (mSelectImage.contains(mAdapter.getItem(position))) {
            ImagePreviewActivity.start(getActivity(), mSelectImage, mSelectImage.indexOf(mAdapter.getItem(position)));
        } else {
            ImagePreviewActivity.start(getActivity(), mAdapter.getItem(position));
        }
    }

    /**
     * {@link BaseAdapter.OnItemLongClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position) {
        if (mSelectImage.size() < mMaxSelect) {
            // 长按的时候模拟选中
            return itemView.findViewById(R.id.fl_image_select_check).performClick();
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
        if (childView.getId() == R.id.fl_image_select_check) {
            if (mSelectImage.contains(mAdapter.getItem(position))) {
                mSelectImage.remove(mAdapter.getItem(position));

                if (mSelectImage.isEmpty()) {
                    mFloatingView.hide();
                    postDelayed(() -> {
                        mFloatingView.setImageResource(R.drawable.camera_ic);
                        mFloatingView.show();
                    }, 200);
                }

            } else {

                if (mMaxSelect == 1 && mSelectImage.size() == 1) {

                    List<String> data = mAdapter.getData();
                    if (data != null) {
                        int index = data.indexOf(mSelectImage.get(0));
                        if (index != -1) {
                            mSelectImage.remove(0);
                            mAdapter.notifyItemChanged(index);
                        }
                    }
                    mSelectImage.add(mAdapter.getItem(position));

                } else if (mSelectImage.size() < mMaxSelect) {

                    mSelectImage.add(mAdapter.getItem(position));

                    if (mSelectImage.size() == 1) {
                        mFloatingView.hide();
                        postDelayed(() -> {
                            mFloatingView.setImageResource(R.drawable.succeed_ic);
                            mFloatingView.show();
                        }, 200);
                    }
                } else {
                    toast(String.format(getString(R.string.image_select_max_hint), mMaxSelect));
                }
            }
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void run() {
        mAllAlbum.clear();
        mAllImage.clear();

        final Uri contentUri = MediaStore.Files.getContentUri("external");
        final String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
        final String selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + MediaStore.MediaColumns.SIZE + ">0";

        ContentResolver contentResolver = getContentResolver();
        String[] projections = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE};

        Cursor cursor = null;
        if (XXPermissions.hasPermission(this, Permission.READ_EXTERNAL_STORAGE)) {
            cursor = contentResolver.query(contentUri, projections, selection, new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)}, sortOrder);
        }
        if (cursor != null && cursor.moveToFirst()) {

            int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);

            do {
                long size = cursor.getLong(sizeIndex);
                // 图片大小不得小于 10 KB
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
                if (parentFile != null) {
                    // 获取目录名作为专辑名称
                    String albumName = parentFile.getName();
                    List<String> data = mAllAlbum.get(albumName);
                    if (data == null) {
                        data = new ArrayList<>();
                        mAllAlbum.put(albumName, data);
                    }
                    data.add(path);
                    mAllImage.add(path);
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        postDelayed(() -> {
            // 滚动回第一个位置
            mRecyclerView.scrollToPosition(0);
            // 设置新的列表数据
            mAdapter.setData(mAllImage);

            if (mSelectImage.isEmpty()) {
                mFloatingView.setImageResource(R.drawable.camera_ic);
            } else {
                mFloatingView.setImageResource(R.drawable.succeed_ic);
            }

            // 执行列表动画
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.fall_down_layout));
            mRecyclerView.scheduleLayoutAnimation();

            // 设置右标提
            setRightTitle(R.string.image_select_all);

            if (mAllImage.isEmpty()) {
                // 显示空布局
                showEmpty();
            } else {
                // 显示加载完成
                showComplete();
            }
        }, 500);
    }

    /**
     * 图片选择监听
     */
    public interface OnPhotoSelectListener {

        /**
         * 选择回调
         *
         * @param data          图片列表
         */
        void onSelected(List<String> data);

        /**
         * 取消回调
         */
        default void onCancel() {}
    }
}