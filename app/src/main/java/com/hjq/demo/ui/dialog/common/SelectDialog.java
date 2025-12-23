package com.hjq.demo.ui.dialog.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.custom.widget.layout.SimpleLayout;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppAdapter;
import com.hjq.toast.Toaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/10/09
 *    desc   : 单选或者多选对话框
 */
public final class SelectDialog {

    public static final class Builder
            extends StyleDialog.Builder<Builder> {

        @NonNull
        private final SimpleLayout mSimpleLayout;
        @NonNull
        private final RecyclerView mRecyclerView;
        @NonNull
        private final SelectAdapter mAdapter;

        @SuppressWarnings("rawtypes")
        @Nullable
        private OnMultiListener mListener;

        public Builder(@NonNull Context context) {
            super(context);

            setCustomView(R.layout.select_dialog);
            mSimpleLayout = findViewById(R.id.sl_select_layout);
            mRecyclerView = findViewById(R.id.rv_select_list);
            mRecyclerView.setItemAnimator(null);

            mAdapter = new SelectAdapter(getContext());
            mRecyclerView.setAdapter(mAdapter);

            mSimpleLayout.setMaxHeight(getScreenHeight() / 4 * 3);
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

        /**
         * 设置多选监听
         */
        @SuppressWarnings("rawtypes")
        public Builder setMultiListener(OnMultiListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * 设置单选监听
         */
        @SuppressWarnings("rawtypes")
        public Builder setSingleListener(@Nullable OnSingleListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @SuppressWarnings("all")
        @Override
        public void onClick(@NonNull View view) {
            int viewId = view.getId();
            if (viewId == R.id.tv_ui_confirm) {
                Map<Integer, Object> data = mAdapter.getSelectSet();
                if (data.size() >= mAdapter.getMinSelect()) {
                    performClickDismiss();
                    if (mListener == null) {
                        return;
                    }
                    mListener.onSelected(getDialog(), data);
                } else {
                    Toaster.show(String.format(getString(R.string.select_min_hint), mAdapter.getMinSelect()));
                }
            } else if (viewId == R.id.tv_ui_cancel) {
                performClickDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        /**
         *  获取屏幕的高度
         */
        private int getScreenHeight() {
            Resources resources = getResources();
            DisplayMetrics outMetrics = resources.getDisplayMetrics();
            return outMetrics.heightPixels;
        }
    }

    private static final class SelectAdapter extends AppAdapter<Object>
            implements BaseAdapter.OnItemClickListener {

        /** 最小选择数量 */
        private int mMinSelect = 1;
        /** 最大选择数量 */
        private int mMaxSelect = Integer.MAX_VALUE;

        /** 选择对象集合 */
        @SuppressLint("UseSparseArrays")
        @NonNull
        private final Map<Integer, Object> mSelectSet = new HashMap<>();

        private SelectAdapter(@NonNull Context context) {
            super(context);
            setOnItemClickListener(this);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        @SuppressLint("NotifyDataSetChanged")
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

        @NonNull
        private Map<Integer, Object> getSelectSet() {
            return mSelectSet;
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
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
                    Toaster.show(String.format(getString(R.string.select_max_hint), mMaxSelect));
                }
            }
        }

        private final class ViewHolder extends AppViewHolder {

            private final TextView mTextView;
            private final CheckBox mCheckBox;

            ViewHolder() {
                super(R.layout.select_item);
                mTextView = findViewById(R.id.tv_select_text);
                mCheckBox = findViewById(R.id.tv_select_checkbox);
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

    public interface OnMultiListener<T> {

        /**
         * 选择回调
         *
         * @param data              选择的位置和数据
         */
        void onSelected(@NonNull BaseDialog dialog, Map<Integer, T> data);

        /**
         * 取消回调
         */
        default void onCancel(@NonNull BaseDialog dialog) {
            // default implementation ignored
        }
    }

    public interface OnSingleListener<T> extends OnMultiListener<T> {

        @Override
        default void onSelected(@NonNull BaseDialog dialog, Map<Integer, T> data) {
            Set<Integer> keys = data.keySet();
            Iterator<Integer> iterator = keys.iterator();
            if (!iterator.hasNext()) {
                return;
            }
            Integer key = iterator.next();
            if (key == null) {
                return;
            }
            onSelected(dialog, key, data.get(key));
        }

        /**
         * 选择回调
         *
         * @param position          选择的位置
         * @param data              选择的数据
         */
        void onSelected(@NonNull BaseDialog dialog, int position, T data);
    }
}