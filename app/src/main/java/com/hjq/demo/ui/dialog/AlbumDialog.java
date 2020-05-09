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

            setContentView(R.layout.dialog_album);
            setHeight(getResources().getDisplayMetrics().heightPixels / 2);

            mRecyclerView = findViewById(R.id.rv_album_list);
            mAdapter = new AlbumAdapter(context);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        public Builder setData(List<AlbumBean> data) {
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
            List<AlbumBean> data = mAdapter.getData();
            if (data == null) {
                return;
            }

            for (AlbumBean bean : data) {
                if (bean.isSelect()) {
                    bean.setSelect(false);
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

    private static final class AlbumAdapter extends MyAdapter<AlbumBean> {

        private AlbumAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        final class ViewHolder extends MyAdapter.ViewHolder {

            private final ImageView mIconView;
            private final TextView mNameView;
            private final TextView mCountView;
            private final CheckBox mCheckBox;

            private ViewHolder() {
                super(R.layout.item_album);
                mIconView = (ImageView) findViewById(R.id.iv_album_icon);
                mNameView = (TextView) findViewById(R.id.tv_album_name);
                mCountView = (TextView) findViewById(R.id.tv_album_count);
                mCheckBox = (CheckBox) findViewById(R.id.rb_album_check);
            }

            @Override
            public void onBindView(int position) {
                AlbumBean bean = getItem(position);

                GlideApp.with(getContext())
                        .load(bean.getIcon())
                        .into(mIconView);

                mNameView.setText(bean.getName());
                mCountView.setText(String.format(getString(R.string.photo_total), bean.getCount()));
                mCheckBox.setChecked(bean.isSelect());
                mCheckBox.setVisibility(bean.isSelect() ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    /**
     * 专辑信息类
     */
    public static class AlbumBean {

        /** 封面 */
        private String icon;
        /** 名称 */
        private String name;
        /** 数量 */
        private int count;
        /** 选中 */
        private boolean select;

        public AlbumBean(String icon, String name, int count, boolean select) {
            this.icon = icon;
            this.name = name;
            this.count = count;
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

        public int getCount() {
            return count;
        }

        public boolean isSelect() {
            return select;
        }
    }

    public interface OnListener {

        /**
         * 选择条目时回调
         */
        void onSelected(BaseDialog dialog, int position, AlbumBean bean);
    }
}