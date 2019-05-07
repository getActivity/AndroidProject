package com.hjq.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatDialog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/24
 *    desc   : Dialog 基类
 */
public class BaseDialog extends AppCompatDialog implements
        DialogInterface.OnShowListener,
        DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private List<BaseDialog.OnShowListener> mOnShowListeners;
    private List<BaseDialog.OnCancelListener> mOnCancelListeners;
    private List<BaseDialog.OnDismissListener> mOnDismissListeners;

    public BaseDialog(Context context) {
        this(context, R.style.BaseDialogStyle);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId > 0 ? themeResId : R.style.BaseDialogStyle);
    }

    /**
     * 设置一个显示监听器
     *
     * @param listener       监听器对象
     * @deprecated          请使用 {@link #addOnShowListener(BaseDialog.OnShowListener)}}
     */
    @Deprecated
    @Override
    public void setOnShowListener(@Nullable DialogInterface.OnShowListener listener) {
        addOnShowListener(new ShowListenerWrapper(listener));
    }

    /**
     * 设置一个取消监听器
     *
     * @param listener       监听器对象
     * @deprecated          请使用 {@link #addOnCancelListener(BaseDialog.OnCancelListener)}
     */
    @Deprecated
    @Override
    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        addOnCancelListener(new CancelListenerWrapper(listener));
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener       监听器对象
     * @deprecated          请使用 {@link #addOnDismissListener(BaseDialog.OnDismissListener)}
     */
    @Deprecated
    @Override
    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        addOnDismissListener(new DismissListenerWrapper(listener));
    }

    /**
     * 添加一个取消监听器
     *
     * @param listener      监听器对象
     */
    public void addOnShowListener(@Nullable BaseDialog.OnShowListener listener) {
        if (mOnShowListeners == null) {
            mOnShowListeners = new ArrayList<>();
            super.setOnShowListener(this);
        }
        mOnShowListeners.add(listener);
    }

    /**
     * 添加一个取消监听器
     *
     * @param listener      监听器对象
     */
    public void addOnCancelListener(@Nullable BaseDialog.OnCancelListener listener) {
        if (mOnCancelListeners == null) {
            mOnCancelListeners = new ArrayList<>();
            super.setOnCancelListener(this);
        }
        mOnCancelListeners.add(listener);
    }

    /**
     * 添加一个销毁监听器
     *
     * @param listener      监听器对象
     */
    public void addOnDismissListener(@Nullable BaseDialog.OnDismissListener listener) {
        if (mOnDismissListeners == null) {
            mOnDismissListeners = new ArrayList<>();
            super.setOnDismissListener(this);
        }
        mOnDismissListeners.add(listener);
    }

    /**
     * 设置显示监听器集合
     */
    private void setOnShowListeners(@Nullable List<BaseDialog.OnShowListener> listeners) {
        super.setOnShowListener(this);
        mOnShowListeners = listeners;
    }

    /**
     * 设置取消监听器集合
     */
    private void setOnCancelListeners(@Nullable List<BaseDialog.OnCancelListener> listeners) {
        super.setOnCancelListener(this);
        mOnCancelListeners = listeners;
    }

    /**
     * 设置销毁监听器集合
     */
    private void setOnDismissListeners(@Nullable List<BaseDialog.OnDismissListener> listeners) {
        super.setOnDismissListener(this);
        mOnDismissListeners = listeners;
    }

    /**
     * {@link DialogInterface.OnShowListener}
     */
    @Override
    public void onShow(DialogInterface dialog) {
        if (mOnShowListeners != null) {
            for (BaseDialog.OnShowListener listener : mOnShowListeners) {
                listener.onShow(this);
            }
        }
    }

    /**
     * {@link DialogInterface.OnCancelListener}
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnCancelListeners != null) {
            for (BaseDialog.OnCancelListener listener : mOnCancelListeners) {
                listener.onCancel(this);
            }
        }
    }

    /**
     * {@link DialogInterface.OnDismissListener}
     */
    @Override
    public void onDismiss(DialogInterface dialog) {

        // 移除和这个 Dialog 相关的消息回调
        HANDLER.removeCallbacksAndMessages(this);

        if (mOnDismissListeners != null) {
            for (BaseDialog.OnDismissListener listener : mOnDismissListeners) {
                listener.onDismiss(this);
            }
        }
    }

    /**
     * 延迟执行
     */
    public final boolean post(Runnable r) {
        return postDelayed(r, 0);
    }

    /**
     * 延迟一段时间执行
     */
    public final boolean postDelayed(Runnable r, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postAtTime(r, SystemClock.uptimeMillis() + delayMillis);
    }

    /**
     * 在指定的时间执行
     */
    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return HANDLER.postAtTime(r, this, uptimeMillis);
    }

    /**
     * Dialog 动画样式
     */
    public static final class AnimStyle {

        // 默认动画效果
        static final int DEFAULT = R.style.ScaleAnimStyle;

        // 缩放动画
        public static final int SCALE = R.style.ScaleAnimStyle;

        // IOS 动画
        public static final int IOS = R.style.IOSAnimStyle;

        // 吐司动画
        public static final int TOAST = android.R.style.Animation_Toast;

        // 顶部弹出动画
        public static final int TOP = R.style.TopAnimStyle;

        // 底部弹出动画
        public static final int BOTTOM = R.style.BottomAnimStyle;

        // 左边弹出动画
        public static final int LEFT = R.style.LeftAnimStyle;

        // 右边弹出动画
        public static final int RIGHT = R.style.RightAnimStyle;
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder> {

        protected static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
        protected static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

        private BaseDialog mDialog;

        // Context 对象
        private Context mContext;

        // Dialog 布局
        private View mContentView;

        // Dialog Show 监听
        private List<BaseDialog.OnShowListener> mOnShowListeners;
        // Dialog Cancel 监听
        private List<BaseDialog.OnCancelListener> mOnCancelListeners;
        // Dialog Dismiss 监听
        private List<BaseDialog.OnDismissListener> mOnDismissListeners;
        // Dialog Key 监听
        private OnKeyListener mOnKeyListener;

        // 点击空白是否能够取消  默认点击阴影可以取消
        private boolean mCancelable = true;

        private SparseArray<CharSequence> mTextArray = new SparseArray<>();
        private SparseIntArray mVisibilityArray = new SparseIntArray();
        private SparseArray<Drawable> mBackgroundArray = new SparseArray<>();
        private SparseArray<Drawable> mImageArray = new SparseArray<>();
        private SparseArray<BaseDialog.OnClickListener> mClickArray = new SparseArray<>();

        // 主题
        private int mThemeResId = -1;
        // 动画
        private int mAnimations = -1;
        // 位置
        private int mGravity = Gravity.CENTER;
        // 宽度和高度
        private int mWidth = WRAP_CONTENT;
        private int mHeight = WRAP_CONTENT;
        // 垂直和水平边距
        private int mVerticalMargin;
        private int mHorizontalMargin;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 延迟执行，一定要在创建了Dialog之后调用（供子类调用）
         */
        protected final boolean post(Runnable r) {
            return mDialog.post(r);
        }

        /**
         * 延迟一段时间执行，一定要在创建了Dialog之后调用（仅供子类调用）
         */
        protected final boolean postDelayed(Runnable r, long delayMillis) {
            return mDialog.postDelayed(r, delayMillis);
        }

        /**
         * 在指定的时间执行，一定要在创建了Dialog之后调用（仅供子类调用）
         */
        protected final boolean postAtTime(Runnable r, long uptimeMillis) {
            return mDialog.postAtTime(r, uptimeMillis);
        }

        /**
         * 是否设置了取消（仅供子类调用）
         */
        protected boolean isCancelable() {
            return mCancelable;
        }

        /**
         * 获取上下文对象（仅供子类调用）
         */
        protected Context getContext() {
            return mContext;
        }

        /**
         * 获取 Dialog 重心（仅供子类调用）
         */
        protected int getGravity() {
            return mGravity;
        }

        /**
         * 获取资源对象（仅供子类调用）
         */
        protected Resources getResources() {
            return mContext.getResources();
        }

        /**
         * 根据 id 获取一个文本（仅供子类调用）
         */
        protected CharSequence getText(@StringRes int resId) {
            return mContext.getText(resId);
        }

        /**
         * 根据 id 获取一个 String（仅供子类调用）
         */
        protected String getString(@StringRes int resId) {
            return mContext.getString(resId);
        }

        /**
         * 根据 id 获取一个颜色（仅供子类调用）
         */
        protected int getColor(@ColorRes int id) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mContext.getColor(id);
            }else {
                return mContext.getResources().getColor(id);
            }
        }

        /**
         * 根据 id 获取一个 Drawable（仅供子类调用）
         */
        protected Drawable getDrawable(@DrawableRes int id) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return mContext.getDrawable(id);
            }else {
                return mContext.getResources().getDrawable(id);
            }
        }

        /**
         * 根据 id 查找 View（仅供子类调用）
         */
        protected <V extends View> V findViewById(@IdRes int id) {
            return mContentView.findViewById(id);
        }

        /**
         * 获取当前 Dialog 对象（仅供子类调用）
         */
        protected BaseDialog getDialog() {
            return mDialog;
        }

        /**
         * 销毁当前 Dialog（仅供子类调用）
         */
        protected void dismiss() {
            mDialog.dismiss();
        }

        /**
         * 设置主题 id
         */
        public B setThemeStyle(@StyleRes int themeResId) {
            mThemeResId = themeResId;
            return (B) this;
        }

        /**
         * 设置布局
         */
        public B setContentView(@LayoutRes int layoutId) {
            return setContentView(LayoutInflater.from(mContext).inflate(layoutId, null));
        }
        public B setContentView(@NonNull View view) {
            mContentView = view;
            return (B) this;
        }

        /**
         * 设置重心位置
         */
        public B setGravity(int gravity) {
            // 适配 Android 4.2 新特性，布局反方向（开发者选项 - 强制使用从右到左的布局方向）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gravity = Gravity.getAbsoluteGravity(gravity, mContext.getResources().getConfiguration().getLayoutDirection());
            }
            mGravity = gravity;
            if (mAnimations == -1) {
                switch (mGravity) {
                    case Gravity.TOP:
                        mAnimations = AnimStyle.TOP;
                        break;
                    case Gravity.BOTTOM:
                        mAnimations = AnimStyle.BOTTOM;
                        break;
                    case Gravity.LEFT:
                        mAnimations = AnimStyle.LEFT;
                        break;
                    case Gravity.RIGHT:
                        mAnimations = AnimStyle.RIGHT;
                        break;
                }
            }
            return (B) this;
        }

        /**
         * 设置宽度
         */
        public B setWidth(int width) {
            mWidth = width;
            return (B) this;
        }

        /**
         * 设置高度
         */
        public B setHeight(int height) {
            mHeight = height;
            return (B) this;
        }

        /**
         * 是否可以取消
         */
        public B setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return (B) this;
        }

        /**
         * 设置动画，已经封装好几种样式，具体可见{@link AnimStyle}类
         */
        public B setAnimStyle(@StyleRes int resId) {
            mAnimations = resId;
            return (B) this;
        }

        /**
         * 设置垂直间距
         */
        public B setVerticalMargin(int margin) {
            mVerticalMargin = margin;
            return (B) this;
        }

        /**
         * 设置水平间距
         */
        public B setHorizontalMargin(int margin) {
            mHorizontalMargin = margin;
            return (B) this;
        }

        /**
         * 添加显示监听
         */
        public B addOnShowListener(@NonNull BaseDialog.OnShowListener listener) {
            if (mOnShowListeners == null) {
                mOnShowListeners = new ArrayList<>();
            }
            mOnShowListeners.add(listener);
            return (B) this;
        }

        /**
         * 添加取消监听
         */
        public B addOnCancelListener(@NonNull BaseDialog.OnCancelListener listener) {
            if (mOnCancelListeners == null) {
                mOnCancelListeners = new ArrayList<>();
            }
            mOnCancelListeners.add(listener);
            return (B) this;
        }

        /**
         * 添加销毁监听
         */
        public B addOnDismissListener(@NonNull BaseDialog.OnDismissListener listener) {
            if (mOnDismissListeners == null) {
                mOnDismissListeners = new ArrayList<>();
            }
            mOnDismissListeners.add(listener);
            return (B) this;
        }

        /**
         * 设置按键监听
         */
        public B setOnKeyListener(@NonNull OnKeyListener onKeyListener) {
            mOnKeyListener = onKeyListener;
            return (B) this;
        }

        /**
         * 设置文本
         */
        public B setText(@IdRes int id, @StringRes int resId) {
            return setText(id, mContext.getResources().getString(resId));
        }
        public B setText(@IdRes int id, CharSequence text) {
            mTextArray.put(id, text);
            return (B) this;
        }

        /**
         * 设置可见状态
         */
        public B setVisibility(@IdRes int id, int visibility) {
            mVisibilityArray.put(id, visibility);
            return (B) this;
        }

        /**
         * 设置背景
         */
        public B setBackground(@IdRes int id, @DrawableRes int resId) {
            return setBackground(id, mContext.getResources().getDrawable(resId));
        }
        public B setBackground(@IdRes int id, Drawable drawable) {
            mBackgroundArray.put(id, drawable);
            return (B) this;
        }

        /**
         * 设置图片
         */
        public B setImageDrawable(@IdRes int id, @DrawableRes int resId) {
            return setBackground(id, mContext.getResources().getDrawable(resId));
        }
        public B setImageDrawable(@IdRes int id, Drawable drawable) {
            mImageArray.put(id, drawable);
            return (B) this;
        }

        /**
         * 设置点击事件
         */
        public B setOnClickListener(@IdRes int id, @NonNull BaseDialog.OnClickListener listener) {
            mClickArray.put(id, listener);
            return (B) this;
        }

        /**
         * 创建
         */
        public BaseDialog create() {

            // 判断布局是否为空
            if (mContentView == null) {
                throw new IllegalArgumentException("Dialog layout cannot be empty");
            }

            ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            if (layoutParams != null) {

                if (mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    mWidth = layoutParams.width;
                }
                if (mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    mHeight = layoutParams.height;
                }
            }

//            // 判断有没有设置主题
//            if (mThemeResId == -1) {
//                mDialog = new BaseDialog(mContext);
//            } else {
//                mDialog = new BaseDialog(mContext, mThemeResId);
//            }

            mDialog = createDialog(mContext, mThemeResId);

            mDialog.setContentView(mContentView);

            mDialog.setCancelable(mCancelable);
            if (mCancelable) {
                mDialog.setCanceledOnTouchOutside(true);
            }

            if (mOnShowListeners != null) {
                mDialog.setOnShowListeners(mOnShowListeners);
            }

            if (mOnCancelListeners != null) {
                mDialog.setOnCancelListeners(mOnCancelListeners);
            }

            if (mOnDismissListeners != null) {
                mDialog.setOnDismissListeners(mOnDismissListeners);
            }

            if (mOnKeyListener != null) {
                mDialog.setOnKeyListener(mOnKeyListener);
            }

            // 判断有没有设置动画
            if (mAnimations == -1) {
                // 没有的话就设置默认的动画
                mAnimations = AnimStyle.DEFAULT;
            }

            // 设置参数
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            params.gravity = mGravity;
            params.windowAnimations = mAnimations;
            params.horizontalMargin = mHorizontalMargin;
            params.verticalMargin = mVerticalMargin;
            mDialog.getWindow().setAttributes(params);

            // 设置文本
            for (int i = 0; i < mTextArray.size(); i++) {
                ((TextView) mContentView.findViewById(mTextArray.keyAt(i))).setText(mTextArray.valueAt(i));
            }

            // 设置可见状态
            for (int i = 0; i < mVisibilityArray.size(); i++) {
                mContentView.findViewById(mVisibilityArray.keyAt(i)).setVisibility(mVisibilityArray.valueAt(i));
            }

            // 设置背景
            for (int i = 0; i < mBackgroundArray.size(); i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mContentView.findViewById(mBackgroundArray.keyAt(i)).setBackground(mBackgroundArray.valueAt(i));
                }else {
                    mContentView.findViewById(mBackgroundArray.keyAt(i)).setBackgroundDrawable(mBackgroundArray.valueAt(i));
                }
            }

            // 设置图片
            for (int i = 0; i < mImageArray.size(); i++) {
                ((ImageView) mContentView.findViewById(mImageArray.keyAt(i))).setImageDrawable(mImageArray.valueAt(i));
            }

            // 设置点击事件
            for (int i = 0; i < mClickArray.size(); i++) {
                mContentView.findViewById(mClickArray.keyAt(i)).setOnClickListener(new ViewClickWrapper(mDialog, mClickArray.valueAt(i)));
            }

            return mDialog;
        }

        /**
         * 创建对话框对象（子类可以重写此方法来改变 Dialog 类型）
         */
        protected BaseDialog createDialog(Context context, int themeResId) {
            return new BaseDialog(context, themeResId);
        }

        /**
         * 显示
         */
        public BaseDialog show() {
            final BaseDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    public interface OnClickListener<V extends View> {
        void onClick(BaseDialog dialog, V view);
    }

    public interface OnShowListener {
        void onShow(BaseDialog dialog);
    }

    public interface OnCancelListener {
        void onCancel(BaseDialog dialog);
    }

    public interface OnDismissListener {
        void onDismiss(BaseDialog dialog);
    }

    /**
     * 点击事件包装类
     */
    private static final class ViewClickWrapper implements View.OnClickListener {

        private final BaseDialog mDialog;
        private final BaseDialog.OnClickListener mListener;

        private ViewClickWrapper(BaseDialog dialog, BaseDialog.OnClickListener listener) {
            mDialog = dialog;
            mListener = listener;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final void onClick(View v) {
            mListener.onClick(mDialog, v);
        }
    }

    /**
     * 显示监听包装类
     */
    private static final class ShowListenerWrapper implements BaseDialog.OnShowListener {

        private final DialogInterface.OnShowListener mListener;

        private ShowListenerWrapper(DialogInterface.OnShowListener listener) {
            mListener = listener;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            mListener.onShow(dialog);
        }
    }

    /**
     * 取消监听包装类
     */
    private static final class CancelListenerWrapper implements BaseDialog.OnCancelListener {

        private final DialogInterface.OnCancelListener mListener;

        private CancelListenerWrapper(DialogInterface.OnCancelListener listener) {
            mListener = listener;
        }

        @Override
        public void onCancel(BaseDialog dialog) {
            mListener.onCancel(dialog);
        }
    }

    /**
     * 销毁监听包装类
     */
    private static final class DismissListenerWrapper implements BaseDialog.OnDismissListener {

        private final DialogInterface.OnDismissListener mListener;

        private DismissListenerWrapper(DialogInterface.OnDismissListener listener) {
            mListener = listener;
        }

        @Override
        public void onDismiss(BaseDialog dialog) {
            mListener.onDismiss(dialog);
        }
    }
}