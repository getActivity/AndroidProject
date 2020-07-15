package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.common.MyAdapter;
import com.hjq.demo.widget.PasswordView;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/2
 *    desc   : 支付密码对话框
 */
public final class PayPasswordDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        /** 输入键盘文本 */
        private static final String[] KEYBOARD_TEXT = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", ""};

        private OnListener mListener;
        private boolean mAutoDismiss = true;
        private final LinkedList<String> mRecordList = new LinkedList<>();

        private final TextView mTitleView;
        private final ImageView mCloseView;

        private final TextView mSubTitleView;
        private final TextView mMoneyView;

        private final PasswordView mPasswordView;
        private final RecyclerView mRecyclerView;
        private final KeyboardAdapter mAdapter;

        public Builder(Context context) {
            super(context);
            setContentView(R.layout.pay_password_dialog);
            setCancelable(false);

            mTitleView = findViewById(R.id.tv_pay_title);
            mCloseView = findViewById(R.id.iv_pay_close);
            mSubTitleView = findViewById(R.id.tv_pay_sub_title);
            mMoneyView = findViewById(R.id.tv_pay_money);
            mPasswordView = findViewById(R.id.pw_pay_view);
            mRecyclerView = findViewById(R.id.rv_pay_list);
            setOnClickListener(mCloseView);

            mAdapter = new KeyboardAdapter(getContext());
            mAdapter.setData(Arrays.asList(KEYBOARD_TEXT));
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        public Builder setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }

        public Builder setTitle(CharSequence title) {
            mTitleView.setText(title);
            return this;
        }

        public Builder setSubTitle(@StringRes int id) {
            return setSubTitle(getString(id));
        }

        public Builder setSubTitle(CharSequence subTitle) {
            mSubTitleView.setText(subTitle);
            return this;
        }

        public Builder setMoney(@StringRes int id) {
            return setSubTitle(getString(id));
        }

        public Builder setMoney(CharSequence money) {
            mMoneyView.setText(money);
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

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            switch (mAdapter.getItemViewType(position)) {
                case KeyboardAdapter.TYPE_DELETE:
                    // 点击回退按钮删除
                    if (mRecordList.size() != 0) {
                        mRecordList.removeLast();
                    }
                    break;
                case KeyboardAdapter.TYPE_EMPTY:
                    // 点击空白的地方不做任何操作
                    break;
                default:
                    // 判断密码是否已经输入完毕
                    if (mRecordList.size() < PasswordView.PASSWORD_COUNT) {
                        // 点击数字，显示在密码行
                        mRecordList.add(KEYBOARD_TEXT[position]);
                    }

                    // 判断密码是否已经输入完毕
                    if (mRecordList.size() == PasswordView.PASSWORD_COUNT) {
                        if (mListener != null) {
                            postDelayed(() -> {

                                if (mAutoDismiss) {
                                    dismiss();
                                }
                                // 获取输入的支付密码
                                StringBuilder password = new StringBuilder();
                                for (String s : mRecordList) {
                                    password.append(s);
                                }
                                mListener.onCompleted(getDialog(), password.toString());

                            }, 300);
                        }
                    }
                    break;
            }
            mPasswordView.setPassWordLength(mRecordList.size());
        }

        @SingleClick
        @Override
        public void onClick(View v) {
            if (v == mCloseView) {
                if (mAutoDismiss) {
                    dismiss();
                }

                if (mListener != null) {
                    mListener.onCancel(getDialog());
                }
            }
        }
    }

    private static final class KeyboardAdapter extends MyAdapter<String> {

        /** 数字按钮条目 */
        private static final int TYPE_NORMAL = 0;
        /** 删除按钮条目 */
        private static final int TYPE_DELETE = 1;
        /** 空按钮条目 */
        private static final int TYPE_EMPTY = 2;

        private KeyboardAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 9:
                    return TYPE_EMPTY;
                case 11:
                    return TYPE_DELETE;
                default:
                    return TYPE_NORMAL;
            }
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_DELETE:
                    return new MyAdapter.SimpleHolder(R.layout.pay_password_delete_item);
                case TYPE_EMPTY:
                    return new MyAdapter.SimpleHolder(R.layout.pay_password_empty_item);
                default:
                    return new KeyboardAdapter.ViewHolder();
            }
        }

        private final class ViewHolder extends MyAdapter.ViewHolder {

            private final TextView mTextView;

            private ViewHolder() {
                super(R.layout.pay_password_normal_item);
                mTextView = (TextView) getItemView();
            }

            @Override
            public void onBindView(int position) {
                mTextView.setText(getItem(position));
            }
        }

        @Override
        protected RecyclerView.LayoutManager generateDefaultLayoutManager(Context context) {
            return new GridLayoutManager(getContext(), 3);
        }
    }

    public interface OnListener {

        /**
         * 输入完成时回调
         *
         * @param password      输入的密码
         */
        void onCompleted(BaseDialog dialog, String password);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}