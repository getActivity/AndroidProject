package com.hjq.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/24
 *    desc   : Dialog 基类
 */
public class BaseDialog extends AppCompatDialog {

    public BaseDialog(Context context) {
        this(context, R.style.BaseDialogStyle);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

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

        public Builder(Context context) {
            this(context, -1);
        }

        public Builder(Context context, int themeResId) {
            mContext = context;
            mThemeResId = themeResId;
        }

        /**
         * 设置布局
         */
        public Builder setContentView(View view) {
            mContentView = view;
            return this;
        }

        /**
         * 设置布局
         */
        public Builder setContentView(int layoutId) {
            mContentView = LayoutInflater.from(mContext).inflate(layoutId, null);
            return this;
        }

        /**
         * 设置重心位置
         */
        public Builder setGravity(int gravity) {
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
            return this;
        }

        /**
         * 设置宽度
         */
        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        /**
         * 设置高度
         */
        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        /**
         * 是否可以取消
         */
        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * 设置动画，已经封装好几种样式，具体可见{@link AnimStyle}类
         */
        public Builder setAnimStyle(int resId) {
            mAnimations = resId;
            return this;
        }

        /**
         * 占满宽度
         */
        public Builder fullWidth() {
            mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /**
         * 占满高度
         */
        public Builder fullHeight() {
            mHeight = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /**
         * 设置取消监听
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * 设置销毁监听
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * 设置按键监听
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * 设置文本
         */
        public Builder setText(int viewId, int resId) {
            return setText(viewId, mContext.getResources().getString(resId));
        }

        /**
         * 设置文本
         */
        public Builder setText(int viewId, CharSequence text) {
            mTextArray.put(viewId, text);
            return this;
        }

        /**
         * 设置可见状态
         */
        public Builder setVisibility(int viewId, int visibility) {
            mVisibilityArray.put(viewId, visibility);
            return this;
        }

        /**
         * 设置设置背景
         */
        public Builder setBackground(int viewId, int resId) {
            return setBackground(viewId, mContext.getResources().getDrawable(resId));
        }

        /**
         * 设置背景
         */
        public Builder setBackground(int viewId, Drawable drawable) {
            mBackgroundArray.put(viewId, drawable);
            return this;
        }

        /**
         * 设置设置背景
         */
        public Builder setImageDrawable(int viewId, int resId) {
            return setBackground(viewId, mContext.getResources().getDrawable(resId));
        }

        /**
         * 设置背景
         */
        public Builder setImageDrawable(int viewId, Drawable drawable) {
            mImageArray.put(viewId, drawable);
            return this;
        }

        /**
         * 设置点击事件
         */
        public Builder setOnClickListener(int view, BaseDialog.OnClickListener listener) {
            mClickArray.put(view, listener);
            return this;
        }

        /**
         * 创建
         */
        public BaseDialog create() {

            // 判断布局是否为空
            if (mContentView == null) {
                throw new IllegalArgumentException("Dialog layout cannot be empty");
            }

            final BaseDialog dialog;

            // 判断有没有设置主题
            if (mThemeResId == -1) {
                dialog = new BaseDialog(mContext);
            } else {
                dialog = new BaseDialog(mContext, mThemeResId);
            }

            dialog.setContentView(mContentView);

            dialog.setCancelable(mCancelable);
            if (mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }

            if (mOnCancelListener != null) {
                dialog.setOnCancelListener(mOnCancelListener);
            }

            if (mOnDismissListener != null) {
                dialog.setOnDismissListener(mOnDismissListener);
            }

            if (mOnKeyListener != null) {
                dialog.setOnKeyListener(mOnKeyListener);
            }

            // 判断有没有设置动画
            if (mAnimations == -1) {
                // 没有的话就设置默认的动画
                mAnimations = AnimStyle.DEFAULT;
            }

            // 设置参数
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            params.gravity = mGravity;
            params.windowAnimations = mAnimations;
            dialog.getWindow().setAttributes(params);

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
                new ViewClickHandler(dialog, mContentView.findViewById(mClickArray.keyAt(i)), mClickArray.valueAt(i));
            }

            return dialog;
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
        void onClick(BaseDialog dialog, V view);
    }

    public static final class AnimStyle {

        // 默认动画效果
        static final int DEFAULT = R.style.DialogScaleAnim;

        // 缩放动画
        public static final int SCALE = R.style.DialogScaleAnim;

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