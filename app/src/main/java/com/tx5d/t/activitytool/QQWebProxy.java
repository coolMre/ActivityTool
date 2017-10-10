package com.tx5d.t.activitytool;


import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mre on 2017/9/29.
 */

public class QQWebProxy {

    public String getmCookies() {
        return mCookies;
    }

    private String mCookies=null;
    HashMap<String, String> vule=new HashMap<String, String>();
    QQWebProxy(String cookies)
    {
        mCookies=cookies;
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.cookieJar(new CookieJar() {
//            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
//
//            @Override
//            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                cookieStore.put(url.host(), cookies);
//            }
//
//            @Override
//            public List<Cookie> loadForRequest(HttpUrl url) {
//                List<Cookie> cookies = cookieStore.get(url.host());
//                return cookies != null ? cookies : new ArrayList<Cookie>();
//            }
//        });
//        client = builder.build();
//        final Request request = new Request.Builder()
//                .url("http://game.qq.com/comm-htdocs/login/loginSuccess.html")
//                .addHeader("Cookie",mCookies)
//                .build();
//        //new call
//        Call call = client.newCall(request);
//        //请求加入调度
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                //请求失败
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                //不是UI线程,请不要在此更新界面
//                String htmlStr = response.body().string();
//                Log.e("TAG", "htmlStr ==" + htmlStr);
//            }
//        });
    }
    public static  void sendGet(String url,String Cookie,String Host, String Referer, String User_Agent,Callback callback) {
        String result = "";
        ///创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        Request.Builder req= new Request.Builder()
                .url(url);
        if(!Cookie.isEmpty())
        {
            req.addHeader("Cookie",Cookie);
        }
        if(!Host.isEmpty())
        {
            req.addHeader("Host",Host);
        }
        if(!Referer.isEmpty())
        {
            req.addHeader("Referer",Referer);
        }
        if(!User_Agent.isEmpty())
        {
            req.addHeader("User-Agent",User_Agent);
        }

        Request request=req.build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(callback);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                //请求失败
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                //不是UI线程,请不要在此更新界面
//                String htmlStr = response.body().string();
//                Log.e("TAG", "htmlStr ==" + htmlStr);
//            }
//        });
//        Response response = null;
//        try {
//            response = mOkHttpClient.newCall(request).execute();
//            result=response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Response response = mOkHttpClient.newCall(request).execute();
//        if (response.isSuccessful()) {
//            return response.body().string();
//        } else {
//            throw new IOException("Unexpected code " + response);
//        }


        //return result;
    }

    public static String sendPost(String url,String postBody,String Cookie,String Host, String Referer, String User_Agent) {
//        RequestBody requestBody = new FormBody.Builder()
//                .add()
//                .build();
        ///创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        Request.Builder req= new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), postBody))
                .url(url);
        if(!Cookie.isEmpty())
        {
            req.addHeader("Cookie",Cookie);
        }
        if(!Host.isEmpty())
        {
            req.addHeader("Host",Host);
        }
        if(!Referer.isEmpty())
        {
            req.addHeader("Referer",Referer);
        }
        if(!User_Agent.isEmpty())
        {
            req.addHeader("User-Agent",User_Agent);
        }

        final Request request=req.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "Result=" + response.body().string());
            }
        });
        return "";
    }

    /**
     * 正则表达式匹配两个指定字符串中间的内容
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap,String rgex){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }

    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个，方法与getSubUtil一样
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap,String rgex){
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";
    }


}

