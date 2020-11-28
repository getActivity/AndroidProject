package com.hjq.demo.http.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/05/05
 *    desc   : boolean / Boolean 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#BOOLEAN}
 */
public class BooleanTypeAdapter extends TypeAdapter<Boolean> {

    @Override
    public Boolean read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case BOOLEAN:
                return in.nextBoolean();
            case STRING:
                // 如果后台返回 "true" 或者 "TRUE"，则处理为 true，否则为 false
                return Boolean.parseBoolean(in.nextString());
            case NUMBER:
                // 如果这个后台返回是 1 则处理为 true，否则为 false
                return in.nextInt() == 1;
            case NULL:
                in.nextNull();
                return null;
            default:
                in.skipValue();
                return null;
        }
    }
    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        out.value(value);
    }
}
