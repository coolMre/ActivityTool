package com.tx5d.t.activitytool;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //private WebView webView;

    private WebView mWebView;
    private ListView mListView;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    private DNFWebProxy  mDNFWebProxy;
    private Spinner mSpinner1;//大区
    private Spinner mSpinner2;//区
    private Spinner mSpinner3;//角色
    ArrayAdapter<String> Adapter1 = null;  //大区适配器
    ArrayAdapter<String> Adapter2 = null;  //区适配器
    ArrayAdapter<String> Adapter3= null;   //角色适配器
    private List<ServerSelect> serverlist;

    private Gson gson;
    private GsonBuilder builder;
    private atcDate SatcDate;

    private boolean isLoadError =false;
    private String urlBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        builder=new GsonBuilder();
        gson=builder.create();
        initView();
        initEvents();
        Intent intent = getIntent();
        if (intent != null)
        {
            String intentAction = intent.getAction();
            if (Intent.ACTION_VIEW.equals(intentAction)) {
                Uri intentData = intent.getData();
                String url=intentData.toString();
                if (url.startsWith("http") || url.startsWith("https"))
                {
                    mWebView.loadUrl(intentData.toString());
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            //mWebView.loadUrl(data.getStringExtra("return"));
        }
    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Uri uri = intent.getData();
//        if (uri != null) {
//            System.out.println(uri.toString());
//            mWebView.loadUrl(uri.toString());
//        }
//    }

    private void initView() {

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_wb);
//        mTvTitle = (TextView) findViewById(R.id.act_webview_title);
        mWebView = (WebView) findViewById(R.id.webview);
        mListView = (ListView) findViewById(R.id.mlistview);

        DNFWebProxy.sendGet("http://www.tx5d.com/api/v2/g.ashx", "", "", "", "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                SatcDate=gson.fromJson(result,atcDate.class);
            }
        });

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100:
                        //完成主界面更新,拿到数据
                        List<String> data = (ArrayList<String>)msg.obj;
                        Adapter3 = new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_spinner_item, data);
                        // 设置二级下拉列表的选项内容适配器
                        mSpinner3.setAdapter(Adapter3);
                        mSpinner3.setSelection(0,true);
                        break;
                    case 10000:
                        //完成主界面更新,拿到数据
                        final String conten = (String)msg.obj;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, conten, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }

        };
        mSpinner1=(Spinner) findViewById(R.id.spinner_area1);
        mSpinner2=(Spinner) findViewById(R.id.spinner_area2);
        mSpinner3=(Spinner) findViewById(R.id.spinner_role);
        serverlist=DNFHelper.GETServerSelect();
        List<String> tmpitem=new ArrayList<String>();
        tmpitem.add("请选择服务器");
        for (ServerSelect item:serverlist)
        {
            tmpitem.add(item.t);
        }
        Adapter1 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, tmpitem);
        mSpinner1.setAdapter(Adapter1);
        mSpinner1.setSelection(0,true);
        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if(position<1)
                {
                    return;
                }
                List<String> tmpitem=new ArrayList<String>();
                tmpitem.add("请选择大区");
                for (ServerSelect item:serverlist.get(position-1).opt_data_array)
                {
                    tmpitem.add(item.t);
                }
                Adapter2 = new ArrayAdapter<String>(
                        MainActivity.this, android.R.layout.simple_spinner_item, tmpitem);
                // 设置二级下拉列表的选项内容适配器
                mSpinner2.setAdapter(Adapter2);
                mSpinner2.setSelection(0,true);
                //provincePosition = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if(position<1)
                {
                    return;
                }
                if(mDNFWebProxy==null)
                {
                    return;
                }
                mDNFWebProxy.GetRolelist(mHandler,serverlist.get(mSpinner1.getSelectedItemPosition()-1).opt_data_array.get(position-1).v);
//                List<String> tmpitem=new ArrayList<String>();
//                tmpitem.add("请选择大区");
//                for (ServerSelect item:serverlist.get(position-1).opt_data_array)
//                {
//                    tmpitem.add(item.t);
//                }
//                Adapter3 = new ArrayAdapter<String>(
//                        MainActivity.this, android.R.layout.simple_spinner_item, list);
//                // 设置二级下拉列表的选项内容适配器
//                mSpinner3.setAdapter(Adapter3);
//                mSpinner3.setSelection(0,true);
                //provincePosition = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        webviewConfig();
    }


    public void webviewConfig() {
//        mTvTitle.setText(mTitle);

        if (mWebView == null) {
            return;
        }
        mWebView.requestFocus();
        //?水平不显示滚动条?
        mWebView.setHorizontalScrollBarEnabled(false);
        //?禁止即在网页顶出现一个空白，又自动回去。
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings webSettings = mWebView.getSettings();
        //支持js
        webSettings.setJavaScriptEnabled(true);
        //设置支持缩放
        webSettings.setSupportZoom(true);
        //缓存设置
        webSettings.setDomStorageEnabled(true);
        //  出现net::ERR_CACHE_MISS错误提示
//        if (Build.VERSION.SDK_INT >= 19) {
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        //?允许访问文件????
        webSettings.setAllowFileAccess(true);
        //设置此属性，可任意比例缩放,(双击放大,缩小)
        webSettings.setUseWideViewPort(true);
        //显示内建的放大缩小按钮
        webSettings.setBuiltInZoomControls(true);
        //?充满全屏幕??
        webSettings.setLoadWithOverviewMode(true);
        /**
         ?* 用WebView显示图片，可使用这个参数 设置网页布局类型：
         ?* 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小
         ?* 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
         ?*/
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //隐藏放大缩小的按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }
        //设置在解码时使用的默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        //api大于21  5.0以上访问https需要添加此设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //mWebView.postUrl(url,urlBody.getBytes());
        //参数一:桥梁类-->1:普通类,2定义的方法是给js调用 3.方法参数只能是string
        //参数二:别名
        //mWebView.addJavascriptInterface(new Object(), "bindJs");
        mWebView.addJavascriptInterface(this, "openHtfApp");
    }
    private void initEvents() {

        if (mWebView == null) {
            return;
        }

        /**
         * 监听加载progress
         * */
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //设置进度条的最大值
                if (mProgressBar != null) {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });


        /**
         *
         * 监听webview加载过程(开始,结束)
         *
         * */
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isLoadError = false;
                //LogUtils.w("webView: "+url);
                if (mProgressBar != null) {//开始加载的时候,开始显示进度条
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }


            //处理https请求，为WebView处理ssl证书设置，WebView默认是不处理https请求的，页面显示空白，需
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                //handler.cancel(); // Android默认的处理方式
                handler.proceed();  // 接受所有网站的证书
                //handleMessage(Message msg); // 进行其他处理
            }

            //加载失败的回调
            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {

                super.onReceivedError(view, request, error);
                if (mProgressBar != null) {
//                    mProgressBar.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
                //LogUtils.e(error.toString());

                isLoadError =true;
                // 加载网页失败时处理?
                view.loadDataWithBaseURL(null,
                        "<html><body><h1>Page not find!</h1></body></html>",
                        "text/html",
                        "utf-8",
                        null);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.startsWith("http://game.qq.com/comm-htdocs/login/loginSuccess.html"))
                {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String CookieStr = cookieManager.getCookie(url);
                    mDNFWebProxy=new DNFWebProxy(CookieStr);
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();

                        }
                    });
                    view.setVisibility(View.INVISIBLE);

                }
                //加载完成之后,隐藏进度条
                if (mProgressBar != null) {
//                    mProgressBar.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            /**
             * url重定向会执行此方法以及点击页面某些链接也会执行此方法
             *
             *     返回true，WebView将不会处理该按键事件，返回false，WebView将处理该按键事件。默认返回是false。
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                if (url.startsWith("http") || url.startsWith("https"))
                {
                    //hitTestResult==null解决重定向问题
                    if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                        view.loadUrl(url);
                        return true;
                    }
                }
                else
                    {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url+"&schemacallback=onestep://"));
                    if (in.resolveActivity(getPackageManager()) == null) {
                        //说明系统中不存在这个activity
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "应用未安装", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        ComponentName pk= in.resolveActivity(getPackageManager());
                        //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        in.setComponent(pk);
                        startActivity(in);
                        //startActivity(in);
                        if(url.startsWith("wtloginmqq"))
                        {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "启动【QQ】 成功,点击【登录】后请选择用【一步DNF活动助手】打开连接！！！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                if (url.startsWith("http") || url.startsWith("https"))
//                { //http和https协议开头的执行正常的流程
//                    return super.shouldInterceptRequest(view, url);
//                } else {
//                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url+"&schemacallback=onestep://"));
//                    if (in.resolveActivity(getPackageManager()) == null) {
//                        //说明系统中不存在这个activity
//                        view.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this, "应用未安装", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    } else {
//                        ComponentName pk= in.resolveActivity(getPackageManager());
//                        //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                        in.setComponent(pk);
//                        startActivity(in);
//                        //startActivity(in);
//                        if(url.startsWith("wtloginmqq"))
//                        {
//                            view.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(MainActivity.this, "启动【QQ】 成功,点击【登录】后请选择用本工具打开连接！！！", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                        }
//
//                    }
////                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////                        startActivityForResult(in,1);
////                        startActivity(in);
//                    return null;
//                }
//            }

        });
    }


    public void onClick(View view){
        switch (view.getId())
        {
            case R.id.bt_onesub:{
                //mwebView = (WebView) findViewById(R.id.webview);
                //WebView加载web资源
//                mWebView.loadUrl("http://m.baidu.com");
//                mWebView.loadUrl("http://m.qzone.com");
                mWebView.loadUrl("http://dnf.qq.com/comm-htdocs/milo_mobile/login.html?sData=&s_url=http%3A%2F%2Fgame.qq.com%2Fcomm-htdocs%2Flogin%2FloginSuccess.html");
//                mWebView.loadUrl("http://dnf.qq.com/comm-htdocs/milo_mobile/login.html?sData=&s_url=http%3A%2F%2Fdnf.qq.com%2Fact%2Fa20130805weixin%2Fcdkey.htm%3Fbg%3Dpe");
                //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            }

        }

    }

}
