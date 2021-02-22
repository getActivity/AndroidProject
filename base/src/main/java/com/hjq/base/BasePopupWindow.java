package com.hjq.base;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import androidx.core.content.ContextCompat;
import androidx.core.widget.PopupWindowCompat;

import com.hjq.base.action.ActivityAction;
import com.hjq.base.action.AnimAction;
import com.hjq.base.action.ClickAction;
import com.hjq.base.action.HandlerAction;
import com.hjq.base.action.KeyboardAction;
import com.hjq.base.action.ResourcesAction;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/16
 *    desc   : PopupWindow 基类
 */
public class BasePopupWindow extends PopupWindow
        implements ActivityAction, HandlerAction, ClickAction,
        AnimAction, KeyboardAction, PopupWindow.OnDismissListener {

    private final Context mContext;
    private PopupBackground mPopupBackground;

    private List<BasePopupWindow.OnShowListener> mShowListeners;
    private List<BasePopupWindow.OnDismissListener> mDismissListeners;

    public BasePopupWindow(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener       销毁监听器对象
     * @deprecated           请使用 {@link #addOnDismissListener(BasePopupWindow.OnDismissListener)}
     */
    @Deprecated
    @Override
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
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
        if (mShowListeners == null) {
            mShowListeners = new ArrayList<>();
        }
        mShowListeners.add(listener);
    }

    /**
     * 添加一个销毁监听器
     *
     * @param listener      监听器对象
     */
    public void addOnDismissListener(@Nullable BasePopupWindow.OnDismissListener listener) {
        if (mDismissListeners == null) {
            mDismissListeners = new ArrayList<>();
            super.setOnDismissListener(this);
        }
        mDismissListeners.add(listener);
    }

    /**
     * 移除一个显示监听器
     *
     * @param listener      监听器对象
     */
    public void removeOnShowListener(@Nullable BasePopupWindow.OnShowListener listener) {
        if (mShowListeners != null) {
            mShowListeners.remove(listener);
        }
    }

    /**
     * 移除一个销毁监听器
     *
     * @param listener      监听器对象
     */
    public void removeOnDismissListener(@Nullable BasePopupWindow.OnDismissListener listener) {
        if (mDismissListeners != null) {
            mDismissListeners.remove(listener);
        }
    }

    /**
     * 设置显示监听器集合
     */
    private void setOnShowListeners(@Nullable List<BasePopupWindow.OnShowListener> listeners) {
        mShowListeners = listeners;
    }

    /**
     * 设置销毁监听器集合
     */
    private void setOnDismissListeners(@Nullable List<BasePopupWindow.OnDismissListener> listeners) {
        super.setOnDismissListener(this);
        mDismissListeners = listeners;
    }

    /**
     * {@link PopupWindow.OnDismissListener}
     */
    @Override
    public void onDismiss() {
        if (mDismissListeners != null) {
            for (BasePopupWindow.OnDismissListener listener : mDismissListeners) {
                listener.onDismiss(this);
            }
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
        if (isShowing() || getContentView() == null) {
            return;
        }

        if (mShowListeners != null) {
            for (BasePopupWindow.OnShowListener listener : mShowListeners) {
                listener.onShow(this);
            }
        }
        super.showAsDropDown(anchor, xOff, yOff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (isShowing() || getContentView() == null) {
            return;
        }

        if (mShowListeners != null) {
            for (BasePopupWindow.OnShowListener listener : mShowListeners) {
                listener.onShow(this);
            }
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        removeCallbacks();
    }

    @Override
    public <V extends View> V findViewById(@IdRes int id) {
        return getContentView().findViewById(id);
    }

    @Override
    public void setWindowLayoutType(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setWindowLayoutType(type);
        } else {
            PopupWindowCompat.setWindowLayoutType(this, type);
        }
    }

    @Override
    public int getWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getWindowLayoutType();
        } else {
            return PopupWindowCompat.getWindowLayoutType(this);
        }
    }

    @Override
    public void setOverlapAnchor(boolean overlapAnchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setOverlapAnchor(overlapAnchor);
        } else {
            PopupWindowCompat.setOverlapAnchor(this, overlapAnchor);
        }
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
        private final Activity mActivity;
        /** Context 对象 */
        private final Context mContext;
        /** PopupWindow 布局 */
        private View mContentView;
        /** PopupWindow 对象 */
        private BasePopupWindow mPopupWindow;

        /** PopupWindow 创建监听 */
        private BasePopupWindow.OnCreateListener mCreateListener;
        /** PopupWindow 显示监听 */
        private final List<BasePopupWindow.OnShowListener> mShowListeners = new ArrayList<>();
        /** PopupWindow 销毁监听 */
        private final List<BasePopupWindow.OnDismissListener> mDismissListeners = new ArrayList<>();

        /** 动画样式 */
        private int mAnimations = BasePopupWindow.ANIM_DEFAULT;
        /** 重心位置 */
        private int mGravity = DEFAULT_ANCHORED_GRAVITY;

        /** 宽度和高度 */
        private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
        private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;

        /** 是否可触摸 */
        private boolean mTouchable = true;
        /** 是否有焦点 */
        private boolean mFocusable = true;
        /** 是否外层可触摸 */
        private boolean mOutsideTouchable = false;

        /** 背景遮盖层透明度 */
        private float mBackgroundDimAmount;

        /** 水平偏移 */
        private int mXOffset;
        /** 垂直偏移 */
        private int mYOffset;

        /** 点击事件集合 */
        private SparseArray<BasePopupWindow.OnClickListener<? extends View>> mClickArray;

        public Builder(Activity activity) {
            this((Context) activity);
        }

        public Builder(Context context) {
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

            ViewGroup.LayoutParams params = mContentView.getLayoutParams();
            if (params != null &&
                    mWidth == ViewGroup.LayoutParams.WRAP_CONTENT &&
                    mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                // 如果当前 PopupWindow 的宽高设置了自适应，就以布局中设置的宽高为主
                setWidth(params.width);
                setHeight(params.height);
            }

            // 如果当前没有设置重心，就自动获取布局重心
            if (mGravity == DEFAULT_ANCHORED_GRAVITY) {
                if (params instanceof FrameLayout.LayoutParams) {
                    setGravity(((FrameLayout.LayoutParams) params).gravity);
                } else if (params instanceof LinearLayout.LayoutParams) {
                    setGravity(((LinearLayout.LayoutParams) params).gravity);
                } else {
                    // 默认重心是居中
                    setGravity(Gravity.CENTER);
                }
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
         * 设置动画，已经封装好几种样式，具体可见{@link AnimAction}类
         */
        public B setAnimStyle(@StyleRes int id) {
            mAnimations = id;
            if (isCreated()) {
                mPopupWindow.setAnimationStyle(id);
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
        public B setOnCreateListener(@NonNull BasePopupWindow.OnCreateListener listener) {
            mCreateListener = listener;
            return (B) this;
        }

        /**
         * 添加显示监听
         */
        public B addOnShowListener(@NonNull BasePopupWindow.OnShowListener listener) {
            mShowListeners.add(listener);
            return (B) this;
        }

        /**
         * 添加销毁监听
         */
        public B addOnDismissListener(@NonNull BasePopupWindow.OnDismissListener listener) {
            mDismissListeners.add(listener);
            return (B) this;
        }

        /**
         * 设置文本
         */
        public B setText(@IdRes int viewId, @StringRes int stringId) {
            return setText(viewId, getString(stringId));
        }
        public B setText(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setText(text);
            return (B) this;
        }

        /**
         * 设置文本颜色
         */
        public B setTextColor(@IdRes int id, @ColorInt int color) {
            ((TextView) findViewById(id)).setTextColor(color);
            return (B) this;
        }

        /**
         * 设置提示
         */
        public B setHint(@IdRes int viewId, @StringRes int stringId) {
            return setHint(viewId, getString(stringId));
        }
        public B setHint(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setHint(text);
            return (B) this;
        }

        /**
         * 设置可见状态
         */
        public B setVisibility(@IdRes int id, int visibility) {
            findViewById(id).setVisibility(visibility);
            return (B) this;
        }

        /**
         * 设置背景
         */
        public B setBackground(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
        }
        public B setBackground(@IdRes int id, Drawable drawable) {
            findViewById(id).setBackground(drawable);
            return (B) this;
        }

        /**
         * 设置图片
         */
        public B setImageDrawable(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
        }
        public B setImageDrawable(@IdRes int id, Drawable drawable) {
            ((ImageView) findViewById(id)).setImageDrawable(drawable);
            return (B) this;
        }

        /**
         * 设置点击事件
         */
        public B setOnClickListener(@IdRes int id, @NonNull BasePopupWindow.OnClickListener<? extends View> listener) {
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
            if (mAnimations == BasePopupWindow.ANIM_DEFAULT) {
                switch (mGravity) {
                    case Gravity.TOP:
                        mAnimations = BasePopupWindow.ANIM_TOP;
                        break;
                    case Gravity.BOTTOM:
                        mAnimations = BasePopupWindow.ANIM_BOTTOM;
                        break;
                    case Gravity.LEFT:
                        mAnimations = BasePopupWindow.ANIM_LEFT;
                        break;
                    case Gravity.RIGHT:
                        mAnimations = BasePopupWindow.ANIM_RIGHT;
                        break;
                    default:
                        mAnimations = BasePopupWindow.ANIM_DEFAULT;
                        break;
                }
            }

            mPopupWindow = createPopupWindow(mContext);
            mPopupWindow.setContentView(mContentView);
            mPopupWindow.setWidth(mWidth);
            mPopupWindow.setHeight(mHeight);
            mPopupWindow.setAnimationStyle(mAnimations);
            mPopupWindow.setFocusable(mFocusable);
            mPopupWindow.setTouchable(mTouchable);
            mPopupWindow.setOutsideTouchable(mOutsideTouchable);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            mPopupWindow.setOnShowListeners(mShowListeners);
            mPopupWindow.setOnDismissListeners(mDismissListeners);

            mPopupWindow.setBackgroundDimAmount(mBackgroundDimAmount);

            for (int i = 0; mClickArray != null && i < mClickArray.size(); i++) {
                View view = mContentView.findViewById(mClickArray.keyAt(i));
                if (view != null) {
                    view.setOnClickListener(new BasePopupWindow.ViewClickWrapper(mPopupWindow, mClickArray.valueAt(i)));
                }
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
            if (mActivity == null) {
                return;
            }

            if (mActivity.isFinishing() || mActivity.isDestroyed()) {
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
            if (mActivity == null) {
                return;
            }

            if (mActivity.isFinishing() || mActivity.isDestroyed()) {
                return;
            }

            if (!isCreated()) {
                create();
            }
            mPopupWindow.showAtLocation(parent, mGravity, mXOffset, mYOffset);
        }

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
            return mPopupWindow != null && mPopupWindow.isShowing();
        }

        /**
         * 销毁当前 PopupWindow
         */
        public void dismiss() {
            if (mActivity == null) {
                return;
            }

            if (mActivity.isFinishing() || mActivity.isDestroyed()) {
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
        protected BasePopupWindow createPopupWindow(Context context) {
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
        public final void post(Runnable r) {
            if (isShowing()) {
                mPopupWindow.post(r);
            } else {
                addOnShowListener(new ShowPostWrapper(r));
            }
        }

        /**
         * 延迟一段时间执行
         */
        public final void postDelayed(Runnable r, long delayMillis) {
            if (isShowing()) {
                mPopupWindow.postDelayed(r, delayMillis);
            } else {
                addOnShowListener(new ShowPostDelayedWrapper(r, delayMillis));
            }
        }

        /**
         * 在指定的时间执行
         */
        public final void postAtTime(Runnable r, long uptimeMillis) {
            if (isShowing()) {
                mPopupWindow.postAtTime(r, uptimeMillis);
            } else {
                addOnShowListener(new ShowPostAtTimeWrapper(r, uptimeMillis));
            }
        }
    }

    /**
     * PopupWindow 背景遮盖层实现类
     */
    private static class PopupBackground implements
            BasePopupWindow.OnShowListener,
            BasePopupWindow.OnDismissListener {

        private float mAlpha;

        private void setAlpha(float alpha) {
            mAlpha = alpha;
        }

        @Override
        public void onShow(BasePopupWindow popupWindow) {
            popupWindow.setActivityAlpha(mAlpha);
        }

        @Override
        public void onDismiss(BasePopupWindow popupWindow) {
            popupWindow.setActivityAlpha(1);
        }
    }

    /**
     * 销毁监听包装类
     */
    private static final class DismissListenerWrapper
            extends SoftReference<PopupWindow.OnDismissListener>
            implements BasePopupWindow.OnDismissListener {

        private DismissListenerWrapper(PopupWindow.OnDismissListener referent) {
            super(referent);
        }

        @Override
        public void onDismiss(BasePopupWindow popupWindow) {
            // 在横竖屏切换后监听对象会为空
            if (get() != null) {
                get().onDismiss();
            }
        }
    }

    /**
     * 点击事件包装类
     */
    @SuppressWarnings("rawtypes")
    private static final class ViewClickWrapper
            implements View.OnClickListener {

        private final BasePopupWindow mBasePopupWindow;
        private final BasePopupWindow.OnClickListener mListener;

        private ViewClickWrapper(BasePopupWindow popupWindow, BasePopupWindow.OnClickListener listener) {
            mBasePopupWindow = popupWindow;
            mListener = listener;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final void onClick(View view) {
            mListener.onClick(mBasePopupWindow, view);
        }
    }

    /**
     * post 任务包装类
     */
    private static final class ShowPostWrapper implements OnShowListener {

        private final Runnable mRunnable;

        private ShowPostWrapper(Runnable r) {
            mRunnable = r;
        }

        @Override
        public void onShow(BasePopupWindow dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.post(mRunnable);
            }
        }
    }

    /**
     * postDelayed 任务包装类
     */
    private static final class ShowPostDelayedWrapper implements OnShowListener {

        private final Runnable mRunnable;
        private final long mDelayMillis;

        private ShowPostDelayedWrapper(Runnable r, long delayMillis) {
            mRunnable = r;
            mDelayMillis = delayMillis;
        }

        @Override
        public void onShow(BasePopupWindow dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.postDelayed(mRunnable, mDelayMillis);
            }
        }
    }

    /**
     * postAtTime 任务包装类
     */
    private static final class ShowPostAtTimeWrapper implements OnShowListener {

        private final Runnable mRunnable;
        private final long mUptimeMillis;

        private ShowPostAtTimeWrapper(Runnable r, long uptimeMillis) {
            mRunnable = r;
            mUptimeMillis = uptimeMillis;
        }

        @Override
        public void onShow(BasePopupWindow dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.postAtTime(mRunnable, mUptimeMillis);
            }
        }
    }

    /**
     * 点击监听器
     */
    public interface OnClickListener<V extends View> {
        void onClick(BasePopupWindow popupWindow, V view);
    }

    /**
     * 创建监听器
     */
    public interface OnCreateListener {

        /**
         * PopupWindow 创建了
         */
        void onCreate(BasePopupWindow popupWindow);
    }

    /**
     * 显示监听器
     */
    public interface OnShowListener {

        /**
         * PopupWindow 显示了
         */
        void onShow(BasePopupWindow popupWindow);
    }

    /**
     * 销毁监听器
     */
    public interface OnDismissListener {

        /**
         * PopupWindow 销毁了
         */
        void onDismiss(BasePopupWindow popupWindow);
    }
}