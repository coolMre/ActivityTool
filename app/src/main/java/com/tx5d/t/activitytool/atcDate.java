package com.tx5d.t.activitytool;

import java.util.ArrayList;

/**
 * Created by Mre on 2017/10/10.
 */

public class atcDate {
    public String adlink ;
    public String ver ;
    public ArrayList<actinfo> Date ;

    public String getAdlink() {
        return adlink;
    }

    public void setAdlink(String adlink) {
        this.adlink = adlink;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public ArrayList<actinfo> getDate() {
        return Date;
    }

    public void setDate(ArrayList<actinfo> date) {
        Date = date;
    }
}
