package com.hjq.demo.ui.dialog.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.base.BottomSheetDialog;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.http.glide.GlideApp;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/27
 *    desc   : 相册专辑选取对话框
 */
public final class AlbumDialog  {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        @NonNull
        private final RecyclerView mRecyclerView;
        @NonNull
        private final AlbumAdapter mAdapter;

        @Nullable
        private OnListener mListener;

        public Builder(@NonNull Context context) {
            super(context);

            setContentView(R.layout.album_dialog);

            mRecyclerView = findViewById(R.id.rv_album_list);
            mAdapter = new AlbumAdapter(context);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        public Builder setData(List<AlbumInfo> data) {
            mAdapter.setData(data);
            // 滚动到选中的位置
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isSelect()) {
                    mRecyclerView.scrollToPosition(i);
                    break;
                }
            }
            return this;
        }

        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
            List<AlbumInfo> data = mAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                AlbumInfo albumInfo = data.get(i);
                if (!albumInfo.isSelect()) {
                    continue;
                }
                albumInfo.setSelect(false);
                mAdapter.notifyItemChanged(i);
                break;
            }

            AlbumInfo albumInfo = mAdapter.getItem(position);
            albumInfo.setSelect(true);
            mAdapter.notifyItemChanged(position);

            // 延迟消失
            postDelayed(() -> {
                if (mListener != null) {
                    mListener.onSelected(getDialog(), position, albumInfo);
                }
                dismiss();
            }, 300);
        }

        @NonNull
        @Override
        protected BaseDialog createDialog(@NonNull Context context, int themeId) {
            BottomSheetDialog dialog = new BottomSheetDialog(context, themeId);
            dialog.getBottomSheetBehavior().setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);
            return dialog;
        }
    }

    private static final class AlbumAdapter extends AppAdapter<AlbumInfo> {

        private AlbumAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppViewHolder {

            private final ImageView mIconView;
            private final TextView mNameView;
            private final TextView mRemarkView;
            private final CheckBox mCheckBox;

            private ViewHolder() {
                super(R.layout.album_item);
                mIconView = findViewById(R.id.iv_album_icon);
                mNameView = findViewById(R.id.tv_album_name);
                mRemarkView = findViewById(R.id.tv_album_remark);
                mCheckBox = findViewById(R.id.rb_album_check);
            }

            @Override
            public void onBindView(int position) {
                AlbumInfo info = getItem(position);

                GlideApp.with(getContext())
                        .asBitmap()
                        .load(info.getIcon())
                        .into(mIconView);

                mNameView.setText(info.getName());
                mRemarkView.setText(info.getRemark());
                mCheckBox.setChecked(info.isSelect());
                mCheckBox.setVisibility(info.isSelect() ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    /**
     * 专辑信息类
     */
    public static class AlbumInfo {

        /** 封面 */
        private final String icon;
        /** 名称 */
        private final String name;
        /** 备注 */
        private final String remark;
        /** 选中 */
        private boolean select;

        public AlbumInfo(String icon, String name, String remark, boolean select) {
            this.icon = icon;
            this.name = name;
            this.remark = remark;
            this.select = select;
        }

        public String getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public String getRemark() {
            return remark;
        }

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }
    }

    public interface OnListener {

        /**
         * 选择条目时回调
         */
        void onSelected(@NonNull BaseDialog dialog, int position, @NonNull AlbumInfo bean);
    }
}