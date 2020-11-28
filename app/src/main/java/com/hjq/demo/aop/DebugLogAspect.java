package com.hjq.demo.aop;

import android.os.Looper;
import android.os.Trace;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hjq.demo.other.AppConfig;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.TimeUnit;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/06
 *    desc   : Debug 日志打印
 */
@Aspect
public class DebugLogAspect {

    /**
     * 构造方法切入点
     */
    @Pointcut("execution(@com.hjq.demo.aop.DebugLog *.new(..))")
    public void constructor() {}

    /**
     * 方法切入点
     */
    @Pointcut("execution(@com.hjq.demo.aop.DebugLog * *(..))")
    public void method() {}

    /**
     * 在连接点进行方法替换
     */
    @Around("(method() || constructor()) && @annotation(debugLog)")
    public Object aroundJoinPoint(ProceedingJoinPoint joinPoint, DebugLog debugLog) throws Throwable {
        enterMethod(joinPoint, debugLog);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();

        exitMethod(joinPoint, debugLog, result, TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos));

        return result;
    }


    /**
     * 方法执行前切入
     */
    private void enterMethod(ProceedingJoinPoint joinPoint, DebugLog debugLog) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        // 方法所在类
        String className = codeSignature.getDeclaringType().getName();
        // 方法名
        String methodName = codeSignature.getName();
        // 方法参数名集合
        String[] parameterNames = codeSignature.getParameterNames();
        // 方法参数集合
        Object[] parameterValues = joinPoint.getArgs();

        //记录并打印方法的信息
        StringBuilder builder = getMethodLogInfo(className, methodName, parameterNames, parameterValues);

        log(debugLog.value(), builder.toString());

        final String section = builder.toString().substring(2);
        Trace.beginSection(section);
    }

    /**
     * 获取方法的日志信息
     *
     * @param className         类名
     * @param methodName        方法名
     * @param parameterNames    方法参数名集合
     * @param parameterValues   方法参数值集合
     */
    @NonNull
    private StringBuilder getMethodLogInfo(String className, String methodName, String[] parameterNames, Object[] parameterValues) {
        StringBuilder builder = new StringBuilder("\u21E2 ");
        builder.append(className)
                .append(".")
                .append(methodName)
                .append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append('=');
            builder.append(parameterValues[i].toString());
        }
        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }
        return builder;
    }


    /**
     * 方法执行完毕，切出
     *
     * @param result            方法执行后的结果
     * @param lengthMillis      执行方法所需要的时间
     */
    private void exitMethod(ProceedingJoinPoint joinPoint, DebugLog debugLog, Object result, long lengthMillis) {
        if (!AppConfig.isDebug()) {
            return;
        }

        Trace.endSection();

        Signature signature = joinPoint.getSignature();

        String className = signature.getDeclaringType().getName();
        String methodName = signature.getName();

        StringBuilder builder = new StringBuilder("\u21E0 ")
                .append(className)
                .append(".")
                .append(methodName)
                .append(" [")
                .append(lengthMillis)
                .append("ms]");

        //  判断方法是否有返回值
        if (signature instanceof MethodSignature && ((MethodSignature) signature).getReturnType() != void.class) {
            builder.append(" = ");
            builder.append(result.toString());
        }

        log(debugLog.value(), builder.toString());
    }

    private void log(String tag, String msg) {
        Log.d(tag, msg);
    }
}