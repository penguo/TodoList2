package com.pepg.todolist;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Optional.BroadcastManager;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int DBVERSION = 3;

    Button btnList, btnSetting, btnDaily;
    TextView tvTitle, tvInfo;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, DBVERSION);
    PermissionListener permissionlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnList = (Button) findViewById(R.id.mainA_btn_list);
        btnSetting = (Button) findViewById(R.id.mainA_btn_setting);
        btnDaily = (Button) findViewById(R.id.mainA_btn_daily);
        tvTitle = (TextView) findViewById(R.id.mainA_tv_title);
        tvInfo = (TextView) findViewById(R.id.mainA_tv_info);

        // TODO: BUILD INFO SET
        tvInfo.setText("build 0.171124.0011");


        btnList.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnDaily.setOnClickListener(this);

        initSetting();
    }

    public void initSetting(){
        Manager.editMode = false;
        Manager.isAnimationActive = true;

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        getPermission();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case (R.id.mainA_btn_list):
                dbManager.DATA_SORTTYPE = "DEFAULT";
                intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                finish();
                break;
            case (R.id.mainA_btn_setting):
                dbManager.reset();
                break;
            case (R.id.mainA_btn_daily):
                notificationSomethings();
                break;
            case (R.id.mainA_tv_title):
                break;
        }
    }

    public void getPermission(){
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE)
                .check();
    }

    public void daily() {
        Toast.makeText(this, "준비 중 입니다.", Toast.LENGTH_SHORT).show();
    }


    public void notificationSomethings() {
        Resources res = getResources();

        Intent notificationIntent = new Intent(this, ListActivity.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }
}
