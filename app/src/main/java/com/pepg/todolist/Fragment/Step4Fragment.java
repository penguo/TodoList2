/*
 * Copyright Fobid. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.pepg.todolist.Adapter.AlarmRcvAdapter;
import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.UpdateSemi;

import com.pepg.todolist.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * author @Fobid
 */

public class Step4Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Activity activity;
    int id;
    DBManager dbManager;
    FloatingActionButton fabAdd;
    RecyclerView rcvAlarm;
    SwipeRefreshLayout swipe;
    AlarmRcvAdapter alarmRcvAdapter;
    String result;

    public Step4Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step4, container, false);
        activity = getActivity();

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);
        fabAdd = (FloatingActionButton) layout.findViewById(R.id.fdalarm_fab);
        rcvAlarm = (RecyclerView) layout.findViewById(R.id.fdalarm_rcv);
        swipe = (SwipeRefreshLayout) layout.findViewById(R.id.fdalarm_swipe);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(activity);
        rcvAlarm.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, rcvLayoutManager.getOrientation());
        rcvAlarm.addItemDecoration(dividerItemDecoration);
        alarmRcvAdapter = new AlarmRcvAdapter(dbManager, activity, id);
        rcvAlarm.setAdapter(alarmRcvAdapter);

        swipe.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light
        );
        swipe.setOnRefreshListener(this);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelectOption();
            }
        });

    }

    public void DateSelectOption() {
        result = "";
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
            sb.append(dayOfMonth + "-");
            result = sb.toString();
            TimeSelectOption();
        }
    };

    public void TimeSelectOption() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(activity, listenerTime, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        dialog.show();
    }

    private TimePickerDialog.OnTimeSetListener listenerTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            StringBuffer sb = new StringBuffer();
            if (hourOfDay < 10) {
                sb.append("0");
            }
            sb.append(hourOfDay + "-");
            if (minute < 10) {
                sb.append("0");
            }
            sb.append(minute);
            result = result + sb.toString();
            dbManager.insertAlarm(id, result, "");
            Manager.alarmSet(activity, dbManager);
            alarmRcvAdapter.refresh();
        }
    };

    @Override
    public void onRefresh() {
        alarmRcvAdapter.refresh();
        swipe.setRefreshing(false);
    }
}