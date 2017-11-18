package com.pepg.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pepg.todolist.DataBase.dbManager;

import com.pepg.todolist.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int DBVERSION = 3;

    Button btnList, btnSetting, btnDaily;
    TextView tvTitle, tvInfo;
    final dbManager dbManager = new dbManager(this, "todolist2.db", null, DBVERSION);

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
        tvInfo.setText("build 0.171116.1923");

        btnList.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnDaily.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case(R.id.mainA_btn_list):
                dbManager.DATA_SORTTYPE = "DEFAULT";
                intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            case(R.id.mainA_btn_setting):
                dbManager.reset();
                break;
            case(R.id.mainA_btn_daily):
                daily();
                break;
            case(R.id.mainA_tv_title):
                break;
        }
    }

    public void daily(){
        Toast.makeText(this, "준비 중 입니다.", Toast.LENGTH_SHORT).show();
    }
}
