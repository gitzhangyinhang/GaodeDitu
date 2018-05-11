package gaodetest.qianchi.com.gaodeditu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.yield;

/**
 * Created by Administrator on 2018/3/28.
 */

public class LoginActity extends AppCompatActivity {

    public Map<String, String> retMap = new HashMap<String, String>();
    public OkHttpClient okHttpClient = new OkHttpClient();
    private EditText mUser; // 帐号编辑框
    private EditText mPassword; // 密码编辑框

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();//去掉标题栏

        actionBar.hide();

        SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);

        /*SharedPreferences.Editor editor=myPreference.edit();
        editor.putString("access_token","zheshikongde");
        editor.commit();*/
        String access_token = myPreference.getString("access_token", "zheshikongde");


        if (!access_token.equals("zheshikongde")) {

            // 给bnt1添加点击响应事件
            Intent intent = new Intent(LoginActity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //Intent.FLAG_ACTIVITY_CLEAR_TOP，这样开启B时将会清除该进程空间的所有Activity。
            //启动
            startActivity(intent);
            finish();
            //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();

        } else {
            setContentView(R.layout.activity_login);
         /*   String  message=myPreference.getString("message","zheshikongde");
            if(message.equals("401")){

                new AlertDialog.Builder(LoginActity.this)
                        .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                        .setTitle("登录失败")
                        .setMessage("帐号，密码已过期，\n请重新登录！")
                        .create().show();
            }*/
            Button login_button = null;
            login_button = (Button) findViewById(R.id.login_btn);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mUser = (EditText) findViewById(R.id.login_edtId);
                    mPassword = (EditText) findViewById(R.id.login_edtPwd);

                    if ("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString()))   //判断 帐号和密码
                    {
                        new AlertDialog.Builder(LoginActity.this)
                                .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                                .setTitle("登录错误")
                                .setMessage("帐号或者密码不能为空，\n请输入后再登录！")
                                .create().show();
                    } else {

                        GetLoginInfo();
                        SharedPreferences pref = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                        String access_token = pref.getString("access_token", "zheshikongde");
                        if (!access_token.equals("zheshikongde"))   //判断 帐号和密码
                        {
                            SharedPreferences myPreference = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPreference.edit();
                            editor.putString("username", mUser.getText().toString());
                            editor.putString("password", mPassword.getText().toString());
                            editor.commit();
                            // 给bnt1添加点击响应事件
                            Intent intent = new Intent(LoginActity.this, MainActivity.class);
                            //启动
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(LoginActity.this)
                                    .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                                    .setTitle("登录失败")
                                    .setMessage("帐号或者密码不正确，\n请检查后重新输入！")
                                    .create().show();
                        }

                    }


                }
            });
        }

    }

    public void GetLoginInfo() {//得到登录信息

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://ldz2017.vicp.io:18539/Token";

                RequestBody requestBody = new FormBody.Builder()
                        .add("grant_type", "password")
                        .add("username", mUser.getText().toString())
                        .add("password", mPassword.getText().toString())
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
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
