package com.tx5d.t.activitytool;

import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Mre on 2017/9/30.
 */

public class DNFWebProxy extends QQWebProxy {
    DNFWebProxy(String cookies) {
        super(cookies);
    }
    private String md5str;
    List<Role> roleList=new ArrayList<Role>();
    List<String> rolenamelist=new ArrayList<String>();
    Map<String,String> sSDIDList=new HashMap<String, String>();
    Thread taskthread;
    public void GetRolelist(final Handler mHandler,final String area,final String areaname)
    {
//        String query_role_result="var query_role_result_150674287133515422={version:'V1.0.704899.709050.20170710172507',retCode:0, serial_num:'AMS-DNF-0930114112-T8znZ9-980875-5381', data:'_idip_req_id_=&_webplat_msg=4|18594479 %E6%9A%97%E2%98%86%E5%A4%A9 3 54|25211972 %7B%E6%97%A0%E5%8F%8C%E5%89%91%E5%A7%AC%7D 11 88|30322182 %E6%9E%81%E9%99%90%E9%BB%91%E6%AD%A6 9 17|30438254 %E5%A7%AC%E9%AD%85%E6%97%A0%E5%8F%8C 1 86|&_webplat_msg_code=0&area=117&msg=4|18594479 %E6%9A%97%E2%98%86%E5%A4%A9 3 54|25211972 %7B%E6%97%A0%E5%8F%8C%E5%89%91%E5%A7%AC%7D 11 88|30322182 %E6%9E%81%E9%99%90%E9%BB%91%E6%AD%A6 9 17|30438254 %E5%A7%AC%E9%AD%85%E6%97%A0%E5%8F%8C 1 86|&result=0&uin=752776449&',msg:'success',checkparam:'dnf|yes|752776449|117|18594479*25211972*30322182*30438254*',md5str:'15E01799C6BECD105A9767412E64038C',infostr:'',checkstr:''};";
        //String query_role_result="";
        sendGet("http://comm.aci.game.qq.com/main?game=dnf&area="+area+"&sCloudApiName=ams.gameattr.role&iAmsActivityId=http%3A%2F%2Fdnf.qq.com%2Fact%2Fa20130805weixin%2Fcdkey.htm&sServiceDepartment=x6m5",getmCookies(),"comm.aci.game.qq.com","http://dnf.qq.com/act/a20130805weixin/cdkey.htm?bg=pe","",new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //不是UI线程,请不要在此更新界面
                try {
                    String query_role_result = response.body().string();
                    String data=getSubUtilSimple(query_role_result,"_webplat_msg=\\d*\\|(.*?)\\|&_webplat_msg_code=0");
                    vule.put("{area}",area);
                    vule.put("{areaname}",areaname);
                    vule.put("{u1areaname}", URLEncoder.encode(areaname, "UTF-8").toUpperCase());
                    vule.put("{u2areaname}",URLEncoder.encode(vule.get("{u1areaname}"), "UTF-8").toUpperCase());
                    vule.put("{area}",area);
                    vule.put("{roleid}","");
                    vule.put("{md5str}",getSubUtilSimple(query_role_result,"md5str:'(.*?)'"));
                    vule.put("{checkparam}",getSubUtilSimple(query_role_result,"checkparam:'(.*?)'"));
                    vule.put("{u1checkparam}", URLEncoder.encode(vule.get("{checkparam}"), "UTF-8"));
                    vule.put("{u2checkparam}", URLEncoder.encode(vule.get("{u1checkparam}"), "UTF-8"));

                    if(data.isEmpty())
                    {
                        Message msg =new Message();
                        msg.what=10000;
                        msg.obj = "该大区没有角色！";
                        mHandler.sendMessage(msg);
                        return;
                    }
                    String[] roledata=data.split("[|]");
                    rolenamelist.clear();
                    roleList.clear();
                    for(String item:roledata)
                    {
                        String[] rolesplit=item.split("[ ]");
                        try {
                            String name=java.net.URLDecoder.decode(rolesplit[1],"UTF-8");
                            rolenamelist.add(name);
                            roleList.add(new Role(name,rolesplit[0]));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
//                mHandler.sendEmptyMessage(0);
                    //需要数据传递，用下面方法；
                    Message msg =new Message();
                    msg.what=100;
                    msg.obj = rolenamelist;
                    mHandler.sendMessage(msg);
                    Log.e("TAG", "htmlStr ==" + query_role_result);
                }
                catch (Exception e)
                {
                    Message msg =new Message();
                    msg.what=10000;
                    msg.obj = "获取角色出错！！！";
                    mHandler.sendMessage(msg);
                }

            }
        });
    }
    public void SetRoleId(int i)
    {
        vule.put("{roleid}","");
        if(i==-1)
            return;
        vule.put("{roleid}",roleList.get(i).roleid);
        vule.put("{rolename}",roleList.get(i).rolename);
        try {
            vule.put("{u1rolename}", URLEncoder.encode(vule.get("{rolename}"), "UTF-8").toUpperCase());
            vule.put("{u2rolename}", URLEncoder.encode(vule.get("{u1rolename}"), "UTF-8").toUpperCase());
        }
        catch (Exception e)
        {

        }

    }
    public boolean ValueVerify()
    {
        return vule.containsKey("{area}")&&!(vule.get("{roleid}").equals(""));
    }
    public void GetAllGift(final Handler mHandler,final  List<actinfo> actlist)
    {
        if(taskthread!=null&&taskthread.getState()== Thread.State.RUNNABLE)
        {
            Message msg =new Message();
            msg.what=10000;
            msg.obj = "任务进行中！！！";
            mHandler.sendMessage(msg);
            return;
        }
        taskthread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                for(actinfo tmp:actlist)
                {
                    if(tmp.getAutoSub().equals("1"))
                        GetGift(mHandler,tmp);
                    Thread.sleep(500);
                }
                }
                catch (Exception e)
                {
                    Message msg =new Message();
                    msg.what=10000;
                    msg.obj = "任务线程出错,任务终止！！！";
                    mHandler.sendMessage(msg);

                }
            }
        });
        taskthread.start();

    }
    public void GetGift(final Handler mHandler,final actinfo act)
    {
        if(act.getAutoSub().equals("0"))
            return;
        String gifurl=act.subURL
                .replace("{g_tk}",vule.get("{gtk}"))
                .replace("{area}",vule.get("{area}"))
                .replace("{roleid}",vule.get("{roleid}"))
                .replace("{ametk}",vule.get("{ametk}"))
                .replace("{actid}",Integer.toString(act.getActid()))
                .replace("{flowid}",Integer.toString(act.getFlowid()));

        String postdata= act.subDate
                .replace("{g_tk}",vule.get("{gtk}"))
                .replace("{area}",vule.get("{area}"))
                .replace("{roleid}",vule.get("{roleid}"))
                .replace("{ametk}",vule.get("{ametk}"))
                .replace("{actid}",Integer.toString(act.getActid()))
                .replace("{flowid}",Integer.toString(act.getFlowid()));
        String au="";
        final String tmpurl=gifurl;
        final String tmpdate=postdata;
        switch (act.getModel())
        {
            case 1:
                au=act.getExt1();
                break;
            case 2:
                final String ext1=act.getExt1();
                if (!sSDIDList.containsKey(ext1))
                {

                    sendGet(ext1, getmCookies(), act.getExt2(), act.getExt3(), "", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            try {
                                sSDIDList.put(ext1,getSubUtilSimple(result,"\"sSDID\":\"(.*?)\""));
                                String newgifurl=tmpurl
                                        .replace("{md5str}",vule.get("{md5str}"))
                                        .replace("{ametk}",vule.get("{ametk}"))
                                        .replace("{sSDID}",sSDIDList.get(ext1));
                                String newdata=tmpdate
                                        .replace("{checkparam}",vule.get("{u2checkparam}"))
                                        .replace("{md5str}",vule.get("{md5str}"))
                                        .replace("{ametk}",vule.get("{ametk}"))
                                        .replace("{sSDID}",sSDIDList.get(ext1));
                                AmsSub(mHandler,newgifurl,newdata,act);
                            }
                            catch (Exception e)
                            {
                                Message msg =new Message();
                                msg.what=10000;
                                msg.obj = "获取sSDID出错,任务终止！！！";
                                mHandler.sendMessage(msg);
                                return;
                            }


                        }
                    });
                }
                else
                {
                    String newgifurl=tmpurl
                            .replace("{md5str}",vule.get("{md5str}"))
                            .replace("{ametk}",vule.get("{ametk}"))
                            .replace("{sSDID}",sSDIDList.get(ext1));
                    String newdata=tmpdate
                            .replace("{checkparam}",vule.get("{u2checkparam}"))
                            .replace("{md5str}",vule.get("{md5str}"))
                            .replace("{ametk}",vule.get("{ametk}"))
                            .replace("{sSDID}",sSDIDList.get(ext1));
                    AmsSub(mHandler,newgifurl,newdata,act);

                }
                return;

            case 3:
                au=act.getExt3();
                break;
            case 4:
                gifurl = gifurl
                        .replace("{u1rolename}", vule.get("{u1rolename}"))
                        .replace("{QQ}", vule.get("{QQ}"))
                        .replace("{u1areaname}", vule.get("{u1areaname}"));
                break;
        }

        AmsSub(mHandler,gifurl,postdata,act);

    }
    public  void AmsSub(final Handler mHandler,final String url,final String Date,final actinfo act)
    {
        if(act.subMethod.toLowerCase().equals("post")) {
            sendPost(url,Date, getmCookies(), act.getHost(), act.getReferer(), "", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //请求失败
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //不是UI线程,请不要在此更新界面
                    try {
                        String result = response.body().string();
                        AmsAnalysis(mHandler,result,act);

//                mHandler.sendEmptyMessage(0);
                        //需要数据传递，用下面方法；
//                        Message msg =new Message();
//                        msg.what = 10000;
//                        msg.obj = "成功！！！";
//                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        Message msg = new Message();
                        msg.what = 10000;
                        msg.obj = "获取出错！！！";
                        mHandler.sendMessage(msg);
                    }

                }
            });
        }
        else
        {
            sendGet(url,getmCookies(), act.getHost(), act.getReferer(), "", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //请求失败
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //不是UI线程,请不要在此更新界面
                    try {
                        String result = response.body().string();
                        AmsAnalysis(mHandler,result,act);
//                        Message msg = new Message();
//                        msg.what = 10000;
//                        msg.obj = "成功！！！";
//                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        Message msg = new Message();
                        msg.what = 10000;
                        msg.obj = "获取出错！！！";
                        mHandler.sendMessage(msg);
                    }

                }
            });

        }

    }
    public  void AmsAnalysis(final Handler mHandler,final String data,final actinfo act)
    {
        StringBuilder msgrul=new StringBuilder();
        long time=System.currentTimeMillis();
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        String code,cdkey,gifname,num,msg;
        switch (act.getModel())
        {
            case 1:
                msg= getSubUtilSimple(data,"\"msg\":\"(.*?)\"");
                String actname=decodeUnicode(getSubUtilSimple(data,"\"actname\":\"(.*?)\""));
                if(msg.equals("success"))
                {
                    String actop=getSubUtilSimple(data,"\"op\":\"(.*?)\"");
                    switch (actop)
                    {
                        case "online_cdk":
                            cdkey=getSubUtilSimple(data,"\"cdkey\":\"(.*?)\"");
                            msgrul.append( "\r\n======CDK=======\r\n" + cdkey + "\r\n================");
                            break;
                        case "add_money":
                            num=getSubUtilSimple(data,"\"num\":(\\d+)\\W");
                            msgrul.append( "\r\n======num=======\r\n" + num + "\r\n================");
                            break;
                        case "lottery_route_must":
                            cdkey=getSubUtilSimple(data,"\"cdkey\":\"(.*?)\"");
                            gifname=getSubUtilSimple(data,"\"name\":\"(.*?)\"");
                            msgrul.append( "\r\n======num=======\r\n" + cdkey+ "\r\n"+gifname + "\r\n================");
                            break;
                        case "view_money":
                            num=getSubUtilSimple(data,"\"op\":(\\d+)\\W");
                            msgrul.append( "\r\n======money=======\r\n" + num + "\r\n================");
                            break;
                        case "query_gift_list":
                            List<String> info=getSubUtil(data,"\"info\":\"(\\w{16})\"");
                            List<String> name=getSubUtil(data,"\"name\":\"(.*?)\"");
                            msgrul.append("\r\n======礼物列表=======\r\n");
                            for (int i=0;i<info.size();i++)
                            {
                                msgrul.append("\r\n|->"+info.get(i)+"<-|"+decodeUnicode(name.get(i))+"│\r\n");
                            }
                            msgrul.append("\r\n=====================\r\n");
                            break;
                    }
                    msgrul.append("\r\n"+format.format(date) + ",XXOO【" + actname + "】成功");
                }
                else
                {
                    msgrul.append("\r\n"+format.format(date) + ",XXOO【" + actname + "】失败，原因：" +msg);
                }
                break;
            case 2:case 3:
                code=getSubUtilSimple(data,"\"modRet\":(.*?)\".*sMsg");
                if(code.equals(""))
                {
                    msg= getSubUtilSimple(data,"\"sMsg\":\"(.*?)\"");
                    msgrul.append("\r\n"+format.format(date) + ",【"+act.getActname()+"】:"+decodeUnicode(msg));
                }
                else
                {
                    msg=getSubUtilSimple(data,"\"modRet\".*\"sMsg\":\"(.*?)\"");
                    msgrul.append("\r\n"+format.format(date) + ",【"+act.getActname()+"】:"+decodeUnicode(msg));
                }
                break;
            case 4:
                code=getSubUtilSimple(data,"\"sMsg\":(\\d*)");
                if(code.equals(""))
                {
                    msg= getSubUtilSimple(data,"\"msg\":\"(.*?)\"");
                    msgrul.append("\r\n"+format.format(date) + ",【"+act.getActname()+"】:"+decodeUnicode(msg));
                }
                else
                {
                    msg=getSubUtilSimple(data,"\"gift_name\":\"(.*?)\"");
                    msgrul.append("\r\n"+format.format(date) + ",【"+act.getActname()+"】:"+decodeUnicode(msg));
                }
                break;
        }
        Message ssmsg = new Message();
        ssmsg.what = 233;
        ssmsg.obj = msgrul.toString();
        mHandler.sendMessage(ssmsg);

    }
}
