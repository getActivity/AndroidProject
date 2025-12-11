package com.hjq.demo.aop;

import androidx.annotation.NonNull;
import com.flyjingfish.android_aop_annotation.ProceedJoinPoint;
import com.flyjingfish.android_aop_annotation.base.BasePointCut;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/06
 *    desc   : 防重复点击切面
 */
public class SingleClickCut implements BasePointCut<SingleClick> {

    private static long lastTime;

    @Nullable
    private static String lastTag;

    @Override
    public Object invoke(@NonNull ProceedJoinPoint joinPoint, @NonNull SingleClick anno) throws Throwable {
        String className = joinPoint.getTarget() != null ? joinPoint.getTarget().getClass().getName() : "";
        String methodName = joinPoint.getTargetMethod().getName();
        Object[] parameterValues = joinPoint.getArgs();

        StringBuilder builder = new StringBuilder(className).append(".").append(methodName).append("(");
        for (int i = 0; i < (parameterValues != null ? parameterValues.length : 0); i++) {
            if (i == 0) {
                builder.append(parameterValues[i]);
            } else {
                builder.append(", ").append(parameterValues[i]);
            }
        }
        builder.append(")");
        String tag = builder.toString();

        long now = System.currentTimeMillis();
        if (now - lastTime < anno.value() && tag.equals(lastTag)) {
            Timber.tag("SingleClick");
            Timber.i("%d 毫秒内发生快速点击：%s", anno.value(), tag);
            return null;
        }
        lastTime = now;
        lastTag = tag;
        return joinPoint.proceed();
    }
}