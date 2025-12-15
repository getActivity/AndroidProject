package com.hjq.demo.http.exception;

import androidx.annotation.NonNull;
import com.hjq.demo.http.model.HttpData;
import com.hjq.http.exception.HttpException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/12/19
 *    desc   : 返回结果异常
 */
public final class ResultException extends HttpException {

    @NonNull
    private final HttpData<?> mData;

    public ResultException(@NonNull String message, @NonNull HttpData<?> data) {
        super(message);
        mData = data;
    }

    public ResultException(@NonNull String message, @NonNull Throwable cause, @NonNull HttpData<?> data) {
        super(message, cause);
        mData = data;
    }

    @NonNull
    public HttpData<?> getHttpData() {
        return mData;
    }
}