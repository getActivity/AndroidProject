package com.hjq.demo.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyAdapter;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/10/09
 *    desc   : 单选或者多选对话框
 */
public final class SelectDialog {

    public static final class Builder
            extends UIDialog.Builder<Builder> {

        private OnListener mListener;

        private final SelectAdapter mAdapter;

        public Builder(Context context) {
            super(context);

            setCustomView(R.layout.select_dialog);
            RecyclerView recyclerView = findViewById(R.id.rv_select_list);
            recyclerView.setItemAnimator(null);

            mAdapter = new SelectAdapter(getContext());
            recyclerView.setAdapter(mAdapter);
        }

        public Builder setList(int... ids) {
            List<String> data = new ArrayList<>(ids.length);
            for (int id : ids) {
                data.add(getString(id));
            }
            return setList(data);
        }

        public Builder setList(String... data) {
            return setList(Arrays.asList(data));
        }

        @SuppressWarnings("all")
        public Builder setList(List data) {
            mAdapter.setData(data);
            return this;
        }

        /**
         * 设置默认选中的位置
         */
        public Builder setSelect(int... positions) {
            mAdapter.setSelect(positions);
            return this;
        }

        /**
         * 设置最大选择数量
         */
        public Builder setMaxSelect(int count) {
            mAdapter.setMaxSelect(count);
            return this;
        }

        /**
         * 设置最小选择数量
         */
        public Builder setMinSelect(int count) {
            mAdapter.setMinSelect(count);
            return this;
        }

        /**
         * 设置单选模式
         */
        public Builder setSingleSelect() {
            mAdapter.setSingleSelect();
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @SuppressWarnings("all")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ui_confirm:
                    HashMap<Integer, Object> data = mAdapter.getSelectSet();
                    if (data.size() >= mAdapter.getMinSelect()) {
                        autoDismiss();
                        if (mListener != null) {
                            mListener.onSelected(getDialog(), data);
                        }
                    } else {
                        ToastUtils.show(String.format(getString(R.string.select_min_hint), mAdapter.getMinSelect()));
                    }
                    break;
                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onCancel(getDialog());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static final class SelectAdapter extends MyAdapter<Object>
            implements BaseAdapter.OnItemClickListener {

        /** 最小选择数量 */
        private int mMinSelect = 1;
        /** 最大选择数量 */
        private int mMaxSelect = Integer.MAX_VALUE;

        /** 选择对象集合 */
        @SuppressLint("UseSparseArrays")
        private final HashMap<Integer, Object> mSelectSet = new HashMap<>();

        private SelectAdapter(Context context) {
            super(context);
            setOnItemClickListener(this);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private void setSelect(int... positions) {
            for (int position : positions) {
                mSelectSet.put(position, getItem(position));
            }
            notifyDataSetChanged();
        }

        private void setMaxSelect(int count) {
            mMaxSelect = count;
        }

        private void setMinSelect(int count) {
            mMinSelect = count;
        }

        private int getMinSelect() {
            return mMinSelect;
        }

        private void setSingleSelect() {
            setMaxSelect(1);
            setMinSelect(1);
        }

        private boolean isSingleSelect() {
            return mMaxSelect == 1 && mMinSelect == 1;
        }

        private HashMap<Integer, Object> getSelectSet() {
            return mSelectSet;
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */

        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (mSelectSet.containsKey(position)) {
                // 当前必须不是单选模式才能取消选中
                if (!isSingleSelect()) {
                    mSelectSet.remove(position);
                    notifyItemChanged(position);
                }
            } else {
                if (mMaxSelect == 1) {
                    mSelectSet.clear();
                    notifyDataSetChanged();
                }

                if (mSelectSet.size() < mMaxSelect) {
                    mSelectSet.put(position, getItem(position));
                    notifyItemChanged(position);
                } else {
                    ToastUtils.show(String.format(getString(R.string.select_max_hint), mMaxSelect));
                }
            }
        }

        private final class ViewHolder extends MyAdapter.ViewHolder {

            private final TextView mTextView;
            private final CheckBox mCheckBox;

            ViewHolder() {
                super(R.layout.select_item);
                mTextView = (TextView) findViewById(R.id.tv_select_text);
                mCheckBox = (CheckBox) findViewById(R.id.tv_select_checkbox);
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).toString());
                mCheckBox.setChecked(mSelectSet.containsKey(position));
                if (mMaxSelect == 1) {
                    mCheckBox.setClickable(false);
                } else {
                    mCheckBox.setEnabled(false);
                }
            }
        }
    }

    public interface OnListener<T> {

        /**
         * 选择回调
         *
         * @param data              选择的位置和数据
         */
        void onSelected(BaseDialog dialog, HashMap<Integer, T> data);

        /**
         * 取消回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}