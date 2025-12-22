package com.hjq.demo.other;

import com.hjq.core.tools.AndroidVersion;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/08/12
 *    desc   : 自定义日志打印规则
 */
public final class DebugLoggerTree extends Timber.DebugTree {

    private static final int MAX_TAG_LENGTH = 23;

    /**
     * 创建日志堆栈 TAG
     */
    @Override
    protected String createStackElementTag(@NotNull StackTraceElement element) {
        String tag = "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
        // 日志 TAG 长度限制已经在 Android 8.0 被移除
        if (tag.length() <= MAX_TAG_LENGTH || AndroidVersion.isAndroid8()) {
            return tag;
        }
        return tag.substring(0, MAX_TAG_LENGTH);
    }
}