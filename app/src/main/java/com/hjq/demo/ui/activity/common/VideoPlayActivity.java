package com.hjq.demo.ui.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.widget.PlayerView;
import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/02/16
 *    desc   : 视频播放界面
 */
public class VideoPlayActivity extends AppActivity
        implements PlayerView.OnPlayListener {

    public static final String INTENT_KEY_PARAMETERS = "parameters";

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
        mBuilder = getParcelable(INTENT_KEY_PARAMETERS);
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
     * {@link PlayerView.OnPlayListener}
     */
    @Override
    public void onClickBack(@NonNull PlayerView view) {
        onBackPressed();
    }

    @Override
    public void onPlayStart(@NonNull PlayerView view) {
        int progress = mBuilder.getPlayProgress();
        if (progress > 0) {
            mPlayerView.setProgress(progress);
        }
    }

    @Override
    public void onPlayProgress(@NonNull PlayerView view) {
        // 记录播放进度
        mBuilder.setPlayProgress(view.getProgress());
    }

    @Override
    public void onPlayEnd(@NonNull PlayerView view) {
        if (mBuilder.isLoopPlay()) {
            mPlayerView.setProgress(0);
            mPlayerView.start();
            return;
        }

        if (mBuilder.isAutoOver()) {
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存播放进度
        outState.putParcelable(INTENT_KEY_PARAMETERS, mBuilder);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 读取播放进度
        mBuilder = savedInstanceState.getParcelable(INTENT_KEY_PARAMETERS);
    }

    /** 竖屏播放 */
    public static final class Portrait extends VideoPlayActivity {}

    /** 横屏播放 */
    public static final class Landscape extends VideoPlayActivity {}

    /**
     * 播放参数构建
     */
    public static final class Builder implements Parcelable {

        /** 视频源 */
        private String videoSource;
        /** 视频标题 */
        private String videoTitle;

        /** 播放进度 */
        private int playProgress;
        /** 手势开关 */
        private boolean gestureEnabled = true;
        /** 循环播放 */
        private boolean loopPlay = false;
        /** 自动播放 */
        private boolean autoPlay = true;
        /** 播放完关闭 */
        private boolean autoOver = true;

        /** 播放方向 */
        private int activityOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        public Builder() {}

        protected Builder(Parcel in) {
            videoSource = in.readString();
            videoTitle = in.readString();
            activityOrientation = in.readInt();

            playProgress = in.readInt();
            gestureEnabled = in.readByte() != 0;
            loopPlay = in.readByte() != 0;
            autoPlay = in.readByte() != 0;
            autoOver = in.readByte() != 0;
        }

        public Builder setVideoSource(File file) {
            videoSource = file.getPath();
            if (videoTitle == null) {
                videoTitle = file.getName();
            }
            return this;
        }

        public Builder setVideoSource(String url) {
            videoSource = url;
            return this;
        }

        private String getVideoSource() {
            return videoSource;
        }

        public Builder setVideoTitle(String title) {
            videoTitle = title;
            return this;
        }

        private String getVideoTitle() {
            return videoTitle;
        }

        public Builder setPlayProgress(int progress) {
            playProgress = progress;
            return this;
        }

        private int getPlayProgress() {
            return playProgress;
        }

        public Builder setGestureEnabled(boolean enabled) {
            gestureEnabled = enabled;
            return this;
        }

        private boolean isGestureEnabled() {
            return gestureEnabled;
        }

        public Builder setLoopPlay(boolean enabled) {
            loopPlay = enabled;
            return this;
        }

        private boolean isLoopPlay() {
            return loopPlay;
        }

        public Builder setAutoPlay(boolean enabled) {
            autoPlay = enabled;
            return this;
        }

        public boolean isAutoPlay() {
            return autoPlay;
        }

        public Builder setAutoOver(boolean enabled) {
            autoOver = enabled;
            return this;
        }

        private boolean isAutoOver() {
            return autoOver;
        }

        public Builder setActivityOrientation(int orientation) {
            activityOrientation = orientation;
            return this;
        }

        public void start(@NonNull Context context) {
            Intent intent = new Intent();
            switch (activityOrientation) {
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    intent.setClass(context, VideoPlayActivity.Landscape.class);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    intent.setClass(context, VideoPlayActivity.Portrait.class);
                    break;
                default:
                    intent.setClass(context, VideoPlayActivity.class);
                    break;
            }

            intent.putExtra(INTENT_KEY_PARAMETERS, this);
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
            dest.writeString(videoSource);
            dest.writeString(videoTitle);
            dest.writeInt(activityOrientation);
            dest.writeInt(playProgress);
            dest.writeByte(gestureEnabled ? (byte) 1 : (byte) 0);
            dest.writeByte(loopPlay ? (byte) 1 : (byte) 0);
            dest.writeByte(autoPlay ? (byte) 1 : (byte) 0);
            dest.writeByte(autoOver ? (byte) 1 : (byte) 0);
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