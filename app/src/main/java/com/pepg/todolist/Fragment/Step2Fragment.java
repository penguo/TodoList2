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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.pepg.todolist.AddguideActivity.DATA_STATIC;

/**
 * author @Fobid
 */

public class Step2Fragment extends Fragment {

    DBManager dbManager;
    LinearLayout layoutTodo, layoutSchedule, layoutDate, layoutBucket, layoutConti;
    TextView tvStartDate, tvDateMiddle, tvDate;
    ImageView ivCheckSchedule, ivCheckTodo, ivCheckBucket, ivCheckConti;
    int step;
    Activity activity;
    String[] items, strings;
    String result;

    public Step2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step2, container, false);
        activity = getActivity();

        dbManager = new DBManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);
        layoutTodo = (LinearLayout) layout.findViewById(R.id.fs2_layout_todo);
        layoutSchedule = (LinearLayout) layout.findViewById(R.id.fs2_layout_schedule);
        layoutBucket = (LinearLayout) layout.findViewById(R.id.fs2_layout_bucket);
        layoutConti = (LinearLayout) layout.findViewById(R.id.fs2_layout_conti);
        ivCheckTodo = (ImageView) layout.findViewById(R.id.fs2_iv_check_todo);
        ivCheckSchedule = (ImageView) layout.findViewById(R.id.fs2_iv_check_schedule);
        ivCheckBucket = (ImageView) layout.findViewById(R.id.fs2_iv_check_bucket);
        ivCheckConti = (ImageView) layout.findViewById(R.id.fs2_iv_check_conti);

        layoutDate = (LinearLayout) layout.findViewById(R.id.fs2_layout_date);
        tvStartDate = (TextView) layout.findViewById(R.id.fs2_tv_startdate);
        tvDateMiddle = (TextView) layout.findViewById(R.id.fs2_tv_datemiddle);
        tvDate = (TextView) layout.findViewById(R.id.fs2_tv_date);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DATA_STATIC.setType(1);
                DateSelectOption();
            }
        });
        layoutSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DATA_STATIC.setType(2);
                DateSelectOption();
            }
        });
        layoutBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DATA_STATIC.setType(3);
                DateSelectOption();
            }
        });
        layoutConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DATA_STATIC.setType(4);
                DateSelectOption();
            }
        });
        layoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DateSelectOption();
            }
        });
        setData();
    }

    public void setData() {
        ivCheckSchedule.setVisibility(View.GONE);
        ivCheckTodo.setVisibility(View.GONE);
        ivCheckBucket.setVisibility(View.GONE);
        ivCheckConti.setVisibility(View.GONE);
        switch (DATA_STATIC.getType()) {
            case (1): // 할 일
                layoutDate.setVisibility(View.VISIBLE);
                ivCheckTodo.setVisibility(View.VISIBLE);

                tvStartDate.setVisibility(View.VISIBLE);
                tvDateMiddle.setVisibility(View.VISIBLE);

                tvStartDate.setText(DATA_STATIC.getCreatedate());
                break;
            case (2): // 일정
                layoutDate.setVisibility(View.VISIBLE);
                ivCheckSchedule.setVisibility(View.VISIBLE);

                tvStartDate.setVisibility(View.GONE);
                tvDateMiddle.setVisibility(View.GONE);
                break;
            case (3): // 버킷리스트
                layoutDate.setVisibility(View.VISIBLE);
                ivCheckBucket.setVisibility(View.VISIBLE);

                tvStartDate.setVisibility(View.GONE);
                tvDateMiddle.setVisibility(View.GONE);
                break;
            case (4): // 계속
                layoutDate.setVisibility(View.VISIBLE);
                ivCheckConti.setVisibility(View.VISIBLE);

                tvStartDate.setVisibility(View.VISIBLE);
                tvDateMiddle.setVisibility(View.VISIBLE);

                tvStartDate.setText(DATA_STATIC.getCreatedate());
                break;
        }
        if (DATA_STATIC.getDate().equals(getString(R.string.unregistered))) {
            layoutDate.setVisibility(View.GONE);
            ivCheckTodo.setVisibility(View.GONE);
            ivCheckSchedule.setVisibility(View.GONE);
        }
        tvDate.setText(DATA_STATIC.getDate());
    }

    public void DateSelectOption() {
        step++;
        DatePickerDialog dpDialog;
        Calendar cal = Calendar.getInstance();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

        dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));

        switch (DATA_STATIC.getType()) {
            case (1):
                if (step == 1) {
                    dpDialog.setTitle("시작 날짜");
                } else if (step == 2) {
                    dpDialog.setTitle("종료 날짜");

                    // 종료 날짜의 최소날짜 제한
                    strings = DATA_STATIC.getCreatedate().split("\u002D");
                    dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                    cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                    cal.add(Calendar.DATE, 1);
                    dpDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                }
                dpDialog.show();
                break;
            case (2):
                dpDialog.show();
                break;
            case (3):
                DATA_STATIC.setCreatedate(activity.getString(R.string.date_forever));
                DATA_STATIC.setDate(activity.getString(R.string.date_forever));
                setData();
                ((AddguideActivity) getActivity()).setData(2);
                break;
            case (4):
                dpDialog.show();
                break;
        }

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
            result = sb.toString();
            switch (DATA_STATIC.getType()) {
                case (1):
                    if (step == 1) {
                        DATA_STATIC.setCreatedate(result);
                        DateSelectOption();
                    } else if (step == 2) {
                        DATA_STATIC.setDate(result);
                        setData();
                        ((AddguideActivity) getActivity()).setData(2);
                    }
                    break;
                case (2):
                    DATA_STATIC.setCreatedate(result);
                    DATA_STATIC.setDate(result);
                    setData();
                    ((AddguideActivity) getActivity()).setData(2);
                    break;
                case(3):
                    // 여기로 넘어올 일 없다.
                    break;
                case(4):
                    if (step == 1) {
                        DATA_STATIC.setCreatedate(result);
                        DateSelectOption();
                    } else if (step == 2) {
                        DATA_STATIC.setDate(result);
                        setData();
                        ((AddguideActivity) getActivity()).setData(2);
                    }
                    break;
            }
        }
    };
}