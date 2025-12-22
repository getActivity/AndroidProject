package com.hjq.base;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.hjq.core.action.ActivityAction;
import com.hjq.core.action.AnimAction;
import com.hjq.core.action.ClickAction;
import com.hjq.core.action.HandlerAction;
import com.hjq.core.action.KeyboardAction;
import com.hjq.core.action.ResourcesAction;
import com.hjq.core.tools.AndroidVersion;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/16
 *    desc   : PopupWindow 技术基类
 */
public class BasePopupWindow extends PopupWindow
        implements LifecycleOwner, ActivityAction, HandlerAction, ClickAction,
        AnimAction, KeyboardAction, PopupWindow.OnDismissListener {

    @NonNull
    private LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    @NonNull
    private final Context mContext;

    @Nullable
    private PopupBackground mPopupBackground;

    @NonNull
    private final List<BasePopupWindow.OnShowListener> mShowListeners = new ArrayList<>();
    @NonNull
    private final List<BasePopupWindow.OnDismissListener> mDismissListeners = new ArrayList<>();

    public BasePopupWindow(@NonNull Context context) {
        super(context);
        mContext = context;
        // 添加监听为自己，注意这里需要调用父类的方法
        super.setOnDismissListener(new ListenersWrapper<>(this));
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener       销毁监听器对象
     * @deprecated           请使用 {@link #addOnDismissListener(BasePopupWindow.OnDismissListener)}
     */
    @Deprecated
    @Override
    public void setOnDismissListener(@Nullable PopupWindow.OnDismissListener listener) {
        if (listener == null) {
            return;
        }
        addOnDismissListener(new DismissListenerWrapper(listener));
    }

    /**
     * 添加一个显示监听器
     *
     * @param listener      监听器对象
     */
    public void addOnShowListener(@Nullable BasePopupWindow.OnShowListener listener) {
        if (listener == null) {
            return;
        }
        if (mShowListeners.contains(listener)) {
            return;
        }
        mShowListeners.add(listener);
    }

    /**
     * 添加一个销毁监听器
     *
     * @param listener      监听器对象
     */
    public void addOnDismissListener(@Nullable BasePopupWindow.OnDismissListener listener) {
        if (listener == null) {
            return;
        }
        if (mDismissListeners.contains(listener)) {
            return;
        }
        mDismissListeners.add(listener);
    }

    /**
     * 移除一个显示监听器
     *
     * @param listener      监听器对象
     */
    public void removeOnShowListener(@Nullable BasePopupWindow.OnShowListener listener) {
        if (listener == null) {
            return;
        }
        mShowListeners.remove(listener);
    }

    /**
     * 移除一个销毁监听器
     *
     * @param listener      监听器对象
     */
    public void removeOnDismissListener(@Nullable BasePopupWindow.OnDismissListener listener) {
        if (listener == null) {
            return;
        }
        mDismissListeners.remove(listener);
    }

    public void onShow() {
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        handleLifecycleEvent(Lifecycle.Event.ON_START);
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        List<BasePopupWindow.OnShowListener> listeners = new ArrayList<>(mShowListeners);
        for (BasePopupWindow.OnShowListener listener : listeners) {
            listener.onShow(this);
        }
    }

    /**
     * {@link PopupWindow.OnDismissListener}
     */
    @Override
    public void onDismiss() {
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        List<BasePopupWindow.OnDismissListener> listeners = new ArrayList<>(mDismissListeners);
        for (BasePopupWindow.OnDismissListener listener : listeners) {
            listener.onDismiss(this);
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
        if (isShowing() || getContentView() == null) {
            return;
        }
        super.showAsDropDown(anchor, xOff, yOff, gravity);
        onShow();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (isShowing() || getContentView() == null) {
            return;
        }
        super.showAtLocation(parent, gravity, x, y);
        onShow();
    }

    @Override
    public void dismiss() {
        removeCallbacks();
        super.dismiss();
    }

    /**
     * 处理 Lifecycle 事件
     */
    public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
        // 以下代码主要是为了解决复用 BasePopupWindow 对象会出现异常的问题
        // https://github.com/androidx/androidx/blob/4bb422f5c09d4ed7200f1bdc03a463b39743af85/lifecycle/lifecycle-runtime/src/commonMain/kotlin/androidx/lifecycle/LifecycleRegistry.kt#L89
        switch (mLifecycle.getCurrentState()) {
            case INITIALIZED:
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // 如果当前是初始化状态，并且下一个状态事件是销毁，必须要有 Create 事件过渡，否则会出现报错
                    // java.lang.IllegalStateException: State must be at least 'CREATED'  to be moved to `DESTROYED` in component
                    mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
                }
                break;
            case DESTROYED:
                if (event != Lifecycle.Event.ON_DESTROY) {
                    // 如果当前是销毁状态，并且下一个状态事件不是销毁，需要重置一下 Lifecycle，否则会出现报错
                    // java.lang.IllegalStateException: State is 'DESTROYED' and cannot be moved to `STARTED` in component
                    mLifecycle = new LifecycleRegistry(this);
                }
                break;
            default:
                break;
        }
        // 处理下一个状态事件
        mLifecycle.handleLifecycleEvent(event);
    }

    @Override
    public <V extends View> V findViewById(@IdRes int id) {
        return getContentView().findViewById(id);
    }

    /**
     * 设置背景遮盖层的透明度
     */
    public void setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
        float alpha = 1 - dimAmount;
        if (isShowing()) {
            setActivityAlpha(alpha);
        }
        if (mPopupBackground == null && alpha != 1) {
            mPopupBackground = new PopupBackground();
            addOnShowListener(mPopupBackground);
            addOnDismissListener(mPopupBackground);
        }
        if (mPopupBackground != null) {
            mPopupBackground.setAlpha(alpha);
        }
    }

    /**
     * 设置 Activity 窗口透明度
     */
    private void setActivityAlpha(float alpha) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        WindowManager.LayoutParams params = activity.getWindow().getAttributes();

        final ValueAnimator animator = ValueAnimator.ofFloat(params.alpha, alpha);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            if (value != params.alpha) {
                params.alpha = value;
                activity.getWindow().setAttributes(params);
            }
        });
        animator.start();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends BasePopupWindow.Builder<?>> implements
            ActivityAction, ResourcesAction, ClickAction, KeyboardAction {

        private static final int DEFAULT_ANCHORED_GRAVITY = Gravity.TOP | Gravity.START;

        /** Activity 对象 */
        @Nullable
        private final Activity mActivity;
        /** Context 对象 */
        @NonNull
        private final Context mContext;
        /** PopupWindow 对象 */
        private BasePopupWindow mPopupWindow;
        /** PopupWindow 布局 */
        private View mContentView;

        /** 动画样式 */
        private int mAnimStyle = BasePopupWindow.ANIM_DEFAULT;

        /** 宽度和高度 */
        private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
        private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;

        /** 重心位置 */
        private int mGravity = DEFAULT_ANCHORED_GRAVITY;
        /** 水平偏移 */
        private int mXOffset;
        /** 垂直偏移 */
        private int mYOffset;

        /** 是否可触摸 */
        private boolean mTouchable = true;
        /** 是否有焦点 */
        private boolean mFocusable = true;
        /** 是否外层可触摸 */
        private boolean mOutsideTouchable = false;

        /** 背景遮盖层透明度 */
        private float mBackgroundDimAmount;

        /** PopupWindow 创建监听 */
        @Nullable
        private BasePopupWindow.OnCreateListener mCreateListener;
        /** PopupWindow 显示监听 */
        @NonNull
        private final List<BasePopupWindow.OnShowListener> mShowListeners = new ArrayList<>();
        /** PopupWindow 销毁监听 */
        @NonNull
        private final List<BasePopupWindow.OnDismissListener> mDismissListeners = new ArrayList<>();

        /** 点击事件集合 */
        private SparseArray<BasePopupWindow.OnClickListener<? extends View>> mClickArray;

        public Builder(@NonNull Activity activity) {
            this((Context) activity);
        }

        public Builder(@NonNull Context context) {
            mContext = context;
            mActivity = getActivity();
        }

        /**
         * 设置布局
         */
        public B setContentView(@LayoutRes int id) {
            // 这里解释一下，为什么要传 new FrameLayout，因为如果不传的话，XML 的根布局获取到的 LayoutParams 对象会为空，也就会导致宽高解析不出来
            return setContentView(LayoutInflater.from(mContext).inflate(id, new FrameLayout(mContext), false));
        }
        public B setContentView(View view) {
            // 请不要传入空的布局
            if (view == null) {
                throw new IllegalArgumentException("are you ok?");
            }

            mContentView = view;

            if (isCreated()) {
                mPopupWindow.setContentView(view);
                return (B) this;
            }

            ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            if (layoutParams != null &&
                    mWidth == ViewGroup.LayoutParams.WRAP_CONTENT &&
                    mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                // 如果当前 PopupWindow 的宽高设置了自适应，就以布局中设置的宽高为主
                setWidth(layoutParams.width);
                setHeight(layoutParams.height);
            }

            // 如果当前没有设置重心，就自动获取布局重心
            if (mGravity == DEFAULT_ANCHORED_GRAVITY) {
                if (layoutParams instanceof FrameLayout.LayoutParams) {
                    int gravity = ((FrameLayout.LayoutParams) layoutParams).gravity;
                    if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                        setGravity(gravity);
                    }
                } else if (layoutParams instanceof LinearLayout.LayoutParams) {
                    int gravity = ((LinearLayout.LayoutParams) layoutParams).gravity;
                    if (gravity != Gravity.NO_GRAVITY) {
                        setGravity(gravity);
                    }
                }

                if (mGravity == Gravity.NO_GRAVITY) {
                    // 默认重心是居中
                    setGravity(Gravity.CENTER);
                }
            }
            return (B) this;
        }

        /**
         * 设置动画，已经封装好几种样式，具体可见{@link AnimAction}类
         */
        public B setAnimStyle(@StyleRes int id) {
            mAnimStyle = id;
            if (isCreated()) {
                mPopupWindow.setAnimationStyle(id);
            }
            return (B) this;
        }

        /**
         * 设置宽度
         */
        public B setWidth(int width) {
            mWidth = width;
            if (isCreated()) {
                mPopupWindow.setWidth(width);
                return (B) this;
            }

            ViewGroup.LayoutParams params = mContentView != null ? mContentView.getLayoutParams() : null;
            if (params != null) {
                params.width = width;
                mContentView.setLayoutParams(params);
            }
            return (B) this;
        }

        /**
         * 设置高度
         */
        public B setHeight(int height) {
            mHeight = height;
            if (isCreated()) {
                mPopupWindow.setHeight(height);
                return (B) this;
            }

            // 这里解释一下为什么要重新设置 LayoutParams
            // 因为如果不这样设置的话，第一次显示的时候会按照 PopupWindow 宽高显示
            // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
            // 所以这里我们需要对 View 的 LayoutParams 也进行设置
            ViewGroup.LayoutParams params = mContentView != null ? mContentView.getLayoutParams() : null;
            if (params != null) {
                params.height = height;
                mContentView.setLayoutParams(params);
            }
            return (B) this;
        }

        /**
         * 设置重心位置
         */
        public B setGravity(int gravity) {
            // 适配布局反方向
            mGravity = Gravity.getAbsoluteGravity(gravity, getResources().getConfiguration().getLayoutDirection());
            return (B) this;
        }

        /**
         * 设置水平偏移量
         */
        public B setXOffset(int offset) {
            mXOffset = offset;
            return (B) this;
        }

        /**
         * 设置垂直偏移量
         */
        public B setYOffset(int offset) {
            mYOffset = offset;
            return (B) this;
        }

        /**
         * 是否可触摸
         */
        public B setTouchable(boolean touchable) {
            mTouchable = touchable;
            if (isCreated()) {
                mPopupWindow.setTouchable(touchable);
            }
            return (B) this;
        }

        /**
         * 是否有焦点
         */
        public B setFocusable(boolean focusable) {
            mFocusable = focusable;
            if (isCreated()) {
                mPopupWindow.setFocusable(focusable);
            }
            return (B) this;
        }

        /**
         * 是否外层可触摸
         */
        public B setOutsideTouchable(boolean outsideTouchable) {
            mOutsideTouchable = outsideTouchable;
            if (isCreated()) {
                mPopupWindow.setOutsideTouchable(outsideTouchable);
            }
            return (B) this;
        }

        /**
         * 设置背景遮盖层的透明度
         */
        public B setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
            mBackgroundDimAmount = dimAmount;
            if (isCreated()) {
                mPopupWindow.setBackgroundDimAmount(dimAmount);
            }
            return (B) this;
        }

        /**
         * 设置创建监听
         */
        public B setOnCreateListener(@Nullable BasePopupWindow.OnCreateListener listener) {
            mCreateListener = listener;
            return (B) this;
        }

        /**
         * 添加显示监听
         */
        public B addOnShowListener(@Nullable BasePopupWindow.OnShowListener listener) {
            if (listener == null) {
                return (B) this;
            }
            if (mShowListeners.contains(listener)) {
                return (B) this;
            }
            mShowListeners.add(listener);
            if (isCreated()) {
                mPopupWindow.addOnShowListener(listener);
            }
            return (B) this;
        }

        /**
         * 移除显示监听
         */
        public B removeOnShowListener(@Nullable BasePopupWindow.OnShowListener listener) {
            if (listener == null) {
                return (B) this;
            }
            mShowListeners.remove(listener);
            if (isCreated()) {
                mPopupWindow.removeOnShowListener(listener);
            }
            return (B) this;
        }

        /**
         * 添加销毁监听
         */
        public B addOnDismissListener(@Nullable BasePopupWindow.OnDismissListener listener) {
            if (listener == null) {
                return (B) this;
            }
            if (mDismissListeners.contains(listener)) {
                return (B) this;
            }
            mDismissListeners.add(listener);
            if (isCreated()) {
                mPopupWindow.addOnDismissListener(listener);
            }
            return (B) this;
        }

        /**
         * 移除销毁监听
         */
        public B removeOnDismissListener(@Nullable BasePopupWindow.OnDismissListener listener) {
            if (listener == null) {
                return (B) this;
            }
            mDismissListeners.remove(listener);
            if (isCreated()) {
                mPopupWindow.removeOnDismissListener(listener);
            }
            return (B) this;
        }

        /**
         * 设置文本
         */
        public B setTextByTextView(@IdRes int viewId, @StringRes int stringId) {
            return setTextByTextView(viewId, getString(stringId));
        }
        public B setTextByTextView(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setText(text);
            return (B) this;
        }

        /**
         * 设置文本颜色
         */
        public B setTextColorByTextView(@IdRes int id, @ColorInt int color) {
            ((TextView) findViewById(id)).setTextColor(color);
            return (B) this;
        }

        /**
         * 设置可见状态
         */
        public B setVisibilityByView(@IdRes int id, int visibility) {
            findViewById(id).setVisibility(visibility);
            return (B) this;
        }

        /**
         * 设置背景
         */
        public B setBackgroundByView(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackgroundByView(viewId, getDrawable(drawableId));
        }
        public B setBackgroundByView(@IdRes int id, Drawable drawable) {
            findViewById(id).setBackground(drawable);
            return (B) this;
        }

        /**
         * 设置图片
         */
        public B setDrawableByImageView(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackgroundByView(viewId, getDrawable(drawableId));
        }
        public B setDrawableByImageView(@IdRes int id, Drawable drawable) {
            ((ImageView) findViewById(id)).setImageDrawable(drawable);
            return (B) this;
        }

        /**
         * 设置点击事件
         */
        public B setOnClickListenerByView(@IdRes int id, @NonNull BasePopupWindow.OnClickListener<?> listener) {
            if (mClickArray == null) {
                mClickArray = new SparseArray<>();
            }
            mClickArray.put(id, listener);

            if (isCreated()) {
                View view = mPopupWindow.findViewById(id);
                if (view != null) {
                    view.setOnClickListener(new ViewClickWrapper(mPopupWindow, listener));
                }
            }
            return (B) this;
        }

        /**
         * 创建
         */
        @SuppressLint("RtlHardcoded")
        public BasePopupWindow create() {
            // 判断布局是否为空
            if (mContentView == null) {
                throw new IllegalArgumentException("are you ok?");
            }

            // 如果当前正在显示
            if (isShowing()) {
                dismiss();
            }

            // 如果当前没有设置重心，就设置一个默认的重心
            if (mGravity == DEFAULT_ANCHORED_GRAVITY) {
                mGravity = Gravity.CENTER;
            }

            // 如果当前没有设置动画效果，就设置一个默认的动画效果
            if (mAnimStyle == BasePopupWindow.ANIM_DEFAULT) {
                switch (mGravity) {
                    case Gravity.TOP:
                        mAnimStyle = BasePopupWindow.ANIM_TOP;
                        break;
                    case Gravity.BOTTOM:
                        mAnimStyle = BasePopupWindow.ANIM_BOTTOM;
                        break;
                    case Gravity.LEFT:
                        mAnimStyle = BasePopupWindow.ANIM_LEFT;
                        break;
                    case Gravity.RIGHT:
                        mAnimStyle = BasePopupWindow.ANIM_RIGHT;
                        break;
                    default:
                        mAnimStyle = BasePopupWindow.ANIM_DEFAULT;
                        break;
                }
            }

            mPopupWindow = createPopupWindow(mContext);
            mPopupWindow.setContentView(mContentView);
            mPopupWindow.setWidth(mWidth);
            mPopupWindow.setHeight(mHeight);
            mPopupWindow.setAnimationStyle(mAnimStyle);
            mPopupWindow.setFocusable(mFocusable);
            mPopupWindow.setTouchable(mTouchable);
            mPopupWindow.setOutsideTouchable(mOutsideTouchable);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            for (OnShowListener listener : mShowListeners) {
                mPopupWindow.addOnShowListener(listener);
            }

            for (OnDismissListener listener : mDismissListeners) {
                mPopupWindow.addOnDismissListener(listener);
            }

            mPopupWindow.setBackgroundDimAmount(mBackgroundDimAmount);

            for (int i = 0; mClickArray != null && i < mClickArray.size(); i++) {
                View view = mContentView.findViewById(mClickArray.keyAt(i));
                if (view != null) {
                    view.setOnClickListener(new BasePopupWindow.ViewClickWrapper(mPopupWindow, mClickArray.valueAt(i)));
                }
            }

            // 将 PopupWindow 的生命周期和 Activity 绑定在一起
            if (mActivity != null) {
                PopupWindowLifecycle.with(mActivity, mPopupWindow);
            }

            if (mCreateListener != null) {
                mCreateListener.onCreate(mPopupWindow);
            }

            return mPopupWindow;
        }

        /**
         * 显示为下拉
         */
        public void showAsDropDown(View anchor) {
            if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                return;
            }

            if (!isCreated()) {
                create();
            }
            mPopupWindow.showAsDropDown(anchor, mXOffset, mYOffset, mGravity);
        }

        /**
         * 显示在指定位置
         */
        public void showAtLocation(View parent) {
            if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                return;
            }

            if (!isCreated()) {
                create();
            }
            mPopupWindow.showAtLocation(parent, mGravity, mXOffset, mYOffset);
        }

        @NonNull
        @Override
        public Context getContext() {
            return mContext;
        }

        /**
         * 当前 PopupWindow 是否创建了
         */
        public boolean isCreated() {
            return mPopupWindow != null;
        }

        /**
         * 当前 PopupWindow 是否显示了
         */
        public boolean isShowing() {
            return isCreated() && mPopupWindow.isShowing();
        }

        /**
         * 销毁当前 PopupWindow
         */
        public void dismiss() {
            if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
                return;
            }

            if (mPopupWindow == null) {
                return;
            }
            mPopupWindow.dismiss();
        }

        /**
         * 创建 PopupWindow 对象（子类可以重写此方法来改变 PopupWindow 类型）
         */
        @NonNull
        protected BasePopupWindow createPopupWindow(@NonNull Context context) {
            return new BasePopupWindow(context);
        }

        /**
         * 获取 PopupWindow 的根布局
         */
        public View getContentView() {
            return mContentView;
        }

        /**
         * 根据 id 查找 View
         */
        @Override
        public  <V extends View> V findViewById(@IdRes int id) {
            if (mContentView == null) {
                // 没有 setContentView 就想 findViewById ?
                throw new IllegalStateException("are you ok?");
            }
            return mContentView.findViewById(id);
        }

        /**
         * 获取当前 PopupWindow 对象
         */
        @Nullable
        public BasePopupWindow getPopupWindow() {
            return mPopupWindow;
        }

        /**
         * 延迟执行
         */
        public final void post(Runnable runnable) {
            if (isShowing()) {
                mPopupWindow.post(runnable);
                return;
            }

            addOnShowListener(new OnShowListener() {

                @Override
                public void onShow(@NonNull BasePopupWindow popupWindow) {
                    removeOnShowListener(this);
                    popupWindow.post(runnable);
                }
            });
        }

        /**
         * 延迟一段时间执行
         */
        public final void postDelayed(Runnable runnable, long delayMillis) {
            if (isShowing()) {
                mPopupWindow.postDelayed(runnable, delayMillis);
                return;
            }

            addOnShowListener(new OnShowListener() {

                @Override
                public void onShow(@NonNull BasePopupWindow popupWindow) {
                    removeOnShowListener(this);
                    popupWindow.postDelayed(runnable, delayMillis);
                }
            });
        }
    }

    /**
     * PopupWindow 生命周期绑定
     */
    private static final class PopupWindowLifecycle implements Application.ActivityLifecycleCallbacks,
                                                    BasePopupWindow.OnShowListener, BasePopupWindow.OnDismissListener {

        private static void with(@NonNull Activity activity, @NonNull BasePopupWindow popupWindow) {
            new PopupWindowLifecycle(activity, popupWindow);
        }

        private BasePopupWindow mPopupWindow;
        private Activity mActivity;

        private PopupWindowLifecycle(@NonNull Activity activity, @NonNull BasePopupWindow popupWindow) {
            mActivity = activity;
            popupWindow.addOnShowListener(this);
            popupWindow.addOnDismissListener(this);
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            // default implementation ignored
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            // default implementation ignored
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            // default implementation ignored
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            if (mActivity != activity) {
                return;
            }

            unregisterActivityLifecycleCallbacks();
            mActivity = null;

            if (mPopupWindow == null) {
                return;
            }
            mPopupWindow.removeOnShowListener(this);
            mPopupWindow.removeOnDismissListener(this);
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }

        @Override
        public void onShow(@NonNull BasePopupWindow popupWindow) {
            mPopupWindow = popupWindow;
            registerActivityLifecycleCallbacks();
        }

        @Override
        public void onDismiss(@NonNull BasePopupWindow popupWindow) {
            mPopupWindow = null;
            unregisterActivityLifecycleCallbacks();
        }

        /**
         * 注册 Activity 生命周期监听
         */
        private void registerActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return;
            }

            if (AndroidVersion.isAndroid10()) {
                mActivity.registerActivityLifecycleCallbacks(this);
            } else {
                mActivity.getApplication().registerActivityLifecycleCallbacks(this);
            }
        }

        /**
         * 反注册 Activity 生命周期监听
         */
        private void unregisterActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return;
            }

            if (AndroidVersion.isAndroid10()) {
                mActivity.unregisterActivityLifecycleCallbacks(this);
            } else {
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        }
    }

    /**
     * PopupWindow 监听包装类（修复原生 PopupWindow 监听器对象导致的内存泄漏）
     */
    private static final class ListenersWrapper<T extends PopupWindow.OnDismissListener>
            extends SoftReference<T> implements PopupWindow.OnDismissListener {

        private ListenersWrapper(T listener) {
            super(listener);
        }

        @Override
        public void onDismiss() {
            if (get() == null) {
                return;
            }
            get().onDismiss();
        }
    }

    /**
     * PopupWindow 背景遮盖层实现类
     */
    private static class PopupBackground implements BasePopupWindow.OnShowListener, BasePopupWindow.OnDismissListener {

        private float mAlpha;

        private void setAlpha(float alpha) {
            mAlpha = alpha;
        }

        @Override
        public void onShow(@NonNull BasePopupWindow popupWindow) {
            popupWindow.setActivityAlpha(mAlpha);
        }

        @Override
        public void onDismiss(@NonNull BasePopupWindow popupWindow) {
            popupWindow.setActivityAlpha(1);
        }
    }

    /**
     * 销毁监听包装类
     */
    private static final class DismissListenerWrapper extends SoftReference<PopupWindow.OnDismissListener>
                                                      implements BasePopupWindow.OnDismissListener {

        private DismissListenerWrapper(PopupWindow.OnDismissListener listener) {
            super(listener);
        }

        @Override
        public void onDismiss(@NonNull BasePopupWindow popupWindow) {
            // 在横竖屏切换后监听对象会为空
            if (get() == null) {
                return;
            }
            get().onDismiss();
        }
    }

    /**
     * 点击事件包装类
     */
    @SuppressWarnings("rawtypes")
    private static final class ViewClickWrapper implements View.OnClickListener {

        @NonNull
        private final BasePopupWindow mBasePopupWindow;
        @Nullable
        private final BasePopupWindow.OnClickListener mListener;

        private ViewClickWrapper(@NonNull BasePopupWindow popupWindow, @Nullable BasePopupWindow.OnClickListener listener) {
            mBasePopupWindow = popupWindow;
            mListener = listener;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onClick(@NonNull View view) {
            if (mListener == null) {
                return;
            }
            mListener.onClick(mBasePopupWindow, view);
        }
    }

    /**
     * 点击监听器
     */
    public interface OnClickListener<V extends View> {

        /**
         * 点击事件触发了
         */
        void onClick(@NonNull BasePopupWindow popupWindow, V view);
    }

    /**
     * 创建监听器
     */
    public interface OnCreateListener {

        /**
         * PopupWindow 创建了
         */
        void onCreate(@NonNull BasePopupWindow popupWindow);
    }

    /**
     * 显示监听器
     */
    public interface OnShowListener {

        /**
         * PopupWindow 显示了
         */
        void onShow(@NonNull BasePopupWindow popupWindow);
    }

    /**
     * 销毁监听器
     */
    public interface OnDismissListener {

        /**
         * PopupWindow 销毁了
         */
        void onDismiss(@NonNull BasePopupWindow popupWindow);
    }
}