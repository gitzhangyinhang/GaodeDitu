package gaodetest.qianchi.com.gaodeditu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView = null;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle = null;
    private int dingweiPress = 1;
    public Map<String, String> retMap = new HashMap<String, String>();
    public List<CarStatuseEntity> carStatuseEntityList1 = new ArrayList<CarStatuseEntity>();
    public List<CarStatuseEntity> carStatuseEntityList2 = new ArrayList<CarStatuseEntity>();
    public MarkerOptions markerOptions = new MarkerOptions();
    public OkHttpClient okHttpClient = new OkHttpClient();
    public int isFirst = 1;//是否第一次进入
    public String access_token = null;
    private String mUser; // 帐号
    private String mPassword; // 密码
    ListView listView ;
    public Button basicmap;
    public Button rsmap;
    public Button nightmap;
    public Button navimap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();//去掉标题栏
        actionBar.hide();
        setContentView(R.layout.activity_main);
        SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
        access_token = myPreference.getString("access_token", "zheshikongde");

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

    /*    LatLng latLng =new LatLng(30.67,104.06);
        MarkerOptions markerOptions=new MarkerOptions();
        BitmapDescriptor giflist =BitmapDescriptorFactory.fromResource(R.mipmap.greencar);
        //定义了一个marker 的选项
        markerOptions.anchor(0.5f, 0.5f)//定义marker 图标的锚点。
                .position(latLng)//设置位置
                .title("成都市青羊区")//设置 Marker 的标题
                .icon(giflist)//设置MarkerOptions 对象的自定义图标
                .draggable(true)//设置标记是否可拖动。
                .period(50);//设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快
        aMap.addMarker(markerOptions);//添加到地图上
*/

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Styles
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
       /* aMap.setMyLocationEnabled(false);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。*/
        /*aMap.showIndoorMap(true);     //true：显示室内地图；false：不显示；*/
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 设置正常显示模式，aMap是地图控制器对象。
        //aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图，aMap是地图控制器对象。
        aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
        aMap.setMaxZoomLevel(17);//限制缩放级别
        init();//设置地图各种现实模式

        listView = (ListView) this.findViewById(R.id.listView);

        GetCarStatus();//得到车辆信息
        
        TextView img_dingwei = (TextView) findViewById(R.id.img_dingwei);//定位按钮
        img_dingwei.setOnClickListener(this);//定位按钮点击事件
        TextView img_guiji = (TextView) findViewById(R.id.img_guiji);//轨迹跟踪按钮
        img_guiji.setOnClickListener(this);//定位按钮点击事件
        TextView img_settings = (TextView) findViewById(R.id.img_settings);//注销按钮
        img_settings.setOnClickListener(this);//注销按钮点击事件
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");//获取字体库
        img_dingwei.setTypeface(font);//设置字体库
        img_guiji.setTypeface(font);//设置字体库
        img_settings.setTypeface(font);//设置字体库


    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        basicmap = (Button)findViewById(R.id.basicmap);
        basicmap.setOnClickListener(this);
        rsmap = (Button)findViewById(R.id.rsmap);
        rsmap.setOnClickListener(this);
        nightmap = (Button)findViewById(R.id.nightmap);
        nightmap.setOnClickListener(this);
        navimap = (Button)findViewById(R.id.navimap);
        navimap.setOnClickListener(this);


    }





    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.basicmap:{
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                break;}
            case R.id.rsmap:{
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                break;}
            case R.id.nightmap:{
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
                break;}
            case R.id.navimap:{
                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;}
            case R.id.img_dingwei: {

                if (dingweiPress == 1) {
                    TextView img_dingwei = (TextView) findViewById(R.id.img_dingwei);//定位按钮
                    img_dingwei.setTextColor(Color.GREEN);
                    TextView img_guiji = (TextView) findViewById(R.id.img_guiji);//轨迹跟踪按钮
                    img_guiji.setTextColor(Color.WHITE);
                    TextView img_settings = (TextView) findViewById(R.id.img_settings);//注销按钮
                    img_settings.setTextColor(Color.WHITE);

                    listView.setVisibility(View.VISIBLE);//显示该元素

                    dingweiPress = 2;
                } else {
                    TextView img_dingwei = (TextView) findViewById(R.id.img_dingwei);//定位按钮
                    img_dingwei.setTextColor(Color.WHITE);
                    TextView img_guiji = (TextView) findViewById(R.id.img_guiji);//轨迹跟踪按钮
                    img_guiji.setTextColor(Color.WHITE);
                    TextView img_settings = (TextView) findViewById(R.id.img_settings);//注销按钮
                    img_settings.setTextColor(Color.WHITE);
                    listView.setVisibility(View.INVISIBLE);//隐藏该元素
                    dingweiPress = 1;
                }
                break;
            }
            case R.id.img_guiji: {
                TextView img_dingwei = (TextView) findViewById(R.id.img_dingwei);//定位按钮
                img_dingwei.setTextColor(Color.WHITE);
                TextView img_guiji = (TextView) findViewById(R.id.img_guiji);//轨迹跟踪按钮
                img_guiji.setTextColor(Color.GREEN);
                TextView img_settings = (TextView) findViewById(R.id.img_settings);//注销按钮
                img_settings.setTextColor(Color.WHITE);

                // 给bnt1添加点击响应事件
                Intent intent = new Intent(MainActivity.this, GuijiGenzongActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //Intent.FLAG_ACTIVITY_CLEAR_TOP，这样开启B时将会清除该进程空间的所有Activity。
                //启动
                startActivity(intent);
                break;
            }
            case R.id.img_settings: {
                TextView img_dingwei = (TextView) findViewById(R.id.img_dingwei);//定位按钮
                img_dingwei.setTextColor(Color.WHITE);
                TextView img_guiji = (TextView) findViewById(R.id.img_guiji);//轨迹跟踪按钮
                img_guiji.setTextColor(Color.WHITE);
                TextView img_settings = (TextView) findViewById(R.id.img_settings);//注销按钮
                img_settings.setTextColor(Color.GREEN);
                //注销按钮
                setDialog();
                break;
            }
            case R.id.btn_piehuanzhuanghao:
                //切换账号
                Toast.makeText(this, "即将切换账号", Toast.LENGTH_SHORT).show();
                SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPreference.edit();
                editor.putString("access_token", "zheshikongde");
                editor.commit();
                // 给bnt1添加点击响应事件
                Intent intent = new Intent(MainActivity.this, LoginActity.class);
                //启动
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_tuichuyingyong:
                //退出程序
                Toast.makeText(this, "即将退出应用", Toast.LENGTH_SHORT).show();

                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
               /* android.os.Process.killProcess(android.os.Process.myPid());
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);*/
                break;


        }
    }

    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String PlateID = data.get("PlateID").toString();
            if (PlateID.equals("车牌号")) {

            } else {
                for (CarStatuseEntity carStatuseEntity : carStatuseEntityList2) {
                    if (carStatuseEntity.getCar().getPlateID().equals(PlateID)) {
                        LatLng latLng = new LatLng(carStatuseEntity.getLatitude(), carStatuseEntity.getLongitude());
                        markerOptions.position(latLng);//设置显示位置
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));//显示当前位置，设置缩放级别
                        listView.setVisibility(View.INVISIBLE);
                        dingweiPress = 1;//显示LISTVIEW
                        TextView img_dingwei = (TextView) findViewById(R.id.img_dingwei);//定位按钮
                        img_dingwei.setTextColor(Color.WHITE);

                    }
                }
            }

        }
    }

    private void setDialog() {//退出按钮效果
        Dialog mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_piehuanzhuanghao).setOnClickListener(this);
        root.findViewById(R.id.btn_tuichuyingyong).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.BottomDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    public void GetAllMarkerOptions(List<CarStatuseEntity> carStatuseEntityList2) {//把所有车辆显示在地图上

   /*   BitmapDescriptor greencar =BitmapDescriptorFactory.fromResource(R.mipmap.greencar);*/
        MarkerOptions markerOptions2 = new MarkerOptions();


        if (isFirst == 1) {//如果第一次进入
            carStatuseEntityList1 = carStatuseEntityList2;//把第二次的数据赋值给以一次的List
        }
        aMap.clear();//清除之前显示的车辆Marker
        for (int i = 0; i < carStatuseEntityList2.size(); i++) {//循环第二次得到的数据

            if (!carStatuseEntityList2.get(i).isLocated()) {//如果没有定位
                continue;
            }

             final SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
            if (carStatuseEntityList2.get(i).getStatus().equals("离线")) {
                SmoothMoveMarker smoothMarker1 = new SmoothMoveMarker(aMap);
                smoothMarker1.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.graycar));// 设置滑动的图标
                List<LatLng> subList = new ArrayList<LatLng>();
                LatLng latLng1 = new LatLng(carStatuseEntityList1.get(i).getLatitude(), carStatuseEntityList1.get(i).getLongitude());
                LatLng latLng2 = new LatLng(carStatuseEntityList2.get(i).getLatitude(), carStatuseEntityList2.get(i).getLongitude());



                final String carPlateID=carStatuseEntityList2.get(i).getCar().getPlateID();
                Marker marker=aMap.addMarker(new MarkerOptions()
                        .position(latLng1)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(carPlateID)))
                        .draggable(true));
                TranslateAnimation translateAnimation=new TranslateAnimation(latLng2);
                translateAnimation.setDuration(2400);
                marker.setAnimation( translateAnimation);
                marker.startAnimation();//开始动画

                subList.add(0, latLng1);
                subList.add(1, latLng2);
                smoothMarker1.setPoints(subList);
                smoothMarker1.setTotalDuration(2);  // 设置滑动的总时间
               smoothMarker.startSmoothMove();   // 开始滑动


            }


            if (carStatuseEntityList2.get(i).getStatus().equals("停驶")) {



                smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.redcar));// 设置滑动的图标
                List<LatLng> subList = new ArrayList<LatLng>();
                LatLng latLng1 = new LatLng(carStatuseEntityList1.get(i).getLatitude(), carStatuseEntityList1.get(i).getLongitude());
                LatLng latLng2 = new LatLng(carStatuseEntityList2.get(i).getLatitude(), carStatuseEntityList2.get(i).getLongitude());


                final String carPlateID=carStatuseEntityList2.get(i).getCar().getPlateID();
                Marker marker=aMap.addMarker(new MarkerOptions()
                        .position(latLng1)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(carPlateID)))
                        .draggable(true));
                TranslateAnimation translateAnimation=new TranslateAnimation(latLng2);
                translateAnimation.setDuration(2400);
                marker.setAnimation( translateAnimation);
                marker.startAnimation();//开始动画


                subList.add(0, latLng1);
                subList.add(1, latLng2);
                smoothMarker.setPoints(subList);
                smoothMarker.setTotalDuration(2);  // 设置滑动的总时间
                smoothMarker.startSmoothMove();   // 开始滑动


            }

            if (carStatuseEntityList2.get(i).getStatus().equals("行驶")) {

                smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.greencar));// 设置滑动的图标
                List<LatLng> subList = new ArrayList<LatLng>();
                final LatLng latLng1 = new LatLng(carStatuseEntityList1.get(i).getLatitude(), carStatuseEntityList1.get(i).getLongitude());
                final LatLng latLng2 = new LatLng(carStatuseEntityList2.get(i).getLatitude(), carStatuseEntityList2.get(i).getLongitude());

                final String carPlateID=carStatuseEntityList2.get(i).getCar().getPlateID();

                Marker marker=aMap.addMarker(new MarkerOptions()
                        .position(latLng1)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(carPlateID)))
                        .draggable(true));
                TranslateAnimation translateAnimation=new TranslateAnimation(latLng2);
                translateAnimation.setDuration(2400);
                marker.setAnimation( translateAnimation);
                marker.startAnimation();//开始动画



                subList.add(0, latLng1);
                subList.add(1, latLng2);
                smoothMarker.setPoints(subList);
                smoothMarker.setTotalDuration(2);  // 设置滑动的总时间
                smoothMarker.startSmoothMove();   // 开始滑动


            }


        }
        carStatuseEntityList1 = carStatuseEntityList2;//把第二次的数据赋值给第一次的List

        if (isFirst == 1) {//是第一次进入就会设置显示区域和缩放级别
            LatLng latLng = new LatLng(carStatuseEntityList2.get(1).getLatitude(), carStatuseEntityList2.get(1).getLongitude());
            markerOptions.position(latLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 12));//显示当前位置，设置缩放级别

            isFirst = 2;
        }
      /*  if (dingweiPress == 1) {
          *//*  ListView listView = (ListView) this.findViewById(R.id.listView);
              listView.setVisibility(View.INVISIBLE);//隐藏该元素*//*
        } else {*/

        handler.sendEmptyMessage(0);//线程之间通讯用的处理器

    }


    protected Bitmap getMyBitmap(String pm_val) {
        Bitmap bitmap = BitmapDescriptorFactory.fromResource(
                R.drawable.popnf).getBitmap();
        bitmap = Bitmap.createBitmap(bitmap, 0 ,0, bitmap.getWidth(),
                bitmap.getHeight());
      /*  Bitmap  bitmap = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);*/
        Canvas canvas = new Canvas(bitmap);
/*        Paint p = new Paint();
        p.setColor(Color.GRAY);// 设置灰色
        p.setStyle(Paint.Style.FILL);//设置填满
        RectF oval2 = new RectF(60, 100, 200, 240);// 设置个新的长方形，扫描测量
        canvas.drawArc(oval2, 200, 130, true, p);*/

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(false);
        textPaint.setTextSize(40f);
        textPaint.setColor(getResources().getColor(R.color.color5));
        canvas.drawText(pm_val, 20, 75 ,textPaint);// 设置bitmap上面的文字位置
        return bitmap;
    }


public void add(){
    Date date=new Date();
   System.out.println("shijian "+date);
}



    private Handler handler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            if(msg.what==0) //如果消息是刚才发送的标识
            {
                addData();
            }
            return false;
        }
    });

    public void addData (){ // 添加数据到界面

        //获取到集合数据
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item1 = new HashMap<String, Object>();
        item1.put("PlateID", "车牌号");
        item1.put("Device", "终端号");
        item1.put("Status", "状态");
        item1.put("Speed", "速度");
        data.add(item1);
        for (CarStatuseEntity carStatuseEntity : carStatuseEntityList2) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("PlateID", carStatuseEntity.getCar().getPlateID());
            item.put("Device", carStatuseEntity.getCar().getDevice().getId());
            item.put("Status", carStatuseEntity.getStatus());
            item.put("Speed", carStatuseEntity.getSpeed());
            data.add(item);
        }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item,
                new String[]{"PlateID", "Device", "Status", "Speed"}, new int[]{R.id.PlateID, R.id.Device, R.id.Status, R.id.Speed});
        //实现列表的显示
        listView.setAdapter(adapter);
        //条目点击事件
        listView.setOnItemClickListener(new ItemClickListener());
    }



    public void GetCarStatus() { //得到车辆信息


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    String url = "http://ldz2017.vicp.io:18539/api/GetAllCarsStatus";
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + access_token)
                            .get()
                            .build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        String jsonString = response.body().string();//得到Json数据

                  /*  response.code()==401*/
                        if (response.code() == 401) {

                            GetLoginInfo();//冲心得到Token

                        } else {


                            Gson gson = new Gson();  //得到数据封装成List
                            Type type = new TypeToken<ArrayList<CarStatuseEntity>>() {
                            }.getType();
                            carStatuseEntityList2 = gson.fromJson(jsonString, type);//把JSON数据转化为类


                            GetAllMarkerOptions(carStatuseEntityList2); //把所有车辆显示在地图上
                            Thread.sleep(2000);
                        }
                    } catch (IOException e) {
                        e.getStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    public void GetLoginInfo() {//得到登录信息
        SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
        mUser = myPreference.getString("username", "kong");//得到账号
        mPassword = myPreference.getString("password", "kong");//得到密码

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://ldz2017.vicp.io:18539/Token";

                RequestBody requestBody = new FormBody.Builder()
                        .add("grant_type", "password")
                        .add("username", mUser)
                        .add("password", mPassword)
                        .build();
                Request request = new Request.Builder()
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();

                    String jsonString = response.body().string();//得到Json数据
                    Log.i("BHJ", jsonString);

                    Gson gson = new Gson();
                    retMap = gson.fromJson(jsonString,
                            new TypeToken<Map<String, String>>() {
                            }.getType());
                    SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPreference.edit();
                    editor.putString("access_token", retMap.get("access_token"));
                    editor.commit();

                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        });
        a.start();

    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

    }

    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

}
