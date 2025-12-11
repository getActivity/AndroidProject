package com.hjq.demo.http.model;

import androidx.annotation.NonNull;
import com.hjq.demo.other.AppConfig;
import com.hjq.http.config.IRequestBodyStrategy;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.RequestBodyType;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/02
 *    desc   : 服务器配置
 */
public class RequestServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return AppConfig.getHostUrl() + "api/";
    }

    @NonNull
    @Override
    public IRequestBodyStrategy getBodyType() {
        // 以表单的形式提交参数
        return RequestBodyType.FORM;
    }
}