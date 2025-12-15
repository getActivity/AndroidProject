package com.hjq.demo.http.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IHttpCacheStrategy;
import com.hjq.http.request.HttpRequest;
import java.lang.reflect.Type;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2025/03/23
 *    desc   : 请求缓存策略实现类
 */
public final class HttpCacheStrategy implements IHttpCacheStrategy {

    @Nullable
    @Override
    public Object readCache(@NonNull HttpRequest<?> httpRequest, @NonNull Type type, long cacheTime) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        String cacheValue = HttpCacheManager.readHttpCache(cacheKey);
        if (cacheValue == null || cacheValue.isEmpty() || "{}".equals(cacheValue)) {
            return null;
        }
        EasyLog.printLog(httpRequest, "----- read cache key -----");
        EasyLog.printJson(httpRequest, cacheKey);
        EasyLog.printLog(httpRequest, "----- read cache value -----");
        EasyLog.printJson(httpRequest, cacheValue);
        EasyLog.printLog(httpRequest, "cacheTime = " + cacheTime);
        boolean cacheInvalidate = HttpCacheManager.isCacheInvalidate(cacheKey, cacheTime);
        EasyLog.printLog(httpRequest, "cacheInvalidate = " + cacheInvalidate);
        if (cacheInvalidate) {
            // 表示缓存已经过期了，直接返回 null 给外层，表示缓存不可用
            return null;
        }
        return GsonFactory.getSingletonGson().fromJson(cacheValue, type);
    }

    @Override
    public boolean writeCache(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Object result) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        String cacheValue = GsonFactory.getSingletonGson().toJson(result);
        if (cacheValue == null || cacheValue.isEmpty() || "{}".equals(cacheValue)) {
            return false;
        }
        EasyLog.printLog(httpRequest, "----- write cache key -----");
        EasyLog.printJson(httpRequest, cacheKey);
        EasyLog.printLog(httpRequest, "----- write cache value -----");
        EasyLog.printJson(httpRequest, cacheValue);
        boolean writeHttpCacheResult = HttpCacheManager.writeHttpCache(cacheKey, cacheValue);
        EasyLog.printLog(httpRequest, "writeHttpCacheResult = " + writeHttpCacheResult);
        boolean refreshHttpCacheTimeResult = HttpCacheManager.setHttpCacheTime(cacheKey, System.currentTimeMillis());
        EasyLog.printLog(httpRequest, "refreshHttpCacheTimeResult = " + refreshHttpCacheTimeResult);
        return writeHttpCacheResult && refreshHttpCacheTimeResult;
    }

    @Override
    public boolean deleteCache(@NonNull HttpRequest<?> httpRequest) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        EasyLog.printLog(httpRequest, "----- delete cache key -----");
        EasyLog.printJson(httpRequest, cacheKey);
        boolean deleteHttpCacheResult = HttpCacheManager.deleteHttpCache(cacheKey);
        EasyLog.printLog(httpRequest, "deleteHttpCacheResult = " + deleteHttpCacheResult);
        return deleteHttpCacheResult;
    }

    @Override
    public void clearCache() {
        HttpCacheManager.clearCache();
    }
}