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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * author @Fobid
 */

public class Step2Fragment extends Fragment {

    DBManager dbManager;
    LinearLayout layoutTodo, layoutSchedule, layoutDate, layoutBucket;
    TextView tvStartDate, tvDateMiddle, tvDate;
    ImageView ivCheckSchedule, ivCheckTodo, ivCheckBucket;
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
        layoutDate = (LinearLayout) layout.findViewById(R.id.fs2_layout_date);
        tvStartDate = (TextView) layout.findViewById(R.id.fs2_tv_startdate);
        tvDateMiddle = (TextView) layout.findViewById(R.id.fs2_tv_datemiddle);
        tvDate = (TextView) layout.findViewById(R.id.fs2_tv_date);
        ivCheckSchedule = (ImageView) layout.findViewById(R.id.fs2_iv_check_schedule);
        ivCheckTodo = (ImageView) layout.findViewById(R.id.fs2_iv_check_todo);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DBManager.DATA_TYPE = 1;
                if (Manager.isOnFastAdd) {
                    DateSelectOption();
                } else {
                    DateSelectOption2();
                }
            }
        });
        layoutSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DBManager.DATA_TYPE = 2;
                if (Manager.isOnFastAdd) {
                    DateSelectOption();
                } else {
                    DateSelectOption2();
                }
            }
        });
        layoutSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                DBManager.DATA_TYPE = 2;
                if (Manager.isOnFastAdd) {
                    DateSelectOption();
                } else {
                    DateSelectOption2();
                }
            }
        });
        layoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step = 0;
                if (Manager.isOnFastAdd) {
                    DateSelectOption();
                } else {
                    DateSelectOption2();
                }
            }
        });
        setData();
    }

    public void setData() {
        switch(DBManager.DATA_TYPE){
            case(1): // 할 일
                layoutDate.setVisibility(View.VISIBLE);
                ivCheckSchedule.setVisibility(View.GONE);
                ivCheckTodo.setVisibility(View.VISIBLE);
                tvStartDate.setVisibility(View.VISIBLE);
                tvDateMiddle.setVisibility(View.VISIBLE);
                tvStartDate.setText(DBManager.DATA_CREATEDATE);
                break;
            case(2): // 일정
                tvStartDate.setVisibility(View.GONE);
                tvDateMiddle.setVisibility(View.GONE);
                layoutDate.setVisibility(View.VISIBLE);
                ivCheckSchedule.setVisibility(View.VISIBLE);
                ivCheckTodo.setVisibility(View.GONE);
                break;
        }
        if (DBManager.DATA_DATE.equals(getString(R.string.unregistered))) {
            layoutDate.setVisibility(View.GONE);
            ivCheckTodo.setVisibility(View.GONE);
            ivCheckSchedule.setVisibility(View.GONE);
        }
        tvDate.setText(DBManager.DATA_DATE);
    }

    // DetailBodyFragment 의 복사

    public void DateSelectOption() {
        step++;
        List<String> itemAlready = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        switch (DBManager.DATA_TYPE) {
            case (1):
                if (step == 1) {
                    builder.setTitle("할 일의 시작 날짜를 선택해주세요.");
                    itemAlready.add("오늘부터");
                    itemAlready.add(activity.getString(R.string.new_date));
                } else if (step == 2) {
                    builder.setTitle("할 일의 종료 날짜를 선택해주세요.");
                    itemAlready.addAll(Arrays.asList(getResources().getStringArray(R.array.itemlist_date)));
                    itemAlready.add(activity.getString(R.string.date_forever));
                    itemAlready.add(activity.getString(R.string.new_date));
                }
                break;
            case (2):
                builder.setTitle("일정 날짜를 선택해주세요.");
                itemAlready.addAll(Arrays.asList(getResources().getStringArray(R.array.itemlist_date)));
                itemAlready.add(activity.getString(R.string.new_date));
                break;
        }

        items = new String[itemAlready.size()];
        for (int i = 0; i < itemAlready.size(); i++) {
            items[i] = itemAlready.get(i);
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                Calendar cal = Calendar.getInstance();
                if (!items[which].toString().equals(activity.getString(R.string.new_date))) {
                    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    switch (DBManager.DATA_TYPE) {
                        case (1):
                            if (step == 1) {
                                switch (which) {
                                    case (0):
                                        result = CurDateFormat.format(cal.getTime()) + "";
                                        break;
                                }
                                DBManager.DATA_CREATEDATE = result;
                                DateSelectOption();
                            } else if (step == 2) {
                                strings = DBManager.DATA_CREATEDATE.split("\u002D");
                                cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                                switch (which) {
                                    case (0):
                                        cal.add(Calendar.DATE, 1);
                                        result = CurDateFormat.format(cal.getTime()) + "";
                                        break;
                                    case (1):
                                        cal.add(Calendar.DATE, 7);
                                        result = CurDateFormat.format(cal.getTime()) + "";
                                        break;
                                    case (2):
                                        cal.add(Calendar.MONTH, 1);
                                        result = CurDateFormat.format(cal.getTime()) + "";
                                        break;
                                    case (3):
                                        result = activity.getString(R.string.date_forever);
                                        break;
                                }
                                DBManager.DATA_DATE = result;
                                setData();
                                ((AddguideActivity) getActivity()).setData(2);
                            }
                            break;
                        case (2):
                            switch (which) {
                                case (0):
                                    cal.add(Calendar.DATE, 1);
                                    result = CurDateFormat.format(cal.getTime()) + "";
                                    break;
                                case (1):
                                    cal.add(Calendar.DATE, 7);
                                    result = CurDateFormat.format(cal.getTime()) + "";
                                    break;
                                case (2):
                                    cal.add(Calendar.MONTH, 1);
                                    result = CurDateFormat.format(cal.getTime()) + "";
                                    break;
                            }
                            DBManager.DATA_CREATEDATE = result;
                            DBManager.DATA_DATE = result;
                            setData();
                            ((AddguideActivity) getActivity()).setData(2);
                            break;
                    }
                } else {
                    DatePickerDialog dpDialog;
                    SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
                    SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
                    SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
                    if (DBManager.DATA_TYPE == 1 && step == 2) {
                        strings = DBManager.DATA_CREATEDATE.split("\u002D");
                        dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                        cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                        cal.add(Calendar.DATE, 1);
                        dpDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                    } else {
                        dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));

                    }
                    dpDialog.show();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void DateSelectOption2() {
        step++;
        DatePickerDialog dpDialog;
        Calendar cal = Calendar.getInstance();
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
        if (DBManager.DATA_TYPE == 1 && step == 2) {
            strings = DBManager.DATA_CREATEDATE.split("\u002D");
            dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
            cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
            cal.add(Calendar.DATE, 1);
            dpDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        } else {
            dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));
        }


        switch (DBManager.DATA_TYPE) {
            case (1):
                if (step == 1) {
                    dpDialog.setTitle("시작 날짜");
                } else if (step == 2) {
                    dpDialog.setTitle("종료 날짜");
                }
                break;
            case (2):
                break;
        }

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
            result = sb.toString();
            switch (DBManager.DATA_TYPE) {
                case (1):
                    if (step == 1) {
                        DBManager.DATA_CREATEDATE = result;
                        if (Manager.isOnFastAdd) {
                            DateSelectOption();
                        } else {
                            DateSelectOption2();
                        }
                    } else if (step == 2) {
                        DBManager.DATA_DATE = result;
                        setData();
                        ((AddguideActivity) getActivity()).setData(2);
                    }
                    break;
                case (2):
                    DBManager.DATA_CREATEDATE = result;
                    DBManager.DATA_DATE = result;
                    setData();
                    ((AddguideActivity) getActivity()).setData(2);
                    break;
            }
        }
    };
}