package com.hjq.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.base.BaseRecyclerViewAdapter;
import com.hjq.dialog.widget.PasswordView;

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
            extends BaseDialogFragment.Builder<Builder>
            implements BaseRecyclerViewAdapter.OnItemClickListener, View.OnClickListener {

        // 输入键盘文本
        private static final String[] KEYBOARD_TEXT = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", ""};

        private OnListener mListener;
        private boolean mAutoDismiss = true;

        private PasswordView mPasswordView;
        private RecyclerView mRecyclerView;
        private LinkedList<String> mRecordList = new LinkedList<>();
        private TextView mTitleView;
        private TextView mSubTitleView;
        private TextView mMoneyView;
        private ImageView mCloseView;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_pay_password);
            setGravity(Gravity.BOTTOM);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setCancelable(false);

            mRecyclerView = findViewById(R.id.rv_dialog_pay_list);
            mPasswordView = findViewById(R.id.pw_dialog_pay_view);
            mTitleView = findViewById(R.id.tv_dialog_pay_title);
            mSubTitleView = findViewById(R.id.tv_dialog_pay_sub_title);
            mMoneyView = findViewById(R.id.tv_dialog_pay_money);
            mCloseView = findViewById(R.id.iv_dialog_pay_close);

            mCloseView.setOnClickListener(this);

            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            keyboardAdapter adapter = new keyboardAdapter(getContext());
            adapter.setData(Arrays.asList(KEYBOARD_TEXT));
            adapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(adapter);
        }

        public Builder setTitle(int resId) {
            return setTitle(getContext().getText(resId));
        }

        public Builder setTitle(CharSequence title) {
            if (title != null && !"".equals(title.toString())) {
                mTitleView.setText(title);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
            return this;
        }

        public Builder setSubTitle(int resId) {
            return setSubTitle(getContext().getText(resId));
        }

        public Builder setSubTitle(CharSequence subTitle) {
            if (subTitle != null && !"".equals(subTitle.toString())) {
                mSubTitleView.setText(subTitle);
                mSubTitleView.setVisibility(View.VISIBLE);
            } else {
                mSubTitleView.setVisibility(View.GONE);
            }
            return this;
        }

        public Builder setMoney(int resId) {
            return setSubTitle(getContext().getText(resId));
        }

        public Builder setMoney(CharSequence money) {
            if (money != null && !"".equals(money.toString())) {
                mMoneyView.setText(money);
                mMoneyView.setVisibility(View.VISIBLE);
            } else {
                mMoneyView.setVisibility(View.GONE);
            }
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
         * {@link BaseRecyclerViewAdapter.OnItemClickListener}
         */
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            switch (position) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 10:
                    // 判断密码是否已经输入完毕
                    if (mRecordList.size() < PasswordView.PASSWORD_COUNT) {
                        // 点击数字，显示在密码行
                        mRecordList.add(KEYBOARD_TEXT[position]);
                    }

                    // 判断密码是否已经输入完毕
                    if (mRecordList.size() == PasswordView.PASSWORD_COUNT) {
                        if (mListener != null) {
                            mPasswordView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mAutoDismiss) {
                                        dismiss();
                                    }
                                    // 获取输入的支付密码
                                    StringBuilder password = new StringBuilder();
                                    for (String s : mRecordList) {
                                        password.append(s);
                                    }
                                    mListener.onCompleted(getDialog(), password.toString());
                                }
                            }, 300);
                        }
                    }
                    break;
                case 9:
                    // 点击空白的地方不做任何操作
                    break;
                case 11:
                    // 点击回退按钮删除
                    if (mRecordList.size() != 0) {
                        mRecordList.removeLast();
                    }
                    break;
                default:
                    break;
            }
            mPasswordView.setPassWordLength(mRecordList.size());
        }

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

    private static final class keyboardAdapter extends BaseRecyclerViewAdapter<String, keyboardAdapter.ViewHolder> {

        private keyboardAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public keyboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new keyboardAdapter.ViewHolder(parent, R.layout.item_dialog_pay_password);
        }

        @Override
        public void onBindViewHolder(@NonNull keyboardAdapter.ViewHolder holder, int position) {
            holder.mTextView.setText(getItem(position));

            switch (position) {
                case 9:
                    holder.mTextView.setBackgroundColor(0xFFD5D8DB);
                    holder.mTextView.setVisibility(View.VISIBLE);
                    holder.mImageView.setVisibility(View.GONE);
                    holder.itemView.setBackgroundColor(0xFFECECEC);
                    break;
                case 11:
                    holder.mTextView.setBackgroundColor(0xFFD5D8DB);
                    holder.mTextView.setVisibility(View.GONE);
                    holder.mImageView.setVisibility(View.VISIBLE);
                    holder.itemView.setBackgroundResource(R.drawable.dialog_pay_password_item_del_selector);
                    break;
                default:
                    break;
            }
        }

        final class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

            private TextView mTextView;
            private ImageView mImageView;

            private ViewHolder(ViewGroup parent, int layoutId) {
                super(parent, layoutId);
                mTextView = (TextView) findViewById(R.id.tv_dialog_pay_key);
                mImageView = (ImageView) findViewById(R.id.iv_dialog_pay_delete);
            }
        }
    }

    public interface OnListener {

        /**
         * 输入完成时回调
         *
         * @param password 六位支付密码
         */
        void onCompleted(Dialog dialog, String password);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);
    }
}