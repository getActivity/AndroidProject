package com.hjq.demo.http.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.request.HttpRequest;
import com.tencent.mmkv.MMKV;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2022/03/22
 *    desc   : Http 缓存管理器
 */
public final class HttpCacheManager {

    @NonNull
    private static final MMKV HTTP_CACHE_CONTENT = MMKV.mmkvWithID("http_cache_content");;

    @NonNull
    private static final MMKV HTTP_CACHE_TIME = MMKV.mmkvWithID("http_cache_time");

    /**
     * 生成缓存的 key
     */
    @NonNull
    public static String generateCacheKey(@NonNull HttpRequest<?> httpRequest) {
        IRequestApi requestApi = httpRequest.getRequestApi();
        return "请替换成当前的用户 id" + "\n" + requestApi.getApi() + "\n" + GsonFactory.getSingletonGson().toJson(requestApi);
    }

    /**
     * 读取缓存
     */
    @Nullable
    public static String readHttpCache(@NonNull String cacheKey) {
        String cacheValue = HTTP_CACHE_CONTENT.getString(cacheKey, null);
        if (cacheValue == null || cacheValue.isEmpty() || "{}".equals(cacheValue)) {
            return null;
        }
        return cacheValue;
    }

    /**
     * 写入缓存
     */
    public static boolean writeHttpCache(@NonNull String cacheKey, @NonNull String cacheValue) {
        return HTTP_CACHE_CONTENT.putString(cacheKey, cacheValue).commit();
    }

    /**
     * 删除缓存
     */
    public static boolean deleteHttpCache(@NonNull String cacheKey) {
        return HTTP_CACHE_CONTENT.remove(cacheKey).commit();
    }

    /**
     * 清理缓存
     */
    public static void clearCache() {
        HTTP_CACHE_CONTENT.clearMemoryCache();
        HTTP_CACHE_CONTENT.clearAll();

        HTTP_CACHE_TIME.clearMemoryCache();
        HTTP_CACHE_TIME.clearAll();
    }

    /**
     * 获取 Http 写入缓存的时间
     */
    public static long getHttpCacheTime(@NonNull String cacheKey) {
        return HTTP_CACHE_TIME.getLong(cacheKey, 0);
    }

    /**
     * 设置 Http 写入缓存的时间
     */
    public static boolean setHttpCacheTime(@NonNull String cacheKey, long cacheTime) {
        return HTTP_CACHE_TIME.putLong(cacheKey, cacheTime).commit();
    }

    /**
     * 判断缓存是否过期
     */
    public static boolean isCacheInvalidate(@NonNull String cacheKey, long maxCacheTime) {
        if (maxCacheTime == Long.MAX_VALUE) {
            // 表示缓存长期有效，永远不会过期
            return false;
        }
        long httpCacheTime = getHttpCacheTime(cacheKey);
        if (httpCacheTime == 0) {
            // 表示不知道缓存的时间，这里默认当做已经过期了
            return true;
        }
        return httpCacheTime + maxCacheTime < System.currentTimeMillis();
    }
}