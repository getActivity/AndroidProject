# 保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# 保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

# 保持 Serializable 不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

# 避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

# Android 混淆的误区
# 网上很多现成并且成熟的混淆规则，大家参考一下即可，但是不要盲目照找，我们现在大家理解每个混淆规则是什么意思，自己参照的时候注意一下。
# 举个例子，我们现在开发 Android 我们肯定会引用 support-v4 、support-v7 ，但是 support 包里面的功能我们不可能全部都使用到。
# 而网上有很多博客中写了 keep 掉 v7 v4 包，其实我们是没有必要的。要知道 v7 包足足有 15000 个左右的方法呢！进行混淆是很有必要的。
# -dontwarn androidx.**


######## 记录生成的日志数据,gradle build时在本项目根目录输出 ########

# apk 包内所有 class 的内部结构
#-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 apk 中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt