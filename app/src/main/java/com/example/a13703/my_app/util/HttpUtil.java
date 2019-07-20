package com.example.a13703.my_app.util;

import android.webkit.WebSettings;

import com.example.a13703.my_app.MyApplication;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 13703 on 2019/7/7.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(MyApplication.getContext()))
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
