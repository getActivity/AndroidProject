package com.hjq.demo.mvp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/05/11
 *    desc   : Mvp 实例化注解
 */
@Target(ElementType.FIELD) // 字段注解
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
public @interface MvpInject {}