package com.qiushi.wechatshop.config.net;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.qiushi.wechatshop.AppContext;
import com.qiushi.wechatshop.config.Constants;
import com.qiushi.wechatshop.model.PhoneInfo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetConfig implements Interceptor, CookieJar {
    private static NetConfig instance;

    private OkHttpClient client;
    private PersistentCookieStore cookieStore;
    private Gson gson;

    private NetConfig() {
    }

    public static NetConfig getInstance() {
        if (instance == null) {
            synchronized (NetConfig.class) {
                instance = new NetConfig();
            }
        }
        return instance;
    }

    public OkHttpClient getClient() {
        if (client == null)
            client = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(this)
                    .cookieJar(this)
                    .build();
        return client;
    }

    public Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder()
                    .registerTypeAdapter(String.class, new StringConverter())
                    .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                    .create();
        return gson;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Logger.d("URL = " + request.url().toString());
        Request.Builder builder = request.newBuilder();
        builder.addHeader("imei", PhoneInfo.getInstance().getImei())
                .addHeader("model", PhoneInfo.getInstance().getModel())
                .addHeader("brand", PhoneInfo.getInstance().getBrand())
                .addHeader("version", PhoneInfo.getInstance().getVersion())
                .addHeader("channel", PhoneInfo.getInstance().getChannel())
                .addHeader("device", "android");
//        Map<String, String> headers = Utils.getHttpHeaders();
//        if (headers != null) {
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                builder.addHeader(entry.getKey(), entry.getValue());
//            }
//        }
        Response response = chain.proceed(builder
                .build());
        ResponseBody responseBody = response.body();
        if (null != responseBody) {
            okhttp3.MediaType mediaType = responseBody.contentType();
            if (null != mediaType && "text/html; charset=UTF-8".equals(mediaType.toString())) {
                if (Constants.IS_DEVELOPER && "POST".equals(request.method())) {//打印请求参数
                    StringBuilder sb = new StringBuilder();
                    if (request.body() instanceof FormBody) {
                        FormBody body = (FormBody) request.body();
                        if (null != body) {
                            for (int i = 0; i < body.size(); i++) {
                                sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                            }
                            if (sb.length() > 0) {
                                sb.delete(sb.length() - 1, sb.length());
                                Logger.d("Params ? " + sb.toString());
                            }
                        }
                    }
                }
                String content = responseBody.string();
                Logger.json(content);
                return response.newBuilder()
                        .body(ResponseBody.create(mediaType, content))
                        .build();
            }
        }
        return response;
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        for (Cookie item : cookies) {
            getCookieStore().add(url, item);
        }
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        return getCookieStore().get(url);
    }

    private PersistentCookieStore getCookieStore() {
        if (cookieStore == null)
            cookieStore = new PersistentCookieStore(AppContext.context);
        return cookieStore;
    }
}