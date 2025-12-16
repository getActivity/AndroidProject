package com.hjq.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/09/12
 *    desc   : 在 BaseDialog 基础上加上 {@link com.google.android.material.bottomsheet.BottomSheetDialog} 特性
 */
public final class BottomSheetDialog extends BaseDialog
        implements OnTouchListener, View.OnClickListener {

    @NonNull
    private final BottomSheetBehavior<FrameLayout> mBottomSheetBehavior;
    private boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;
    private boolean mCanceledOnTouchOutsideSet;

    public BottomSheetDialog(@NonNull Context context) {
        this(context, R.style.BaseDialogTheme);
    }

    public BottomSheetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mBottomSheetBehavior = new BottomSheetBehavior<>(getContext(), null);
        mBottomSheetBehavior.addBottomSheetCallback(new MyBottomSheetCallback());
        mBottomSheetBehavior.setHideable(mCancelable);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window == null) {
            return;
        }

        // 沉浸式状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // 隐藏底部导航栏
        View decorView = window.getDecorView();
        int uiOptions = decorView.getSystemUiVisibility() |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(wrapContentView(getLayoutInflater().inflate(layoutResId, null, false)));
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(wrapContentView(view));
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable LayoutParams params) {
        view.setLayoutParams(params);
        super.setContentView(wrapContentView(view));
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (mCancelable == cancelable) {
            return;
        }
        mCancelable = cancelable;
        mBottomSheetBehavior.setHideable(cancelable);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            return;
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void cancel() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            super.cancel();
            return;
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !mCancelable) {
            mCancelable = true;
        }
        mCanceledOnTouchOutside = cancel;
        mCanceledOnTouchOutsideSet = true;
    }

    private boolean shouldWindowCloseOnTouchOutside() {
        if (!mCanceledOnTouchOutsideSet) {
            TypedArray array = getContext().obtainStyledAttributes(new int[]{android.R.attr.windowCloseOnTouchOutside});
            mCanceledOnTouchOutside = array.getBoolean(0, true);
            array.recycle();
            mCanceledOnTouchOutsideSet = true;
        }
        return mCanceledOnTouchOutside;
    }

    @SuppressLint("ClickableViewAccessibility")
    private View wrapContentView(@NonNull View view) {
        CoordinatorLayout rootLayout = new CoordinatorLayout(getContext());
        rootLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        View touchView = new View(getContext());
        touchView.setSoundEffectsEnabled(false);
        touchView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        touchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        FrameLayout contentLayout = new FrameLayout(getContext());
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        layoutParams.setBehavior(mBottomSheetBehavior);
        contentLayout.setLayoutParams(layoutParams);
        contentLayout.addView(view);

        rootLayout.addView(touchView);
        rootLayout.addView(contentLayout);

        touchView.setOnClickListener(this);
        ViewCompat.setAccessibilityDelegate(contentLayout, new BehaviorAccessibilityDelegate());
        contentLayout.setOnTouchListener(this);
        return rootLayout;
    }

    public BottomSheetBehavior<FrameLayout> getBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }

    /**
     * {@link View.OnClickListener}
     */
    @Override
    public void onClick(@NonNull View view) {
        if (mCancelable && isShowing() && shouldWindowCloseOnTouchOutside()) {
            cancel();
        }
    }

    /**
     * {@link OnTouchListener}
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(@NonNull View view, @NonNull MotionEvent event) {
        return true;
    }

    private class MyBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // default implementation ignored
        }
    }

    private class BehaviorAccessibilityDelegate extends AccessibilityDelegateCompat {

        @Override
        public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            if (mCancelable) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS);
                info.setDismissable(true);
            } else {
                info.setDismissable(false);
            }
        }

        @Override
        public boolean performAccessibilityAction(@NonNull View host, int action, Bundle args) {
            if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && mCancelable) {
                cancel();
                return true;
            }
            return super.performAccessibilityAction(host, action, args);
        }
    }
}