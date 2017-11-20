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
package com.pepg.todolist.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.UpdateSemi;

import com.pepg.todolist.R;

import static android.app.Activity.RESULT_OK;

/**
 * author @Fobid
 */

public class Step4Fragment extends Fragment implements View.OnClickListener {

    Button btnSave;
    dbManager dbM;
    FloatingActionButton fab;
    UpdateSemi us;
    SemiListRcvAdapter semiRcvAdapter;
    RecyclerView rcvFs4;

    public Step4Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step4, container, false);

        dbM = new dbManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);

        btnSave = (Button) layout.findViewById(R.id.fs4_btn_save);
        fab = (FloatingActionButton) layout.findViewById(R.id.fs4_fab_semiadd);

        rcvFs4 = (RecyclerView) layout.findViewById(R.id.fs4_rcv);

        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(getContext());
        rcvFs4.setLayoutManager(rcvLayoutManager);

        semiRcvAdapter = new SemiListRcvAdapter(dbM, this.getActivity(), 0, true);
        rcvFs4.setAdapter(semiRcvAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), rcvLayoutManager.getOrientation());
        rcvFs4.addItemDecoration(dividerItemDecoration);

        us = new UpdateSemi(semiRcvAdapter, this.getActivity(), dbM);

        btnSave.setOnClickListener(this);
        fab.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.fs4_btn_save):
                ((AddguideActivity) getActivity()).save();
                break;
            case (R.id.fs4_fab_semiadd):
                us.updateSemi(0, this.getContext(), true);
                semiRcvAdapter.notifyDataSetChanged();
                break;
        }
    }
}