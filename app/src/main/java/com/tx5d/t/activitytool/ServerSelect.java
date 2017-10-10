package com.tx5d.t.activitytool;

import java.util.List;

/**
 * Created by Mre on 2017/9/29.
 */

public class ServerSelect
{
    ServerSelect()
    {
    }
    ServerSelect(String t ,String v ,List<ServerSelect> opt_data_array)
    {
        this.t=t;
        this.v=v;
        this.opt_data_array=opt_data_array;
    }
    public String t ;
    public String v ;
    public List<ServerSelect> opt_data_array ;
}