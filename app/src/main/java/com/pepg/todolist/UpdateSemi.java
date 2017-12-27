package com.pepg.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.DataBase.DataSemi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.pepg.todolist.InfoActivity.DATA_INFO;

/**
 * Created by pengu on 2017-09-19.
 */

public class UpdateSemi {

    SemiListRcvAdapter semiRcvAdapter;
    DBManager dbManager;
    Context context;
    Activity activity;
    String date;
    DataSemi data;
    TextView tvDate;

    public UpdateSemi(SemiListRcvAdapter semiRcvAdapter, Activity activity, DBManager DBManager) {
        this.semiRcvAdapter = semiRcvAdapter;
        this.dbManager = DBManager;
        this.activity = activity;
    }

    public void updateSemi(Context cont, final DataSemi inputData) {
        context = cont;
        data = inputData;

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.update_semi, null);
        final EditText etTitle = (EditText) updateLayout.findViewById(R.id.semiup_et_title);
        final EditText etMemo = (EditText) updateLayout.findViewById(R.id.semiup_et_memo);
        tvDate = (TextView) updateLayout.findViewById(R.id.semiup_tv_date);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;

        etTitle.setText(data.getTitle());
        etMemo.setText(data.getMemo());
        tvDate.setText(data.getDate());
        if (data.isNew()) {
            builder.setTitle("세부todo 추가");
        } else {
            builder.setTitle("세부todo 수정");
        }
        builder.setView(updateLayout);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelectOption();
            }
        });
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                data.setTitle(etTitle.getText().toString());
                data.setMemo(etMemo.getText().toString());
                if (data.isNew()) {
                    dbManager.insertSemi(data);
                } else {
                    dbManager.updateSemi(data);
                }
                switch (activity.getLocalClassName()) {
                    case ("InfoActivity"):
                        ((InfoActivity) activity).refreshRcv();
                        break;
                    default:
                        semiRcvAdapter.refresh();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Dialog
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void DateSelectOption() {
        date = data.getDate();
        DatePickerDialog dpDialog;
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

        dpDialog = new DatePickerDialog(activity, listenerDate, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));
        dpDialog.show();
    }

    private DatePickerDialog.OnDateSetListener listenerDate = new DatePickerDialog.OnDateSetListener() {
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
            sb.append(dayOfMonth);
            date = sb.toString();
            data.setDate(date);
            tvDate.setText(date);
        }
    };

}
