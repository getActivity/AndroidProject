package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.common.MyAdapter;
import com.hjq.demo.http.glide.GlideApp;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/07/27
 *    desc   : 相册专辑选取对话框
 */
public final class AlbumDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        private OnListener mListener;

        private final RecyclerView mRecyclerView;
        private final AlbumAdapter mAdapter;

        public Builder(Context context) {
            super(context);

            setContentView(R.layout.album_dialog);
            setHeight(getResources().getDisplayMetrics().heightPixels / 2);

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
                }
            }
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            List<AlbumInfo> data = mAdapter.getData();
            if (data == null) {
                return;
            }

            for (AlbumInfo info : data) {
                if (info.isSelect()) {
                    info.setSelect(false);
                    break;
                }
            }
            mAdapter.getItem(position).setSelect(true);
            mAdapter.notifyDataSetChanged();

            // 延迟消失
            postDelayed(() -> {

                if (mListener != null) {
                    mListener.onSelected(getDialog(), position, mAdapter.getItem(position));
                }
                dismiss();

            }, 300);
        }
    }

    private static final class AlbumAdapter extends MyAdapter<AlbumInfo> {

        private AlbumAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends MyAdapter.ViewHolder {

            private final ImageView mIconView;
            private final TextView mNameView;
            private final TextView mRemarkView;
            private final CheckBox mCheckBox;

            private ViewHolder() {
                super(R.layout.album_item);
                mIconView = (ImageView) findViewById(R.id.iv_album_icon);
                mNameView = (TextView) findViewById(R.id.tv_album_name);
                mRemarkView = (TextView) findViewById(R.id.tv_album_remark);
                mCheckBox = (CheckBox) findViewById(R.id.rb_album_check);
            }

            @Override
            public void onBindView(int position) {
                AlbumInfo info = getItem(position);

                GlideApp.with(getContext())
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
        private String icon;
        /** 名称 */
        private String name;
        /** 备注 */
        private String remark;
        /** 选中 */
        private boolean select;

        public AlbumInfo(String icon, String name, String remark, boolean select) {
            this.icon = icon;
            this.name = name;
            this.remark = remark;
            this.select = select;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSelect(boolean select) {
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
    }

    public interface OnListener {

        /**
         * 选择条目时回调
         */
        void onSelected(BaseDialog dialog, int position, AlbumInfo bean);
    }
}