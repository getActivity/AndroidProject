package com.hjq.demo.widget.webview;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.permission.PermissionDescription;
import com.hjq.demo.permission.PermissionInterceptor;
import com.hjq.demo.ui.dialog.common.InputDialog;
import com.hjq.demo.ui.dialog.common.MessageDialog;
import com.hjq.demo.ui.dialog.common.TipsDialog;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/24
 *    desc   : 基于原生 WebChromeClient 封装
 */
public class BrowserChromeClient extends WebChromeClient {

    @NonNull
    private final BrowserView mBrowserView;

    public BrowserChromeClient(@NonNull BrowserView view) {
        mBrowserView = view;
    }

    @Override
    public void onProgressChanged(@NonNull WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        log(String.format("onProgressChanged: newProgress = %s", newProgress));
    }

    /**
     * 网页在控制台打印日志时回调
     */
    @Override
    public boolean onConsoleMessage(@NonNull ConsoleMessage consoleMessage) {
        int priority = -1;
        switch (consoleMessage.messageLevel()) {
            case TIP:
            case LOG:
                priority = Log.INFO;
                break;
            case WARNING:
                priority = Log.WARN;
                break;
            case ERROR:
                priority = Log.ERROR;
                break;
            case DEBUG:
                priority = Log.DEBUG;
                break;
            default:
                break;
        }
        if (priority > 0) {
            // 打印一份网页的日志到 Logcat 上面
            Timber.log(priority, "onConsoleMessage: lineNumber = %s, sourceId = %s, message = %s",
                String.valueOf(consoleMessage.lineNumber()), consoleMessage.sourceId(), consoleMessage.message());
        }
        return super.onConsoleMessage(consoleMessage);
    }

    /**
     * 请求权限
     */
    @Override
    public void onPermissionRequest(@NonNull PermissionRequest request) {
        log(String.format("onPermissionRequest: requestOrigin = %s, requestResources = %s",
            request.getOrigin(), Arrays.toString(request.getResources())));
        List<IPermission> permissions = new ArrayList<>();
        String[] requestResources = request.getResources();
        if (requestResources == null) {
            // 如果网页请求的资源为空
            request.deny();
            return;
        }

        for (String resource : requestResources) {

            // 如果网页请求的是摄像头资源
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(resource)) {
                permissions.add(PermissionLists.getCameraPermission());
                continue;
            }

            // 如果网页请求的是麦克风资源
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(resource)) {
                permissions.add(PermissionLists.getRecordAudioPermission());
                continue;
            }

            // 如果网页请求的是别的资源
            request.deny();
            return;
        }

        if (permissions.isEmpty()) {
            // 如果网页请求的是别的资源
            request.deny();
            return;
        }

        Activity activity = mBrowserView.getActivity();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            if (XXPermissions.isGrantedPermissions(mBrowserView.getContext(), permissions)) {
                request.grant(requestResources);
            } else {
                request.deny();
            }
            return;
        }

        XXPermissions.with(activity)
            .permissions(permissions)
            .interceptor(new PermissionInterceptor())
            .description(new PermissionDescription())
            .request((grantedList, deniedList) -> {
                boolean allGranted = deniedList.isEmpty();
                if (!allGranted) {
                    request.deny();
                    return;
                }
                request.grant(requestResources);
            });
    }

    /**
     * 网页弹出警告框
     */
    @Override
    public boolean onJsAlert(@NonNull WebView view, @NonNull String url, @NonNull String message, @Nullable JsResult result) {
        log(String.format("onJsAlert: url = %s, message = %s", url, message));
        Activity activity = mBrowserView.getActivity();
        if (activity == null) {
            return false;
        }

        new TipsDialog.Builder(activity)
            .setIcon(TipsDialog.ICON_WARNING)
            .setMessage(message)
            .setCancelable(false)
            .addOnDismissListener(dialog -> {
                log("onJsAlert: call result.confirm()");
                if (result == null) {
                    return;
                }
                result.confirm();
            })
            .show();
        return true;
    }

    /**
     * 网页弹出确定取消框
     */
    @Override
    public boolean onJsConfirm(@NonNull WebView view, @NonNull String url, @NonNull String message, @Nullable JsResult result) {
        log(String.format("onJsConfirm: url = %s, message = %s", url, message));
        Activity activity = mBrowserView.getActivity();
        if (activity == null) {
            return false;
        }

        new MessageDialog.Builder(activity)
            .setMessage(message)
            .setCancelable(false)
            .setListener(new MessageDialog.OnListener() {

                @Override
                public void onConfirm(@NonNull BaseDialog dialog) {
                    log("onJsConfirm: call result.confirm()");
                    if (result == null) {
                        return;
                    }
                    result.confirm();
                }

                @Override
                public void onCancel(@NonNull BaseDialog dialog) {
                    log("onJsConfirm: call result.cancel()");
                    if (result == null) {
                        return;
                    }
                    result.cancel();
                }
            })
            .show();
        return true;
    }

    /**
     * 网页弹出输入框
     */
    @Override
    public boolean onJsPrompt(@NonNull WebView view, @NonNull String url, @NonNull String message,
                              @NonNull String defaultValue, @Nullable JsPromptResult result) {
        log(String.format("onJsPrompt: url = %s, message = %s, defaultValue = %s", url, message, defaultValue));
        Activity activity = mBrowserView.getActivity();
        if (activity == null) {
            return false;
        }

        new InputDialog.Builder(activity)
            .setContent(defaultValue)
            .setHint(message)
            .setCancelable(false)
            .setListener(new InputDialog.OnListener() {

                @Override
                public void onConfirm(@NonNull BaseDialog dialog, String content) {
                    log(String.format("onJsPrompt: call result.confirm(%s)", content));
                    if (result == null) {
                        return;
                    }
                    result.confirm(content);
                }

                @Override
                public void onCancel(@NonNull BaseDialog dialog) {
                    log("onJsPrompt: call result.cancel()");
                    if (result == null) {
                        return;
                    }
                    result.cancel();
                }
            })
            .show();
        return true;
    }

    /**
     * 网页请求定位功能
     * 测试地址：https://map.baidu.com/
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(@NonNull String origin, @Nullable GeolocationPermissions.Callback callback) {
        log(String.format("onGeolocationPermissionsShowPrompt: origin = %s", origin));
        Activity activity = mBrowserView.getActivity();
        if (activity == null) {
            return;
        }

        new MessageDialog.Builder(activity)
            .setMessage(R.string.common_web_location_permission_title)
            .setConfirm(R.string.common_web_location_permission_allow)
            .setCancel(R.string.common_web_location_permission_reject)
            .setCancelable(false)
            .setListener(new MessageDialog.OnListener() {

                @Override
                public void onConfirm(@NonNull BaseDialog dialog) {
                    XXPermissions.with(activity)
                        .permission(PermissionLists.getAccessFineLocationPermission())
                        .permission(PermissionLists.getAccessCoarseLocationPermission())
                        .interceptor(new PermissionInterceptor())
                        .description(new PermissionDescription())
                        .request((grantedList, deniedList) -> {
                            boolean allGranted = deniedList.isEmpty();
                            if (!allGranted) {
                                return;
                            }
                            log(String.format("onGeolocationPermissionsShowPrompt: callback.invoke(%s, true, true)", origin));
                            if (callback == null) {
                                return;
                            }
                            callback.invoke(origin, true, true);
                        });
                }

                @Override
                public void onCancel(@NonNull BaseDialog dialog) {
                    log(String.format("onGeolocationPermissionsShowPrompt: callback.invoke(%s, false, true)", origin));
                    if (callback == null) {
                        return;
                    }
                    callback.invoke(origin, false, true);
                }
            })
            .show();
    }

    /**
     * 网页弹出选择文件请求
     * 测试地址：https://app.xunjiepdf.com/jpg2pdf/、http://www.script-tutorials.com/demos/199/index.html
     *
     * @param callback              文件选择回调
     * @param params                文件选择参数
     */
    @Override
    public boolean onShowFileChooser(@NonNull WebView webView, @Nullable ValueCallback<Uri[]> callback, @NonNull FileChooserParams params) {
        log(String.format("onShowFileChooser: paramsTitle = %s, paramsMode = %s, paramsFilenameHint = %s, paramsAcceptTypes = %s",
            params.getTitle(), params.getMode(), params.getFilenameHint(), Arrays.toString(params.getAcceptTypes())));

        Activity activity = mBrowserView.getActivity();
        if (!(activity instanceof BaseActivity)) {
            return false;
        }

        openSystemFileChooser((BaseActivity) activity, params, callback);
        return true;
    }

    /**
     * 打开系统文件选择器
     */
    private void openSystemFileChooser(@NonNull BaseActivity activity, @NonNull FileChooserParams params, @Nullable ValueCallback<Uri[]> callback) {
        Intent intent = params.createIntent();
        // 是否是多选模式
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, params.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE);
        Intent chooserIntent = Intent.createChooser(intent, params.getTitle());
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(chooserIntent, (resultCode, data) -> {
            List<Uri> uris = new ArrayList<>();
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    // 如果用户只选择了一个文件
                    uris.add(uri);
                } else {
                    // 如果用户选择了多个文件
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            uris.add(clipData.getItemAt(i).getUri());
                        }
                    }
                }
            }
            Uri[] result = uris.toArray(new Uri[0]);
            log(String.format("onShowFileChooser: callback.onReceiveValue(%s)", Arrays.toString(result)));
            if (callback == null) {
                return;
            }
            // 不管用户最后有没有选择文件，最后必须要调用 onReceiveValue，如果没有调用就会导致网页再次点击上传无响应
            callback.onReceiveValue(result);
        });
    }

    protected void log(String message) {
        Timber.i(message);
    }
}