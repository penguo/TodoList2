package com.pepg.todolist;

import android.app.Activity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.fragment.DetailAlarmFragment;
import com.pepg.todolist.fragment.DetailSemiFragment;

import static com.pepg.todolist.Manager.RESULT_CLOSE;

public class DetailItemActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager vp;
    View includeHead, includePB;
    Activity activity;
    ImageButton btnReturn, btnEdit;
    TextView tvTitle, tvCategory, tvDate, tvMemo, tvDday, tvAch, tvAchHead, tvAlarmHead, tvHeadAch;
    LinearLayout layoutHead, layoutDate, layoutAch, layoutAlarm, layoutMemo, layoutAchBg, layoutAlarmBg, layoutMemoBg;
    ImageView ivZoomAch, ivZoomAlarm;
    Toolbar toolbar;
    RoundCornerProgressBar pb, pbHead;

    final dbManager dbM = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        activity = this;
        includeHead = findViewById(R.id.detail_head);
        tvTitle = (TextView) includeHead.findViewById(R.id.detail_tv_title);
        tvCategory = (TextView) includeHead.findViewById(R.id.detail_tv_category);
        btnEdit = (ImageButton) includeHead.findViewById(R.id.detail_btn_edit);
        btnReturn = (ImageButton) includeHead.findViewById(R.id.detail_btn_return);
        toolbar = (Toolbar) includeHead.findViewById(R.id.detail_toolbar);
        layoutHead = (LinearLayout) includeHead.findViewById(R.id.detail_layout_head);
        tvDday = (TextView) includeHead.findViewById(R.id.detail_tv_dday);

        tvDate = (TextView) includeHead.findViewById(R.id.detail_tv_date);
        tvMemo = (TextView) includeHead.findViewById(R.id.detail_tv_memo);
        tvAch = (TextView) includeHead.findViewById(R.id.detail_tv_ach);
        tvAchHead = (TextView) includeHead.findViewById(R.id.detail_tv_ach_head);
        tvAlarmHead = (TextView) includeHead.findViewById(R.id.detail_tv_alarm_head);
        layoutDate = (LinearLayout) includeHead.findViewById(R.id.detail_layout_date);
        layoutAch = (LinearLayout) includeHead.findViewById(R.id.detail_layout_ach);
        layoutAchBg = (LinearLayout) includeHead.findViewById(R.id.detail_layout_ach_bg);
        layoutAlarm = (LinearLayout) includeHead.findViewById(R.id.detail_layout_alarm);
        layoutAlarmBg = (LinearLayout) includeHead.findViewById(R.id.detail_layout_alarm_bg);
        layoutMemo = (LinearLayout) includeHead.findViewById(R.id.detail_layout_memo);
        layoutMemoBg = (LinearLayout) includeHead.findViewById(R.id.detail_layout_memo_bg);

        ivZoomAch = (ImageView) findViewById(R.id.detail_iv_zoom_ach);
        ivZoomAlarm = (ImageView) findViewById(R.id.detail_iv_zoom_alarm);

        includePB = includeHead.findViewById(R.id.detail_pb);
        pb = (RoundCornerProgressBar) includePB.findViewById(R.id.progressBar);

        tvHeadAch = (TextView) findViewById(R.id.detail_head_tv_ach);
        pbHead = (RoundCornerProgressBar) findViewById(R.id.detail_head_pb);


        vp = (ViewPager) findViewById(R.id.detail_item_vp);

        vp.setAdapter(new DetailItemActivity.pagerAdapter(getSupportFragmentManager()));

        int itemNum = getIntent().getIntExtra("item", -1);
        setData(itemNum);

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
        btnEdit.setOnClickListener(this);
        layoutAch.setOnClickListener(this);
        layoutAlarm.setOnClickListener(this);
    }

    private void setData(int item) {
        tvTitle.setText(dbManager.DATA_TITLE);
        tvCategory.setText(dbManager.DATA_CATEGORY);
        tvDate.setText(dbManager.DATA_DATE);
        pb.setSecondaryProgress(Manager.getSuggestAch(dbManager.DATA_CREATEDATE, dbManager.DATA_DATE));
        switch (item) {
            case (1):
                updateAch();
                layoutDate.setVisibility(View.GONE);
                layoutAlarm.setVisibility(View.GONE);
                layoutMemo.setVisibility(View.GONE);
                layoutAchBg.setBackgroundResource(R.color.colorPrimaryDark2);
                ivZoomAch.setImageResource(R.drawable.ic_zoomout_black);
                tvAchHead.setTextColor(getResources().getColor(R.color.white07));
                layoutAch.setElevation(2);
                break;
            case (2):
                updateAch();
                tvMemo.setText(dbManager.DATA_MEMO);
                layoutDate.setVisibility(View.GONE);
                layoutAch.setVisibility(View.GONE);
                layoutMemo.setVisibility(View.GONE);
                layoutAlarmBg.setBackgroundResource(R.color.colorPrimaryDark2);
                ivZoomAlarm.setImageResource(R.drawable.ic_zoomout_black);
                tvAlarmHead.setTextColor(getResources().getColor(R.color.white07));
                layoutAlarm.setElevation(2);
                break;
        }
        setDday();
        vp.setCurrentItem(item - 1);
    }

    private void setDday() {
        tvDday.setText(Manager.getDday(tvDate.getText().toString()));
        try {
            if (dbManager.DATA_DDAY >= 10 || dbManager.DATA_DDAY <= -10) {
                tvDday.setTextSize(16);
            } else {
                tvDday.setTextSize(21);
            }
        } catch (Exception e) {
            tvDday.setTextSize(16);
        }
    }

    public void updateAch() {
        pb.setProgress(dbManager.DATA_ACH);
        tvAch.setText((dbManager.DATA_ACH_FINISH / 100) + " / " + (dbManager.DATA_ACH_MAX / 100));
        pbHead.setProgress(dbManager.DATA_ACH);
        tvHeadAch.setText(dbManager.DATA_ACH + "%");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.detail_btn_return):
                setResult(RESULT_CLOSE);
                finish();
                break;
            case (R.id.detail_btn_edit):
                Toast.makeText(activity, "wait", Toast.LENGTH_SHORT).show();
                break;
            case(R.id.detail_layout_ach):
            case(R.id.detail_layout_alarm):
                onBackPressed();
                break;
        }
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DetailSemiFragment();
                case 1:
                    return new DetailAlarmFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        supportFinishAfterTransition();
    }
}
