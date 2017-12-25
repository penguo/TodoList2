package com.pepg.todolist;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.auth.Session;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Login.KakaoSignupActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int DBVERSION = 5;
    public static String APPVERSION = "alpha 1.1.0 debug20171225.1244";

    Button btnList;
    TextView tvTitle, tvInfo;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, DBVERSION);
    PermissionListener permissionlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnList = (Button) findViewById(R.id.mainA_btn_list);
        tvTitle = (TextView) findViewById(R.id.mainA_tv_title);
        tvInfo = (TextView) findViewById(R.id.mainA_tv_info);

        // TODO: BUILD INFO SET
        tvInfo.setText("Version " + APPVERSION);

        btnList.setOnClickListener(this);

//        Log.e("CHECK",Session.getCurrentSession().checkAndImplicitOpen()+"");
        initSetting();
        startApplication();
    }

    public void initSetting() {
        Manager.editMode = false;
        Manager.isAnimationActive = true;
        Manager.setSetting(this);
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        getPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.mainA_btn_list):
                startApplication();
                break;
            case (R.id.mainA_tv_title):
                break;
        }
    }

    private void startApplication() {
        Intent intent;
        dbManager.DATA_SORTbyCATEGORY = "DEFAULT";
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            intent = new Intent(this, KakaoSignupActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        }
        finish();
    }

    public void getPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE)
                .check();
    }

    public void daily() {
        Toast.makeText(this, "준비 중 입니다.", Toast.LENGTH_SHORT).show();
    }

}
