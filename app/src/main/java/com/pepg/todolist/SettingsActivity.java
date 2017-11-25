package com.pepg.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.pepg.todolist.Fragment.SettingsFragment;


/**
 * Created by pengu on 2017-11-24.
 */

public class SettingsActivity extends AppCompatActivity {

    ImageButton btnReturn;


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

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_framelayout, new SettingsFragment())
                .commit();
    }
}