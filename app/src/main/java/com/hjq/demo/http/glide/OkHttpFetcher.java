package com.hjq.demo.http.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/15
 *    desc   : OkHttp 加载器
 */
public final class OkHttpFetcher implements DataFetcher<InputStream>, Callback {

    private final Call.Factory mCallFactory;
    private final GlideUrl mGlideUrl;
    private InputStream mInputStream;
    private ResponseBody mResponseBody;
    private DataCallback<? super InputStream> mDataCallback;
    private volatile Call mCall;

    OkHttpFetcher(Call.Factory factory, GlideUrl url) {
        mCallFactory = factory;
        mGlideUrl = url;
    }

    @Override
    public void loadData(@NonNull Priority priority,
                         @NonNull final DataFetcher.DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = new Request.Builder().url(mGlideUrl.toStringUrl());
        for (Map.Entry<String, String> headerEntry : mGlideUrl.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        Request request = requestBuilder.build();
        mDataCallback = callback;

        mCall = mCallFactory.newCall(request);
        mCall.enqueue(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        mDataCallback.onLoadFailed(e);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        mResponseBody = response.body();
        if (response.isSuccessful()) {
            long contentLength = Preconditions.checkNotNull(mResponseBody).contentLength();
            mInputStream = ContentLengthInputStream.obtain(mResponseBody.byteStream(), contentLength);
            mDataCallback.onDataReady(mInputStream);
        } else {
            mDataCallback.onLoadFailed(new HttpException(response.message(), response.code()));
        }
    }

    @Override
    public void cleanup() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException ignored) {}

        if (mResponseBody != null) {
            mResponseBody.close();
        }
        mDataCallback = null;
    }

    @Override
    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}