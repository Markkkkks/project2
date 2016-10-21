package com.example.zeus.project1014;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.Dialog;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by zeus on 2016/10/16.
 */

public class friends extends Activity {
    private ImageView add_friend,edit;
    private ListView lv;
    private Button mBack;
//    public class friend_datas extends Application{}
    private ArrayList<Object>friendsData;
    private int requestCode;
    private new_friend_dialog add;
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

    void initview() {
        lv = (ListView) findViewById(R.id.listView);
        add_friend = (ImageView) findViewById(R.id.id_addfriend);
        edit = (ImageView) findViewById(R.id.id_edit);
        mBack = (Button) findViewById(R.id.id_back_to_map);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(friends.this,"长按朋友实现删除",Toast.LENGTH_LONG).show();
            }
        });
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_save_pop:

                    String name = add.text_name.getText().toString().trim();
                    String mobile = add.text_mobile.getText().toString().trim();
                    System.out.println(name+"——"+mobile+"——");
                    break;
            }
        }
    };
    public void showEditDialog() {
        add = new new_friend_dialog(this, onClickListener,
                new new_friend_dialog.PriorityListener() {
            @Override
            public void changeArraylist(friend_data item_data) {
                friendsData.add(item_data);
                add.cancel();
                saveObject("aa");
                if(getObject("aa")!=null)
                    friendsData= (ArrayList<Object>) getObject("aa");
                lv.setAdapter(new myadapter(friends.this,friendsData));
            }
        });
        //add.setCanceledOnTouchOutside(true);//点击对话框外返回
        add.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);
        //对全局变量的修改
//        Data Classfriend= (Data) getApplication();
//        friends_Data=Classfriend.getFriends_Data();
//        friends_Data=new ArrayList<Object>();
//        Classfriend.setFriends_Data(friends_Data);
        initview();
        if(getObject("aa")!=null)
            friendsData= (ArrayList<Object>) getObject("aa");
        else
            friendsData=new ArrayList<Object>();
        lv.setAdapter(new myadapter(friends.this,friendsData));
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                friend_data item= (friend_data) lv.getAdapter().getItem(position);
                Toast.makeText(friends.this,"??",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(friends.this,delete_item.class);
                i.putExtra("position",position);
                startActivityForResult(i,requestCode);

                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friend_data itemdata= (friend_data) friendsData.get(position);
                AlertDialog show = new AlertDialog.Builder(friends.this).setTitle("朋友信息").setItems(
                        new String[]{"名字:"+itemdata.getName(),
                                "TELnum:"+itemdata.getTel_num(),
                                "经度:"+itemdata.getLatititue(),
                                "纬度:"+itemdata.getLongtitue(),
                                "距离："+ itemdata.getDistence()
                        },
                        null).setNegativeButton(
                        "确定", null).show();
            }
        });
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        saveObject("aa");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        friendsData= (ArrayList<Object>) getObject("aa");
        lv.setAdapter(new myadapter(friends.this,friendsData));
    }
}
