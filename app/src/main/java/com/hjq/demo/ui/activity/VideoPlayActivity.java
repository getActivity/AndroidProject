package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.other.IntentKey;
import com.hjq.demo.widget.PlayerView;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/02/16
 *    desc   : 视频播放界面
 */
public final class VideoPlayActivity extends AppActivity
        implements PlayerView.onPlayListener {

    private PlayerView mPlayerView;
    private VideoPlayActivity.Builder mBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.video_play_activity;
    }

    @Override
    protected void initView() {
        mPlayerView = findViewById(R.id.pv_video_play_view);
        mPlayerView.setLifecycleOwner(this);
        mPlayerView.setOnPlayListener(this);
    }

    @Override
    protected void initData() {
        mBuilder = getParcelable(IntentKey.VIDEO);
        if (mBuilder == null) {
            throw new IllegalArgumentException("are you ok?");
        }
        mPlayerView.setVideoTitle(mBuilder.getVideoTitle());
        mPlayerView.setVideoSource(mBuilder.getVideoSource());
        mPlayerView.setGestureEnabled(mBuilder.isGestureEnabled());
        if (mBuilder.isAutoPlay()) {
            mPlayerView.start();
        }
    }

    /**
     * {@link PlayerView.onPlayListener}
     */
    @Override
    public void onClickBack(PlayerView view) {
        onBackPressed();
    }

    @Override
    public void onPlayStart(PlayerView view) {
        int progress = mBuilder.getPlayProgress();
        if (progress > 0) {
            mPlayerView.setProgress(progress);
        }
    }

    @Override
    public void onPlayEnd(PlayerView view) {
        if (mBuilder.isLoopPlay()) {
            mPlayerView.setProgress(0);
            mPlayerView.start();
        } else if (mBuilder.isAutoOver()) {
            finish();
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }

    /**
     * 播放参数构建
     */
    public static final class Builder implements Parcelable {

        /** 视频源 */
        private String mVideoSource;
        /** 视频标题 */
        private String mVideoTitle;
        /** 播放进度 */
        private int mPlayProgress;
        /** 手势开关 */
        private boolean mGestureEnabled = true;
        /** 循环播放 */
        private boolean mLoopPlay = false;
        /** 自动播放 */
        private boolean mAutoPlay = true;
        /** 播放完关闭 */
        private boolean mAutoOver = true;

        public Builder() {}

        protected Builder(Parcel in) {
            mVideoSource = in.readString();
            mVideoTitle = in.readString();
            mPlayProgress = in.readInt();
            mGestureEnabled = in.readByte() != 0;
            mLoopPlay = in.readByte() != 0;
            mAutoPlay = in.readByte() != 0;
            mAutoOver = in.readByte() != 0;
        }

        public Builder setVideoSource(File file) {
            mVideoSource = file.getPath();
            if (mVideoTitle == null) {
                mVideoTitle = file.getName();
            }
            return this;
        }

        public Builder setVideoSource(String url) {
            mVideoSource = url;
            return this;
        }

        private String getVideoSource() {
            return mVideoSource;
        }

        public Builder setVideoTitle(String title) {
            mVideoTitle = title;
            return this;
        }

        private String getVideoTitle() {
            return mVideoTitle;
        }

        public Builder setPlayProgress(int progress) {
            mPlayProgress = progress;
            return this;
        }

        private int getPlayProgress() {
            return mPlayProgress;
        }

        public Builder setGestureEnabled(boolean enabled) {
            mGestureEnabled = enabled;
            return this;
        }

        private boolean isGestureEnabled() {
            return mGestureEnabled;
        }

        public Builder setLoopPlay(boolean enabled) {
            mLoopPlay = enabled;
            return this;
        }

        private boolean isLoopPlay() {
            return mLoopPlay;
        }

        public Builder setAutoPlay(boolean enabled) {
            mAutoPlay = enabled;
            return this;
        }

        public boolean isAutoPlay() {
            return mAutoPlay;
        }

        public Builder setAutoOver(boolean enabled) {
            mAutoOver = enabled;
            return this;
        }

        private boolean isAutoOver() {
            return mAutoOver;
        }

        public void start(Context context) {
            Intent intent = new Intent(context, VideoPlayActivity.class);
            intent.putExtra(IntentKey.VIDEO, this);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mVideoSource);
            dest.writeString(mVideoTitle);
            dest.writeInt(mPlayProgress);
            dest.writeByte(mGestureEnabled ? (byte) 1 : (byte) 0);
            dest.writeByte(mLoopPlay ? (byte) 1 : (byte) 0);
            dest.writeByte(mAutoPlay ? (byte) 1 : (byte) 0);
            dest.writeByte(mAutoOver ? (byte) 1 : (byte) 0);
        }

        public static final Parcelable.Creator<Builder> CREATOR = new Parcelable.Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
    }
}