package com.tx5d.t.activitytool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
    public void GetRolelist(final Handler mHandler,final String area)
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
                    String data=getSubUtilSimple(query_role_result,"_webplat_msg=\\d\\|(.*?)\\|&_webplat_msg_code=0");

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
                    msg.obj = rolenamelist;//可以是基本类型，可以是对象，可以是List、map等；
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


        //return  rolenamelist;
    }
}
