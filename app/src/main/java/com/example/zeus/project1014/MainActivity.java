package com.example.zeus.project1014;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private MyLocationData mMyLocationData;
    private boolean isFirstin = true;
    private Context mContext;
    private int ACCESS_COARSE_LOCATION_REQUEST_CODE;
    //储存我的实时经纬度
    private double mLatititue;
    private double mLongtitue;
    private ArrayList<friend_data>friends_Data;
    private Button mHome,mFriend,mFlash;
    private static final String strRes = "android.provider.Telephony.SMS_RECEIVED";

    /* 自定义ACTION常数，作为广播的Intent Filter识别常数 */
    private static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

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
    private void saveObject(String name){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(friends_Data);
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

    //计算距离
    public Double Distance(double lat1, double lng1,double lat2, double lng2)
    {


        Double R=6370996.81;  //地球的半径

    /*
     * 获取两点间x,y轴之间的距离
     */
        Double x = (lng2 - lng1)*Math.PI*R*Math.cos(((lat1+lat2)/2)*Math.PI/180)/180;
        Double y = (lat2 - lat1)*Math.PI*R/180;


        Double distance = Math.hypot(x, y);   //得到两点之间的直线距离

        return   distance;

    }

    //画标记和连线
    public void DrawLine_and_Maker(friend_data friend){
        LatLng point = new LatLng(friend.getLatititue(),friend.getLongtitue());
        //LatLng point = new LatLng(22.258956,113.590602 );
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.bitmap);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        OverlayOptions text=new TextOptions().position(point)
                .bgColor(0xAAFFFF00)
                .fontSize(75)
                .fontColor(0xFF000000)
                .text(friend.getName())
                .rotate(0);
        mBaiduMap.addOverlay(text);
        DrawLines(mLatititue,mLongtitue,friend.getLatititue(),friend.getLongtitue());
    }

    //画线
    public void  DrawLines(double startlats,double startlongs,double latline,double lngline){

        LatLng p1 = new LatLng(startlats, startlongs);
        LatLng p2 = new LatLng(latline, lngline);
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(p1);
        points.add(p2);
        OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
        mBaiduMap.addOverlay(ooPolyline);

  /*
     * 调用Distance方法获取两点间x,y轴之间的距离
     */
        double cc= Distance(startlats,  startlongs,latline,lngline);

        int length=(int)cc;

        //Toast.makeText(this, "您与终端距离"+length+"米", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        //判定权限，如果已取得权限，则初始化图层界面
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_COARSE_LOCATION_REQUEST_CODE);
        }
        setContentView(R.layout.activity_main);
        mContext=this;
        //初始化地图
        Initview();
        //初始化定位
        InitLocation();
    }
    //初始化定位功能
    private void InitLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //初始化图像
    private void Initview() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mHome= (Button) findViewById(R.id.btn_locate);
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latlng=new LatLng(mLatititue,mLongtitue);//暨南大学珠海校区
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
            }
        });
        mFriend= (Button) findViewById(R.id.btn_friends);
        mFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,friends.class);
                startActivity(i);
            }
        });

        //mFlash用于发送短信
        mFlash= (Button) findViewById(R.id.btn_refresh);
        mFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空图标和画线
                mBaiduMap.clear();
                //Toast.makeText(MainActivity.this,"发送短信",Toast.LENGTH_SHORT).show();
                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage("15820578276", null, "123", null, null);
                try
                {
          /* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
                    Intent itSend = new Intent(SMS_SEND_ACTIOIN);
                    Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);

          /* sentIntent参数为传送后接受的广播信息PendingIntent */
                    PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);

          /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
                    PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);

          /* 发送SMS短信，注意倒数的两个PendingIntent参数 */
                    friends_Data= (ArrayList<friend_data>) getObject("aa");
                    if (friends_Data==null)
                        return;
                    for (friend_data item:
                         friends_Data) {
                        smsManager.sendTextMessage(item.getTel_num(), null, "Where are you", mSendPI, mDeliverPI);

                    }

                    Toast.makeText(MainActivity.this,"sending",Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"出现错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //权限处理
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Initview();
                InitLocation();
            }
            else {
                // Permission Denied
                Toast.makeText(this, "访问被拒绝！", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //以上代码为Android6.0以上申请定位权限


//定位监听
    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mMyLocationData=new MyLocationData.Builder()//
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(mMyLocationData);
            mLatititue=location.getLatitude();
            mLongtitue=location.getLongitude();
            if(isFirstin)
            {
                LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());//暨南大学珠海校区
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
                isFirstin=false;
                Toast.makeText(mContext,location.getAddrStr(),Toast.LENGTH_SHORT).show();
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            //Log.i("BaiduLocationApiDem", sb.toString());
        }
    }






    //短信接收响应
    private BroadcastReceiver SMSBroadcastReceiver =new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            if (null != bundle) {
                Object[] smsObj = (Object[]) bundle.get("pdus");
                for (Object object : smsObj) {
                    msg = SmsMessage.createFromPdu((byte[]) object);
                    Date date = new Date(msg.getTimestampMillis());//时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);
                    System.out.println("number:" + msg.getOriginatingAddress()
                            + "   body:" + msg.getDisplayMessageBody() + "  time:"
                            + msg.getTimestampMillis());
                    String content = msg.getDisplayMessageBody();

                    //在这里写自己的逻辑
                    if (content.contains("//")) {
                        //TODO
                        String[] a = content.split("//");
                        Toast.makeText(MainActivity.this,
                                Double.valueOf(a[0]) + "//" + Double.valueOf(a[1]),
                                Toast.LENGTH_SHORT).show();
                        friends_Data = (ArrayList<friend_data>) getObject("aa");
                        for (friend_data item
                                : friends_Data) {
                            if (  msg.getOriginatingAddress().contains(item.getTel_num())) {
                                item.setLatititue(Double.valueOf(a[0]));
                                item.setLongtitue(Double.valueOf(a[1]));
                                DrawLine_and_Maker(item);
                                //设置对象的距离
                                item.setDistence(Distance(mLatititue,mLongtitue,
                                        item.getLatititue(),item.getLongtitue()));
                            }
                        }
                        saveObject("aa");
                    }
                    if (content.equals("Where are you")) {
                        //响应码？
//                        Toast.makeText(MainActivity.this,//
//                                "收到where are you",
//                                Toast.LENGTH_SHORT).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        //* 建立自定义Action常数的Intent(给PendingIntent参数之用) */
                        Intent itSend = new Intent(SMS_SEND_ACTIOIN);
                        Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
                         /* sentIntent参数为传送后接受的广播信息PendingIntent */
                        PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);

          /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
                        PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);
                        smsManager.sendTextMessage(msg.getOriginatingAddress(), null,//
                                Double.toString(mLatititue) + "//" + Double.toString(mLongtitue),
                                mSendPI, mDeliverPI);
                    }

                }
            }
        }

    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onStop() {
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
            mLocationClient.start();
        //开启短信服务
        IntentFilter filter = new IntentFilter();
        filter.addAction(strRes);
        registerReceiver(SMSBroadcastReceiver,filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


}



