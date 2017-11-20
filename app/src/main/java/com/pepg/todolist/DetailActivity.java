package com.pepg.todolist;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.dbManager;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    TextView tvTitle, tvCategory, tvDate, tvMemo, tvDday, tvAch, tvHeadAch;
    ImageButton btnEdit, btnReturn;
    int id;
    final dbManager dbManager = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    View includePB;
    RoundCornerProgressBar pb, pbHead;
    LinearLayout layoutHead, layoutBody, layoutDate, layoutAch, layoutAlarm, layoutMemo, layoutAchBg, layoutAlarmBg, layoutMemoBg;
    ImageView ivZoomAch, ivZoomAlarm, ivZoomMemo;
    Toolbar toolbar;
    Activity activity;

    public DetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        activity = this;

        tvTitle = (TextView) findViewById(R.id.detail_tv_title);
        tvCategory = (TextView) findViewById(R.id.detail_tv_category);
        btnEdit = (ImageButton) findViewById(R.id.detail_btn_edit);
        btnReturn = (ImageButton) findViewById(R.id.detail_btn_return);
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        layoutHead = (LinearLayout) findViewById(R.id.detail_layout_head);
        tvDday = (TextView) findViewById(R.id.detail_tv_dday);

        tvDate = (TextView) findViewById(R.id.detail_tv_date);
        tvMemo = (TextView) findViewById(R.id.detail_tv_memo);
        tvAch = (TextView) findViewById(R.id.detail_tv_ach);
        layoutDate = (LinearLayout) findViewById(R.id.detail_layout_date);
        layoutAch = (LinearLayout) findViewById(R.id.detail_layout_ach);
        layoutAchBg = (LinearLayout) findViewById(R.id.detail_layout_ach_bg);
        layoutAlarm = (LinearLayout) findViewById(R.id.detail_layout_alarm);
        layoutAlarmBg = (LinearLayout) findViewById(R.id.detail_layout_alarm_bg);
        layoutMemo = (LinearLayout) findViewById(R.id.detail_layout_memo);
        layoutMemoBg = (LinearLayout) findViewById(R.id.detail_layout_memo_bg);
        layoutBody = (LinearLayout) findViewById(R.id.detail_layout_body);
        includePB = findViewById(R.id.detail_pb);
        pb = (RoundCornerProgressBar) includePB.findViewById(R.id.progressBar);

        tvHeadAch = (TextView) findViewById(R.id.detail_head_tv_ach);
        pbHead = (RoundCornerProgressBar) findViewById(R.id.detail_head_pb);

        ivZoomAch = (ImageView) findViewById(R.id.detail_iv_zoom_ach);
        ivZoomAlarm = (ImageView) findViewById(R.id.detail_iv_zoom_alarm);
        ivZoomMemo = (ImageView) findViewById(R.id.detail_iv_zoom_memo);

        setData();

        btnEdit.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        layoutAch.setOnClickListener(this);
        layoutAlarm.setOnClickListener(this);
        layoutMemo.setOnClickListener(this);
        layoutAch.setOnLongClickListener(this);
        layoutAlarm.setOnLongClickListener(this);
        layoutMemo.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case (R.id.detail_btn_edit):
                intent = new Intent(DetailActivity.this, UpdateActivity.class);
                intent.putExtra("_id", id);
                startActivityForResult(intent, Manager.RC_DETAIL_TO_UPDATE);
                break;
            case (R.id.detail_btn_return):
                onBackPressed();
                break;
            case (R.id.detail_layout_ach):
                clickAnimation(1);
                break;
            case (R.id.detail_layout_alarm):
                clickAnimation(2);
                break;
            case (R.id.detail_layout_memo):
//                clickAnimation();
                break;
        }
    }

    private void setData() {
        id = getIntent().getIntExtra("_id", -1);
        dbManager.getValue("_id", id);
        if (!dbManager.DATA_TITLE.equals(activity.getString(R.string.empty_data))) {
            tvTitle.setText(dbManager.DATA_TITLE);
            tvCategory.setText(dbManager.DATA_CATEGORY);
        } else {
            tvTitle.setText(dbManager.DATA_CATEGORY);
            tvCategory.setVisibility(View.GONE);
        }
        tvDate.setText(dbManager.DATA_DATE);
        tvMemo.setText(dbManager.DATA_MEMO);
        setDday();
        updateAch();
        pb.setSecondaryProgress(Manager.getSuggestAch(dbManager.DATA_CREATEDATE, dbManager.DATA_DATE));
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
                setData();
            }
        }
        if (requestCode == Manager.RC_DETAIL_TO_DETAILITEM) {
            if (resultCode == RESULT_OK) {
                dbManager.setSemiPosition(id);
                setData();
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private void clickAnimation(int itemNum) {
        List<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(Pair.create((View) toolbar, "toolbar"));
        pairs.add(Pair.create((View) layoutHead, "layout_head"));
        switch (itemNum) {
            case (1):
                pairs.add(Pair.create((View) layoutAch, "layout_ach"));
                break;
            case (2):
                pairs.add(Pair.create((View) layoutAlarm, "layout_alarm"));
                break;
        }
        Intent intent = new Intent(this, DetailItemActivity.class);
        intent.putExtra("item", itemNum);
        Bundle options = ActivityOptions.makeSceneTransitionAnimation(this,
                pairs.toArray(new Pair[pairs.size()])).toBundle();
        activity.startActivityForResult(intent, Manager.RC_DETAIL_TO_DETAILITEM, options);
    }
}