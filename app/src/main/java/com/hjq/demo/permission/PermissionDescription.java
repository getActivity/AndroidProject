package com.hjq.demo.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.demo.R;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import com.hjq.demo.ui.popup.PermissionDescriptionPopup;
import com.hjq.permissions.OnPermissionDescription;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限请求描述实现
 */
public final class PermissionDescription implements OnPermissionDescription {

    /** 消息处理 Handler 对象 */
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /** 权限请求描述弹窗显示类型：Dialog */
    private static final int DESCRIPTION_WINDOW_TYPE_DIALOG = 0;
    /** 权限请求描述弹窗显示类型：PopupWindow */
    private static final int DESCRIPTION_WINDOW_TYPE_POPUP = 1;

    /** 权限请求描述弹窗显示类型 */
    private int mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_DIALOG;

    /** 消息 Token */
    @NonNull
    private final Object mHandlerToken = new Object();

    /** 权限申请说明弹窗 */
    @Nullable
    private PopupWindow mPermissionPopupWindow;

    /** 权限申请说明对话框 */
    @Nullable
    private Dialog mPermissionDialog;

    @Override
    public void askWhetherRequestPermission(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestList,
                                            @NonNull Runnable continueRequestRunnable,
                                            @NonNull Runnable breakRequestRunnable) {
        // 以下情况使用 Dialog 来展示权限说明弹窗，否则使用 PopupWindow 来展示权限说明弹窗
        // 1. 如果请求的权限显示的系统界面是不透明的 Activity
        // 2. 如果当前 Activity 的屏幕是横屏状态的话，要求物理尺寸要够大，否则显示的顶部弹窗会被遮挡住，
        //    设备的物理屏幕尺寸还小于 8.5 寸（目前大多数小屏平板大多数集中在 8、8.7、8.8、10 寸），
        //    实测 8 寸的平板获取到的物理尺寸到只有 7.958788793906728，所以这里的代码判断基本上是针对 8.5 寸及以上的平板做优化。
        if (isActivityLandscape(activity) && getPhysicalScreenSize(activity) < 8.5) {
            mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_DIALOG;
        } else {
            mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_POPUP;
            for (IPermission permission : requestList) {
                if (permission.getPermissionPageType(activity) == PermissionPageType.OPAQUE_ACTIVITY) {
                    mDescriptionWindowType = DESCRIPTION_WINDOW_TYPE_DIALOG;
                }
            }
        }

        if (mDescriptionWindowType == DESCRIPTION_WINDOW_TYPE_POPUP) {
            continueRequestRunnable.run();
            return;
        }

        showDialog(activity, activity.getString(R.string.common_permission_description_title),
            generatePermissionDescription(activity, requestList),
            activity.getString(R.string.common_permission_confirm), dialog -> {
                dialog.dismiss();
                continueRequestRunnable.run();
            });
    }

    @Override
    public void onRequestPermissionStart(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        if (mDescriptionWindowType != DESCRIPTION_WINDOW_TYPE_POPUP) {
            return;
        }

        Runnable showPopupRunnable = () -> showPopupWindow(activity, generatePermissionDescription(activity, requestList));
        // 这里解释一下为什么要延迟一段时间再显示 PopupWindow，这是因为系统没有开放任何 API 给外层直接获取权限是否永久拒绝
        // 目前只有申请过了权限才能通过 shouldShowRequestPermissionRationale 判断是不是永久拒绝，如果此前没有申请过权限，则无法判断
        // 针对这个问题能想到最佳的解决方案是：先申请权限，如果极短的时间内，权限申请没有结束，则证明权限之前没有被用户勾选了《不再询问》
        // 此时系统的权限弹窗正在显示给用户，这个时候再去显示应用的 PopupWindow 权限说明弹窗给用户看，所以这个 PopupWindow 是在发起权限申请后才显示的
        // 这样做是为了避免 PopupWindow 显示了又马上消失，这样就不会出现 PopupWindow 一闪而过的效果，提升用户的视觉体验
        // 最后补充一点：350 毫秒只是一个经验值，经过测试可覆盖大部分机型，具体可根据实际情况进行调整，这里不做强制要求
        // 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/366
        HANDLER.postAtTime(showPopupRunnable, mHandlerToken, SystemClock.uptimeMillis() + 350);
    }

    @Override
    public void onRequestPermissionEnd(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        // 移除跟这个 Token 有关但是没有还没有执行的消息
        HANDLER.removeCallbacksAndMessages(mHandlerToken);
        // 销毁当前正在显示的弹窗
        dismissPopupWindow();
        dismissDialog();
    }

    /**
     * 生成权限描述文案
     */
    private String generatePermissionDescription(@NonNull Activity activity, @NonNull List<IPermission> requestList) {
        return PermissionConverter.getDescriptionsByPermissions(activity, requestList);
    }

    /**
     * 显示 Dialog
     *
     * @param dialogTitle               对话框标题
     * @param dialogMessage             对话框消息
     * @param confirmButtonText         对话框确认按钮文本
     * @param listener                  对话框监听事件
     */
    private void showDialog(@NonNull Activity activity, @Nullable String dialogTitle, @Nullable String dialogMessage,
        @Nullable String confirmButtonText, @Nullable MessageDialog.OnListener listener) {
        if (mPermissionDialog != null) {
            dismissDialog();
        }
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        // 另外这里需要判断 Activity 的类型来申请权限，这是因为只有 AppCompatActivity 才能调用 AndroidX 库的 AlertDialog 来显示，否则会出现报错
        // java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity
        // 为什么不直接用系统包 AlertDialog 来显示，而是两套规则？因为系统包 AlertDialog 是系统自带的类，不同 Android 版本展现的样式可能不太一样
        // 如果这个 Android 版本比较低，那么这个对话框的样式就会变得很丑，准确来讲也不能说丑，而是当时系统的 UI 设计就是那样，它只是跟随系统的样式而已
        mPermissionDialog = new MessageDialog.Builder(activity)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setConfirm(confirmButtonText)
            .setCancelable(false)
            .setListener(listener)
            .create();
        mPermissionDialog.show();
    }

    /**
     * 销毁 Dialog
     */
    private void dismissDialog() {
        if (mPermissionDialog == null) {
            return;
        }
        if (!mPermissionDialog.isShowing()) {
            return;
        }
        mPermissionDialog.dismiss();
        mPermissionDialog = null;
    }

    /**
     * 显示 PopupWindow
     *
     * @param content               弹窗显示的内容
     */
    private void showPopupWindow(@NonNull Activity activity, @NonNull String content) {
        if (mPermissionPopupWindow != null) {
            dismissPopupWindow();
        }
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        mPermissionPopupWindow = new PermissionDescriptionPopup.Builder(activity)
            .setDescription(content)
            .create();
        mPermissionPopupWindow.showAtLocation(decorView, Gravity.TOP, 0, 0);
    }

    /**
     * 销毁 PopupWindow
     */
    private void dismissPopupWindow() {
        if (mPermissionPopupWindow == null) {
            return;
        }
        if (!mPermissionPopupWindow.isShowing()) {
            return;
        }
        mPermissionPopupWindow.dismiss();
        mPermissionPopupWindow = null;
    }

    /**
     * 判断当前 Activity 是否是横盘显示
     */
    public static boolean isActivityLandscape(@NonNull Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 获取当前设备的物理屏幕尺寸
     */
    @SuppressWarnings("deprecation")
    public static double getPhysicalScreenSize(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        if (defaultDisplay == null) {
            return 0;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);

        float screenWidthInInches;
        float screenHeightInInches;
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        screenWidthInInches = point.x / metrics.xdpi;
        screenHeightInInches = point.y / metrics.ydpi;

        // 勾股定理：直角三角形的两条直角边的平方和等于斜边的平方
        return Math.sqrt(Math.pow(screenWidthInInches, 2) + Math.pow(screenHeightInInches, 2));
    }
}