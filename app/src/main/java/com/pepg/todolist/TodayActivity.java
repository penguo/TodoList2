package com.pepg.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.Adapter.TodayRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.pepg.todolist.InfoActivity.DATA_INFO;

public class TodayActivity extends AppCompatActivity implements View.OnClickListener {

    DBManager dbManager;
    RecyclerView rcvToday;
    TodayRcvAdapter todayRcvAdapter;
    Activity activity;
    String date;
    Calendar dateCal;
    TextView tvHeader;
    ImageButton btnDate;
    boolean isToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        activity = this;

        rcvToday = (RecyclerView) findViewById(R.id.today_rcv);
        tvHeader = (TextView) findViewById(R.id.today_tv_header);
        btnDate = (ImageButton) findViewById(R.id.today_btn_date);

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);
        Calendar cal = Calendar.getInstance();
        date = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
        dateCal = Calendar.getInstance();
        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(activity);
        rcvToday.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, rcvLayoutManager.getOrientation());
        rcvToday.addItemDecoration(dividerItemDecoration);

        btnDate.setOnClickListener(this);

        setData();
    }

    public void setData() {
        todayRcvAdapter = new TodayRcvAdapter(dbManager, activity, date, isToday);
        rcvToday.setAdapter(todayRcvAdapter);
        ArrayList<Integer> dateCut = Manager.getDateCut(date);
        tvHeader.setText(dateCut.get(0) + "년 " + dateCut.get(1) + "월 " + dateCut.get(2) + "일 (" + Manager.getDayOfWeek(dateCut.get(3)) + ")");
    }


    public void DateSelectOption() {
        DatePickerDialog dpDialog;
        ArrayList<Integer> dateCut = Manager.getDateCut(date);
        dpDialog = new DatePickerDialog(activity, listener, dateCut.get(0), dateCut.get(1) - 1, dateCut.get(2));
        dpDialog.show();
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            StringBuffer sb = new StringBuffer();
            sb.append(year + "-");
            monthOfYear++;
            if (monthOfYear < 10) {
                sb.append("0");
            }
            sb.append(monthOfYear + "-");
            if (dayOfMonth < 10) {
                sb.append("0");
            }
            sb.append(dayOfMonth + "");
            date = sb.toString();
            setData();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.today_btn_date):
                DateSelectOption();
                break;
        }
    }
}
