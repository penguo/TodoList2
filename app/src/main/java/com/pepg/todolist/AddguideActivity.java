package com.pepg.todolist;

import android.app.Activity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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

    DBManager dbM = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);

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

        dbM.resetPublicData();
        dbM.DATA_DATE = getString(R.string.unregistered);
        dbM.DATA_CATEGORY = getString(R.string.unregistered);

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
                if (state == 0) {
                    if (vp.getCurrentItem() != 0) {
                        dbM.DATA_TITLE = fs1.getDataTitle();
                        Manager.controlKeyboard(false, activity);
                    } else {
                        Manager.controlKeyboard(true, activity);
                    }
                }
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
        dbM.insertSimply();
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
        dbM.deleteDummyData_Semi();
        finish();
    }
}
