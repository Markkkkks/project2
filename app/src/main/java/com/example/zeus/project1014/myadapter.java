package com.example.zeus.project1014;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zeus on 2016/10/16.
 */
public class myadapter extends BaseAdapter  {
    private ListView lv;
    private Context context;
    public myadapter(Context context){
        this.context=context;
    }
    private ArrayList<Object> datas;
    public myadapter(Context context,ArrayList<Object> a){
        this.context=context;
        this.datas=a;

    }
    public  Context getContext(){
        return context;
    }



    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final friend_data data= (friend_data) getItem(position);
        RelativeLayout tv=null;
        if(convertView!=null)
            tv= (RelativeLayout) convertView;
        else {
            tv= (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.friend_item,null);
        }

        //绑定
        ImageView icon= (ImageView) tv.findViewById(R.id.id_icon);
        TextView name= (TextView) tv.findViewById(R.id.id_name);
        //输出
        name.setText(data.getName());
        return tv;
    }
}
