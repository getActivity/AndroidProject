package com.hjq.demo.permission;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限转换器（根据权限获取对应的名称和说明）
 */
public final class PermissionConverter {

    /** 权限名称映射（为了适配多语种，这里存储的是 StringId，而不是 String） */
    private static final Map<String, Integer> PERMISSION_NAME_MAP = new HashMap<>();

    /** 权限描述映射（为了适配多语种，这里存储的是 StringId，而不是 String） */
    private static final Map<Integer, Integer> PERMISSION_DESCRIPTION_MAP = new HashMap<>();

    static {
        PERMISSION_NAME_MAP.put(PermissionGroups.STORAGE, R.string.common_permission_storage);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_storage, R.string.common_permission_storage_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.IMAGE_AND_VIDEO_MEDIA, R.string.common_permission_image_and_video);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_image_and_video, R.string.common_permission_image_and_video_description);

        PERMISSION_NAME_MAP.put(PermissionNames.READ_MEDIA_AUDIO, R.string.common_permission_music_and_audio);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_music_and_audio, R.string.common_permission_music_and_audio_description);

        PERMISSION_NAME_MAP.put(PermissionNames.CAMERA, R.string.common_permission_camera);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_camera, R.string.common_permission_camera_description);

        PERMISSION_NAME_MAP.put(PermissionNames.RECORD_AUDIO, R.string.common_permission_microphone);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_microphone, R.string.common_permission_microphone_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.NEARBY_DEVICES, R.string.common_permission_nearby_devices);
        // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
        if (AndroidVersion.isAndroid13())  {
            // 需要填充文案：蓝牙权限描述 + WIFI 权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_nearby_devices_description);
        } else {
            // 需要填充文案：蓝牙权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_nearby_devices_description);
        }

        PERMISSION_NAME_MAP.put(PermissionGroups.LOCATION, R.string.common_permission_location);
        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
        if (AndroidVersion.isAndroid13())  {
            // 需要填充文案：前台定位权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_location_description);
        } else if (AndroidVersion.isAndroid12())  {
            // 需要填充文案：前台定位权限描述 + WIFI 权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_location_description);
        } else {
            // 需要填充文案：前台定位权限描述 + 蓝牙权限描述 + WIFI 权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_location_description);
        }

        // 后台定位权限虽然属于定位权限组，但是只要是属于后台权限，都有独属于自己的一套规则
        PERMISSION_NAME_MAP.put(PermissionNames.ACCESS_BACKGROUND_LOCATION, R.string.common_permission_location_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location_background, R.string.common_permission_location_background_description);

        int sensorsPermissionNameStringId;
        if (AndroidVersion.isAndroid16()) {
            sensorsPermissionNameStringId = R.string.common_permission_health_data;
        } else {
            sensorsPermissionNameStringId = R.string.common_permission_body_sensors;
        }
        PERMISSION_NAME_MAP.put(PermissionGroups.SENSORS, sensorsPermissionNameStringId);
        PERMISSION_DESCRIPTION_MAP.put(sensorsPermissionNameStringId, R.string.common_permission_body_sensors_description);

        // 后台传感器权限虽然属于传感器权限组，但是只要是属于后台权限，都有独属于自己的一套规则
        int bodySensorsBackgroundPermissionNameStringId;
        if (AndroidVersion.isAndroid16()) {
            bodySensorsBackgroundPermissionNameStringId = R.string.common_permission_health_data_background;
        } else {
            bodySensorsBackgroundPermissionNameStringId = R.string.common_permission_body_sensors_background;
        }
        PERMISSION_NAME_MAP.put(PermissionNames.BODY_SENSORS_BACKGROUND, bodySensorsBackgroundPermissionNameStringId);
        PERMISSION_DESCRIPTION_MAP.put(bodySensorsBackgroundPermissionNameStringId, R.string.common_permission_body_sensors_background_description);

        // Android 16 这个版本开始，传感器权限被进行了精细化拆分，拆分成了无数个健康权限
        PERMISSION_NAME_MAP.put(PermissionGroups.HEALTH, R.string.common_permission_health_data);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_health_data, R.string.common_permission_health_data_description);

        PERMISSION_NAME_MAP.put(PermissionNames.READ_HEALTH_DATA_IN_BACKGROUND, R.string.common_permission_health_data_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_health_data_background, R.string.common_permission_health_data_background_description);

        PERMISSION_NAME_MAP.put(PermissionNames.READ_HEALTH_DATA_HISTORY, R.string.common_permission_health_data_past);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_health_data_past, R.string.common_permission_health_data_past_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.CALL_LOG, R.string.common_permission_call_logs);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_call_logs, R.string.common_permission_call_logs_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.PHONE, R.string.common_permission_phone);
        // 注意：在 Android 9.0 的时候，读写通话记录权限已经归到一个单独的权限组了，但是在 Android 9.0 之前，读写通话记录权限归属电话权限组
        if (AndroidVersion.isAndroid9())  {
            // 需要填充文案：电话权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_phone, R.string.common_permission_phone_description);
        } else {
            // 需要填充文案：电话权限描述 + 通话记录权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_phone, R.string.common_permission_phone_description);
        }

        PERMISSION_NAME_MAP.put(PermissionGroups.CONTACTS, R.string.common_permission_contacts);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_contacts, R.string.common_permission_contacts_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.CALENDAR, R.string.common_permission_calendar);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_calendar, R.string.common_permission_calendar_description);

        // 注意：在 Android 10 的版本，这个权限的名称为《健身运动权限》，但是到了 Android 11 的时候，这个权限的名称被修改成了《身体活动权限》
        // 没错就改了一下权限的叫法，其他的一切没有变，Google 产品经理真的是闲的蛋疼，但是吐槽归吐槽，框架也要灵活应对一下，避免小白用户跳转到设置页找不到对应的选项
        int activityRecognitionPermissionNameStringId = AndroidVersion.isAndroid11() ? R.string.common_permission_activity_recognition_api30 : R.string.common_permission_activity_recognition_api29;
        PERMISSION_NAME_MAP.put(PermissionNames.ACTIVITY_RECOGNITION, activityRecognitionPermissionNameStringId);
        PERMISSION_DESCRIPTION_MAP.put(activityRecognitionPermissionNameStringId, R.string.common_permission_activity_recognition_description);

        PERMISSION_NAME_MAP.put(PermissionNames.ACCESS_MEDIA_LOCATION, R.string.common_permission_access_media_location_information);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_access_media_location_information, R.string.common_permission_access_media_location_information_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.SMS, R.string.common_permission_sms);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_sms, R.string.common_permission_sms_description);

        PERMISSION_NAME_MAP.put(PermissionNames.GET_INSTALLED_APPS, R.string.common_permission_get_installed_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_get_installed_apps, R.string.common_permission_get_installed_apps_description);

        PERMISSION_NAME_MAP.put(PermissionNames.MANAGE_EXTERNAL_STORAGE, R.string.common_permission_all_file_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_all_file_access, R.string.common_permission_all_file_access_description);

        PERMISSION_NAME_MAP.put(PermissionNames.REQUEST_INSTALL_PACKAGES, R.string.common_permission_install_unknown_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_install_unknown_apps, R.string.common_permission_install_unknown_apps_description);

        PERMISSION_NAME_MAP.put(PermissionNames.SYSTEM_ALERT_WINDOW, R.string.common_permission_display_over_other_apps);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_display_over_other_apps, R.string.common_permission_display_over_other_apps_description);

        PERMISSION_NAME_MAP.put(PermissionNames.WRITE_SETTINGS, R.string.common_permission_modify_system_settings);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_modify_system_settings, R.string.common_permission_modify_system_settings_description);

        PERMISSION_NAME_MAP.put(PermissionNames.NOTIFICATION_SERVICE, R.string.common_permission_allow_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_allow_notifications, R.string.common_permission_allow_notifications_description);

        PERMISSION_NAME_MAP.put(PermissionNames.POST_NOTIFICATIONS, R.string.common_permission_post_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_post_notifications, R.string.common_permission_post_notifications_description);

        PERMISSION_NAME_MAP.put(PermissionNames.BIND_NOTIFICATION_LISTENER_SERVICE, R.string.common_permission_allow_notifications_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_allow_notifications_access, R.string.common_permission_allow_notifications_access_description);

        PERMISSION_NAME_MAP.put(PermissionNames.PACKAGE_USAGE_STATS, R.string.common_permission_apps_with_usage_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_apps_with_usage_access, R.string.common_permission_apps_with_usage_access_description);

        PERMISSION_NAME_MAP.put(PermissionNames.SCHEDULE_EXACT_ALARM, R.string.common_permission_alarms_reminders);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_alarms_reminders, R.string.common_permission_alarms_reminders_description);

        PERMISSION_NAME_MAP.put(PermissionNames.ACCESS_NOTIFICATION_POLICY, R.string.common_permission_do_not_disturb_access);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_do_not_disturb_access, R.string.common_permission_do_not_disturb_access_description);

        PERMISSION_NAME_MAP.put(PermissionNames.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, R.string.common_permission_ignore_battery_optimize);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_ignore_battery_optimize, R.string.common_permission_ignore_battery_optimize_description);

        PERMISSION_NAME_MAP.put(PermissionNames.BIND_VPN_SERVICE, R.string.common_permission_vpn);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_vpn, R.string.common_permission_vpn_description);

        PERMISSION_NAME_MAP.put(PermissionNames.PICTURE_IN_PICTURE, R.string.common_permission_picture_in_picture);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_picture_in_picture, R.string.common_permission_picture_in_picture_description);

        PERMISSION_NAME_MAP.put(PermissionNames.USE_FULL_SCREEN_INTENT, R.string.common_permission_full_screen_notifications);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_full_screen_notifications, R.string.common_permission_full_screen_notifications_description);

        PERMISSION_NAME_MAP.put(PermissionNames.BIND_DEVICE_ADMIN, R.string.common_permission_device_admin);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_device_admin, R.string.common_permission_device_admin_description);

        PERMISSION_NAME_MAP.put(PermissionNames.BIND_ACCESSIBILITY_SERVICE, R.string.common_permission_accessibility_service);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_accessibility_service, R.string.common_permission_accessibility_service_description);

        PERMISSION_NAME_MAP.put(PermissionNames.MANAGE_MEDIA, R.string.common_permission_manage_media);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_manage_media, R.string.common_permission_manage_media_description);
    }

    /**
     * 通过权限获得名称
     */
    @NonNull
    public static String getNickNamesByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> permissionNameList = getNickNameListByPermissions(context, permissions, true);

        StringBuilder builder = new StringBuilder();
        for (String permissionName : permissionNameList) {
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (builder.length() == 0) {
                builder.append(permissionName);
            } else {
                builder.append(context.getString(R.string.common_permission_comma))
                    .append(permissionName);
            }
        }
        if (builder.length() == 0) {
            // 如果没有获得到任何信息，则返回一个默认的文本
            return context.getString(R.string.common_permission_unknown);
        }
        return builder.toString();
    }

    @NonNull
    public static List<String> getNickNameListByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions, boolean filterHighVersionPermissions) {
        List<String> permissionNickNameList = new ArrayList<>();
        for (IPermission permission : permissions) {
            // 如果当前设置了过滤高版本权限，并且这个权限是高版本系统才出现的权限，则不继续往下执行
            // 避免出现在低版本上面执行拒绝权限后，连带高版本的名称也一起显示出来，但是在低版本上面是没有这个权限的
            if (filterHighVersionPermissions && permission.getFromAndroidVersion(context) > AndroidVersion.getSdkVersion()) {
                continue;
            }
            String permissionName = getNickNameByPermission(context, permission);
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (permissionNickNameList.contains(permissionName)) {
                continue;
            }
            permissionNickNameList.add(permissionName);
        }
        return permissionNickNameList;
    }

    public static String getNickNameByPermission(@NonNull Context context, @NonNull IPermission permission) {
        Integer permissionNameStringId = getPermissionNickNameStringId(context, permission);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        return context.getString(permissionNameStringId);
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionsByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> descriptionList = getDescriptionListByPermissions(context, permissions);

        StringBuilder builder = new StringBuilder();
        for (String description : descriptionList) {
            if (TextUtils.isEmpty(description)) {
                continue;
            }
            if (builder.length() == 0) {
                builder.append(description);
            } else {
                builder.append("\n")
                    .append(description);
            }
        }
        return builder.toString();
    }

    @NonNull
    public static List<String> getDescriptionListByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> descriptionList = new ArrayList<>();
        for (IPermission permission : permissions) {
            String permissionDescription = getDescriptionByPermission(context, permission);
            if (TextUtils.isEmpty(permissionDescription)) {
                continue;
            }
            if (descriptionList.contains(permissionDescription)) {
                continue;
            }
            descriptionList.add(permissionDescription);
        }
        return descriptionList;
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionByPermission(@NonNull Context context, @NonNull IPermission permission) {
        Integer permissionNameStringId = getPermissionNickNameStringId(context, permission);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        String permissionNickName = context.getString(permissionNameStringId);
        Integer permissionDescriptionStringId = getPermissionDescriptionStringId(permissionNameStringId);
        String permissionDescription;
        if (permissionDescriptionStringId == null || permissionDescriptionStringId == 0) {
            permissionDescription = "";
        } else {
            permissionDescription = context.getString(permissionDescriptionStringId);
        }
        return permissionNickName + context.getString(R.string.common_permission_colon) + permissionDescription;
    }

    /**
     * 获取这个权限对应的别名 StringId
     */
    @Nullable
    public static Integer getPermissionNickNameStringId(@NonNull Context context, @NonNull IPermission permission) {
        String permissionName = permission.getPermissionName();
        String permissionGroup = permission.getPermissionGroup(context);
        Integer permissionNameStringId = PERMISSION_NAME_MAP.get(permissionName);
        if (permissionNameStringId != null && permissionNameStringId > 0) {
            return permissionNameStringId;
        }
        Integer permissionGroupStringId = PERMISSION_NAME_MAP.get(permissionGroup);
        if (permissionGroupStringId != null && permissionGroupStringId > 0) {
            return permissionGroupStringId;
        }
        return permissionNameStringId;
    }

    /**
     * 获取这个权限对应的描述 StringId
     */
    @Nullable
    public static Integer getPermissionDescriptionStringId(@IdRes int permissionNickNameStringId) {
        return PERMISSION_DESCRIPTION_MAP.get(permissionNickNameStringId);
    }
}