# 忽略警告
-ignorewarning

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/umeng-analytics-v5.2.4.jar

# 标题栏框架
#-keep class com.hjq.bar.** {*;}

# 吐司框架
#-keep class com.hjq.toast.** {*;}

# 权限请求框架
#-keep class com.hjq.permissions.** {*;}

# 不混淆 WebView 的 JS 接口
-keepattributes *JavascriptInterface*
# 不混淆 WebView 的类的所有的内部类
-keepclassmembers  class  com.veidy.activity.WebViewActivity$*{
    *;
}
# 不混淆 WebChromeClient 中的 openFileChooser 方法
-keepclassmembers class * extends android.webkit.WebChromeClient{
   public void openFileChooser(...);
}

# EventBus3
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}