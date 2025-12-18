package com.hjq.demo.ui.dialog.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.toast.Toaster;
import com.hjq.umeng.sdk.Platform;
import com.hjq.umeng.sdk.UmengClient;
import com.hjq.umeng.sdk.UmengShare;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMQQMini;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;
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
            extends BaseDialog.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        @NonNull
        private final RecyclerView mRecyclerView;
        @NonNull
        private final ShareAdapter mAdapter;

        @NonNull
        private final ShareAction mShareAction;
        @NonNull
        private final ShareBean mCopyLink;

        @Nullable
        private UmengShare.OnShareListener mListener;

        public Builder(@NonNull Activity activity) {
            super(activity);

            setContentView(R.layout.share_dialog);

            final List<ShareBean> data = new ArrayList<>();
            data.add(new ShareBean(Platform.WECHAT, getString(R.string.share_platform_wechat), getDrawable(R.drawable.share_wechat_ic)));
            data.add(new ShareBean(Platform.CIRCLE, getString(R.string.share_platform_moment), getDrawable(R.drawable.share_moment_ic)));
            data.add(new ShareBean(Platform.QQ, getString(R.string.share_platform_qq), getDrawable(R.drawable.share_qq_ic)));
            data.add(new ShareBean(Platform.QZONE, getString(R.string.share_platform_qzone), getDrawable(R.drawable.share_qzone_ic)));

            mCopyLink = new ShareBean(null, getString(R.string.share_platform_link), getDrawable(R.drawable.share_link_ic));

            mAdapter = new ShareAdapter(activity);
            mAdapter.setData(data);
            mAdapter.setOnItemClickListener(this);

            mRecyclerView = findViewById(R.id.rv_share_list);
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, data.size()));
            mRecyclerView.setAdapter(mAdapter);

            mShareAction = new ShareAction(activity);
        }

        /**
         * 分享网页链接：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu7F51u9875u94FEu63A51
         */
        public Builder setShareLink(@NonNull UMWeb content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享图片：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu56FEu72473
         */
        public Builder setShareImage(@NonNull UMImage content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享纯文本：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu7EAFu6587u672C5
         */
        public Builder setShareText(@NonNull String content) {
            mShareAction.withText(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享音乐：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu97F3u4E507
         */
        public Builder setShareMusic(@NonNull UMusic content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享视频：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu89C6u98916
         */
        public Builder setShareVideo(@NonNull UMVideo content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享 Gif 表情：https://developer.umeng.com/docs/128606/detail/193883#h2--gif-8
         */
        public Builder setShareEmoji(@NonNull UMEmoji content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享微信小程序：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu5C0Fu7A0Bu5E8F2
         */
        public Builder setShareMin(@NonNull UMMin content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 分享 QQ 小程序：https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu5C0Fu7A0Bu5E8F2
         */
        public Builder setShareMin(@NonNull UMQQMini content) {
            mShareAction.withMedia(content);
            refreshShareOptions();
            return this;
        }

        /**
         * 设置回调监听器
         */
        public Builder setListener(@Nullable UmengShare.OnShareListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @Override
        public void onItemClick(@NonNull RecyclerView recyclerView, @NonNull View itemView, int position) {
            Platform platform = mAdapter.getItem(position).platform;
            if (platform != null) {
                if (getContext().getPackageName().endsWith(".debug") &&
                        (platform == Platform.WECHAT || platform == Platform.CIRCLE)) {
                    Toaster.show("当前 buildType 不支持进行微信分享");
                    return;
                }

                Activity activity = getActivity();
                if (activity != null) {
                    UmengClient.share(getActivity(), platform, mShareAction, mListener);
                }
            } else {
                if (mShareAction.getShareContent().getShareType() == ShareContent.WEB_STYLE) {
                    ClipboardManager clipboardManager = getSystemService(ClipboardManager.class);
                    if (clipboardManager != null) {
                        // 复制到剪贴板
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("url", mShareAction.getShareContent().mMedia.toUrl()));
                        Toaster.show(R.string.share_platform_copy_hint);
                    }
                }
            }
            dismiss();
        }

        /**
         * 刷新分享选项
         */
        private void refreshShareOptions() {
            if (mShareAction.getShareContent().getShareType() == ShareContent.WEB_STYLE) {
                if (!mAdapter.containsItem(mCopyLink)) {
                    mAdapter.addItem(mCopyLink);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mAdapter.getCount()));
                }
            } else {
                if (mAdapter.containsItem(mCopyLink)) {
                    mAdapter.removeItem(mCopyLink);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mAdapter.getCount()));
                }
            }
        }
    }

    private static class ShareAdapter extends AppAdapter<ShareBean> {

        private ShareAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppViewHolder {

            @NonNull
            private final ImageView mImageView;
            @NonNull
            private final TextView mTextView;

            private ViewHolder() {
                super(R.layout.share_item);
                mImageView = findViewById(R.id.iv_share_image);
                mTextView = findViewById(R.id.tv_share_text);
            }

            @Override
            public void onBindView(int position) {
                ShareBean bean = getItem(position);
                mImageView.setImageDrawable(bean.icon);
                mTextView.setText(bean.name);
            }
        }
    }

    private static class ShareBean {

        /** 分享平台 */
        @Nullable
        final Platform platform;

        /** 分享名称 */
        @NonNull
        final String name;

        /** 分享图标 */
        @NonNull
        final Drawable icon;

        private ShareBean(@Nullable Platform platform, @NonNull String name, @NonNull Drawable icon) {
            this.platform = platform;
            this.name = name;
            this.icon = icon;
        }
    }
}