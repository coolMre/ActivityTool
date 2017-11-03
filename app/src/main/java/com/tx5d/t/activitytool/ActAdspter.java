package com.tx5d.t.activitytool;

/**
 * Created by Mre on 2017/10/11.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mre on 2017/10/11.
 */

public class ActAdspter extends BaseAdapter {

    private int defaultSelection = -1;
    private int bg_selected_color;

    public List<actinfo> getData() {
        return data;
    }

    public List<actinfo> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ActAdspter(Context context,List<actinfo> data){
        this.context=context;
        Resources resources = context.getResources();
        bg_selected_color =resources.getColor(R.color.bg_selected);// 背景选中的颜色
        this.data=new ArrayList<actinfo>();
        loadListnode(data);
        this.layoutInflater=LayoutInflater.from(context);
    }
    private void loadListnode(List<actinfo> Listnodedata)
    {
        for (actinfo tmp:Listnodedata)
        {
            this.data.add(tmp);
            if(tmp.getAtcExt()!=null)
                loadListnode(tmp.getAtcExt());
        }

    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class Zujian{
        public ImageView image;
        public TextView title;
        public Button view;
        public TextView info;
        public TextView autoinfo;
    }
    @Override
    public int getCount() {
        return data.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    //返回当前布局的样式type
    @Override
    public int getItemViewType(int position) {
        if(data.get(position).getAutoSub().equals("0"))
            return 0;
        else
            return 1;
    }

    //返回你有多少个不同的布局
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian=null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            zujian=new Zujian();
            //获得组件，实例化组件
            if(getItemViewType(position)==0)
                {
                    convertView = layoutInflater.inflate(R.layout.tlist, null);
                    // zujian.image=(ImageView)convertView.findViewById(R.id.image);
                    zujian.title=(TextView)convertView.findViewById(R.id.titletextView);
                    //zujian.view=(Button)convertView.findViewById(R.id.view);
                    zujian.info=(TextView)convertView.findViewById(R.id.infotextView);
                    convertView.setTag(zujian);
                }
            else
                {
                    convertView = layoutInflater.inflate(R.layout.ilist, null);
                    //zujian.image=(ImageView)convertView.findViewById(R.id.image);
                    zujian.title=(TextView)convertView.findViewById(R.id.titletextView2);
                    //zujian.view=(Button)convertView.findViewById(R.id.view);
                    zujian.info=(TextView)convertView.findViewById(R.id.infotextView2);
                    zujian.autoinfo=(TextView)convertView.findViewById(R.id.autotextView2);
                    convertView.setTag(zujian);
                }

        }else{
            zujian=(Zujian)convertView.getTag();
        }
        if (position == defaultSelection) {// 选中时设置单纯颜色
            convertView.setBackgroundColor(bg_selected_color);
        } else {// 未选中时设置selector
            convertView.setBackgroundResource(R.drawable.listview_color_selector);
        }
        //绑定数据
//        zujian.image.setBackgroundResource((Integer)data.get(position).get("image"));
        zujian.title.setText(data.get(position).getActname());
        zujian.info.setText(data.get(position).getGiftname());
        switch (data.get(position).getAutoSub())
        {
            case "1":zujian.autoinfo.setText("模式:自动");break;
            case "2":zujian.autoinfo.setText("模式:手动");break;
        }
        return convertView;
    }
    /**
     * @param position
     *            设置高亮状态的item
     */
    public void setSelectPosition(int position) {
//        if(getItemViewType(position)==0)
//            return;
        if (!(position < 0 || position > data.size())) {
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }
    /**
     *            返回高亮状态的item
     */
    public int getSelectPosition() {
        return defaultSelection;
    }


}

