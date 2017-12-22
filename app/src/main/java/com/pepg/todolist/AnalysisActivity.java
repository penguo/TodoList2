package com.pepg.todolist;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.DataBase.DataTodo;

import java.util.ArrayList;

public class AnalysisActivity extends AppCompatActivity implements View.OnClickListener {

    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    final Handler handler = new Handler();

    TextView tvTodoComplete, tvTodoAverage, tvSchedule, tvTodoCount, tvGrade, tvComment;
    ImageButton btnReturn, btnInfo;
    LinearLayout layoutAlert, layoutLoading;
    int countComplete, countAverage, countScheduleFinish;
    int countType1, countType2;

    ArrayList<DataTodo> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        tvTodoComplete = (TextView) findViewById(R.id.analysis_tv_complete);
        tvTodoAverage = (TextView) findViewById(R.id.analysis_tv_average);
        tvSchedule = (TextView) findViewById(R.id.analysis_tv_schedule);
        tvTodoCount = (TextView) findViewById(R.id.analysis_tv_count_todo);
        tvGrade = (TextView) findViewById(R.id.analysis_tv_grade);
        tvComment = (TextView) findViewById(R.id.analysis_tv_comment);
        btnReturn = (ImageButton) findViewById(R.id.analysis_btn_return);
        btnInfo = (ImageButton) findViewById(R.id.analysis_btn_infomation);
        layoutAlert = (LinearLayout) findViewById(R.id.analysis_layout_alert);
        layoutLoading = (LinearLayout) findViewById(R.id.analysis_layout_loading);
//        layoutData = (LinearLayout) findViewById(R.id.analysis_layout_data);

        btnReturn.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        layoutAlert.setOnClickListener(this);

        setData();
    }

    private void setData() {
        dataList = dbManager.getValueListAll();
        if (dataList.size() < 20) {
            layoutAlert.setVisibility(View.VISIBLE);
        }
        int i;
        countComplete = 0;
        countAverage = 0;
        countType1 = 0;
        countType2 = 0;
        for (i = 0; i < dataList.size(); i++) {
            switch (dataList.get(i).getType()) {
                case (1):
                    countType1++;
                    if (dataList.get(i).getAch() == 100) {
                        countComplete++;
                    }
                    countAverage += dataList.get(i).getAch();
                    break;
                case (2):
                    countType2++;
                    if (dataList.get(i).getAch() == 100) {
                        countScheduleFinish++;
                    }
                    break;
            }
        }
        tvTodoCount.setText(countType1 + "");
        tvTodoComplete.setText(countComplete + "");
        tvTodoAverage.setText((countAverage) / (countType1) + "");
        tvSchedule.setText((countScheduleFinish * 100) / (countType2) + "");
        gradeSet();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutLoading.setVisibility(View.GONE);
            }
        }, 0);
    }

    private void gradeSet() {
        int score = 0;
        String grade, comment;

        score += (countComplete * 100) / countType1;
        score += countAverage / countType1;
        score += (countScheduleFinish * 100) / countType2;
        score = score / 3;

        if (score >= 95) {
            grade = "S";
            comment = "완벽합니다!";
        } else if (score >= 90) {
            grade = "A";
            comment = "뛰어납니다.";
        } else if (score >= 70) {
            grade = "B";
            comment = "나쁘지 않습니다. 계속 노력하면 좋은 결과를 얻을 수 있습니다.";
        } else if (score >= 50) {
            grade = "C";
            comment = "많은 노력이 필요합니다.";
        } else {
            grade = "D";
            comment = "매우 많은 노력이 필요합니다.";
        }
        tvGrade.setText(grade);
        tvComment.setText(comment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.analysis_btn_return):
                onBackPressed();
                break;
            case (R.id.analysis_btn_infomation):
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
