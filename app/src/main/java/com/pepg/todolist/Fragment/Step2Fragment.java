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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.Adapter.SimpleRcvAdapter;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;

/**
 * author @Fobid
 */

public class Step2Fragment extends Fragment implements View.OnClickListener {

    DBManager dbM;
    LinearLayout layoutTodo, layoutSchedule;
    TextView tvStartDate, tvDateMiddle, tvDate;
    ImageView ivCheckSchedule, ivCheckTodo;

    public Step2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step2, container, false);

        dbM = new DBManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);
        layoutTodo = (LinearLayout) layout.findViewById(R.id.fs2_layout_todo);
        layoutSchedule = (LinearLayout) layout.findViewById(R.id.fs2_layout_schedule);
        tvStartDate = (TextView) layout.findViewById(R.id.fs2_tv_startdate);
        tvDateMiddle = (TextView) layout.findViewById(R.id.fs2_tv_datemiddle);
        tvDate = (TextView) layout.findViewById(R.id.fs2_tv_date);
        ivCheckSchedule = (ImageView) layout.findViewById(R.id.fs2_iv_schedule_check);
        ivCheckTodo = (ImageView) layout.findViewById(R.id.fs2_iv_todo_check);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutSchedule.setOnClickListener(this);
        layoutTodo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}