package com.pepg.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTitle, tvCategory, tvDate;
    ImageButton btnEdit, btnReturn, btnRefresh;
    int id;
    final dbManager dbManager = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    RecyclerView rcvSemi;
    SemiListRcvAdapter semiRcvAdapter;
    RoundCornerProgressBar pb;
    UpdateSemi us;

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
        rcvSemi = (RecyclerView) findViewById(R.id.detail_rcv_semi);
        pb = (RoundCornerProgressBar) findViewById(R.id.detail_pb);
        btnRefresh = (ImageButton) findViewById(R.id.detail_btn_refresh);

        setData();

        btnEdit.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
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
            case(R.id.detail_btn_refresh):
                setResult(RESULT_OK);
                finish();
                startActivity(getIntent());
                Toast.makeText(this, "Refresh complete.", Toast.LENGTH_SHORT).show();
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
        setRcvSemi();
    }

    public void updateAch() {
        pb.setProgress(dbManager.DATA_ACH);
//        if(dbManager.DATA_ACH == 100){
//            pb.setProgressColor(R.color.colorRemark);
//        }else{
//            pb.setProgressColor(R.color.custom_progress_todo_progress);
//        }
    }

    private void setRcvSemi() {
        rcvSemi.setLayoutManager(new LinearLayoutManager(this));
        semiRcvAdapter = new SemiListRcvAdapter(dbManager, this, id);
        rcvSemi.setAdapter(semiRcvAdapter);
        us = new UpdateSemi(semiRcvAdapter, this, dbManager);
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
            }
        }
    }
}