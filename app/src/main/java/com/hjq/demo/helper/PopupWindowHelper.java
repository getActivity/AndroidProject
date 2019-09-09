package com.hjq.demo.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : PopupWindow辅助类
 */
public final class PopupWindowHelper implements PopupWindow.OnDismissListener {

    /** PopupWindow 对象 */
    private PopupWindow mPopupWindow;
    /** PopupWindow 显示的 View */
    private final View mPopupView;
    /** 记录PopupWindow销毁时间 */
    private long mDismissTime;

    public PopupWindowHelper(View popupView) {
        mPopupView = popupView;
    }

    public PopupWindowHelper(Context context, int id) {
        mPopupView = View.inflate(context, id, null);
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        // 给PopupWindow的View设置缩放动画
        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(200);
        mPopupView.startAnimation(sa);

        mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 需要在popupWindow使用动画，必须先设置背景，否则动画不能显示出效果，为了不和当前的背景冲突，这里设置全透明背景的图片
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置外部可触的，点击其他地方会自动消失
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 监听PopupWindow销毁监听
        mPopupWindow.setOnDismissListener(this);
    }

    /**
     * {@link PopupWindow.OnDismissListener}
     */
    @Override
    public void onDismiss() {
        // 记录当前销毁的时间
        mDismissTime = System.currentTimeMillis();
    }

    /**
     * 显示一个PopupWindow
     *
     * @param clickView     PopupWindow显示在什么View的下方
     */
    public void show(View clickView) {

        // 如果PopupWindow还未初始化就先进行初始化
        if (mPopupWindow == null) {
            initPopupWindow();
        }

        // 避免用户点击clickView导致的销毁后再次显示的Bug
        if (System.currentTimeMillis() - mDismissTime < 500) {
            return;
        }

        /*
        //获取某个view对象在窗口的位置，然后计算出PopupWindow的位置
        int[] location = new int[2];
        mClickView.getLocationInWindow(location);

        //将PopupWindow显示出来
        mPopupWindow.showAtLocation(mParentView, Gravity.LEFT + Gravity.TOP, 0, location[1] + mClickView.getHeight());
         */

        mPopupWindow.showAsDropDown(clickView);
    }

    /**
     * 销毁当前的PopupWindow
     */
    public void dismiss() {
        if (isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 当前PopupWindow是否已经显示
     */
    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /**
     * 获取当前的PopupWindow对象
     */
    @Nullable
    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    /**
     * 获取当前的PopupWindow的View对象
     */
    @NonNull
    public View getPopupView() {
        return mPopupView;
    }
}
