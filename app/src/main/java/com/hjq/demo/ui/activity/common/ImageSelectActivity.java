package com.hjq.demo.ui.activity.common;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.hjq.demo.ui.adapter.common.ImageSelectAdapter;
import com.hjq.demo.ui.dialog.common.AlbumDialog;
import com.hjq.demo.widget.StatusLayout;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.smallest.width.SmallestWidthAdaptation;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/24
 *    desc   : 选择图片
 */
public final class ImageSelectActivity extends AppActivity
        implements StatusAction, Runnable,
        BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener,
        BaseAdapter.OnChildClickListener {

    private static final String INTENT_KEY_IN_MAX_SELECT = "maxSelect";

    private static final String INTENT_KEY_OUT_IMAGE_LIST = "imageList";

    public static void start(@NonNull BaseActivity activity, @Nullable OnImageSelectListener listener) {
        start(activity, 1, listener);
    }

    @Log
    public static void start(@NonNull BaseActivity activity, int maxSelect, @Nullable OnImageSelectListener listener) {
        if (maxSelect < 1) {
            // 最少要选择一个图片
            throw new IllegalArgumentException("are you ok?");
        }

        if (PickVisualMedia.isPhotoPickerAvailable(activity)) {
            PickVisualMediaRequest visualMediaRequest = new Builder()
                // 只选择图片
                .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
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

                Intent intent = new Intent(activity, ImageSelectActivity.class);
                intent.putExtra(INTENT_KEY_IN_MAX_SELECT, maxSelect);
                activity.startActivityForResult(intent, (resultCode, data) -> {

                    if (listener == null) {
                        return;
                    }

                    if (data == null) {
                        listener.onCancel();
                        return;
                    }

                    List<String> list = data.getStringArrayListExtra(INTENT_KEY_OUT_IMAGE_LIST);
                    if (list == null || list.isEmpty()) {
                        listener.onCancel();
                        return;
                    }

                    Iterator<String> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        if (!new File(iterator.next()).isFile()) {
                            iterator.remove();
                        }
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
    private final ArrayList<String> mSelectImage = new ArrayList<>();

    /** 全部图片 */
    private final ArrayList<String> mAllImage = new ArrayList<>();
    /** 图片专辑 */
    private final HashMap<String, List<String>> mAllAlbum = new HashMap<>();

    /** 列表适配器 */
    private final ImageSelectAdapter mAdapter = new ImageSelectAdapter(this, mSelectImage);

    /** 专辑选择对话框 */
    private AlbumDialog.Builder mAlbumDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.image_select_activity;
    }

    @Override
    protected void initView() {
        mStatusLayout = findViewById(R.id.sl_image_select_status);
        mRecyclerView = findViewById(R.id.rv_image_select_list);
        mFloatingView = findViewById(R.id.fab_image_select_floating);
        setOnClickListener(mFloatingView);

        mAdapter.setOnChildClickListener(R.id.fl_image_select_check, this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        // 禁用动画效果
        mRecyclerView.setItemAnimator(null);
        // 添加分割线
        mRecyclerView.addItemDecoration(new GridSpaceDecoration((int) SmallestWidthAdaptation.dp2px(this, 3)));
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
        // 加载图片列表
        ThreadPoolManager.getInstance().execute(this);
    }

    @Override
    public StatusLayout acquireStatusLayout() {
        return mStatusLayout;
    }

    @SingleClick
    @Override
    public void onRightClick(@NonNull TitleBar titleBar) {
        if (mAllImage.isEmpty()) {
            return;
        }

        ArrayList<AlbumDialog.AlbumInfo> data = new ArrayList<>(mAllAlbum.size() + 1);

        int count = 0;
        Set<String> keys = mAllAlbum.keySet();
        for (String key : keys) {
            List<String> list = mAllAlbum.get(key);
            if (list == null || list.isEmpty()) {
                continue;
            }
            count += list.size();
            data.add(new AlbumDialog.AlbumInfo(list.get(0), key, String.format(getString(R.string.image_select_total), list.size()),
                    mAdapter.getData() != mAllImage && mAdapter.getData().equals(list)));
        }
        data.add(0, new AlbumDialog.AlbumInfo(mAllImage.get(0), getString(R.string.image_select_all),
                String.format(getString(R.string.image_select_total), count), mAdapter.getData() == mAllImage));

        if (mAlbumDialog == null) {
            mAlbumDialog = new AlbumDialog.Builder(this)
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
                        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(ImageSelectActivity.this, R.anim.layout_from_right));
                        mRecyclerView.scheduleLayoutAnimation();
                    });
        }
        mAlbumDialog.setData(data)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Iterator<String> iterator = mSelectImage.iterator();
        // 遍历判断选择了的图片是否被删除了
        while (iterator.hasNext()) {
            String path = iterator.next();
            File file = new File(path);
            if (file.isFile()) {
                continue;
            }

            iterator.remove();
            mAllImage.remove(path);

            File parentFile = file.getParentFile();
            if (parentFile == null) {
                continue;
            }

            List<String> data = mAllAlbum.get(parentFile.getName());
            if (data != null) {
                data.remove(path);
                if (data == mAdapter.getData()) {
                    mAdapter.removeItem(path);
                }
            }

            if (mSelectImage.isEmpty()) {
                mFloatingView.setImageResource(R.drawable.camera_ic);
            } else {
                mFloatingView.setImageResource(R.drawable.succeed_ic);
            }
        }
        refreshLayout();
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.fab_image_select_floating) {
            if (mSelectImage.isEmpty()) {
                // 点击拍照
                CameraActivity.start(this, new CameraActivity.OnCameraListener() {
                    @Override
                    public void onSelected(@NonNull File file) {
                        // 当前选中图片的数量必须小于最大选中数
                        if (mSelectImage.size() < mMaxSelect) {
                            mSelectImage.add(file.getPath());
                        }

                        // 这里需要延迟刷新，否则可能会找不到拍照的图片
                        postDelayed(() -> {
                            // 重新加载图片列表
                            ThreadPoolManager.getInstance().execute(ImageSelectActivity.this);
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
            setResult(RESULT_OK, new Intent().putStringArrayListExtra(INTENT_KEY_OUT_IMAGE_LIST, mSelectImage));
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
        ImagePreviewActivity.start(this, mAdapter.getData(), position);
    }

    /**
     * {@link BaseAdapter.OnItemLongClickListener}
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public boolean onItemLongClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
        if (mSelectImage.size() < mMaxSelect) {
            // 长按的时候模拟选中
            return itemView.findViewById(R.id.fl_image_select_check).performClick();
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
        if (childView.getId() == R.id.fl_image_select_check) {

            String path = mAdapter.getItem(position);
            File file = new File(path);
            if (!file.isFile()) {
                mAdapter.removeItem(position);
                toast(R.string.image_select_error);
                return;
            }

            if (mSelectImage.contains(path)) {
                mSelectImage.remove(path);

                if (mSelectImage.isEmpty()) {
                    mFloatingView.setImageResource(R.drawable.camera_ic);
                }

                mAdapter.notifyItemChanged(position);
                return;
            }

            if (mMaxSelect == 1 && mSelectImage.size() == 1) {

                List<String> data = mAdapter.getData();
                int index = data.indexOf(mSelectImage.remove(0));
                if (index != -1) {
                    mAdapter.notifyItemChanged(index);
                }
                mSelectImage.add(path);

            } else if (mSelectImage.size() < mMaxSelect) {

                mSelectImage.add(path);

                if (mSelectImage.size() == 1) {
                    mFloatingView.setImageResource(R.drawable.succeed_ic);
                }
            } else {
                toast(String.format(getString(R.string.image_select_max_hint), mMaxSelect));
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
        if (XXPermissions.isGrantedPermission(this, PermissionLists.getReadMediaVisualUserSelectedPermission())) {
            cursor = contentResolver.query(contentUri, projections, selection, new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)}, sortOrder);
        }
        if (cursor != null && cursor.moveToFirst()) {

            int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
            int widthIndex = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
            int heightIndex = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);

            do {
                long size = cursor.getLong(sizeIndex);
                // 图片大小不得小于 1 KB
                if (size < 1024) {
                    continue;
                }

                String width = cursor.getString(widthIndex);
                String height = cursor.getString(heightIndex);
                // 有些破损的图片宽高是获取不到的，这里需要直接忽略
                // 我自己测试的话，就是获取到等于 null，别人反馈的是获取到 -1
                if (width == null || height == null ||
                        "-1".equals(width) || "-1".equals(height)) {
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
                List<String> data = mAllAlbum.get(albumName);
                if (data == null) {
                    data = new ArrayList<>();
                    mAllAlbum.put(albumName, data);
                }
                data.add(path);
                mAllImage.add(path);

            } while (cursor.moveToNext());

            cursor.close();
        }

        post(() -> {
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
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                    ImageSelectActivity.this, R.anim.layout_fall_down));
            mRecyclerView.scheduleLayoutAnimation();

            refreshLayout();
        });
    }

    /**
     * 刷新布局
     */
    private void refreshLayout() {
        if (mAllImage.isEmpty()) {
            // 显示空布局
            showEmpty();
            // 设置右标题
            setRightTitle(null);
        } else {
            // 显示加载完成
            showComplete();
            // 设置右标题
            setRightTitle(R.string.image_select_all);
        }
    }

    /**
     * 图片选择监听
     */
    public interface OnImageSelectListener {

        /**
         * 选择回调
         *
         * @param data          图片列表
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