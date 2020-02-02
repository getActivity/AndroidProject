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
import com.hjq.demo.ui.adapter.PhotoAdapter;
import com.hjq.demo.ui.dialog.AlbumDialog;
import com.hjq.demo.widget.HintLayout;
import com.hjq.permissions.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/24
 *    desc   : 图片选择
 */
public final class PhotoActivity extends MyActivity
        implements StatusAction,
        BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener,
        BaseAdapter.OnChildClickListener, Runnable {

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
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putExtra(IntentKey.AMOUNT, maxSelect);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSelected(data.getStringArrayListExtra(IntentKey.PICTURE));
            } else {
                listener.onCancel();
            }
        });
    }

    @BindView(R.id.hl_photo_hint)
    HintLayout mHintLayout;
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
        mRecyclerView.addItemDecoration(new GridSpaceDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics())));
        setOnClickListener(R.id.fab_photo_floating);
    }

    @SuppressWarnings("all")
    @Override
    protected void initData() {
        // 获取最大的选择数
        mMaxSelect = getInt(IntentKey.AMOUNT, mMaxSelect);

        // 显示加载进度条
        showLoading();
        // 加载图片列表
        new Thread(PhotoActivity.this).start();
    }

    @Override
    public HintLayout getHintLayout() {
        return mHintLayout;
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
                .setListener((dialog, position, bean) -> {

                    setRightTitle(bean.getName());
                    // 滚动回第一个位置
                    mRecyclerView.scrollToPosition(0);
                    if (position == 0) {
                        mAdapter.setData(mAllPhoto);
                    } else {
                        mAdapter.setData(mAllAlbum.get(bean.getName()));
                    }
                    // 执行列表动画
                    mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_from_right));
                    mRecyclerView.scheduleLayoutAnimation();
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

                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    List<String> data = mAllAlbum.get(parentFile.getName());
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
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_photo_floating) {
            if (mSelectPhoto.isEmpty()) {
                // 点击拍照
                CameraActivity.start(this, file -> {

                    // 当前选中图片的数量必须小于最大选中数
                    if (mSelectPhoto.size() < mMaxSelect) {
                        mSelectPhoto.add(file.getPath());
                    }

                    // 这里需要延迟刷新，否则可能会找不到拍照的图片
                    postDelayed(() -> {
                        // 重新加载图片列表
                        new Thread(PhotoActivity.this).start();
                    }, 1000);
                });
            } else {
                // 完成选择
                setResult(RESULT_OK, new Intent().putStringArrayListExtra(IntentKey.PICTURE, mSelectPhoto));
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
        if (mSelectPhoto.contains(mAdapter.getItem(position))) {
            ImageActivity.start(getActivity(), mSelectPhoto, mSelectPhoto.indexOf(mAdapter.getItem(position)));
        } else {
            ImageActivity.start(getActivity(), mAdapter.getItem(position));
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
        if (mSelectPhoto.size() < mMaxSelect) {
            // 长按的时候模拟选中
            return itemView.findViewById(R.id.fl_photo_check).performClick();
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
        if (childView.getId() == R.id.fl_photo_check) {
            if (mSelectPhoto.contains(mAdapter.getItem(position))) {
                mSelectPhoto.remove(mAdapter.getItem(position));

                if (mSelectPhoto.isEmpty()) {
                    mFloatingView.hide();
                    postDelayed(() -> {
                        mFloatingView.setImageResource(R.drawable.ic_photo_camera);
                        mFloatingView.show();
                    }, 200);
                }

            } else {

                if (mMaxSelect == 1 && mSelectPhoto.size() == 1) {

                    List<String> data = mAdapter.getData();
                    if (data != null) {
                        int index = data.indexOf(mSelectPhoto.get(0));
                        if (index != -1) {
                            mSelectPhoto.remove(0);
                            mAdapter.notifyItemChanged(index);
                        }
                    }
                    mSelectPhoto.add(mAdapter.getItem(position));

                } else if (mSelectPhoto.size() < mMaxSelect) {

                    mSelectPhoto.add(mAdapter.getItem(position));

                    if (mSelectPhoto.size() == 1) {
                        mFloatingView.hide();
                        postDelayed(() -> {
                            mFloatingView.setImageResource(R.drawable.ic_photo_succeed);
                            mFloatingView.show();
                        }, 200);
                    }
                } else {
                    toast(String.format(getString(R.string.photo_max_hint), mMaxSelect));
                }
            }
            mAdapter.notifyItemChanged(position);
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
        projections = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.WIDTH, MediaStore
                .MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE};

        Cursor cursor = contentResolver.query(contentUri, projections, selection, selectionAllArgs, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {

            int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            int widthIndex = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
            int heightIndex = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);

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

                int width = cursor.getInt(widthIndex);
                int height = cursor.getInt(heightIndex);
                if (width < 1 || height < 1) {
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
                    List<String> files = mAllAlbum.get(albumName);
                    if (files == null) {
                        files = new ArrayList<>();
                        mAllAlbum.put(albumName, files);
                    }
                    files.add(path);
                    mAllPhoto.add(path);
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        postDelayed(() -> {
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
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_fall_down));
            mRecyclerView.scheduleLayoutAnimation();

            // 设置右标提
            setRightTitle(R.string.photo_all);

            if (mAllPhoto.isEmpty()) {
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
        void onCancel();
    }
}