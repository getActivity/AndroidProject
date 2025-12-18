package com.hjq.demo.ui.dialog.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.custom.widget.layout.SimpleLayout;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : 菜单选择框
 */
public final class MenuDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        @SuppressWarnings("rawtypes")
        private boolean mAutoDismiss = true;

        @NonNull
        private final SimpleLayout mSimpleLayout;
        @NonNull
        private final RecyclerView mRecyclerView;
        @NonNull
        private final TextView mCancelView;

        @NonNull
        private final MenuAdapter mAdapter;

        @Nullable
        private OnListener mListener;

        public Builder(@NonNull Context context) {
            super(context);
            setContentView(R.layout.menu_dialog);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);

            mSimpleLayout = findViewById(R.id.sl_menu_layout);
            mRecyclerView = findViewById(R.id.rv_menu_list);
            mCancelView  = findViewById(R.id.tv_menu_cancel);
            setOnClickListener(mCancelView);

            mAdapter = new MenuAdapter(getContext());
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);

            mSimpleLayout.setMaxHeight(getScreenHeight() / 4 * 3);
        }

        @Override
        public Builder setGravity(int gravity) {
            switch (gravity) {
                // 如果这个是在中间显示的
                case Gravity.CENTER:
                case Gravity.CENTER_VERTICAL:
                    // 不显示取消按钮
                    setCancel(null);
                    // 重新设置动画
                    setAnimStyle(BaseDialog.ANIM_SCALE);
                    break;
                default:
                    break;
            }
            return super.setGravity(gravity);
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

        public Builder setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }

        public Builder setCancel(CharSequence text) {
            mCancelView.setText(text);
            return this;
        }

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        @SuppressWarnings("rawtypes")
        public Builder setListener(@Nullable OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(@NonNull View view) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (view == mCancelView) {
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @SuppressWarnings("all")
        @Override
        public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener == null) {
                return;
            }
            mListener.onSelected(getDialog(), position, mAdapter.getItem(position));
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

    private static final class MenuAdapter extends AppAdapter<Object> {

        private MenuAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppViewHolder {

            private final TextView mTextView;
            private final View mLineView;

            ViewHolder() {
                super(R.layout.menu_item);
                mTextView = findViewById(R.id.tv_menu_text);
                mLineView = findViewById(R.id.v_menu_line);
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).toString());

                if (position == 0) {
                    // 当前是否只有一个条目
                    if (getCount() == 1) {
                        mLineView.setVisibility(View.GONE);
                    } else {
                        mLineView.setVisibility(View.VISIBLE);
                    }
                } else if (position == getCount() - 1) {
                    mLineView.setVisibility(View.GONE);
                } else {
                    mLineView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public interface OnListener<T> {

        /**
         * 选择条目时回调
         */
        void onSelected(@NonNull BaseDialog dialog, int position, T data);

        /**
         * 点击取消时回调
         */
        default void onCancel(@NonNull BaseDialog dialog) {
            // default implementation ignored
        }
    }
}