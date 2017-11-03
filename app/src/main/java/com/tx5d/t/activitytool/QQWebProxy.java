package com.tx5d.t.activitytool;


import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        String skey=getSubUtilSimple(mCookies," skey=(.*?);");
        String pt2gguin=getSubUtilSimple(mCookies," pt2gguin=(.*?);");
        vule.put("{skey}",skey);
        vule.put("{pt2gguin}",pt2gguin);
        vule.put("{gtk}",GetToKen(skey));
        vule.put("{ametk}",ameCSRFToken(skey));
        vule.put("{QQ}",Long.toString( Long.valueOf(pt2gguin.substring(1))));
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

    public static void sendPost(String url,String postBody,String Cookie,String Host, String Referer, String User_Agent,Callback callback) {
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
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(callback);
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                //请求失败
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("TAG", "Result=" + response.body().string());
//            }
//        });
//        return "";
    }
    public static String decodeUnicode(final String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
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
    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toLowerCase();
    }
    public static String join(String join,String[] strAry){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<strAry.length;i++){
            if(i==(strAry.length-1)){
                sb.append(strAry[i]);
            }else{
                sb.append(strAry[i]).append(join);
            }
        }

        return new String(sb);
    }

    public String GetToKen(String key)
    {
        String skey = key;
        int salt = 5381, ASCIICode;
        String md5key = "tencentQQVIP123443safde&!%^%1282";
        List<String> hash = new ArrayList<String>();
        hash.add(Integer.toString(salt << 5));
        for (int i = 0, len = skey.length(); i < len; ++i)
        {
            ASCIICode = (short)skey.toCharArray()[i];
            hash.add(Integer.toString((salt << 5) + ASCIICode));
            salt = ASCIICode;
        }
        String md5str = join("", hash.toArray(new String[hash.size()])) + md5key;
        return stringToMD5(md5str);
    }
    public String ameCSRFToken(String key)
    {
        //var sAMEStr = milo.cookie.get("skey") || "a1b2c3";
        String skey = key;
        int hash = 5381, ASCIICode;

        for (int i = 0, len = skey.length(); i < len; ++i)
        {
            ASCIICode = (short)skey.toCharArray()[i];
            hash += (hash << 5) + ASCIICode;
        }
        String rul = Integer.toString(hash & 2147483647);
        return rul;
    }


}

