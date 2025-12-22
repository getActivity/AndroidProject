package com.hjq.demo.http.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import com.hjq.core.manager.ActivityManager;
import com.hjq.demo.R;
import com.hjq.demo.http.exception.ResultException;
import com.hjq.demo.http.exception.TokenException;
import com.hjq.demo.ui.activity.account.LoginActivity;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.CancelException;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.FileMd5Exception;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.NetworkException;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.exception.ServerException;
import com.hjq.http.exception.TimeoutException;
import com.hjq.http.request.HttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 请求处理类
 */
public final class RequestHandler implements IRequestHandler {

    private final Application mApplication;

    public RequestHandler(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public Object requestSuccess(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Type type) throws Throwable {
        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            throw new ResponseException(String.format(mApplication.getString(R.string.http_response_error),
                    response.code(), response.message()), response);
        }

        if (Object.class.equals(type)) {
            return "";
        }

        if (Headers.class.equals(type)) {
            return response.headers();
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException(mApplication.getString(R.string.http_response_null_body));
        }

        if (InputStream.class.equals(type)) {
            return body.byteStream();
        }

        if (Bitmap.class.equals(type)) {
            return BitmapFactory.decodeStream(body.byteStream());
        }

        String text;
        try {
            text = body.string();
        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
        }

        // 打印这个 Json 或者文本
        EasyLog.printJson(httpRequest, text);

        if (String.class.equals(type)) {
            return text;
        }

        final Object result;

        try {
            result = GsonFactory.getSingletonGson().fromJson(text, type);
        } catch (Exception e) {
            // 返回结果读取异常
            throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
        }

        if (result instanceof HttpData) {
            HttpData<?> model = (HttpData<?>) result;
            Headers headers = response.headers();
            int headersSize = headers.size();
            Map<String, String> headersMap = new HashMap<>(headersSize);
            for (int i = 0; i < headersSize; i++) {
                headersMap.put(headers.name(i), headers.value(i));
            }
            // Github issue 地址：https://github.com/getActivity/EasyHttp/issues/233
            model.setResponseHeaders(headersMap);

            if (model.isRequestSuccess()) {
                // 代表执行成功
                return result;
            }

            if (model.isTokenInvalidation()) {
                // 代表登录失效，需要重新登录
                throw new TokenException(mApplication.getString(R.string.http_token_error));
            }

            // 代表执行失败
            throw new ResultException(model.getMessage(), model);
        }
        return result;
    }

    @NonNull
    @Override
    public Throwable requestFail(@NonNull HttpRequest<?> httpRequest, @NonNull Throwable throwable) {
        if (throwable instanceof HttpException) {
            if (throwable instanceof TokenException) {
                // 登录信息失效，跳转到登录页
                Application application = ActivityManager.getInstance().getApplication();
                Intent intent = new Intent(application, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.startActivity(intent);
                // 销毁除了登录页之外的 Activity
                ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
            }
            return throwable;
        }

        if (throwable instanceof SocketTimeoutException) {
            return new TimeoutException(mApplication.getString(R.string.http_server_out_time), throwable);
        }

        if (throwable instanceof UnknownHostException) {
            NetworkInfo info = ((ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            // 判断网络是否连接
            if (info == null || !info.isConnected()) {
                // 没有连接就是网络异常
                return new NetworkException(mApplication.getString(R.string.http_network_error), throwable);
            }

            // 有连接就是服务器的问题
            return new ServerException(mApplication.getString(R.string.http_server_error), throwable);
        }

        if (throwable instanceof IOException) {
            // 出现该异常的两种情况
            // 1. 调用 EasyHttp.cancel
            // 2. 网络请求被中断
            return new CancelException(mApplication.getString(R.string.http_request_cancel), throwable);
        }

        return new HttpException(throwable.getMessage(), throwable);
    }

    @NonNull
    @Override
    public Throwable downloadFail(@NonNull HttpRequest<?> httpRequest, @NonNull Throwable throwable) {
        if (throwable instanceof ResponseException) {
            ResponseException responseException = ((ResponseException) throwable);
            Response response = responseException.getResponse();
            responseException.setMessage(String.format(mApplication.getString(R.string.http_response_error),
                    response.code(), response.message()));
            return responseException;
        } else if (throwable instanceof NullBodyException) {
            NullBodyException nullBodyException = ((NullBodyException) throwable);
            nullBodyException.setMessage(mApplication.getString(R.string.http_response_null_body));
            return nullBodyException;
        } else if (throwable instanceof FileMd5Exception) {
            FileMd5Exception fileMd5Exception = ((FileMd5Exception) throwable);
            fileMd5Exception.setMessage(mApplication.getString(R.string.http_response_md5_error));
            return fileMd5Exception;
        }
        return requestFail(httpRequest, throwable);
    }
}