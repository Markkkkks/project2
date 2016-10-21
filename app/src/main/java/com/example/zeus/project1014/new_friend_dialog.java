package com.example.zeus.project1014;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by zeus on 2016/10/16.
 */
public class new_friend_dialog extends Dialog {

    /**
     * 上下文对象 *
     */
    Activity context;

    private Button btn_save;
    private PriorityListener listener ;
    public EditText text_name;

    public EditText text_mobile;

    public EditText text_info;
    private ArrayList<Object>datas;

    private View.OnClickListener mClickListener;



    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
    public new_friend_dialog(Activity context) {
        super(context);
        this.context = context;

    }
    public interface PriorityListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void changeArraylist(friend_data item_data);
    }

    public new_friend_dialog(Activity context,
                             View.OnClickListener clickListener,
                             PriorityListener listener) {
        super(context);
        this.context = context;
        this.mClickListener = clickListener;
        this.datas=datas;
        this.listener = listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.new_friend);

        text_name = (EditText) findViewById(R.id.text_name);
        text_mobile = (EditText) findViewById(R.id.text_mobile);



  /*
   * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
   * 对象,这样这可以以同样的方式改变这个Activity的属性.
   */
        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        // 根据id在布局中找到控件对象
        btn_save = (Button) findViewById(R.id.btn_save_pop);

        // 为按钮绑定点击事件监听器
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tel_num=text_mobile.getText().toString();
                String name=text_name.getText().toString();

                if(!isMobileNO(Tel_num)){
                    Toast.makeText(context
                            ,"非法输入"
                            ,Toast.LENGTH_SHORT)
                            .show();
                    cancel();
                }
                else {
                    friend_data item_data = new friend_data(name, Tel_num);
                    listener.changeArraylist(item_data);
                }

            }
        });

        this.setCancelable(true);
    }
}