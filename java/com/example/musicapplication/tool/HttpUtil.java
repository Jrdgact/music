package com.example.musicapplication.tool;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    //和服务器的交互
    public static void sendOkHttpRequest(final String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).get().build();//请求
        client.newCall(request).enqueue(callback);
    }

}
