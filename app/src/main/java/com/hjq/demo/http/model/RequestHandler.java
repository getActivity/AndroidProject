package com.hjq.demo.http.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.hjq.demo.R;
import com.hjq.demo.helper.ActivityStackManager;
import com.hjq.demo.http.json.BooleanTypeAdapter;
import com.hjq.demo.http.json.DoubleTypeAdapter;
import com.hjq.demo.http.json.FloatTypeAdapter;
import com.hjq.demo.http.json.IntegerTypeAdapter;
import com.hjq.demo.http.json.ListTypeAdapter;
import com.hjq.demo.http.json.LongTypeAdapter;
import com.hjq.demo.http.json.StringTypeAdapter;
import com.hjq.demo.ui.activity.LoginActivity;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.CancelException;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.NetworkException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.exception.ResultException;
import com.hjq.http.exception.ServerException;
import com.hjq.http.exception.TimeoutException;
import com.hjq.http.exception.TokenException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

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

    private Gson mGson;

    public RequestHandler(Application application) {
        mApplication = application;
    }

    @Override
    public Object requestSucceed(LifecycleOwner lifecycle, Response response, Type type) throws Exception {

        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            // 返回响应异常
            throw new ResponseException(mApplication.getString(R.string.http_response_error) + "，responseCode：" + response.code() + "，message：" + response.message(), response);
        }

        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }

        if (Bitmap.class.equals(type)) {
            // 如果这是一个 Bitmap 对象
            return BitmapFactory.decodeStream(body.byteStream());
        }

        String text;
        try {
            text = body.string();
        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
        }

        // 打印这个 Json
        EasyLog.json(text);

        final Object result;
        if (String.class.equals(type)) {
            // 如果这是一个 String 对象
            result = text;
        } else if (JSONObject.class.equals(type)) {
            try {
                // 如果这是一个 JSONObject 对象
                result = new JSONObject(text);
            } catch (JSONException e) {
                throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
            }
        } else if (JSONArray.class.equals(type)) {
            try {
                // 如果这是一个 JSONArray 对象
                result = new JSONArray(text);
            }catch (JSONException e) {
                throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
            }
        } else {

            try {
                if (mGson == null) {
                    // Json 容错处理
                    mGson = new GsonBuilder()
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, new BooleanTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, new IntegerTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, new LongTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, new FloatTypeAdapter()))
                            .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, new DoubleTypeAdapter()))
                            .registerTypeHierarchyAdapter(List.class, new ListTypeAdapter())
                            .create();
                }
                result = mGson.fromJson(text, type);
            } catch (JsonSyntaxException e) {
                // 返回结果读取异常
                throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
            }

            if (result instanceof HttpData) {
                HttpData model = (HttpData) result;
                if (model.getCode() == 0) {
                    // 代表执行成功
                    return result;
                } else if (model.getCode() == 1001) {
                    // 代表登录失效，需要重新登录
                    throw new TokenException(mApplication.getString(R.string.http_account_error));
                } else {
                    // 代表执行失败
                    throw new ResultException(model.getMessage(), model);
                }
            }
        }
        return result;
    }

    @Override
    public Exception requestFail(LifecycleOwner lifecycle,  Exception e) {
        // 判断这个异常是不是自己抛的
        if (e instanceof HttpException) {
            if (e instanceof TokenException) {
                // 登录信息失效，跳转到登录页
                Application application = ActivityStackManager.getInstance().getApplication();
                Intent intent = new Intent(application, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.startActivity(intent);
                // 销毁除了登录页之外的界面
                ActivityStackManager.getInstance().finishAllActivities(LoginActivity.class);
            }
        } else {
            if (e instanceof SocketTimeoutException) {
                e = new TimeoutException(mApplication.getString(R.string.http_server_out_time), e);
            } else if (e instanceof UnknownHostException) {
                NetworkInfo info = ((ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                // 判断网络是否连接
                if (info != null && info.isConnected()) {
                    // 有连接就是服务器的问题
                    e = new ServerException(mApplication.getString(R.string.http_server_error), e);
                } else {
                    // 没有连接就是网络异常
                    e = new NetworkException(mApplication.getString(R.string.http_network_error), e);
                }
            } else if (e instanceof IOException) {
                //e = new CancelException(context.getString(R.string.http_request_cancel), e);
                e = new CancelException("", e);
            } else {
                e = new HttpException(e.getMessage(), e);
            }
        }
        return e;
    }
}