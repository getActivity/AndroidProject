# EasyHttp
# 不混淆实现 OnHttpListener 接口的类，必须要加上此规则，否则会导致泛型解析失败
-keep class * implements com.hjq.http.listener.OnHttpListener {
    *;
}
-keep class * extends com.hjq.http.model.ResponseClass {
    *;
}