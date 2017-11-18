package com.pepg.todolist;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.dbManager;

import com.pepg.todolist.R;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    TextView tvTitle, tvCategory, tvDate;
    ImageButton btnEdit, btnReturn;
    int id;
    final dbManager dbManager = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    RecyclerView rcvSemi;
    SemiListRcvAdapter semiRcvAdapter;
    View includePB;
    RoundCornerProgressBar pb;
    UpdateSemi us;
    SwipeRefreshLayout swipe;

    public DetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = (TextView) findViewById(R.id.detail_tv_title);
        tvCategory = (TextView) findViewById(R.id.detail_tv_category);
        tvDate = (TextView) findViewById(R.id.detail_tv_date);
        btnEdit = (ImageButton) findViewById(R.id.detail_btn_edit);
        btnReturn = (ImageButton) findViewById(R.id.detail_btn_return);
        includePB = findViewById(R.id.detail_pb);
        pb = (RoundCornerProgressBar) includePB.findViewById(R.id.progressBar);

        rcvSemi = (RecyclerView) findViewById(R.id.detail_rcv_semi);
        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(this);
        rcvSemi.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, rcvLayoutManager.getOrientation());
        rcvSemi.addItemDecoration(dividerItemDecoration);

        semiRcvAdapter = new SemiListRcvAdapter(dbManager, this, id);
        rcvSemi.setAdapter(semiRcvAdapter);

        us = new UpdateSemi(semiRcvAdapter, this, dbManager);

        swipe = (SwipeRefreshLayout) findViewById(R.id.detail_swipe);
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
        }
    }

    private void setData() {
        id = getIntent().getIntExtra("_id", -1);
        dbManager.getValue("_id", id);
        tvTitle.setText(dbManager.DATA_TITLE);
        tvCategory.setText(dbManager.DATA_CATEGORY);
        tvDate.setText(dbManager.DATA_DATE);
        pb.setProgress(dbManager.DATA_ACH);
        onRefresh();
    }

    public void updateAch() {
        pb.setProgress(dbManager.DATA_ACH);
//        if(dbManager.DATA_ACH == 100){
//            pb.setProgressColor(R.color.colorRemark);
//        }else{
//            pb.setProgressColor(R.color.custom_progress_todo_progress);
//        }
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
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
}