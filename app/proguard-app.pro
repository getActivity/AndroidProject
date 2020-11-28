# 忽略警告
#-ignorewarning

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/umeng-analytics-v5.2.4.jar

# 不混淆 WebView 的 JS 接口
-keepattributes *JavascriptInterface*
# 不混淆 WebView 的类的所有的内部类
-keepclassmembers class com.hjq.demo.ui.activity.BrowserActivity$*{
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

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
# -keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# AOP
-adaptclassstrings
-keepattributes InnerClasses, EnclosingMethod, Signature, *Annotation*

-keepnames @org.aspectj.lang.annotation.Aspect class * {
    public <methods>;
}

# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**

# 不混淆这个包下的字段名
-keepclassmembernames class com.hjq.demo.http.** {
    <fields>;
}