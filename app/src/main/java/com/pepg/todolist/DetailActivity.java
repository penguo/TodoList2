package com.pepg.todolist;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.dbManager;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnLongClickListener {

    TextView tvTitle, tvCategory, tvDate, tvMemo, tvDday, tvAch;
    ImageButton btnEdit, btnReturn;
    int id, selectedView;
    boolean isSelectedView;
    final dbManager dbManager = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    RecyclerView rcvSemi;
    SemiListRcvAdapter semiRcvAdapter;
    View includePB, includeSemi;
    RoundCornerProgressBar pb;
    UpdateSemi us;
    SwipeRefreshLayout swipe;
    LinearLayout layoutDate, layoutAch, layoutAlarm, layoutMemo, layoutSemi, layoutAchBg, layoutAlarmBg, layoutMemoBg;
    ImageView ivZoomAch, ivZoomAlarm, ivZoomMemo;
    Toolbar toolbar;

    public DetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = (TextView) findViewById(R.id.detail_tv_title);
        tvCategory = (TextView) findViewById(R.id.detail_tv_category);
        tvDate = (TextView) findViewById(R.id.detail_tv_date);
        tvMemo = (TextView) findViewById(R.id.detail_tv_memo);
        tvDday = (TextView) findViewById(R.id.detail_tv_dday);
        tvAch = (TextView) findViewById(R.id.detail_tv_ach);
        btnEdit = (ImageButton) findViewById(R.id.detail_btn_edit);
        btnReturn = (ImageButton) findViewById(R.id.detail_btn_return);
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        layoutDate = (LinearLayout) findViewById(R.id.detail_layout_date);
        layoutAch = (LinearLayout) findViewById(R.id.detail_layout_ach);
        layoutAchBg = (LinearLayout) findViewById(R.id.detail_layout_ach_bg);
        layoutAlarm = (LinearLayout) findViewById(R.id.detail_layout_alarm);
        layoutAlarmBg = (LinearLayout) findViewById(R.id.detail_layout_alarm_bg);
        layoutMemo = (LinearLayout) findViewById(R.id.detail_layout_memo);
        layoutMemoBg = (LinearLayout) findViewById(R.id.detail_layout_memo_bg);

        includePB = findViewById(R.id.detail_pb);
        pb = (RoundCornerProgressBar) includePB.findViewById(R.id.progressBar);

        includeSemi = findViewById(R.id.detail_semi);
        layoutSemi = (LinearLayout) includeSemi.findViewById(R.id.semi_layout);
        rcvSemi = (RecyclerView) includeSemi.findViewById(R.id.semi_rcv);
        swipe = (SwipeRefreshLayout) includeSemi.findViewById(R.id.semi_swipe);

        ivZoomAch = (ImageView) findViewById(R.id.detail_iv_zoom_ach);
        ivZoomAlarm = (ImageView) findViewById(R.id.detail_iv_zoom_alarm);
        ivZoomMemo = (ImageView) findViewById(R.id.detail_iv_zoom_memo);

        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(this);
        rcvSemi.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, rcvLayoutManager.getOrientation());
        rcvSemi.addItemDecoration(dividerItemDecoration);

        semiRcvAdapter = new SemiListRcvAdapter(dbManager, this, id);
        rcvSemi.setAdapter(semiRcvAdapter);

        us = new UpdateSemi(semiRcvAdapter, this, dbManager);

        swipe.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light
        );
        swipe.setOnRefreshListener(this);

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
                if (!isSelectedView) {
                    viewItems(1);
                } else if (selectedView == 1) {
                    viewItems(1);
                }
                break;
            case (R.id.detail_layout_alarm):
                if (!isSelectedView) {
                    viewItems(2);
                } else if (selectedView == 2) {
                    viewItems(2);
                }
                break;
            case (R.id.detail_layout_memo):
                if (!isSelectedView) {
                    viewItems(3);
                } else if (selectedView == 3) {
                    viewItems(3);
                }
                break;
        }
    }

    private void setData() {
        id = getIntent().getIntExtra("_id", -1);
        dbManager.getValue("_id", id);
        tvTitle.setText(dbManager.DATA_TITLE);
        tvCategory.setText(dbManager.DATA_CATEGORY);
        tvDate.setText(dbManager.DATA_DATE);
        tvMemo.setText(dbManager.DATA_MEMO);
        setDday();
        updateAch();
        pb.setSecondaryProgress(Manager.getSuggestAch(dbManager.DATA_CREATEDATE, dbManager.DATA_DATE));
        viewItems(0);
        onRefresh();
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
        tvAch.setText(dbManager.DATA_ACH + "%");
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
                onRefresh();
            }
        }
    }

    @Override
    public void onRefresh() {
        semiRcvAdapter = new SemiListRcvAdapter(dbManager, this, id);
        rcvSemi.setAdapter(semiRcvAdapter);
        swipe.setRefreshing(false);
    }

    private void viewItems(int selected) {
        selectedView = selected;
        switch (selected) {
            case (0):
                layoutDate.setVisibility(View.VISIBLE);

                layoutAch.setVisibility(View.VISIBLE);
                layoutAchBg.setBackgroundResource(R.drawable.xml_item);
                ivZoomAch.setImageResource(R.drawable.ic_zoomin_black);

                layoutAlarm.setVisibility(View.VISIBLE);
                layoutAlarmBg.setBackgroundResource(R.drawable.xml_item);
                ivZoomAlarm.setImageResource(R.drawable.ic_zoomin_black);

                layoutMemo.setVisibility(View.VISIBLE);
                layoutMemoBg.setBackgroundResource(R.drawable.xml_item);
                ivZoomMemo.setImageResource(R.drawable.ic_zoomin_black);
                isSelectedView = false;
                break;
            case (1):
                if (!isSelectedView) {
                    includeSemi.setVisibility(View.VISIBLE);
                    layoutAlarm.setVisibility(View.GONE);
                    layoutMemo.setVisibility(View.GONE);
                    layoutAchBg.setBackgroundResource(R.drawable.xml_item_selected);
                    ivZoomAch.setImageResource(R.drawable.ic_zoomout_black);
                    isSelectedView = true;
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                } else {
                    includeSemi.setVisibility(View.GONE);
                    layoutAlarm.setVisibility(View.VISIBLE);
                    layoutMemo.setVisibility(View.VISIBLE);
                    layoutAchBg.setBackgroundResource(R.drawable.xml_item);
                    ivZoomAch.setImageResource(R.drawable.ic_zoomin_black);
                    isSelectedView = false;
                }
                break;
            case (2):
                if (!isSelectedView) {
//                    includeAlarm.setVisibility(View.VISIBLE);
                    layoutMemo.setVisibility(View.GONE);
                    layoutAlarmBg.setBackgroundResource(R.drawable.xml_item_selected);
                    ivZoomAlarm.setImageResource(R.drawable.ic_zoomout_black);
                    isSelectedView = true;
                } else {
//                    includeAlarm.setVisibility(View.GONE);
                    layoutMemo.setVisibility(View.VISIBLE);
                    layoutAlarmBg.setBackgroundResource(R.drawable.xml_item);
                    ivZoomAlarm.setImageResource(R.drawable.ic_zoomin_black);
                    isSelectedView = false;
                }
                break;
            case (3):
                if (!isSelectedView) {
//                    includeMemo.setVisibility(View.VISIBLE);
                    layoutMemoBg.setBackgroundResource(R.drawable.xml_item_selected);
                    ivZoomMemo.setImageResource(R.drawable.ic_zoomout_black);
                    isSelectedView = true;
                } else {
//                    includeMemo.setVisibility(View.GONE);
                    layoutMemoBg.setBackgroundResource(R.drawable.xml_item);
                    ivZoomMemo.setImageResource(R.drawable.ic_zoomin_black);
                    isSelectedView = false;
                }
                break;
        }
        clickAnimation();
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private void clickAnimation() {
        List<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(Pair.create((View) toolbar, "toolbar"));
        pairs.add(Pair.create((View) layoutAch, "layout_ach"));
        pairs.add(Pair.create((View) layoutAlarm, "layout_alarm"));
        pairs.add(Pair.create((View) layoutMemo, "layout_memo"));

        Intent intent = new Intent(this, DetailActivity.class);
        Bundle options = ActivityOptions.makeSceneTransitionAnimation(this,
                pairs.toArray(new Pair[pairs.size()])).toBundle();
        startActivity(intent, options);
    }
}