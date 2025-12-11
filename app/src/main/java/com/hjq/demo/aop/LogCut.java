package com.hjq.demo.aop;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.os.Trace;
import androidx.annotation.NonNull;
import com.flyjingfish.android_aop_annotation.ProceedJoinPoint;
import com.flyjingfish.android_aop_annotation.base.BasePointCut;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/06
 *    desc   : 日志切面
 */
public class LogCut implements BasePointCut<Log> {

    @Override
    public Object invoke(@NonNull ProceedJoinPoint joinPoint, @NonNull Log anno) throws Throwable {
        enterMethod(joinPoint, anno);
        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        exitMethod(joinPoint, anno, result, TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos));
        return result;
    }

    @SuppressLint("UnclosedTrace")
    private void enterMethod(ProceedJoinPoint joinPoint, Log log) {
        String className = joinPoint.getTarget() != null ? joinPoint.getTarget().getClass().getName() : "";
        String methodName = joinPoint.getTargetMethod().getName();
        String[] parameterNames = null;
        Object[] parameterValues = joinPoint.getArgs();

        StringBuilder builder = getMethodLogInfo(className, methodName, parameterNames, parameterValues);
        log(log.value(), builder.toString());
        final String section = builder.substring(2);
        Trace.beginSection(section);
    }

    @NonNull
    private StringBuilder getMethodLogInfo(String className, String methodName, String[] parameterNames, Object[] parameterValues) {
        StringBuilder builder = new StringBuilder("\u21E2 ");
        builder.append(className).append(".").append(methodName).append('(');
        if (parameterValues != null && parameterNames != null) {
            for (int i = 0; i < parameterValues.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(parameterNames[i]).append('=');
                builder.append(parameterValues[i]);
            }
        }
        builder.append(')');
        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }
        return builder;
    }

    private void exitMethod(ProceedJoinPoint joinPoint, Log log, Object result, long lengthMillis) {
        Trace.endSection();
        String className = joinPoint.getTarget() != null ? joinPoint.getTarget().getClass().getName() : "";
        String methodName = joinPoint.getTargetMethod().getName();
        StringBuilder builder = new StringBuilder("\u21E0 ")
                .append(className)
                .append('.')
                .append(methodName)
                .append(" [")
                .append(lengthMillis)
                .append("ms]");
        if (result != null) {
            builder.append(" = ").append(result);
        }
        log(log.value(), builder.toString());
    }

    private void log(String tag, String msg) {
        Timber.tag(tag);
        Timber.d(msg);
    }
}