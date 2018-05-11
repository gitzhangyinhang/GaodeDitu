package gaodetest.qianchi.com.gaodeditu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GuijiGenzongActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private Context context;
    final Calendar calendar = Calendar.getInstance();  // 获取当前时间
    public List<Car> carEntityList = new ArrayList<Car>();
    public OkHttpClient okHttpClient = new OkHttpClient();
    public String access_token = null;
    private int year, month, day, hour, minute;
    private String year1, month1, day1, hour1, minute1;
    private String year2, month2, day2, hour2, minute2;
    String plateID = null;
    int carID = 0;
    public String startDate;
    public String endDate;
    List<String> plateIDs = null;
    TextView textView1;
    TextView textView2;
    private Boolean IsHaveData=true;//该车牌在选择时间内是否有数据，没有数据不能提交
    private Boolean IsSubmit=true;//是否能提交
    //在TextView1上显示的字符
    private StringBuffer date1, time1;
    //在TextView2上显示的字符
    private StringBuffer date2, time2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();//去掉标题栏
        actionBar.hide();
        setContentView(R.layout.guiji_genzong);

        context = this;
        date1 = new StringBuffer();
        time1 = new StringBuffer();
        date2 = new StringBuffer();
        time2 = new StringBuffer();
        initDateTime();


        // 初始化控件
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        GetAllCars();//获取所有车辆

        plateIDs = new ArrayList<String>();//获取所有车牌
        for (Car car : carEntityList) {
            plateIDs.add(car.getPlateID());
        }



        textView1 = (TextView) findViewById(R.id.textView1);//开始时间textview 设置开始时间
        textView2 = (TextView) findViewById(R.id.textView2);//结束时间textview 设置结束时间
        textView1.setText(date1);
        textView2.setText(date2);

        Button button1 = (Button) findViewById(R.id.button1);//开始时间设置button
        Button button2 = (Button) findViewById(R.id.button2);//结束时间设置button
        Button button3 = (Button) findViewById(R.id.button3);//查询button
        button1.setOnClickListener(this);//开始时间按钮点击事件
        button2.setOnClickListener(this);//结束时间按钮点击事件
        button3.setOnClickListener(this);//查询按钮点击事件


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plateIDs);// 建立Adapter并且绑定数据源
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//设置下拉样式
    /*    Toast.makeText(this, listStr, Toast.LENGTH_SHORT).show();
*/
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                plateID = spinner.getSelectedItem().toString();
                for (Car car : carEntityList) {
                  if(car.getPlateID().equals(plateID)){
                   carID=car.getId();
                  }
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        DecimalFormat mFormat= new DecimalFormat("00");
        mFormat.setRoundingMode(RoundingMode.DOWN);
        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = Integer.parseInt(mFormat.format(Double.valueOf(calendar.get(Calendar.MONTH)))) ;
        day =Integer.parseInt(mFormat.format(Double.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))) ;
        hour =Integer.parseInt(mFormat.format(Double.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))) ;
        minute =Integer.parseInt(mFormat.format(Double.valueOf(calendar.get(Calendar.MINUTE)))) ;

        year1 = String.valueOf(calendar.get(Calendar.YEAR));
        month1 = mFormat.format(Double.valueOf(calendar.get(Calendar.MONTH)+1));
        day1 =  mFormat.format(Double.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        hour1 = "00";
        minute1 = "00";
        date1.append(String.valueOf(year1)).append("-").append(String.valueOf(month1)).append("-").append(day1);
        time1.append(String.valueOf(hour1)).append(":").append(String.valueOf(minute1));
        date1.append(" "+time1);


        year2 = String.valueOf(calendar.get(Calendar.YEAR));
        month2 =mFormat.format(Double.valueOf(calendar.get(Calendar.MONTH)+1));
        day2 = mFormat.format(Double.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        hour2= mFormat.format(Double.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        minute2 =mFormat.format(Double.valueOf(calendar.get(Calendar.MINUTE)));
        date2.append(String.valueOf(year2)).append("-").append(String.valueOf(month2)).append("-").append(day2);
        time2.append(String.valueOf(hour2)).append(":").append(String.valueOf(minute2));
        date2.append(" "+time2);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1: {//开始日期设置
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        dialog.dismiss();

                        //时间设置
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (date1.length() > 0) { //清除上次记录的日期
                                    date1.delete(0, date1.length());
                                }
                                if (time1.length() > 0) { //清除上次记录的日期
                                    time1.delete(0, time1.length());
                                }
                                date1.append(String.valueOf(year1)).append("-").append(String.valueOf(month1)).append("-").append(day1);
                                time1.append(String.valueOf(hour1)).append(":").append(String.valueOf(minute1));
                                textView1.setText(date1.append(" ").append(time1));
                                dialog.dismiss();
                            }
                        });
                        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                             //设置开始时间
                                textView1.setText(date1);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        View dialogView2 = View.inflate(context, R.layout.dialog_time, null);
                        TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.timePicker);
                        timePicker.setCurrentHour(00);
                        timePicker.setCurrentMinute(00);
                        timePicker.setIs24HourView(true); //设置24小时制
                        timePicker.setOnTimeChangedListener(GuijiGenzongActivity.this);
                        dialog2.setTitle("设置时间");
                        dialog2.setView(dialogView2);
                        dialog2.show();


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                 AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date, null);
                 DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

                dialog.setTitle("设置日期");
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                datePicker.init(year, month, day, this);

                break;

            }
            case R.id.button2: {//结束日期设置
                //结束日期设置
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        //时间设置
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (date2.length() > 0) { //清除上次记录的日期
                                    date2.delete(0, date2.length());
                                }
                                if (time2.length() > 0) { //清除上次记录的日期
                                    time2.delete(0, time2.length());
                                }
                                date2.append(String.valueOf(year2)).append("-").append(String.valueOf(month2)).append("-").append(day2);
                                time2.append(String.valueOf(hour2)).append(":").append(String.valueOf(minute2));
                                textView2.setText(date2.append(" ").append(time2));
                                dialog.dismiss();
                            }
                        });
                        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                     //设置结束时间
                                textView2.setText(date2);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        View dialogView2 = View.inflate(context, R.layout.dialog_time2, null);
                        TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.timePicker2);
                        timePicker.setCurrentHour(hour);
                        timePicker.setCurrentMinute(minute);
                        timePicker.setIs24HourView(true); //设置24小时制
                        timePicker.setOnTimeChangedListener(GuijiGenzongActivity.this);
                        dialog2.setTitle("设置时间");
                        dialog2.setView(dialogView2);
                        dialog2.show();


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date2, null);
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker2);

                dialog.setTitle("设置日期");
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                datePicker.init(year, month, day, this);
                break;
            }
            case R.id.button3: {
                alertErrorSelectTime();
                if(IsSubmit){//是否可以提交

                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
                    try{
                        Date d1 =df1.parse(date1.toString());
                        Date d2 =df1.parse(date2.toString());

                        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        df2.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

                        startDate  = df2.format(d1);
                        endDate=  df2.format(d2);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    GetAllTrack();

                    if(IsHaveData){//判断是否有数据

                    /* Toast.makeText(this, date1+ "可以提交" +date2, Toast.LENGTH_SHORT).show();*/
                        Intent intent = new Intent();
                        intent.setClass(GuijiGenzongActivity.this, GuijiShowActivity.class);
                    /* 通过Bundle对象存储需要传递的数据 */
                        Bundle bundle = new Bundle();
                    /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/
                        bundle.putString("carId",String.valueOf(carID) );
                        bundle.putString("plateID",String.valueOf(plateID) );
                        bundle.putString("startDate", startDate);
                        bundle.putString("endDate",endDate);
                   /*把bundle对象assign给Intent*/
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }else{
                          Toast.makeText(this,  "该车牌在这个时间段没有出行数据", Toast.LENGTH_SHORT).show();
                          IsHaveData=true;
                    }

                }else{
                   /* Toast.makeText(this,  date1+"不能提交" +date2, Toast.LENGTH_SHORT).show();*/
                    IsSubmit=true;
                }
                    break;
                }

        }
    }

    public void GetAllTrack() { //得到所有路径信息

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                access_token = myPreference.getString("access_token", "zheshikongde");

                RequestBody requestBody = new FormBody.Builder()
                        .add("CarId", String.valueOf(carID))
                        .add("Begin", startDate)
                        .add("End", endDate)
                        .build();

                String url = "http://ldz2017.vicp.io:18539/api/IsTraceExist";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + access_token)
                        .post(requestBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String jsonString = response.body().string();//得到Json数据
               /*     shoew(jsonString);*/
                    if(jsonString.equals("true")){//true代表有数据
                        IsHaveData=true;//true代表有数据，能跳转
                    }else{
                        IsHaveData=false;//false表示没有数据，不能跳转
                    }
                   /* Gson gson = new Gson();  //得到数据封装成List
                    Type type = new TypeToken<List<Track>>() {
                    }.getType();

                    tracks = gson.fromJson(jsonString, type);//把JSON数据转化为类*/



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


    public void GetAllCars() { //得到车辆信息

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                access_token = myPreference.getString("access_token", "zheshikongde");

                String url = "http://ldz2017.vicp.io:18539/api/GetAllCars";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + access_token)
                        .get()
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String jsonString = response.body().string();//得到Json数据
                    Gson gson = new Gson();  //得到数据封装成List
                    Type type = new TypeToken<ArrayList<Car>>() {
                    }.getType();
                    carEntityList = gson.fromJson(jsonString, type);//把JSON数据转化为类


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


    /**
     * 日期改变的监听事件
     *
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        switch (view.getId()) {
            case R.id.datePicker: {
                this.year1 = String.valueOf(year);

                if ((monthOfYear + 1) < 10) {
                    month1 = "0" + (monthOfYear + 1);

                } else {
                    this.month1 = String.valueOf(monthOfYear + 1);
                }
                if (dayOfMonth < 10) {
                    day1 = "0" + dayOfMonth;
                } else {
                    this.day1 = String.valueOf(dayOfMonth);
                }

                break;
            }
            case R.id.datePicker2: {
                this.year2 = String.valueOf(year);

                if ((monthOfYear + 1) < 10) {
                    month2 = "0" + (monthOfYear + 1);

                } else {
                    this.month2 = String.valueOf(monthOfYear + 1);
                }
                if (dayOfMonth < 10) {
                    day2 = "0" + dayOfMonth;
                } else {
                    this.day2 = String.valueOf(dayOfMonth);
                }

                break;
            }

        }


    }

    /**
     * 时间改变的监听事件
     *
     * @param view
     * @param hourOfDay
     * @param minute
     */

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        switch (view.getId()) {
            case R.id.timePicker: {
                if (hourOfDay < 10) {
                    this.hour1 = "0" + hourOfDay;
           /* Toast.makeText(this, hour1+"", Toast.LENGTH_SHORT).show();*//* Toast.makeText(this, hour1+"", Toast.LENGTH_SHORT).show();*/

                } else {
                    this.hour1 = String.valueOf(hourOfDay);

                }
                if (minute < 10) {
                    this.minute1 = "0" + minute;
                } else {
                    this.minute1 = String.valueOf(minute);
                }
                break;
            }
            case R.id.timePicker2: {
                if (hourOfDay < 10) {
                    this.hour2 = "0" + hourOfDay;
           /* Toast.makeText(this, hour1+"", Toast.LENGTH_SHORT).show();*//* Toast.makeText(this, hour1+"", Toast.LENGTH_SHORT).show();*/

                } else {
                    this.hour2 = String.valueOf(hourOfDay);

                }
                if (minute < 10) {
                    this.minute2 = "0" + minute;
                } else {
                    this.minute2 = String.valueOf(minute);
                }
                break;
            }

        }
    }


    public void alertErrorSelectTime() {
        if (Integer.parseInt (year2) < Integer.parseInt(year1)) {
            Toast.makeText(this, "结束年份不能小于开始年份", Toast.LENGTH_SHORT).show();
            IsSubmit=false;
        } else if (Integer.parseInt(year2)==Integer.parseInt(year1)) {
            if (Integer.parseInt(month2) < Integer.parseInt(month1)) {
                Toast.makeText(this, "结束月份不能小于开始月份", Toast.LENGTH_SHORT).show();
                IsSubmit=false;
            } else if (Integer.parseInt(month2)==Integer.parseInt(month1)) {
                if (Integer.parseInt(day2) < Integer.parseInt(day1)) {
                    Toast.makeText(this, "结束日期不能小于开始日期", Toast.LENGTH_SHORT).show();
                    IsSubmit=false;
                } else if (Integer.parseInt(day2)==Integer.parseInt(day1)) {
                    if (Integer.parseInt(hour2) < Integer.parseInt(hour1)) {
                        Toast.makeText(this, "结束日期不能小于开始日期", Toast.LENGTH_SHORT).show();
                        IsSubmit=false;
                    } else if (Integer.parseInt(hour2)==Integer.parseInt(hour1)) {
                        if (Integer.parseInt(minute2) < Integer.parseInt(minute1)) {
                            Toast.makeText(this, "结束日期不能小于开始日期", Toast.LENGTH_SHORT).show();
                            IsSubmit=false;
                        }
                    }
                }
            }
        }
    }

}
