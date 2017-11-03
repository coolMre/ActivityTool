package com.tx5d.t.activitytool;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
    private LinearLayout mContainer;
    private ImageButton mImageButton;
    private TextView mTextView;
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
    private ActAdspter actAdspter=null;
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
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.xinyue_main, null);
        // 创建PopupWindow对象
        final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        ImageButton btn = (ImageButton) findViewById(R.id.xinyueimageButton);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop.setWidth(getScreenWidth(this.getApplicationContext()));
        final int windowPos[] = calculateCPopWindowPos(view, view);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pop.isShowing()) {
                    // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                    pop.dismiss();
                    lighton();

                } else {

                    pop.showAtLocation(v, Gravity.TOP | Gravity.START, 0,windowPos[1]);
                    lightoff();
                    // 显示窗口
                    //pop.showAsDropDown(v,0,0);
                }

            }
        });
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lighton();
            }
        });
        //pop.showAsDropDown(mImageButton,0,0);
        //pop.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }
    private static int[] calculateCPopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示

        windowPos[0] = screenWidth/2 - windowWidth/2;
        windowPos[1] = screenHeight/2 - windowHeight/2;

        return windowPos;
    }
    //PopupWindow消失时，使屏幕恢复正常
    private void lighton() {
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=1.0f;
        getWindow().setAttributes(lp);
    }
    private void lightoff() {
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.5f;
        getWindow().setAttributes(lp);
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
        mContainer=(LinearLayout) findViewById(R.id.container);
        mListView = (ListView) findViewById(R.id.mlistview);
        mTextView=(TextView)findViewById(R.id.logtextView);
        mTextView.setText(mTextView.getText(), TextView.BufferType.EDITABLE);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mImageButton=(ImageButton) findViewById(R.id.imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListView.getVisibility()==View.GONE)
                {
                    mWebView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mListView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }

            }
        });
        mListView.setVisibility(View.GONE);

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
                        mSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(mDNFWebProxy!=null)
                                    mDNFWebProxy.SetRoleId(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                if(mDNFWebProxy!=null)
                                    mDNFWebProxy.SetRoleId(-1);
                            }
                        });

                        mSpinner3.setSelection(0,true);
                        break;
                    case 200:
                        //完成主界面更新,拿到数据
                        atcDate Svdata = (atcDate)msg.obj;
                        actAdspter=new ActAdspter(MainActivity.this,Svdata.getDate());
                        mListView.setAdapter(actAdspter);
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                actAdspter.setSelectPosition(position);

                            }
                        });
                        break;
                    case 233:
                        //完成主界面更新,拿到数据
                        final String actdata = (String)msg.obj;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                Editable text = (Editable) mTextView.getText();
                                text.append(actdata);
                                mTextView.setText(text);
                                int offset=mTextView.getLineCount()*mTextView.getLineHeight();
                                if(offset>mTextView.getHeight()){
                                    mTextView.scrollTo(0,offset-mTextView.getHeight());
                                    }

                            }
                        });
                        break;
                    case 10000:
                        //完成主界面更新,拿到数据
                        final String content = (String)msg.obj;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
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
                mDNFWebProxy.GetRolelist(mHandler,serverlist.get(mSpinner1.getSelectedItemPosition()-1).opt_data_array.get(position-1).v,serverlist.get(mSpinner1.getSelectedItemPosition()-1).opt_data_array.get(position-1).t);
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
        mContainer.setVisibility(View.GONE);
        mWebView.loadUrl("http://dnf.qq.com/comm-htdocs/milo_mobile/login.html?sData=&s_url=http%3A%2F%2Fgame.qq.com%2Fcomm-htdocs%2Flogin%2FloginSuccess.html");
//        mWebView.loadUrl("http://m.baidu.com/");

        Getactinfo();
    }
    public void Getactinfo()
    {
        DNFWebProxy.sendGet("http://www.tx5d.com/api/v2/g.ashx", "", "", "", "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg =new Message();
                msg.what=10000;
                msg.obj = "获取活动列表出错！！！";
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                SatcDate=gson.fromJson(result,atcDate.class);
                Message msg =new Message();
                msg.what=200;
                msg.obj = SatcDate;//可以是基本类型，可以是对象，可以是List、map等；
                mHandler.sendMessage(msg);
            }
        });
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
//        webSettings.setAppCacheEnabled(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
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
                    view.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mContainer.setVisibility(View.VISIBLE);

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
    //清除所有cookie
    private void removeAllCookieAndProxy()
    {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(mWebView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
//        String testcookie1 = cookieManager.getCookie("qq.com");
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
//        String testcookie2 = cookieManager.getCookie("qq.com");
        mDNFWebProxy=null;
        Adapter3=new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        mSpinner3.setAdapter(Adapter3);
        mListView.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }
    public void submitselect()
    {
        if(mDNFWebProxy==null)
        {
            Message msg =new Message();
            msg.what=10000;
            msg.obj = "请先登录！！！";
            mHandler.sendMessage(msg);
            return;
        }
        if(!mDNFWebProxy.ValueVerify())
        {
            Message msg =new Message();
            msg.what=10000;
            msg.obj = "请先选择大区和角色";
            mHandler.sendMessage(msg);
            return;
        }
        if(actAdspter.getSelectPosition()!=-1)
        {
            actinfo a = actAdspter.data.get(actAdspter.getSelectPosition());
            mDNFWebProxy.GetGift(mHandler,a);
        }

    }
    public void submitall()
    {
        if(mDNFWebProxy==null)
        {
            Message msg =new Message();
            msg.what=10000;
            msg.obj = "请先登录！！！";
            mHandler.sendMessage(msg);
            return;
        }
        if(!mDNFWebProxy.ValueVerify())
        {
            Message msg =new Message();
            msg.what=10000;
            msg.obj = "请先选择大区和角色";
            mHandler.sendMessage(msg);
            return;
        }
        mDNFWebProxy.GetAllGift(mHandler,actAdspter.data);
    }
    private void ToggleContainer()
    {
        if(mContainer.getVisibility()==View.GONE)
        {
            mContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            mContainer.setVisibility(View.GONE);
        }

    }
    public void onClick(View view){

        switch (view.getId())
        {
            case R.id.bt_onesub:{
                submitall();
//                mWebView.loadUrl("http://dnf.qq.com/comm-htdocs/milo_mobile/login.html?sData=&s_url=http%3A%2F%2Fdnf.qq.com%2Fact%2Fa20130805weixin%2Fcdkey.htm%3Fbg%3Dpe");
                //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            }
            break;
            case R.id.bt_sub:{
                submitselect();
            }
            break;
            case R.id.handbtn:{
                ToggleContainer();
            }
            break;
            case R.id.bt_home:{
                removeAllCookieAndProxy();
                mWebView.loadUrl("http://dnf.qq.com/comm-htdocs/milo_mobile/login.html?sData=&s_url=http%3A%2F%2Fgame.qq.com%2Fcomm-htdocs%2Flogin%2FloginSuccess.html");
            }
            break;

        }

    }

}
