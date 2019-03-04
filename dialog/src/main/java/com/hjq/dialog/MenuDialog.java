package com.hjq.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.base.BaseRecyclerViewAdapter;

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
            extends BaseDialogFragment.Builder<Builder>
            implements View.OnClickListener, BaseRecyclerViewAdapter.OnItemClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true;

        private RecyclerView mRecyclerView;
        private MenuAdapter mAdapter;
        private TextView mCancelView;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_menu);
            setGravity(Gravity.BOTTOM);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

            mRecyclerView = findViewById(R.id.rv_dialog_menu_list);
            mCancelView  = findViewById(R.id.tv_dialog_menu_cancel);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = new MenuAdapter(getContext());
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);

            mCancelView.setOnClickListener(this);
        }

        public Builder setList(String... data) {
            return setList(Arrays.asList(data));
        }

        public Builder setList(List<String> data) {
            mAdapter.setData(data);
            return this;
        }

        public Builder setCancel(int resId) {
            return setCancel(getContext().getText(resId));
        }
        public Builder setCancel(CharSequence text) {
            mCancelView.setText(text);
            mCancelView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            return this;
        }

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public Builder setListener(OnListener l) {
            mListener = l;
            return this;
        }

        /**
         * {@link View.OnClickListener}
         */
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
         * {@link BaseRecyclerViewAdapter.OnItemClickListener}
         */
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                mListener.onSelected(getDialog(), position, mAdapter.getItem(position));
            }
        }
    }

    private static final class MenuAdapter extends BaseRecyclerViewAdapter<String, MenuAdapter.ViewHolder> {

        MenuAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(parent, R.layout.item_dialog_menu);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mTextView.setText(getItem(position));

            if (position == 0) {
                // 当前是否只有一个条目
                if (getItemCount() == 1) {
                    holder.itemView.setBackgroundResource(R.drawable.dialog_menu_item);
                    holder.mView.setVisibility(View.GONE);
                }else {
                    holder.itemView.setBackgroundResource(R.drawable.dialog_menu_item_top);
                    holder.mView.setVisibility(View.VISIBLE);
                }
            }else if (position == getItemCount() - 1) {
                holder.itemView.setBackgroundResource(R.drawable.dialog_menu_item_bottom);
                holder.mView.setVisibility(View.GONE);
            }else {
                holder.itemView.setBackgroundResource(R.drawable.dialog_menu_item_middle);
                holder.mView.setVisibility(View.VISIBLE);
            }
        }

        final class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

            private TextView mTextView;
            private View mView;

            private ViewHolder(ViewGroup parent, int layoutId) {
                super(parent, layoutId);
                mTextView = (TextView) findViewById(R.id.tv_dialog_menu_name);
                mView = findViewById(R.id.v_dialog_menu_line);
            }
        }
    }

    public interface OnListener {

        /**
         * 选择条目时回调
         */
        void onSelected(Dialog dialog, int position, String text);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}