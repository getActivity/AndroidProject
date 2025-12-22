package com.hjq.demo.ui.activity.common;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.core.manager.ThreadPoolManager;
import com.hjq.core.tools.AndroidVersion;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.other.AppConfig;
import com.hjq.device.compat.DeviceBrand;
import com.hjq.device.compat.DeviceMarketName;
import com.hjq.device.compat.DeviceOs;
import com.tencent.bugly.library.Bugly;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/06/27
 *    desc   : 崩溃捕捉界面
 */
public final class CrashActivity extends AppActivity {

    private static final String INTENT_KEY_IN_THROWABLE = "throwable";

    /** 系统包前缀列表 */
    private static final String[] SYSTEM_PACKAGE_PREFIX_LIST = new String[]
            {"android", "com.android", "androidx", "com.google.android", "java", "javax", "dalvik", "kotlin"};

    /** 报错代码行数正则表达式 */
    private static final Pattern CODE_REGEX = Pattern.compile("\\(\\w+\\.\\w+:\\d+\\)");

    public static void start(@NonNull Application application, @NonNull Throwable throwable) {
        Intent intent = new Intent(application, CrashActivity.class);
        intent.putExtra(INTENT_KEY_IN_THROWABLE, throwable);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    private TextView mTitleView;
    private DrawerLayout mDrawerLayout;
    private TextView mInfoView;
    private TextView mMessageView;
    private String mStackTrace;

    @Override
    protected int getLayoutId() {
        return R.layout.crash_activity;
    }

    @Override
    protected void initView() {
        mTitleView = findViewById(R.id.tv_crash_title);
        mDrawerLayout = findViewById(R.id.dl_crash_drawer);
        mInfoView = findViewById(R.id.tv_crash_info);
        mMessageView = findViewById(R.id.tv_crash_message);
        setOnClickListener(R.id.iv_crash_info, R.id.iv_crash_share, R.id.iv_crash_restart);

        // 设置状态栏沉浸
        ImmersionBar.setTitleBar(this, findViewById(R.id.ll_crash_info));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void initData() {
        Throwable throwable = getSerializable(INTENT_KEY_IN_THROWABLE);
        if (throwable == null) {
            finish();
            return;
        }

        mTitleView.setText(throwable.getClass().getSimpleName());

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        if (cause != null) {
            cause.printStackTrace(printWriter);
        }
        mStackTrace = stringWriter.toString();
        Matcher matcher = CODE_REGEX.matcher(mStackTrace);
        SpannableStringBuilder spannable = new SpannableStringBuilder(mStackTrace);
        if (spannable.length() > 0) {
            while (matcher.find()) {
                // 不包含左括号（
                int start = matcher.start() + "(".length();
                // 不包含右括号 ）
                int end = matcher.end() - ")".length();

                // 代码信息颜色
                int codeColor = Color.parseColor("#999999");
                int lineIndex = mStackTrace.lastIndexOf("at ", start);
                if (lineIndex != -1) {
                    String lineData = spannable.subSequence(lineIndex, start).toString();
                    if (TextUtils.isEmpty(lineData)) {
                        continue;
                    }
                    // 是否高亮代码行数
                    boolean highlight = true;
                    for (String packagePrefix : SYSTEM_PACKAGE_PREFIX_LIST) {
                        if (lineData.startsWith("at " + packagePrefix)) {
                            highlight = false;
                            break;
                        }
                    }
                    if (highlight) {
                        codeColor = Color.parseColor("#287BDE");
                    }
                }

                // 设置前景
                spannable.setSpan(new ForegroundColorSpan(codeColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 设置下划线
                spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mMessageView.setText(spannable);
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float smallestWidth = Math.min(screenWidth, screenHeight) / displayMetrics.density;

        String targetResource;
        if (displayMetrics.densityDpi > DisplayMetrics.DENSITY_XXHIGH) {
            targetResource = "xxxhdpi";
        } else if (displayMetrics.densityDpi > DisplayMetrics.DENSITY_XHIGH) {
            targetResource = "xxhdpi";
        } else if (displayMetrics.densityDpi > DisplayMetrics.DENSITY_HIGH) {
            targetResource = "xhdpi";
        } else if (displayMetrics.densityDpi > DisplayMetrics.DENSITY_MEDIUM) {
            targetResource = "hdpi";
        } else if (displayMetrics.densityDpi > DisplayMetrics.DENSITY_LOW) {
            targetResource = "mdpi";
        } else {
            targetResource = "ldpi";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("设备品牌：\t").append(DeviceBrand.getBrandName());

        String marketName = DeviceMarketName.getMarketName(this);
        if (!TextUtils.isEmpty(marketName)) {
            builder.append("\n设备名称：\t").append(marketName);
        }

        builder.append("\n设备型号：\t").append(Build.MODEL)
                .append("\n设备类型：\t").append(isTabletDevice() ? "平板" : "手机");

        builder.append("\n屏幕宽高：\t").append(screenWidth).append(" x ").append(screenHeight)
                .append("\n屏幕密度：\t").append(displayMetrics.densityDpi)
                .append("\n密度像素：\t").append(displayMetrics.density)
                .append("\n目标资源：\t").append(targetResource)
                .append("\n最小宽度：\t").append((int) smallestWidth);

        builder.append("\n安卓版本：\t").append(Build.VERSION.RELEASE)
                .append("\nAPI 版本：\t").append(AndroidVersion.getSdkVersion())
                .append("\nCPU 架构：\t").append(Build.SUPPORTED_ABIS[0]);

        String osName = DeviceOs.getOsName();
        if (!TextUtils.isEmpty(osName)) {
            builder.append("\n厂商系统：\t").append(osName);
            String osVersionName = DeviceOs.getOsVersionName();
            if (!TextUtils.isEmpty(osVersionName)) {
                builder.append("\n厂商版本：\t").append(osVersionName);
            }
        }

        builder.append("\n应用版本：\t").append(AppConfig.getVersionName())
                .append("\n版本代码：\t").append(AppConfig.getVersionCode());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            builder.append("\n首次安装：\t").append(dateFormat.format(new Date(packageInfo.firstInstallTime)))
                    .append("\n最近安装：\t").append(dateFormat.format(new Date(packageInfo.lastUpdateTime)))
                    .append("\n崩溃时间：\t").append(dateFormat.format(new Date()));
            List<String> permissionsManifest;
            if (packageInfo.requestedPermissions != null) {
                permissionsManifest = Arrays.asList(packageInfo.requestedPermissions);
            } else {
                permissionsManifest = new ArrayList<>();
            }
            if (permissionsManifest.contains(Manifest.permission.INTERNET)) {
                builder.append("\n当前网络访问：\t");

                ThreadPoolManager.getInstance().execute(() -> {
                    try {
                        InetAddress.getByName("www.baidu.com");
                        builder.append("正常");
                    } catch (UnknownHostException ignored) {
                        builder.append("异常");
                    }
                    post(() -> mInfoView.setText(builder));
                });

            } else {
                mInfoView.setText(builder);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Bugly.handleCatchException(Thread.currentThread(), e, e.getMessage(), null, true);
        }
    }

    @SingleClick
    @Override
    public void onClick(@NonNull View view) {
        int viewId = view.getId();
        if (viewId == R.id.iv_crash_info) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (viewId == R.id.iv_crash_share) {
            // 分享文本
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, mStackTrace);
            Intent chooserIntent = Intent.createChooser(intent, "");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(chooserIntent);
        } else if (viewId == R.id.iv_crash_restart) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        // 重启应用
        RestartActivity.restart(this);
        finish();
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Nullable
    @Override
    public View getImmersionTopView() {
        return findViewById(R.id.ll_crash_bar);
    }

    /**
     * 判断当前设备是否是平板
     */
    public boolean isTabletDevice() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}