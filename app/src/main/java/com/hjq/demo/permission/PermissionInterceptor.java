package com.hjq.demo.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.toast.Toaster;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/01/04
 *    desc   : 权限申请拦截器
 */
public final class PermissionInterceptor implements OnPermissionInterceptor {

    @Override
    public void onRequestPermissionEnd(@NonNull Activity activity, boolean skipRequest,
                                       @NonNull List<IPermission> requestList,
                                       @NonNull List<IPermission> grantedList,
                                       @NonNull List<IPermission> deniedList,
                                       @Nullable OnPermissionCallback callback) {
        if (callback != null) {
            callback.onResult(grantedList, deniedList);
        }

        if (deniedList.isEmpty()) {
            return;
        }
        boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(activity, deniedList);
        String permissionHint = generatePermissionHint(activity, deniedList, doNotAskAgain);
        if (!doNotAskAgain) {
            // 如果没有勾选不再询问选项，就弹 Toast 提示给用户
            Toaster.show(permissionHint);
            return;
        }

        // 如果勾选了不再询问选项，就弹 Dialog 引导用户去授权
        showPermissionSettingDialog(activity, requestList, deniedList, callback, permissionHint);
    }

    private void showPermissionSettingDialog(@NonNull Activity activity,
                                             @NonNull List<IPermission> requestList,
                                             @NonNull List<IPermission> deniedList,
                                             @Nullable OnPermissionCallback callback,
                                             @NonNull String permissionHint) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        new MessageDialog.Builder(activity)
            .setTitle(R.string.common_permission_alert)
            .setMessage(permissionHint)
            .setConfirm(R.string.common_permission_go_to_authorization)
            .setListener(dialog -> {
                dialog.dismiss();
                XXPermissions.startPermissionActivity(activity, deniedList, (grantedList, deniedList1) -> {
                    List<IPermission> latestDeniedList = XXPermissions.getDeniedPermissions(activity, requestList);
                    boolean allGranted = latestDeniedList.isEmpty();
                    if (!allGranted) {
                        // 递归显示对话框，让提示用户授权，只不过对话框是可取消的，用户不想授权了，随时可以点击返回键或者对话框蒙层来取消显示
                        showPermissionSettingDialog(activity, requestList, latestDeniedList, callback,
                            generatePermissionHint(activity, latestDeniedList, true));
                        return;
                    }

                    if (callback == null) {
                        return;
                    }
                    // 用户全部授权了，回调成功给外层监听器，免得用户还要再发起权限申请
                    callback.onResult(requestList, latestDeniedList);
                });
            })
            .show();
    }

    /**
     * 生成权限提示文案
     */
    @NonNull
    private String generatePermissionHint(@NonNull Activity activity, @NonNull List<IPermission> deniedList, boolean doNotAskAgain) {
        int deniedPermissionCount = deniedList.size();
        int deniedLocationPermissionCount = 0;
        int deniedSensorsPermissionCount = 0;
        int deniedHealthPermissionCount = 0;
        for (IPermission deniedPermission : deniedList) {
            String permissionGroup = deniedPermission.getPermissionGroup(activity);
            if (TextUtils.isEmpty(permissionGroup)) {
                continue;
            }
            if (PermissionGroups.LOCATION.equals(permissionGroup)) {
                deniedLocationPermissionCount++;
            } else if (PermissionGroups.SENSORS.equals(permissionGroup)) {
                deniedSensorsPermissionCount++;
            } else if (XXPermissions.isHealthPermission(deniedPermission)) {
                deniedHealthPermissionCount++;
            }
        }

        if (deniedLocationPermissionCount == deniedPermissionCount && AndroidVersion.isAndroid10()) {
            if (deniedLocationPermissionCount == 1) {
                if (XXPermissions.equalsPermission(deniedList.get(0), PermissionNames.ACCESS_BACKGROUND_LOCATION)) {
                    return activity.getString(R.string.common_permission_fail_hint_1,
                        activity.getString(R.string.common_permission_location_background),
                        getBackgroundPermissionOptionLabel(activity));
                } else if (AndroidVersion.isAndroid12() &&
                    XXPermissions.equalsPermission(deniedList.get(0), PermissionNames.ACCESS_FINE_LOCATION)) {
                    // 如果请求的定位权限中，既包含了精确定位权限，又包含了模糊定位权限或者后台定位权限，
                    // 但是用户只同意了模糊定位权限的情况或者后台定位权限，并没有同意精确定位权限的情况，就提示用户开启确切位置选项
                    // 需要注意的是 Android 12 才将模糊定位权限和精确定位权限的授权选项进行分拆，之前的版本没有区分得那么仔细
                    return activity.getString(R.string.common_permission_fail_hint_3,
                        activity.getString(R.string.common_permission_location_fine),
                        activity.getString(R.string.common_permission_location_fine_option));
                }
            } else {
                if (XXPermissions.containsPermission(deniedList, PermissionNames.ACCESS_BACKGROUND_LOCATION)) {
                    if (AndroidVersion.isAndroid12() &&
                        XXPermissions.containsPermission(deniedList, PermissionNames.ACCESS_FINE_LOCATION)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                            activity.getString(R.string.common_permission_location),
                            getBackgroundPermissionOptionLabel(activity),
                            activity.getString(R.string.common_permission_location_fine_option));
                    } else {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                            activity.getString(R.string.common_permission_location),
                            getBackgroundPermissionOptionLabel(activity));
                    }
                }
            }
        } else if (deniedSensorsPermissionCount == deniedPermissionCount && AndroidVersion.isAndroid13()) {
            if (deniedPermissionCount == 1) {
                if (XXPermissions.equalsPermission(deniedList.get(0), PermissionNames.BODY_SENSORS_BACKGROUND)) {
                    if (AndroidVersion.isAndroid16()) {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                            activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_health_data_background_option));
                    } else {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                            activity.getString(R.string.common_permission_body_sensors_background),
                            getBackgroundPermissionOptionLabel(activity));
                    }
                }
            } else {
                if (doNotAskAgain) {
                    if (AndroidVersion.isAndroid16()) {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                            activity.getString(R.string.common_permission_health_data),
                            activity.getString(R.string.common_permission_allow_all_option));
                    } else {
                        return activity.getString(R.string.common_permission_fail_hint_1,
                            activity.getString(R.string.common_permission_body_sensors),
                            getBackgroundPermissionOptionLabel(activity));
                    }
                }
            }
        } else if (deniedHealthPermissionCount == deniedPermissionCount && AndroidVersion.isAndroid16()) {

            switch (deniedPermissionCount) {
                case 1:
                    if (XXPermissions.equalsPermission(deniedList.get(0), PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_3,
                            activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_health_data_background_option));
                    } else if (XXPermissions.equalsPermission(deniedList.get(0), PermissionNames.READ_HEALTH_DATA_HISTORY)) {
                        return activity.getString(R.string.common_permission_fail_hint_3,
                            activity.getString(R.string.common_permission_health_data_past),
                            activity.getString(R.string.common_permission_health_data_past_option));
                    }
                    break;
                case 2:
                    if (XXPermissions.containsPermission(deniedList, PermissionNames.READ_HEALTH_DATA_HISTORY) &&
                        XXPermissions.containsPermission(deniedList, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_3,
                            activity.getString(R.string.common_permission_health_data_past) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_health_data_past_option) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background_option));
                    } else if (XXPermissions.containsPermission(deniedList, PermissionNames.READ_HEALTH_DATA_HISTORY)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                            activity.getString(R.string.common_permission_health_data) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_past),
                            activity.getString(R.string.common_permission_allow_all_option),
                            activity.getString(R.string.common_permission_health_data_background_option));
                    } else if (XXPermissions.containsPermission(deniedList, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                            activity.getString(R.string.common_permission_health_data) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_allow_all_option),
                            activity.getString(R.string.common_permission_health_data_background_option));
                    }
                    break;
                default:
                    if (XXPermissions.containsPermission(deniedList, PermissionNames.READ_HEALTH_DATA_HISTORY) &&
                        XXPermissions.containsPermission(deniedList, PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND)) {
                        return activity.getString(R.string.common_permission_fail_hint_2,
                            activity.getString(R.string.common_permission_health_data) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_past) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background),
                            activity.getString(R.string.common_permission_allow_all_option),
                            activity.getString(R.string.common_permission_health_data_past_option) + activity.getString(R.string.common_permission_and) + activity.getString(R.string.common_permission_health_data_background_option));
                    }
                    break;
            }
            return activity.getString(R.string.common_permission_fail_hint_1,
                activity.getString(R.string.common_permission_health_data),
                activity.getString(R.string.common_permission_allow_all_option));
        }

        return activity.getString(doNotAskAgain ? R.string.common_permission_fail_assign_hint_1 :
                R.string.common_permission_fail_assign_hint_2,
            PermissionConverter.getNickNamesByPermissions(activity, deniedList));
    }

    /**
     * 获取后台权限的《始终允许》选项的文案
     */
    @NonNull
    private String getBackgroundPermissionOptionLabel(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null && AndroidVersion.isAndroid11()) {
            CharSequence backgroundPermissionOptionLabel = packageManager.getBackgroundPermissionOptionLabel();
            if (!TextUtils.isEmpty(backgroundPermissionOptionLabel)) {
                return backgroundPermissionOptionLabel.toString();
            }
        }

        return context.getString(R.string.common_permission_allow_all_the_time_option);
    }
}