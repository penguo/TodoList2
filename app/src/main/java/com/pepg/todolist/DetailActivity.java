package com.pepg.todolist;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.DetailSemiFragment;
import com.pepg.todolist.Fragment.Step1Fragment;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager vp;
    View includeHead;
    Activity activity;
    ImageButton btnReturn, btnEdit;
    TextView tvTitle, tvCategory, tvDate, tvDday, tvAch, tvHeadAch, tvToolbarTitle;
    LinearLayout layoutHead;
    Toolbar toolbar;
    RoundCornerProgressBar pb, pbHead;
    DetailSemiFragment detailSemiFragment;
    FrameLayout frameLayoutHead;
    int itemNum, id;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    boolean isItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        activity = this;
        includeHead = findViewById(R.id.detail_head);

        tvTitle = (TextView) includeHead.findViewById(R.id.detail_tv_title);
        tvCategory = (TextView) includeHead.findViewById(R.id.detail_tv_category);
        btnEdit = (ImageButton) includeHead.findViewById(R.id.detail_btn_edit);
        btnReturn = (ImageButton) includeHead.findViewById(R.id.detail_btn_return);
        toolbar = (Toolbar) includeHead.findViewById(R.id.detail_toolbar);
        layoutHead = (LinearLayout) includeHead.findViewById(R.id.detail_layout_head);
        tvDday = (TextView) includeHead.findViewById(R.id.detail_tv_dday);
        tvToolbarTitle = (TextView) includeHead.findViewById(R.id.detail_tv_toolbar_title);

        tvHeadAch = (TextView) includeHead.findViewById(R.id.detail_tv_head_ach);
        pbHead = (RoundCornerProgressBar) includeHead.findViewById(R.id.detail_pb_head);

        frameLayoutHead = (FrameLayout) findViewById(R.id.detail_framelayout_head);

        DetailSemiFragment detailSemiFragment = new DetailSemiFragment();
        Manager.modifyMode = false;

        vp = (ViewPager) findViewById(R.id.detail_item_vp);
        vp.setAdapter(new DetailActivity.pagerAdapter(getSupportFragmentManager()));

//        setData(-1);

        btnEdit.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
    }

//    private void setData(int item) {
//        if (item == -1) {
//            id = getIntent().getIntExtra("_id", -1);
//            dbManager.getValue("_id", id);
//            isItemSelected = false;
//        } else {
//            btnReturn.setImageResource(R.drawable.ic_zoomout);
//            layoutBody.setElevation(layoutHead.getElevation());
//            frameLayoutHead.setElevation(layoutBody.getElevation());
//            isItemSelected = true;
//        }
//        if (!DBManager.DATA_TITLE.equals(activity.getString(R.string.empty_data))) {
//            tvTitle.setText(DBManager.DATA_TITLE);
//            tvCategory.setText(DBManager.DATA_CATEGORY);
//        } else {
//            tvTitle.setText(DBManager.DATA_CATEGORY);
//            tvCategory.setVisibility(View.GONE);
//        }
//        tvDate.setText(DBManager.DATA_DATE);
//        tvMemo.setText(dbManager.DATA_MEMO);
//        switch (item) {
//            case (-1): //초기화
//                layoutDate.setVisibility(View.VISIBLE);
//
//                layoutAch.setVisibility(View.VISIBLE);
//                layoutAchBg.setBackgroundResource(R.drawable.xml_item);
//                ivZoomAch.setImageResource(R.drawable.ic_zoomin_black);
//                borderAch.setVisibility(View.VISIBLE);
//                tvAchHead.setTextColor(tvAch.getTextColors());
//
//                layoutAlarm.setVisibility(View.VISIBLE);
//                layoutAlarmBg.setBackgroundResource(R.drawable.xml_item);
//                ivZoomAlarm.setImageResource(R.drawable.ic_zoomin_black);
//                borderAlarm.setVisibility(View.VISIBLE);
//                tvAlarmHead.setTextColor(tvAch.getTextColors());
//
//                layoutMemo.setVisibility(View.VISIBLE);
//
//                frameLayoutHead.setBackgroundResource(R.color.colorPrimaryDark);
//                break;
//            case (0):
//                break;
//            case (1):
//                layoutDate.setVisibility(View.GONE);
//                layoutAlarm.setVisibility(View.GONE);
//                layoutMemo.setVisibility(View.GONE);
//                layoutAchBg.setBackgroundResource(R.color.colorPrimaryDark);
//                ivZoomAch.setImageResource(R.drawable.ic_zoomout_black);
//                borderAch.setVisibility(View.GONE);
//                tvAchHead.setTextColor(getResources().getColor(R.color.white07));
//                break;
//            case (2):
//                layoutDate.setVisibility(View.GONE);
//                layoutAch.setVisibility(View.GONE);
//                layoutMemo.setVisibility(View.GONE);
//                layoutAlarmBg.setBackgroundResource(R.color.colorPrimaryDark);
//                ivZoomAlarm.setImageResource(R.drawable.ic_zoomout_black);
//                borderAlarm.setVisibility(View.GONE);
//                tvAlarmHead.setTextColor(getResources().getColor(R.color.white07));
//                break;
//        }
//        vp.setCurrentItem(0);
//        updateAch();
//        setDday();
//    }

    private void setDday() {
        tvDday.setText(Manager.getDday(tvDate.getText().toString()));
        try {
            if (DBManager.DATA_DDAY >= 10 || DBManager.DATA_DDAY <= -10) {
                tvDday.setTextSize(16);
            } else {
                tvDday.setTextSize(21);
            }
        } catch (Exception e) {
            tvDday.setTextSize(16);
        }
    }

    public void updateAch() {
        dbManager.getValue("_id", DBManager.DATA_id);
        pb.setProgress(DBManager.DATA_ACH);
        pb.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
        tvAch.setText((DBManager.DATA_ACH_FINISH / 100) + " / " + (DBManager.DATA_ACH_MAX / 100));
        pbHead.setProgress(DBManager.DATA_ACH);
        tvHeadAch.setText(DBManager.DATA_ACH + "%");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.detail_btn_return):
                onBackPressed();
                break;
            case (R.id.detail_btn_edit):
                setBtnEdit();
                break;
            case (R.id.detail_layout_ach):
                if (!isItemSelected) {
                    clickAnimation(1);
                } else {
                    clickAnimation(-1);
                }
                break;
            case (R.id.detail_layout_alarm):
                if (!isItemSelected) {
                    clickAnimation(2);
                } else {
                    clickAnimation(-1);
                }
                break;
            case (R.id.detail_layout_memo):
//                clickAnimation();
                break;
        }
    }

    public void setBtnEdit() {
        if (!Manager.modifyMode) {
            Manager.modifyMode = true;
            btnEdit.setImageResource(R.drawable.ic_save);
            tvToolbarTitle.setText("Update");
        } else {
            Manager.modifyMode = false;
            btnEdit.setImageResource(R.drawable.ic_edit);
            tvToolbarTitle.setText("Detail");
        }
        detailSemiFragment.onRefresh();
        detailSemiFragment.setFab();
    }

    public void refreshSemiRcv() {
        detailSemiFragment.onRefresh();
        updateAch();
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (itemNum) {
//                case 1:
//                    return detailSemiFragment;
//                case 2:
//                    return new DetailAlarmFragment();
//                default:
//                    return new DetailBodyFragment();
            }
            return new Step1Fragment();
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        supportFinishAfterTransition();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Manager.RC_DETAIL_TO_UPDATE) {
            if (resultCode == RESULT_OK) {
                dbManager.setSemiPosition(id);
//                setData(-1);
            }
        }
        if (requestCode == Manager.RC_DETAIL_TO_DETAILITEM) {
            if (resultCode == RESULT_OK) {
                dbManager.setSemiPosition(id);
//                setData(-1);
            }
        }
    }


    private void clickAnimation(int itemNum) {
//        List<Pair<View, String>> pairs = new ArrayList<>();
//        pairs.add(Pair.create((View) toolbar, "toolbar"));
//        pairs.add(Pair.create((View) layoutHead, "layout_head"));
//        switch (itemNum) {
//            case (1):
//                pairs.add(Pair.create((View) layoutAch, "layout_ach"));
//                break;
//            case (2):
//                pairs.add(Pair.create((View) layoutAlarm, "layout_alarm"));
//                break;
//        }
//        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra("item", itemNum);
//        Bundle options = ActivityOptions.makeSceneTransitionAnimation(this,
//                pairs.toArray(new Pair[pairs.size()])).toBundle();
//        activity.startActivityForResult(intent, Manager.RC_DETAIL_TO_DETAILITEM, options);

//        setData(itemNum);

    }
}
