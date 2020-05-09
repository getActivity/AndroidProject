# Android普遍存在的问题

> 这些问题已经在模板项目中已经被修复好了，具体修复过程如下

#### 修复 Button 在Android 5.1 之后英文字符串自动大写的问题

> 给 Button 添加如下属性即可，模板工程已经把该属性封装到Style中，直接引用style="@style/ButtonStyle"即可

	android:textAllCaps="false"

#### 修复 Button 在设置状态选择器后仍然残留按压阴影的问题

> 给 Button 设置样式如下即可，模板工程已经把该属性封装到Style中，直接引用style="@style/ButtonStyle"即可

	style="Widget.AppCompat.Button.Borderless"

#### 修复某些低配置机型启动页停留在白屏的时间比较长的问题

> 某些低配置机型上出现该问题比较明显，如果配置好的机型则看不出来，添加一个透明的Activity主题样式

    <!-- 解决启动页白屏的问题 -->
    <style name="LauncherTheme" parent="AppTheme">
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
    </style>

> 在清单文件中给启动页的Activity设置主题样式

    <!-- 启动页面（因为使用了LauncherTheme，所以不要给这个Activity设置screenOrientation属性，会导致崩溃） -->
    <activity
        android:name=".ui.activity.LauncherActivity"
        android:theme="@style/LauncherTheme">

        <!-- 程序入口 -->
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <action android:name="android.intent.action.VIEW" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>

    </activity>

> 还有一点需要特别注意，Android 8.0 及之后不允许透明主题的Activity设置屏幕方向，所以请不要给Activity设置该属性，否则会导致崩溃 

#### 修复某些机型在 WebView 长按时布局被顶下来的问题

> 这个问题在只要界面有 WebView 的情况才会发生，在 Android 5.1 经过测试，在 WebView 中长按选择复制文字时，会显示一个类似于 Actionbar 的控件，但是这个东西叫做ActionMode，会将当前 Activity 所在布局顶下去，这时会和我们项目中的标题栏出现冲突，类似于一个界面同时出现了两个标题栏的效果，解决的方法很简单，就是让出现的 ActionMode 悬浮在 Activity上，这样就把项目中的标题栏遮挡住了，不会出现那种类似一个界面出现两种标题栏的效果，当 WebView 取消长按复制文字后，ActionMode也会随之消失

> 如何让 ActionMode 悬浮在 Activity 上呢？其实很简单，在 Application 主题中加入以下属性

    <!--  ActionMode覆盖Actionbar，不顶下来 -->
    <item name="windowActionModeOverlay">true</item>
    <item name="android:windowContentOverlay">@null</item>
    <!--  ActionMode的颜色 -->
    <item name="actionModeBackground">@color/colorPrimary</item>

#### 修复 任务栈中 首页Activity 被重复启动的问题

> 这个问题导致是因为LauncherActivity作为APP的第一个界面，销毁后没有保存任务栈的状态，导致我们在桌面上启动的时候系统误认为当前启动LauncherActivity的任务栈已经被销毁，所以重新创建了新的任务栈并且跳转到LauncherActivity，最终导致用户从桌面点击APP图标时，总是跳转到LauncherActivity而不是HomeActivity

    <!-- 主页界面 -->
    <activity
        android:name=".ui.activity.HomeActivity"
        android:alwaysRetainTaskState="true"
        android:launchMode="singleTop" />

#### 修复 Android 9.0 限制 Http 明文请求的问题

> Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉。
如果当前应用的请求是 http 请求，而非 https ,这样就会导系统禁止当前应用进行该请求，如果 WebView 的 url 用 http 协议，同样会出现加载失败，https 不受影响

> 在 res 下新建一个 xml 目录，然后创建一个名为：network_security_config.xml 文件 ，该文件内容如下

	<?xml version="1.0" encoding="utf-8"?>
	<network-security-config>
	    <base-config cleartextTrafficPermitted="true" />
	</network-security-config>

> 然后在 AndroidManifest.xml application 标签内应用上面的xml配置

	<application
	    android:name=".App"
	    android:icon="@mipmap/ic_launcher"
	    android:label="@string/app_name"
	    android:networkSecurityConfig="@xml/network_security_config"
	    android:roundIcon="@mipmap/ic_launcher_round"
	    android:theme="@style/AppTheme" />

