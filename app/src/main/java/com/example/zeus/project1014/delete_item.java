package com.example.zeus.project1014;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by zeus on 2016/10/19.
 */
public class delete_item extends Activity {
    private Button mYes,mNo;
    private TextView mName,mNum;
    private ArrayList<friend_data> friendsData;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete);
        position= (int) getIntent().getSerializableExtra("position");
        if(getObject("aa")!=null)
            friendsData= (ArrayList<friend_data>) getObject("aa");
        initview();
        mName.setText("名字: "+friendsData.get(position).getName());
        mNum.setText("电话号码: "+friendsData.get(position).getTel_num());

    }
    private void initview(){
        mName= (TextView) findViewById(R.id.id_deletename);
        mNum= (TextView) findViewById(R.id.id_deleteTel);
        mYes= (Button) findViewById(R.id.button1);
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               friendsData.remove(1);
                saveObject("aa");
                finish();
            }
        });
        mNo= (Button) findViewById(R.id.button2);
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }





    private void saveObject(String name){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(friendsData);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }
    //以上为保存对象的代码

    private Object getObject(String name){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }
    //以上为取得对象的代码
}
