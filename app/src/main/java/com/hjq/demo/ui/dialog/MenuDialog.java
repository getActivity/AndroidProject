package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyAdapter;

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
            implements BaseAdapter.OnItemClickListener,
            View.OnLayoutChangeListener, Runnable {

        private OnListener mListener;
        private boolean mAutoDismiss = true;

        private final RecyclerView mRecyclerView;
        private final TextView mCancelView;

        private final MenuAdapter mAdapter;

        public Builder(Context context) {
            super(context);
            setContentView(R.layout.menu_dialog);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);

            mRecyclerView = findViewById(R.id.rv_menu_list);
            mCancelView  = findViewById(R.id.tv_menu_cancel);
            setOnClickListener(mCancelView);

            mAdapter = new MenuAdapter(getContext());
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
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
            mRecyclerView.addOnLayoutChangeListener(this);
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

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (v == mCancelView) {
                if (mListener != null) {
                    mListener.onCancel(getDialog());
                }
            }
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @SuppressWarnings("all")
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                mListener.onSelected(getDialog(), position, mAdapter.getItem(position));
            }
        }

        /**
         * {@link View.OnLayoutChangeListener}
         */
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            mRecyclerView.removeOnLayoutChangeListener(this);
            // 这里一定要加延迟，如果不加在 Android 9.0 上面会导致 setLayoutParams 无效
            post(this);
        }

        @Override
        public void run() {
            final ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
            final int maxHeight = getScreenHeight() / 4 * 3;
            if (mRecyclerView.getHeight() > maxHeight) {
                if (params.height != maxHeight) {
                    params.height = maxHeight;
                    mRecyclerView.setLayoutParams(params);
                }
            } else {
                if (params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mRecyclerView.setLayoutParams(params);
                }
            }
        }

        /**
         *  获取屏幕的高度
         */
        private int getScreenHeight() {
            WindowManager manager = getSystemService(WindowManager.class);
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.heightPixels;
        }
    }

    private static final class MenuAdapter extends MyAdapter<Object> {

        private MenuAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends MyAdapter.ViewHolder {

            private final TextView mTextView;
            private final View mLineView;

            ViewHolder() {
                super(R.layout.menu_item);
                mTextView = (TextView) findViewById(R.id.tv_menu_text);
                mLineView = findViewById(R.id.v_menu_line);
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position).toString());

                if (position == 0) {
                    // 当前是否只有一个条目
                    if (getItemCount() == 1) {
                        mLineView.setVisibility(View.GONE);
                    } else {
                        mLineView.setVisibility(View.VISIBLE);
                    }
                } else if (position == getItemCount() - 1) {
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
        void onSelected(BaseDialog dialog, int position, T t);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}