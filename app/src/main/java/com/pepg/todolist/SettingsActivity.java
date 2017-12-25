package com.pepg.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pepg.todolist.Fragment.SettingsFragment;

import static com.pepg.todolist.MainActivity.APPVERSION;


/**
 * Created by pengu on 2017-11-24.
 */

public class SettingsActivity extends AppCompatActivity {

    ImageButton btnReturn;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnReturn = (ImageButton) findViewById(R.id.setting_btn_return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvVersion = (TextView) findViewById(R.id.setting_tv_version);
        tvVersion.setText(APPVERSION);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_framelayout, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        Manager.setSetting(this);
        super.onBackPressed();
    }
}