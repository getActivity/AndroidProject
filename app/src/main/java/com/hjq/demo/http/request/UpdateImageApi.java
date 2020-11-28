package com.hjq.demo.http.request;

import com.hjq.http.config.IRequestApi;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 上传图片
 */
public final class UpdateImageApi implements IRequestApi {

    @Override
    public String getApi() {
        return "update/image";
    }

    /** 图片文件 */
    private File image;

    public UpdateImageApi setImage(File image) {
        this.image = image;
        return this;
    }
}