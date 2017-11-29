package com.pepg.todolist;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.Step1Fragment;
import com.pepg.todolist.Fragment.Step2Fragment;
import com.pepg.todolist.Fragment.Step3Fragment;
import com.pepg.todolist.Fragment.Step4Fragment;
import com.gigamole.navigationtabstrip.NavigationTabStrip;


public class AddguideActivity extends AppCompatActivity implements View.OnClickListener {

    DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);

    ViewPager vp;
    NavigationTabStrip nts;
    Step1Fragment fs1;
    Activity activity;
    ImageButton btnReturn, btnSave;

    public static int ENABLED_ITEM_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addguide);

        activity = this;

        vp = (ViewPager) findViewById(R.id.aguide_vp);
        nts = (NavigationTabStrip) findViewById(R.id.aguide_nts);
        btnReturn = (ImageButton) findViewById(R.id.aguide_btn_return);
        btnSave = (ImageButton) findViewById(R.id.aguide_btn_save);

        fs1 = new Step1Fragment();

        ENABLED_ITEM_SIZE = 4;
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        nts.setViewPager(vp);

        setData(0);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnReturn.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.aguide_btn_return):
                finish();
                break;
            case (R.id.aguide_btn_save):
                save();
                break;
        }
    }

    public void save() {
        dbManager.insertSimply();
        setResult(RESULT_OK);
        finish();
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fs1;
                case 1:
                    return new Step2Fragment();
                case 2:
                    return new Step3Fragment();
                case 3:
                    return new Step4Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return ENABLED_ITEM_SIZE;
        }
    }

    public void setData(int position) {
        vp.setCurrentItem(position);
    }

    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage("페이지를 나가면 작성한 내용이 모두 사라집니다.");
        dialog.setCancelable(true);
        dialog.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbManager.deleteDummyData_Semi();
                finish();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
