package com.hjq.demo.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import com.airbnb.lottie.LottieAnimationView;
import com.hjq.core.action.ActivityAction;
import com.hjq.custom.widget.layout.SimpleLayout;
import com.hjq.custom.widget.view.PlayButton;
import com.hjq.demo.R;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import java.io.File;
import java.util.Formatter;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/16
 *    desc   : 视频播放控件
 */
public final class PlayerView extends SimpleLayout
        implements LifecycleEventObserver,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, ActivityAction,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    /** 刷新间隔 */
    private static final int REFRESH_TIME = 1000;
    /** 面板隐藏间隔 */
    private static final int CONTROLLER_TIME = 3000;
    /** 提示对话框隐藏间隔 */
    private static final int DIALOG_TIME = 500;
    /** 动画执行时间 */
    private static final int ANIM_TIME = 500;

    private final ViewGroup mTopLayout;
    private final TextView mTitleView;
    private final ImageView mBackView;

    private final ViewGroup mBottomLayout;
    private final TextView mPlayTime;
    private final TextView mTotalTime;
    private final SeekBar mProgressView;

    private final VideoView mVideoView;
    private final PlayButton mControlView;
    private final ImageView mLockView;

    private ViewGroup mMessageLayout;
    private final LottieAnimationView mLottieView;
    private final TextView mMessageView;

    /** 视频宽度 */
    private int mVideoWidth;
    /** 视频高度 */
    private int mVideoHeight;

    /** 锁定面板 */
    private boolean mLockMode;
    /** 显示面板 */
    private boolean mControllerShow = false;

    /** 触摸按下的 X 坐标 */
    private float mViewDownX;
    /** 触摸按下的 Y 坐标 */
    private float mViewDownY;
    /** 手势开关 */
    private boolean mGestureEnabled;
    /** 当前播放进度 */
    private int mCurrentProgress;
    /** 返回监听器 */
    @Nullable
    private OnPlayListener mListener;

    /** 音量管理器 */
    @Nullable
    private final AudioManager mAudioManager;
    /** 最大音量值 */
    private int mMaxVoice;
    /** 当前音量值 */
    private int mCurrentVolume;
    /** 当前亮度值百分比 */
    private float mCurrentBrightnessPercent;
    /** 当前窗口对象 */
    @Nullable
    private Window mWindow;
    /** 调整秒数 */
    private int mAdjustSecond;
    /** 触摸方向 */
    private int mTouchOrientation = -1;

    /**
     * 刷新任务
     */
    private final Runnable mRefreshRunnable = new Runnable() {

        @Override
        public void run() {
            int progress = mVideoView.getCurrentPosition();
            // 这里优化了播放的秒数计算，将 800 毫秒估算成 1 秒
            if (progress + 1000 < mVideoView.getDuration()) {
                // 进行四舍五入计算
                progress = Math.round(progress / 1000f) * 1000;
            }
            mPlayTime.setText(conversionTime(progress));
            mProgressView.setProgress(progress);
            mProgressView.setSecondaryProgress((int) (mVideoView.getBufferPercentage() / 100f * mVideoView.getDuration()));
            if (mVideoView.isPlaying()) {
                if (!mLockMode && mBottomLayout.getVisibility() == GONE) {
                    mBottomLayout.setVisibility(VISIBLE);
                }
                if (!mVideoView.getKeepScreenOn()) {
                    mVideoView.setKeepScreenOn(true);
                }
            } else {
                if (mBottomLayout.getVisibility() == VISIBLE) {
                    mBottomLayout.setVisibility(GONE);
                }
                if (mVideoView.getKeepScreenOn()) {
                    mVideoView.setKeepScreenOn(false);
                }
            }
            postDelayed(this, REFRESH_TIME);

            if (mListener == null) {
                return;
            }
            mListener.onPlayProgress(PlayerView.this);
        }
    };

    /**
     * 显示控制面板
     */
    private final Runnable mShowControllerRunnable = () -> {
        if (!mControllerShow) {
            showController();
        }
    };

    /**
     * 隐藏控制面板
     */
    private final Runnable mHideControllerRunnable = () -> {
        if (mControllerShow) {
            hideController();
        }
    };

    /**
     * 显示提示
     */
    private final Runnable mShowMessageRunnable = () -> {
        hideController();
        mMessageLayout.setVisibility(VISIBLE);
    };

    /**
     * 隐藏提示
     */
    private final Runnable mHideMessageRunnable = () -> mMessageLayout.setVisibility(GONE);

    public PlayerView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater.from(getContext()).inflate(R.layout.widget_player_view, this, true);
        mTopLayout = findViewById(R.id.ll_player_view_top);
        mBackView = findViewById(R.id.iv_player_view_left);
        mTitleView = findViewById(R.id.tv_player_view_title);

        mBottomLayout = findViewById(R.id.ll_player_view_bottom);
        mPlayTime = findViewById(R.id.tv_player_view_play_time);
        mTotalTime = findViewById(R.id.tv_player_view_total_time);
        mProgressView = findViewById(R.id.sb_player_view_progress);

        mVideoView = findViewById(R.id.vv_player_view_video);
        mLockView = findViewById(R.id.iv_player_view_lock);
        mControlView = findViewById(R.id.iv_player_view_control);

        mMessageLayout = findViewById(R.id.cv_player_view_message);
        mLottieView = findViewById(R.id.lav_player_view_lottie);
        mMessageView = findViewById(R.id.tv_player_view_message);

        mBackView.setOnClickListener(this);
        mControlView.setOnClickListener(this);
        mLockView.setOnClickListener(this);
        this.setOnClickListener(this);

        mProgressView.setOnSeekBarChangeListener(this);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnErrorListener(this);

        mAudioManager = ContextCompat.getSystemService(getContext(), AudioManager.class);

        Drawable backIconDrawable;
        // 注意这里不要用 View 的 getLayoutDirection() 来判断，因为获取到的不准确
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            backIconDrawable = ContextCompat.getDrawable(context, R.drawable.arrows_right_ic);
        } else {
            backIconDrawable = ContextCompat.getDrawable(context, R.drawable.arrows_left_ic);
        }
        mBackView.setImageDrawable(backIconDrawable);

        Activity activity = getActivity();
        if (activity != null) {
            mWindow = activity.getWindow();
        }
    }

    /**
     * 设置播放器生命管控（自动回调生命周期方法）
     */
    public void setLifecycleOwner(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    /**
     * {@link LifecycleEventObserver}
     */

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                onResume();
                break;
            case ON_PAUSE:
                onPause();
                break;
            case ON_DESTROY:
                onDestroy();
                break;
            default:
                break;
        }
    }

    /**
     * 设置视频标题
     */
    public void setVideoTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        mTitleView.setText(title);
    }

    /**
     * 设置视频源
     */
    public void setVideoSource(File file) {
        if (file == null || !file.isFile()) {
            return;
        }
        mVideoView.setVideoPath(file.getPath());
    }

    public void setVideoSource(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mVideoView.setVideoURI(Uri.parse(url));
    }

    /**
     * 开始播放
     */
    public void start() {
        mVideoView.start();
        mControlView.play();
        // 延迟隐藏控制面板
        removeCallbacks(mHideControllerRunnable);
        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mVideoView.pause();
        mControlView.pause();
        // 延迟隐藏控制面板
        removeCallbacks(mHideControllerRunnable);
        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
    }

    /**
     * 锁定控制面板
     */
    public void lock() {
        mLockMode = true;
        mLockView.setImageResource(R.drawable.video_lock_close_ic);
        mTopLayout.setVisibility(GONE);
        mBottomLayout.setVisibility(GONE);
        mControlView.setVisibility(GONE);
        // 延迟隐藏控制面板
        removeCallbacks(mHideControllerRunnable);
        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
    }

    /**
     * 解锁控制面板
     */
    public void unlock() {
        mLockMode = false;
        mLockView.setImageResource(R.drawable.video_lock_open_ic);
        mTopLayout.setVisibility(VISIBLE);
        if (mVideoView.isPlaying()) {
            mBottomLayout.setVisibility(VISIBLE);
        }
        mControlView.setVisibility(VISIBLE);
        // 延迟隐藏控制面板
        removeCallbacks(mHideControllerRunnable);
        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    /**
     * 设置视频播放进度
     */
    public void setProgress(int progress) {
        if (progress > mVideoView.getDuration()) {
            progress = mVideoView.getDuration();
        }
        // 要跳转的进度必须和当前播放进度相差 1 秒以上
        if (Math.abs(progress - mVideoView.getCurrentPosition()) > 1000) {
            mVideoView.seekTo(progress);
            mProgressView.setProgress(progress);
        }
    }

    /**
     * 获取视频播放进度
     */
    public int getProgress() {
        return mVideoView.getCurrentPosition();
    }

    /**
     * 获取视频的总进度
     */
    public int getDuration() {
        return mVideoView.getDuration();
    }

    /**
     * 设置手势开关
     */
    public void setGestureEnabled(boolean enabled) {
        mGestureEnabled = enabled;
    }

    /**
     * 设置返回监听
     */
    public void setOnPlayListener(@Nullable OnPlayListener listener) {
        mListener = listener;
        mBackView.setVisibility(mListener != null ? VISIBLE : INVISIBLE);
    }

    /**
     * 显示面板
     */
    public void showController() {
        if (mControllerShow) {
            return;
        }

        mControllerShow = true;

        ValueAnimator topAnimator = ValueAnimator.ofInt(-mTopLayout.getHeight(), 0);
        topAnimator.setDuration(ANIM_TIME);
        topAnimator.addUpdateListener(animation -> {
            int translationY = (int) animation.getAnimatedValue();
            mTopLayout.setTranslationY(translationY);
            if (translationY != -mTopLayout.getHeight()) {
                return;
            }

            if (mTopLayout.getVisibility() == INVISIBLE) {
                mTopLayout.setVisibility(VISIBLE);
            }
        });
        topAnimator.start();

        ValueAnimator bottomAnimator = ValueAnimator.ofInt(mBottomLayout.getHeight(), 0);
        bottomAnimator.setDuration(ANIM_TIME);
        bottomAnimator.addUpdateListener(animation -> {
            int translationY = (int) animation.getAnimatedValue();
            mBottomLayout.setTranslationY(translationY);
            if (translationY != mBottomLayout.getHeight()) {
                return;
            }

            if (mBottomLayout.getVisibility() == INVISIBLE) {
                mBottomLayout.setVisibility(VISIBLE);
            }
        });
        bottomAnimator.start();

        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0f, 1f);
        alphaAnimator.setDuration(ANIM_TIME);
        alphaAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            mLockView.setAlpha(alpha);
            mControlView.setAlpha(alpha);
            if (alpha != 0) {
                return;
            }

            if (mLockView.getVisibility() == INVISIBLE) {
                mLockView.setVisibility(VISIBLE);
            }
            if (mControlView.getVisibility() == INVISIBLE) {
                mControlView.setVisibility(VISIBLE);
            }
        });
        alphaAnimator.start();
    }

    /**
     * 隐藏面板
     */
    public void hideController() {
        if (!mControllerShow) {
            return;
        }

        mControllerShow = false;

        ValueAnimator topAnimator = ValueAnimator.ofInt(0, -mTopLayout.getHeight());
        topAnimator.setDuration(ANIM_TIME);
        topAnimator.addUpdateListener(animation -> {
            int translationY = (int) animation.getAnimatedValue();
            mTopLayout.setTranslationY(translationY);
            if (translationY != -mTopLayout.getHeight()) {
                return;
            }

            if (mTopLayout.getVisibility() == VISIBLE) {
                mTopLayout.setVisibility(INVISIBLE);
            }
        });
        topAnimator.start();

        ValueAnimator bottomAnimator = ValueAnimator.ofInt(0, mBottomLayout.getHeight());
        bottomAnimator.setDuration(ANIM_TIME);
        bottomAnimator.addUpdateListener(animation -> {
            int translationY = (int) animation.getAnimatedValue();
            mBottomLayout.setTranslationY(translationY);
            if (translationY != mBottomLayout.getHeight()) {
                return;
            }

            if (mBottomLayout.getVisibility() == VISIBLE) {
                mBottomLayout.setVisibility(INVISIBLE);
            }
        });
        bottomAnimator.start();

        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(1f, 0f);
        alphaAnimator.setDuration(ANIM_TIME);
        alphaAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
            mLockView.setAlpha(alpha);
            mControlView.setAlpha(alpha);
            if (alpha != 0) {
                return;
            }

            if (mLockView.getVisibility() == VISIBLE) {
                mLockView.setVisibility(INVISIBLE);
            }
            if (mControlView.getVisibility() == VISIBLE) {
                mControlView.setVisibility(INVISIBLE);
            }
        });
        alphaAnimator.start();
    }

    public void onResume() {
        mVideoView.resume();
    }

    public void onPause() {
        mVideoView.suspend();
        pause();
    }

    public void onDestroy() {
        mVideoView.stopPlayback();
        removeCallbacks(mRefreshRunnable);
        removeCallbacks(mShowControllerRunnable);
        removeCallbacks(mHideControllerRunnable);
        removeCallbacks(mShowMessageRunnable);
        removeCallbacks(mHideMessageRunnable);
        removeAllViews();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        // 这里解释一下 onWindowVisibilityChanged 方法调用的时机
        // 从前台返回到后台：先调用 onWindowVisibilityChanged(View.INVISIBLE) 后调用 onWindowVisibilityChanged(View.GONE)
        // 从后台返回到前台：先调用 onWindowVisibilityChanged(View.INVISIBLE) 后调用 onWindowVisibilityChanged(View.VISIBLE)
        super.onWindowVisibilityChanged(visibility);
        // 这里修复了 Activity 从后台返回到前台时 VideoView 从头开始播放的问题
        if (visibility != VISIBLE) {
            return;
        }
        mVideoView.seekTo(mCurrentProgress);
        mProgressView.setProgress(mCurrentProgress);
    }

    /**
     * {@link SeekBar.OnSeekBarChangeListener}
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPlayTime.setText(conversionTime(progress));
            return;
        }

        if (progress != 0) {
            // 记录当前播放进度
            mCurrentProgress = progress;
            return;
        }

        // 如果 Activity 返回到后台，progress 会等于 0，而 mVideoView.getDuration 会等于 -1
        // 所以要避免在这种情况下记录当前的播放进度，以便用户从后台返回到前台的时候恢复正确的播放进度
        if (mVideoView.getDuration() > 0) {
            mCurrentProgress = 0;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        removeCallbacks(mRefreshRunnable);
        removeCallbacks(mHideControllerRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        postDelayed(mRefreshRunnable, REFRESH_TIME);
        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
        // 设置选择的播放进度
        setProgress(seekBar.getProgress());
    }

    /**
     * {@link MediaPlayer.OnPreparedListener}
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        mPlayTime.setText(conversionTime(0));
        mTotalTime.setText(conversionTime(player.getDuration()));
        mProgressView.setMax(mVideoView.getDuration());

        // 获取视频的宽高
        mVideoWidth = player.getVideoWidth();
        mVideoHeight = player.getVideoHeight();

        // VideoView 的宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // 基于比例调整大小
        if (mVideoWidth * viewHeight < viewWidth * mVideoHeight) {
            // 视频宽度过大，进行纠正
            viewWidth = viewHeight * mVideoWidth / mVideoHeight;
        } else if (mVideoWidth * viewHeight > viewWidth * mVideoHeight) {
            // 视频高度过大，进行纠正
            viewHeight = viewWidth * mVideoHeight / mVideoWidth;
        }

        // 重新设置 VideoView 的宽高
        ViewGroup.LayoutParams params = mVideoView.getLayoutParams();
        params.width = viewWidth;
        params.height = viewHeight;
        mVideoView.setLayoutParams(params);

        post(mShowControllerRunnable);
        postDelayed(mRefreshRunnable, REFRESH_TIME / 2);

        if (mListener == null) {
            return;
        }
        mListener.onPlayStart(this);
    }

    /**
     * {@link MediaPlayer.OnCompletionListener}
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        pause();
        if (mListener == null) {
            return;
        }
        mListener.onPlayEnd(this);
    }

    /**
     * {@link MediaPlayer.OnInfoListener}
     */
    @Override
    public boolean onInfo(MediaPlayer player, int what, int extra) {
        switch (what) {
            // 视频播放卡顿开始
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                mLottieView.setAnimation(R.raw.progress);
                mLottieView.playAnimation();
                mMessageView.setText(R.string.common_loading);
                post(mShowMessageRunnable);
                return true;
            // 视频播放卡顿结束
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mLottieView.cancelAnimation();
                mMessageView.setText(R.string.common_loading);
                postDelayed(mHideMessageRunnable, DIALOG_TIME);
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * {@link MediaPlayer.OnErrorListener}
     */

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        if (mListener != null && mListener.onPlayError(this, what, extra)) {
            return true;
        }

        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        String message;
        if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
            message = activity.getString(R.string.common_video_error_not_support);
        } else {
            message = activity.getString(R.string.common_video_error_unknown);
        }
        message += "\n" + String.format(activity.getString(R.string.common_video_error_supplement), what, extra);

        new MessageDialog.Builder(activity)
                .setMessage(message)
                .setConfirm(R.string.common_confirm)
                .setCancel(null)
                .setCancelable(false)
                .setListener(dialog -> onCompletion(player))
                .show();
        return true;
    }

    /**
     * {@link View.OnClickListener}
     */

    @Override
    public void onClick(@NonNull View view) {
        if (view == this) {

            // 先移除之前发送的
            removeCallbacks(mShowControllerRunnable);
            removeCallbacks(mHideControllerRunnable);

            if (mControllerShow) {
                // 隐藏控制面板
                post(mHideControllerRunnable);
                return;
            }

            // 显示控制面板
            post(mShowControllerRunnable);
            postDelayed(mHideControllerRunnable, CONTROLLER_TIME);

        } else if (view == mBackView) {

            if (mListener == null) {
                return;
            }
            mListener.onClickBack(this);

        } else if (view == mControlView) {

            if (mControlView.getVisibility() != VISIBLE) {
                return;
            }

            if (isPlaying()) {
                pause();
            } else {
                start();
            }
            // 先移除之前发送的
            removeCallbacks(mShowControllerRunnable);
            removeCallbacks(mHideControllerRunnable);
            // 重置显示隐藏面板任务
            if (!mControllerShow) {
                post(mShowControllerRunnable);
            }
            postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
            if (mListener == null) {
                return;
            }
            mListener.onClickPlay(this);

        } else if (view == mLockView) {

            if (mLockMode) {
                unlock();
            } else {
                lock();
            }
            if (mListener == null) {
                return;
            }
            mListener.onClickLock(this);
        }
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // 满足任一条件：关闭手势控制、处于锁定状态、处于缓冲状态
        if (!mGestureEnabled || mLockMode || mLottieView.isAnimating()) {
            return super.onTouchEvent(event);
        }

        int layoutDirection = getResources().getConfiguration().getLayoutDirection();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mAudioManager != null) {
                    mMaxVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }

                if (mWindow != null) {
                    mCurrentBrightnessPercent = mWindow.getAttributes().screenBrightness;
                    // 如果当前亮度是默认的，那么就获取系统当前的屏幕亮度
                    if (mCurrentBrightnessPercent == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                        mCurrentBrightnessPercent = (float) getBrightness() / (float) getMaxBrightness();
                    }
                }

                mViewDownX = event.getX();
                mViewDownY = event.getY();
                removeCallbacks(mHideControllerRunnable);
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算偏移的距离（按下的位置 - 当前触摸的位置）
                float distanceX = mViewDownX - event.getX();
                float distanceY = mViewDownY - event.getY();
                // 手指偏移的距离一定不能太短，这个是前提条件
                if (Math.abs(distanceY) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    break;
                }
                if (mTouchOrientation == -1) {
                    // 判断滚动方向是垂直的还是水平的
                    if (Math.abs(distanceY) > Math.abs(distanceX)) {
                        mTouchOrientation = LinearLayout.VERTICAL;
                    } else if (Math.abs(distanceY) < Math.abs(distanceX)) {
                        mTouchOrientation = LinearLayout.HORIZONTAL;
                    }
                }

                // 如果手指触摸方向是水平的
                if (mTouchOrientation == LinearLayout.HORIZONTAL) {
                    int second = -(int) (distanceX / getWidth() * 60f);
                    if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        second = -second;
                    }
                    int progress = getProgress() + second * 1000;
                    if (progress >= 0 && progress <= getDuration()) {
                        mAdjustSecond = second;
                        @DrawableRes
                        int imageResource;
                        if (layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                            imageResource = mAdjustSecond < 0 ? R.drawable.video_schedule_rewind_ic : R.drawable.video_schedule_forward_ic;
                        } else {
                            imageResource = mAdjustSecond < 0 ? R.drawable.video_schedule_forward_ic : R.drawable.video_schedule_rewind_ic;
                        }
                        mLottieView.setImageResource(imageResource);
                        mMessageView.setText(String.format("%s s", Math.abs(mAdjustSecond)));
                        post(mShowMessageRunnable);
                    }
                    break;
                }

                // 如果手指触摸方向是垂直的
                if (mTouchOrientation == LinearLayout.VERTICAL) {
                    // 判断触摸点是在屏幕左边还是右边
                    if ((layoutDirection == View.LAYOUT_DIRECTION_LTR && (int) event.getX() < getWidth() / 2) ||
                        (layoutDirection == View.LAYOUT_DIRECTION_RTL && (int) event.getX() > getWidth() / 2)) {
                        // 手指在屏幕左边
                        float delta = (distanceY / getHeight()) * WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                        if (delta == 0) {
                            break;
                        }

                        // 更新系统亮度
                        float brightness = Math.min(Math.max(mCurrentBrightnessPercent + delta,
                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF),
                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);

                        if (mWindow != null) {
                            WindowManager.LayoutParams attributes = mWindow.getAttributes();
                            attributes.screenBrightness = brightness;
                            mWindow.setAttributes(attributes);
                        }

                        int percent = (int) (brightness * 100);

                        @DrawableRes int iconId;
                        if (percent > 100 / 3 * 2) {
                            iconId = R.drawable.video_brightness_high_ic;
                        } else if (percent > 100 / 3) {
                            iconId = R.drawable.video_brightness_medium_ic;
                        } else {
                            iconId = R.drawable.video_brightness_low_ic;
                        }
                        mLottieView.setImageResource(iconId);
                        mMessageView.setText(String.format("%s %%", percent));
                        post(mShowMessageRunnable);
                        break;
                    }

                    // 手指在屏幕右边
                    float delta = (distanceY / getHeight()) * mMaxVoice;
                    if (delta == 0) {
                        break;
                    }

                    if (mAudioManager != null) {
                        // 更新系统音量
                        int voice = (int) Math.min(Math.max(mCurrentVolume + delta, 0), mMaxVoice);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voice, 0);

                        int percent = voice * 100 / mMaxVoice;

                        @DrawableRes int iconId;
                        if (percent > 100 / 3 * 2) {
                            iconId = R.drawable.video_volume_high_ic;
                        } else if (percent > 100 / 3) {
                            iconId = R.drawable.video_volume_medium_ic;
                        } else if (percent != 0) {
                            iconId = R.drawable.video_volume_low_ic;
                        } else {
                            iconId = R.drawable.video_volume_mute_ic;
                        }
                        mLottieView.setImageResource(iconId);
                        mMessageView.setText(String.format("%s %%", percent));
                        post(mShowMessageRunnable);
                    }
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mViewDownX - event.getX()) <= ViewConfiguration.get(getContext()).getScaledTouchSlop() &&
                        Math.abs(mViewDownY - event.getY()) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    // 如果整个视频播放区域太大，触摸移动会导致触发点击事件，所以这里换成手动派发点击事件
                    if (isEnabled() && isClickable()) {
                        performClick();
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                mTouchOrientation = -1;
                if (mAudioManager != null) {
                    mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
                if (mAdjustSecond != 0) {
                    // 调整播放进度
                    setProgress(getProgress() + mAdjustSecond * 1000);
                    mAdjustSecond = 0;
                }
                postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
                postDelayed(mHideMessageRunnable, DIALOG_TIME);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 时间转换
     */
    public static String conversionTime(int time) {
        Formatter formatter = new Formatter(Locale.getDefault());
        // 总秒数
        int totalSeconds = time / 1000;
        // 小时数
        int hours = totalSeconds / 3600;
        // 分钟数
        int minutes = (totalSeconds / 60) % 60;
        // 秒数
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 获取屏幕当前的亮度
     */
    private int getBrightness() {
        try {
            // 这里需要注意，Settings.System.SCREEN_BRIGHTNESS 获取到的值在小米手机上面会超过 255
            return Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        // 如果没有取值成功，那么就默认设置为一半亮度，防止突然变得很亮或很暗
        return getMaxBrightness() / 2;
    }

    /**
     * 获取屏幕最大显示的亮度，https://blog.csdn.net/jklwan/article/details/93669170
     */
    private int getMaxBrightness() {
        try {
            Resources system = Resources.getSystem();
            int resId = system.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
            if (resId != 0) {
                return system.getInteger(resId);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return 255;
    }

    /**
     * 点击返回监听器
     */
    public interface OnPlayListener {

        /**
         * 点击了返回按钮（可在此处处理返回事件）
         */
        default void onClickBack(@NonNull PlayerView view) {
            // default implementation ignored
        }

        /**
         * 点击了锁定按钮
         */
        default void onClickLock(@NonNull PlayerView view) {
            // default implementation ignored
        }

        /**
         * 点击了播放按钮
         */
        default void onClickPlay(@NonNull PlayerView view) {
            // default implementation ignored
        }

        /**
         * 播放开始（可在此处设置播放进度）
         */
        default void onPlayStart(@NonNull PlayerView view) {
            // default implementation ignored
        }

        /**
         * 播放进度发生改变
         */
        default void onPlayProgress(@NonNull PlayerView view) {
            // default implementation ignored
        }

        /**
         * 播放结束（可在此处结束播放或者循环播放）
         */
        default void onPlayEnd(@NonNull PlayerView view) {
            // default implementation ignored
        }

        /**
         * 播放出错
         */
        default boolean onPlayError(@NonNull PlayerView view, int what, int extra) {
            return false;
        }
    }
}