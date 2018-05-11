package gaodetest.qianchi.com.gaodeditu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/15.
 */

public class GuijiShowActivity extends AppCompatActivity implements View.OnClickListener {
    public MapView mapview;
    private AMap aMap = null;
    private SmoothMoveMarker smoothMarker;
    public int tarckId = 0;
    public int speedId = 0;
    public String access_token = null;
    public OkHttpClient okHttpClient = new OkHttpClient();
    public String carID;
    public String plateID = null;
    public String startDate;
    public String pauseOrPlay="play";
    public String endDate;
    public int showtime = 20;
    public ListView listView;
    public TextView startDateTextView;//开始时间
    public TextView endDateTextView;//结束时间
    public RelativeLayout relativeLayout;
    public List<Track> tracks = new ArrayList<Track>();
    public List<Points> points = new ArrayList<Points>();
    public List<LatLng> lists = new ArrayList<LatLng>();
    public Button basicmap;
    public Button rsmap;
    public Button nightmap;
    public Button navimap;
    public TextView img_play1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();//去掉标题栏
        actionBar.hide();
        setContentView(R.layout.guiji_show);
        mapview = (MapView) findViewById(R.id.TarckShowMap);
        mapview.onCreate(savedInstanceState);

        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapview.getMap();
        }
        aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
        aMap.setMaxZoomLevel(17);//限制缩放级别


        init();//设置地图各种现实模式

       /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
      /*  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();*/

   /*获取Bundle中的数据，注意类型和key*/
        startDate = bundle.getString("startDate");
        endDate = bundle.getString("endDate");
        carID = bundle.getString("carId");//车辆id
        plateID = bundle.getString("plateID");//车牌号
        /*   String a=  gson.toJson(new Date());*/
        /*   Toast.makeText(this, carID+" " +startDate+" "+endDate, Toast.LENGTH_SHORT).show();*/

        listView = (ListView) this.findViewById(R.id.listView2);
        relativeLayout = (RelativeLayout) findViewById(R.id.main_bottom);//菜单栏

        TextView plateIDTextView = (TextView) findViewById(R.id.plateIDTextView);//车牌号设置
        plateIDTextView.setText(plateID);//车牌号设置

        Button button4 = (Button) findViewById(R.id.button4);//切换button
        button4.setOnClickListener(this);//开始时间按钮点击事件

        LinearLayout img_play = (LinearLayout) findViewById(R.id.img_play);//播放按钮
        img_play1 = (TextView) findViewById(R.id.img_play1);//播放图标
        img_play.setOnClickListener(this);//播放按钮点击事件

        LinearLayout img_stop = (LinearLayout) findViewById(R.id.img_stop);//停止按钮
        TextView img_stop1 = (TextView) findViewById(R.id.img_stop1);//停止图标
        img_stop.setOnClickListener(this);//停止按钮点击事件


        LinearLayout img_down = (LinearLayout) findViewById(R.id.img_down);//快退按钮
        TextView img_down1 = (TextView) findViewById(R.id.img_down1);//快退图标
        img_down.setOnClickListener(this);//快退按钮点击事件

        LinearLayout img_speed = (LinearLayout) findViewById(R.id.img_speed);//快进按钮
        TextView img_speed1 = (TextView) findViewById(R.id.img_speed1);//快进图标
        img_speed.setOnClickListener(this);//快进按钮点击事件

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");//获取字体库
        img_play1.setTypeface(font);//设置字体库
        img_stop1.setTypeface(font);//设置字体库

        img_down1.setTypeface(font);//设置字体库
        img_speed1.setTypeface(font);//设置字体库

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {

                GetTracesList();//得到所有数据
            }
        });
        a.start();
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

/*        for (int i = 0; i < tracks.size(); i++) {

            System.out.println(tracks.get(i).getBegin() + "    " + tracks.get(i).getEnd());
        }*/
        addWay();//绘制路径，设置时间
        addDataSelect();//Listview 添加数据，并设置点击事件

                 /* AddIdToTracks();//给每一个Track赋上ID值*/


    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        basicmap = (Button) findViewById(R.id.basicmap);
        basicmap.setOnClickListener(this);
        rsmap = (Button) findViewById(R.id.rsmap);
        rsmap.setOnClickListener(this);
        nightmap = (Button) findViewById(R.id.nightmap);
        nightmap.setOnClickListener(this);
        navimap = (Button) findViewById(R.id.navimap);
        navimap.setOnClickListener(this);

    }

    public void addDate() {//设置菜单栏时间
        startDateTextView = (TextView) findViewById(R.id.startDateTextView);//开始时间textview 设置开始时间
        endDateTextView = (TextView) findViewById(R.id.endDateTextView);//结束时间textview 设置结束时间


        startDateTextView.setText(utc2Local(tracks.get(tarckId).getBegin()));
        ;//开始时间textview 设置开始时间
        endDateTextView.setText(utc2Local(tracks.get(tarckId).getEnd()));//结束时间textview 设置结束时间


    }


    public static String utc2Local(String utcTime) {//utc时间转换成北京时间
        String utcTimePatten = null;//utc时间格式标准
        if (utcTime.length() > 20) {
            utcTimePatten = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        } else {
            utcTimePatten = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        }
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

    public static String utcCutDown(String utcTime) {//utc时间减少八个小时
        String utcTimePatten = null;//utc时间格式标准
        if (utcTime.length() > 20) {
            utcTimePatten = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        } else {
            utcTimePatten = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        }
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getDefault());
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(utcTimePatten);
        localFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

    ;

    public void addWay() {
        addDate();//添加时间在界面
        GetTracePoints();//得到这一行的轨迹点
        pointsTolist();//把这一行的轨迹点转换成list

        aMap.addPolyline(GetPolylineOptions());//绘制轨迹图
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lists.get(0), 14));//显示当前位置，设置缩放级别
        SmoothMoveMarker smoothMarker1 = new SmoothMoveMarker(aMap);
        smoothMarker = smoothMarker1;
        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.greencar));// 设置滑动的图标
        smoothMarker.setPoints(lists);//设置轨迹路线


     /*   smoothMarker.setTotalDuration(20);  // 设置滑动的总时间
        smoothMarker.startSmoothMove();   // 开始滑动*/
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.basicmap: {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                break;
            }
            case R.id.rsmap: {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                break;
            }
            case R.id.nightmap: {
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
                break;
            }
            case R.id.navimap: {
                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;
            }

            case R.id.button4: {
                relativeLayout.setVisibility(view.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.img_play: {//开始移动
                Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");//获取字体库

                if (pauseOrPlay.equals("play")) {
                    pauseOrPlay="pause";
                    img_play1.setText(R.string.img_pause);
                    smoothMarker.setTotalDuration(showtime);
                    aMap.setInfoWindowAdapter(infoWindowAdapter);
                    // 显示 infowindow
                    smoothMarker.getMarker().showInfoWindow();
                    // 设置移动的监听事件  返回 距终点的距离  单位 米
                    smoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
                        @Override
                        public void move(final double distance) {

                            runOnUiThread(new Runnable() {

                                public void run() {
                                    if (infoWindowLayout != null && title != null) {

                                        if (speedId < points.size()) {
                                            System.out.println("当前使用下标" + speedId);
                                            title.setText(points.get(speedId).getSpeed() + "公里/时");
                                            speedId++;

                                        }
                                    }

                                }
                            });

                        }
                    });
                    smoothMarker.startSmoothMove();

                } else {
                    pauseOrPlay="play";
                    img_play1.setText(R.string.img_play);
                    smoothMarker.stopMove();

                }

                break;
            }
            case R.id.img_stop: {//停止
                img_play1.setText(R.string.img_play);
                pauseOrPlay="play";
                speedId = 0;
                aMap.addPolyline(GetPolylineOptions());//绘制轨迹图
                SmoothMoveMarker smoothMarker1 = new SmoothMoveMarker(aMap);
                smoothMarker = smoothMarker1;
                smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.greencar));// 设置滑动的图标
                smoothMarker.setPoints(lists);//设置轨迹路线
                break;
            }
            case R.id.img_down: {//减速
                showtime *= 2;
                smoothMarker.setTotalDuration(showtime);
                break;

            }
            case R.id.img_speed: {//加速
                showtime /= 2;
                smoothMarker.setTotalDuration(showtime);
                break;

            }
        }

    }


    /**
     * 个性化定制的信息窗口视图的类
     * 如果要定制化渲染这个信息窗口，需要重载getInfoWindow(Marker)方法。
     * 如果只是需要替换信息窗口的内容，则需要重载getInfoContents(Marker)方法。
     */
    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {

        // 个性化Marker的InfoWindow 视图
        // 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会调用getInfoContents(Marker)方法获取
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        // 这个方法只有在getInfoWindow(Marker)返回null 时才会被调用
        // 定制化的view 做这个信息窗口的内容，如果返回null 将以默认内容渲染
        @Override
        public View getInfoContents(Marker marker) {

            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    /**
     * 自定义View并且绑定数据方法
     *
     * @param marker 点击的Marker对象
     * @return 返回自定义窗口的视图
     */
    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(this);
            snippet = new TextView(this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);


            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }


    public void addDataSelect() { // 添加数据到界面
        //获取到集合数据
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < tracks.size(); i++) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("tarckId", "路径" + (i + 1));
            item.put("startDate", "开始时间  ：" + utc2Local(tracks.get(i).getBegin()));
            item.put("endDate", "结束时间  ：" + utc2Local(tracks.get(i).getEnd()));
            item.put("Length", "路线长度  ：" + tracks.get(i).getLength() + "米");
            data.add(item);
        }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.guiji_select,
                new String[]{"tarckId", "startDate", "endDate", "Length"}, new int[]{R.id.tarckId, R.id.startDate, R.id.endDate, R.id.Length});
        //实现列表的显示
        listView.setAdapter(adapter);
        //条目点击事件
        listView.setOnItemClickListener(new GuijiShowActivity.ItemClickListener());
    }

    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         /*   ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);*/
       /*     System.out.println("当前list长度"+tracks.get(position).getPoints().size());*/


            speedId = 0;
            relativeLayout.setVisibility(view.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            tarckId = position;//得到应该显示的路径ID
            addWay();//显示路径线路图

        }
    }


    /**
     * 把每一个points的路径转化为list
     */
    public void pointsTolist() {
        lists = new ArrayList<LatLng>();

        for (int j = 0; j < points.size(); j++) {
            LatLng latLng = new LatLng(points.get(j).getLatitude(), points.get(j).getLongitude());
            lists.add(latLng);
        }

        System.out.println("gfdgf");
    }

    public void GetTracesList() { //得到车辆信息

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                access_token = myPreference.getString("access_token", "zheshikongde");

                RequestBody requestBody = new FormBody.Builder()
                        .add("CarId", carID)
                        .add("Begin", startDate)
                        .add("End", endDate)
                        .build();

                String url = "http://ldz2017.vicp.io:18539/api/GetTracesList";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + access_token)
                        .post(requestBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String jsonString = response.body().string();//得到Json数据


                    Gson gson = new Gson();  //得到数据封装成List
                    Type type = new TypeToken<List<Track>>() {
                    }.getType();

                    tracks = gson.fromJson(jsonString, type);//把JSON数据转化为类


                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        });
        a.start();
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void GetTracePoints() { //得到车辆信息

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                access_token = myPreference.getString("access_token", "zheshikongde");

                RequestBody requestBody = new FormBody.Builder()
                        .add("CarId", carID)
                        .add("Begin", tracks.get(tarckId).getBegin())
                        .add("End", tracks.get(tarckId).getEnd())
                        .build();

                String url = "http://ldz2017.vicp.io:18539/api/GetTracePoints";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + access_token)
                        .post(requestBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String jsonString = response.body().string();//得到Json数据


                    Gson gson = new Gson();  //得到数据封装成List
                    Type type = new TypeToken<List<Points>>() {
                    }.getType();

                    points = gson.fromJson(jsonString, type);//把JSON数据转化为类


                    System.out.println("fjiosfjdfo ");
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        });
        a.start();
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapview.onResume();
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    // TODO Auto-generated method stub\


    /**
     * Created by adminZPH on 2017/4/14.
     * 设置线条中的纹理的方法
     *
     * @return PolylineOptions
     */
    public PolylineOptions GetPolylineOptions() {
        aMap.clear();
        PolylineOptions polylienOptions = null;
        polylienOptions = new PolylineOptions();
        //添加纹理图片
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromResource(R.drawable.custtexture);
        textureList.add(mRedTexture);
        // 添加纹理图片对应的顺序

        polylienOptions.setCustomTextureList(textureList);
        polylienOptions.setUseTexture(true);
        polylienOptions.width(30.0f);
        polylienOptions.setPoints(lists);//设置坐标点
        /*polylienOptions.color(Color.argb ( 127,  220,  20,  60));//设置线段颜色*/

        return polylienOptions;
    }
}
