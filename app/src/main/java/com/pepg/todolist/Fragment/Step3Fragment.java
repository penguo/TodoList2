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
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;
import com.pepg.todolist.UpdateSemi;

/**
 * author @Fobid
 */

public class Step3Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View includeSemi;
    DBManager dbManager;
    RecyclerView rcvFs3;
    SemiListRcvAdapter semiRcvAdapter;
    Activity activity;
    SwipeRefreshLayout swipe;
    FloatingActionButton fab;
    UpdateSemi us;
    ImageButton btnLibraryAdd;

    public Step3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step3, container, false);
        activity = getActivity();
        includeSemi = layout.findViewById(R.id.fs3_include);
        rcvFs3 = (RecyclerView) includeSemi.findViewById(R.id.semi_rcv);
        swipe = (SwipeRefreshLayout) includeSemi.findViewById(R.id.semi_swipe);
        fab = (FloatingActionButton) includeSemi.findViewById(R.id.semi_fab);
        btnLibraryAdd = (ImageButton) includeSemi.findViewById(R.id.semi_btn_libraryadd);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);

        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(activity);
        rcvFs3.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, rcvLayoutManager.getOrientation());
        rcvFs3.addItemDecoration(dividerItemDecoration);
        semiRcvAdapter = new SemiListRcvAdapter(dbManager, activity, 0, true);
        rcvFs3.setAdapter(semiRcvAdapter);

        us = new UpdateSemi(semiRcvAdapter, activity, dbManager);

        swipe.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light
        );
        swipe.setOnRefreshListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                us.updateSemi(0, activity, true);
            }
        });
        btnLibraryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Manager.callSemiLibraryAddLayout(activity, dbManager, 0, semiRcvAdapter);
            }
        });
    }

    @Override
    public void onRefresh() {
        semiRcvAdapter.refresh();
        swipe.setRefreshing(false);
    }
}