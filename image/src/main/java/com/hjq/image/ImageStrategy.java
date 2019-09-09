package com.hjq.image;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/12/26
 *    desc   : 图片加载策略接口
 */
public interface ImageStrategy {

    /**
     * 加载图片
     */
    void load(ImageLoader loader);
}