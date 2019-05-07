package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.base.BaseDialogFragment;
import com.hjq.base.BaseRecyclerViewAdapter;
import com.hjq.demo.R;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengClient;
import com.hjq.umeng.UmengShare;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/23
 *    desc   : 分享对话框
 */
public final class ShareDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements BaseRecyclerViewAdapter.OnItemClickListener {

        private ShareAdapter mAdapter;
        private RecyclerView mRecyclerView;

        private UmengShare.ShareData mData;

        private UmengShare.OnShareListener mListener;

        public Builder(FragmentActivity activity) {
            super(activity);

            mRecyclerView = new RecyclerView(activity);
            mRecyclerView.setBackgroundColor(0xFFF7F7F7);
            mRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            setContentView(mRecyclerView);
            setAnimStyle(BaseDialog.AnimStyle.BOTTOM);
            setGravity(Gravity.BOTTOM);
            setWidth(MATCH_PARENT);

            mData = new UmengShare.ShareData(getActivity());

            final List<ShareBean> data = new ArrayList<>();
            data.add(new ShareBean(getDrawable(R.mipmap.icon_share_wx), getString(R.string.dialog_share_platform_wx), Platform.WEIXIN));
            data.add(new ShareBean(getDrawable(R.mipmap.icon_share_pyq), getString(R.string.dialog_share_platform_wx_pyq), Platform.CIRCLE));
            data.add(new ShareBean(getDrawable(R.mipmap.icon_share_qq), getString(R.string.dialog_share_platform_qq), Platform.QQ));
            data.add(new ShareBean(getDrawable(R.mipmap.icon_share_qqkj), getString(R.string.dialog_share_platform_qq_kj), Platform.QZONE));

            mAdapter = new ShareAdapter(activity);
            mAdapter.setData(data);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, data.size()));
            mRecyclerView.setAdapter(mAdapter);
        }

        public Builder setShareTitle(String title) {
            mData.setShareTitle(title);
            return this;
        }

        public Builder setShareDescription(String description) {
            mData.setShareDescription(description);
            return this;
        }

        public Builder setShareLogo(String url) {
            mData.setShareLogo(url);
            return this;
        }

        public Builder setShareLogo(@DrawableRes int resId) {
            mData.setShareLogo(resId);
            return this;
        }

        public Builder setShareUrl(String url) {
            mData.setShareUrl(url);
            return this;
        }

        public Builder setListener(UmengShare.OnShareListener l) {
            mListener = l;
            return this;
        }

        /**
         * {@link BaseRecyclerViewAdapter.OnItemClickListener}
         */
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            UmengClient.share(getActivity(), mAdapter.getItem(position).getSharePlatform(), mData, mListener);
            dismiss();
        }

//        @Override
//        protected BaseDialog createDialog(Context context, int themeResId) {
//            if (getGravity() == Gravity.BOTTOM) {
//                return new BaseBottomDialog(context, themeResId);
//            }
//            return super.createDialog(context, themeResId);
//        }
    }

    /**
     * 适配器
     */
    private static class ShareAdapter extends BaseRecyclerViewAdapter<ShareBean, ShareAdapter.ViewHolder> {

        private ShareAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(getDrawable(R.drawable.selector_selectable_transparent));
            }else {
                textView.setBackgroundDrawable(getDrawable(R.drawable.selector_selectable_transparent));
            }
            textView.setTextColor(0xFF333333);
            textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()));
            textView.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                    0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ShareBean bean = getItem(position);

            holder.mTextView.setText(bean.getShareName());

            Drawable drawable = bean.getShareIcon();
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mTextView.setCompoundDrawables(null, drawable, null, null);
        }

        final class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

            private TextView mTextView;

            private ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
            }
        }
    }

    private static class ShareBean {

        private Drawable mShareIcon; // 分享图标
        private String mShareName; // 分享名称
        private Platform mSharePlatform; // 分享平台

        private ShareBean(Drawable icon, String name, Platform platform) {
            mShareIcon = icon;
            mShareName = name;
            mSharePlatform = platform;
        }

        private Drawable getShareIcon() {
            return mShareIcon;
        }

        private String getShareName() {
            return mShareName;
        }

        private Platform getSharePlatform() {
            return mSharePlatform;
        }
    }
}