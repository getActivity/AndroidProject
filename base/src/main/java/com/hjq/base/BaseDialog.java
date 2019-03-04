package com.hjq.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDialog;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/24
 *    desc   : Dialog 基类
 */
public class BaseDialog extends AppCompatDialog {

    // Dialog Cancel 监听
    private DialogInterface.OnCancelListener mOnCancelListener;
    // Dialog Dismiss 监听
    private DialogInterface.OnDismissListener mOnDismissListener;

    public BaseDialog(Context context) {
        this(context, R.style.BaseDialogStyle);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(mOnCancelListener = listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(mOnDismissListener = listener);
    }

    public OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public static class Builder<B extends Builder> {

        private BaseDialog mDialog;

        // Context 对象
        private Context mContext;

        // Dialog 布局
        private View mContentView;

        // Dialog Cancel 监听
        private DialogInterface.OnCancelListener mOnCancelListener;
        // Dialog Dismiss 监听
        private DialogInterface.OnDismissListener mOnDismissListener;
        // Dialog Key 监听
        private DialogInterface.OnKeyListener mOnKeyListener;

        // 点击空白是否能够取消  默认点击阴影可以取消
        private boolean mCancelable = true;

        private SparseArray<CharSequence> mTextArray = new SparseArray<>();
        private SparseArray<Integer> mVisibilityArray = new SparseArray<>();
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
        private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 垂直和水平边距
        private int mVerticalMargin;
        private int mHorizontalMargin;

        public Builder(Context context) {
            this(context, -1);
        }

        public Builder(Context context, int themeResId) {
            mContext = context;
            mThemeResId = themeResId;
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
         * 获取资源对象（仅供子类调用）
         */
        protected Resources getResources() {
            return mContext.getResources();
        }

        /**
         * 根据 id 获取一个文本（仅供子类调用）
         */
        public String getString(@StringRes int resId) {
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
        protected <T extends View> T findViewById(@IdRes int id) {
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
         * 设置布局
         */
        public B setContentView(int layoutId) {
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
        public B setAnimStyle(int resId) {
            mAnimations = resId;
            return (B) this;
        }

        /**
         * 设置垂直间距
         */
        public B setVerticalMargin(int verticalMargin) {
            this.mVerticalMargin = verticalMargin;
            return (B) this;
        }

        /**
         * 设置水平间距
         */
        public B setHorizontalMargin(int horizontalMargin) {
            this.mHorizontalMargin = horizontalMargin;
            return (B) this;
        }

        /**
         * 设置取消监听
         */
        public B setOnCancelListener(OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return (B) this;
        }

        /**
         * 设置销毁监听
         */
        public B setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return (B) this;
        }

        /**
         * 设置按键监听
         */
        public B setOnKeyListener(OnKeyListener onKeyListener) {
            mOnKeyListener = onKeyListener;
            return (B) this;
        }

        /**
         * 设置文本
         */
        public B setText(@IdRes int id, int resId) {
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
        public B setBackground(@IdRes int id, int resId) {
            return setBackground(id, mContext.getResources().getDrawable(resId));
        }
        public B setBackground(@IdRes int id, Drawable drawable) {
            mBackgroundArray.put(id, drawable);
            return (B) this;
        }

        /**
         * 设置图片
         */
        public B setImageDrawable(@IdRes int id, int resId) {
            return setBackground(id, mContext.getResources().getDrawable(resId));
        }
        public B setImageDrawable(@IdRes int id, Drawable drawable) {
            mImageArray.put(id, drawable);
            return (B) this;
        }

        /**
         * 设置点击事件
         */
        public B setOnClickListener(@IdRes int id, BaseDialog.OnClickListener listener) {
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

            // 判断有没有设置主题
            if (mThemeResId == -1) {
                mDialog = new BaseDialog(mContext);
            } else {
                mDialog = new BaseDialog(mContext, mThemeResId);
            }

            mDialog.setContentView(mContentView);

            mDialog.setCancelable(mCancelable);
            if (mCancelable) {
                mDialog.setCanceledOnTouchOutside(true);
            }

            if (mOnCancelListener != null) {
                mDialog.setOnCancelListener(mOnCancelListener);
            }

            if (mOnDismissListener != null) {
                mDialog.setOnDismissListener(mOnDismissListener);
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
                new ViewClickHandler(mDialog, mContentView.findViewById(mClickArray.keyAt(i)), mClickArray.valueAt(i));
            }

            return mDialog;
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

    /**
     * 处理点击事件类
     */
    private static class ViewClickHandler implements View.OnClickListener {

        private final BaseDialog mDialog;
        private final BaseDialog.OnClickListener mListener;

        ViewClickHandler(BaseDialog dialog, View view, BaseDialog.OnClickListener listener) {
            mDialog = dialog;
            mListener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public final void onClick(View v) {
            mListener.onClick(mDialog, v);
        }
    }

    public interface OnClickListener<V extends View> {
        void onClick(Dialog dialog, V view);
    }

    public static final class AnimStyle {

        // 默认动画效果
        static final int DEFAULT = R.style.DialogScaleAnim;

        // 缩放动画
        public static final int SCALE = R.style.DialogScaleAnim;

        // IOS 动画
        public static final int IOS = R.style.DialogIOSAnim;

        // 吐司动画
        public static final int TOAST = android.R.style.Animation_Toast;

        // 顶部弹出动画
        public static final int TOP = R.style.DialogTopAnim;

        // 底部弹出动画
        public static final int BOTTOM = R.style.DialogBottomAnim;

        // 左边弹出动画
        public static final int LEFT = R.style.DialogLeftAnim;

        // 右边弹出动画
        public static final int RIGHT = R.style.DialogRightAnim;
    }
}