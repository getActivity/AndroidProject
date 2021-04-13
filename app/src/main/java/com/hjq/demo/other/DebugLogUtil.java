package com.hjq.demo.other;

import android.util.Log;

public class
DebugLogUtil {

    // 单例访问
    private static DebugLogUtil debugUtil;
    /**
     * 判断是否在进行Debug开发阶段, 默认false
     */
    private boolean isDebug;
    /**
     * 打印日志过滤标示，默认'Application'
     */
    private String Filter;

    private DebugLogUtil() {
        isDebug = true;
        Filter = "DebugLogUtil";
    }

    private static String commonMsg = "";

    /**
     * 创建该类对象唯一的方法
     *
     * @return
     */
    public static DebugLogUtil getInstance() {
        commonMsg = "-------------------------（召唤师，我在" + getAutoJumpLogInfos()[2] + "->" + getAutoJumpLogInfos()[1];
        if (debugUtil == null) {
            synchronized (DebugLogUtil.class) {
                if (debugUtil == null) {
                    debugUtil = new DebugLogUtil();
                }
            }
        }
        return debugUtil;
    }


    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String filter) {
        Filter = filter;
    }

    public void Verbose(String logCat) {
        if (isDebug) {
            Log.v(this.Filter, logCat + commonMsg);
        }
    }

    public void Debug(String logCat) {
        if (isDebug) {
            Log.d(this.Filter, logCat + commonMsg);
        }
    }

    public void Info(String logCat) {
        if (isDebug) {
            Log.i(this.Filter, logCat + commonMsg);
        }
    }

    public void Warn(String logCat) {
        if (isDebug) {
            Log.w(this.Filter, logCat + commonMsg);
        }
    }

    public void Error(String logCat) {
        if (isDebug) {
            Log.e(this.Filter, logCat + commonMsg);
        }
    }


    /**
     * 获取打印信息所在方法名，行号等信息
     *
     * @return
     */
    private static String[] getAutoJumpLogInfos() {
        String[] infos = new String[]{"", "", ""};
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            Log.e("MyLogger", "Stack is too shallow!!!");
            return infos;
        } else {
            infos[0] = elements[4].getClassName().substring(
                    elements[4].getClassName().lastIndexOf(".") + 1);
            infos[1] = elements[4].getMethodName() + "()";
            infos[2] = " at (" + elements[4].getClassName() + ".java:"
                    + elements[4].getLineNumber() + ")";
            return infos;
        }

    }
}