package com.hjq.demo.http.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 统一接口数据结构
 */
public class HttpData<T> {

    /** 响应头 */
    @Nullable
    private Map<String, String> responseHeaders;

    /** 返回码 */
    private int code;

    /** 提示语 */
    @Nullable
    private String msg;

    /** 数据 */
    @Nullable
    private T data;

    public void setResponseHeaders(@Nullable Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    @Nullable
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public int getCode() {
        return code;
    }

    @NonNull
    public String getMessage() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

    @Nullable
    public T getData() {
        return data;
    }

    /**
     * 是否请求成功
     */
    public boolean isRequestSuccess() {
        return code == 200;
    }

    /**
     * 是否 Token 失效
     */
    public boolean isTokenInvalidation() {
        return code == 1001;
    }
}