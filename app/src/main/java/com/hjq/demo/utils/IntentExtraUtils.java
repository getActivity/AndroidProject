package com.hjq.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.List;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 意图数据存取工具类
 */
public final class IntentExtraUtils {

    private static Class<?> sCurrentClass;

    private static IntentExtraUtils sInstance;

    private static HashMap<String, Object> sMap;

    private IntentExtraUtils() {}

    public static IntentExtraUtils getInstance(Class<? extends Activity> cls) {
        if (sInstance == null) sInstance = new IntentExtraUtils();
        if (sMap == null) sMap = new HashMap<>();
        sCurrentClass = cls;
        return sInstance;
    }

    /**
     * 跳转到Activity
     *
     * @param context       context对象
     */
    public void startActivity(Context context) {
        startActivity(context, false);
    }

    /**
     * 跳转到Activity后再销毁当前Activity
     *
     * @param activity       activity对象
     */
    public void startActivityFinish(Activity activity) {
        startActivity(activity, false);
        activity.finish();
    }

    /**
     * 跳转到Activity
     *
     * @param context       context对象
     * @param newTask       是否开启新的任务栈
     */
    public void startActivity(Context context, boolean newTask) {
        Intent intent = new Intent(context, sCurrentClass);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转到Activity
     *
     * @param activity          activity对象
     * @param requestCode       请求码
     */
    public void startActivity(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, sCurrentClass), requestCode);
    }

    /**
     * 设置结果码
     *
     * @param activity          activity对象
     * @param resultCode        结果码
     */
    public void setResult(Activity activity, int resultCode) {
        activity.setResult(resultCode);
    }

    /**
     * 销毁Activity
     *
     * @param activity          activity对象
     */
    public void finish(Activity activity) {
        activity.finish();
    }

    // Object

    public IntentExtraUtils put(Class<?> clazz, Object object) {
        return put(sCurrentClass + clazz.getName(), object);
    }

    public IntentExtraUtils put(String key, Object object) {
        sMap.put(key, object);
        return this;
    }

    public <T extends Object> T get(Class<T> clazz) {
        return get(sCurrentClass + clazz.getName());
    }

    public <T extends Object> T get(String key) {
        T t = (T) sMap.get(key);
        //移除这个对象，避免内存泄露
        sMap.remove(key);
        return t;
    }

    // String

    public IntentExtraUtils putString(String s) {
        return put(String.class, s);
    }

    public IntentExtraUtils putString(String key, String s) {
        return put(key, s);
    }

    public String getString() {
        return get(String.class);
    }

    public String getString(String key) {
        return get(key);
    }

    // Integer

    public IntentExtraUtils putInteger(Integer i) {
        return put(Integer.class, i);
    }

    public IntentExtraUtils putInteger(String key, Integer i) {
        return put(key, i);
    }

    public Integer getInteger() {
        return get(Integer.class);
    }

    public Integer getInteger(String key) {
        return get(key);
    }

    // Long

    public IntentExtraUtils putLong(Long l) {
        return put(Long.class, l);
    }

    public IntentExtraUtils putLong(String key, Long l) {
        return put(key, l);
    }

    public Long getLong() {
        return get(Long.class);
    }

    public Long getLong(String key) {
        return get(key);
    }

    // Boolean

    public IntentExtraUtils putBoolean(Boolean b) {
        return put(Boolean.class, b);
    }

    public IntentExtraUtils putBoolean(String key, Boolean b) {
        return put(key, b);
    }

    public Boolean getBoolean() {
        return get(Boolean.class);
    }

    public Boolean getBoolean(String key) {
        return get(key);
    }

    // Double

    public IntentExtraUtils putDouble(Double d) {
        return put(Double.class, d);
    }

    public IntentExtraUtils putDouble(String key, Double d) {
        return put(key, d);
    }

    public Double getDouble() {
        return get(Double.class);
    }

    public Double getDouble(String key) {
        return get(key);
    }

    // Float

    public IntentExtraUtils putFloat(Float f) {
        return put(Float.class, f);
    }

    public IntentExtraUtils putFloat(String key, Float f) {
        return put(key, f);
    }

    public Float getFloat() {
        return get(Float.class);
    }

    public Float getFloat(String key) {
        return get(key);
    }

    // List

    public IntentExtraUtils putList(List list) {
        return put(List.class, list);
    }

    public IntentExtraUtils putList(String key, List list) {
        return put(key, list);
    }

    public List getList() {
        return get(List.class);
    }

    public List getList(String key) {
        return get(key);
    }

    public static class Key {

        public static final String ID = "id"; // id
        public static final String TOKEN = "token"; // token
        public static final String ORDER = "order"; // 订单
        public static final String BALANCE = "balance"; // 余额
        public static final String TIME = "time"; // 时间
        public static final String CODE = "code"; // 错误码或者其他码
        public static final String URL = "url"; // URL
        public static final String PATH = "path"; // 路径
        public static final String OTHER = "other"; // 其他

        // 个人信息
        public static final String NAME = "name"; // 姓名
        public static final String AGE = "age"; // 年龄
        public static final String SEX = "sex"; // 性别
        public static final String PHONE = "phone"; // 手机
        public static final String VIP = "vip"; // 会员
        public static final String DESCRIBE = "describe"; // 描述
        public static final String REMARK = "remark"; // 备注
        public static final String CONSTELLATION = "constellation"; // 星座

        // 地方
        public static final String ADDRESS = "address"; // 地址
        public static final String PROVINCE = "province"; // 省
        public static final String CITY = "city"; // 市
        public static final String DISTRICT = "district"; // 区

        // 文件类型相关
        public static final String TXT = "txt"; // 文本
        public static final String PICTURE = "picture"; // 图片
        public static final String VOICE = "voice"; // 音频
        public static final String VIDEO = "video"; // 视频

        // 支付相关
        public static final String BALANCE_PAY = "balance_pay"; // 余额支付
        public static final String WECHAT_PAY = "wechat_pay"; //微信支付
        public static final String ALI_PAY = "ali_pay"; //支付宝支付
        public static final String UNION_PAY = "union_pay"; // 银联支付
    }
}