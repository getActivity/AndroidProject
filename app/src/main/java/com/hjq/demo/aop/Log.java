package com.hjq.demo.aop;

import com.flyjingfish.android_aop_annotation.anno.AndroidAopPointCut;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/06
 *    desc   : Debug 日志注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AndroidAopPointCut(LogCut.class)
public @interface Log {

    String value() default "AOPLog";
}