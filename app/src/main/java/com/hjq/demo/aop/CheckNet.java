package com.hjq.demo.aop;

import com.flyjingfish.android_aop_annotation.anno.AndroidAopPointCut;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/01/11
 *    desc   : 网络检测注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AndroidAopPointCut(CheckNetCut.class)
public @interface CheckNet {}