package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;
import com.pepg.todolist.UpdateSemi;
import com.pepg.todolist.DataBase.DBManager;

/**
 * Created by pengu on 2017-11-20.
 */

public class DetailSemiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View includeSemi;
    SwipeRefreshLayout swipe;
    LinearLayout layoutSemi;
    RecyclerView rcvSemi;
    SemiListRcvAdapter semiRcvAdapter;
    UpdateSemi us;
    Activity activity;
    int id;
    DBManager dbManager;
    FloatingActionButton fab;

    public DetailSemiFragment() {
    }

    public static DetailSemiFragment newInstance() {
        DetailSemiFragment fragment = new DetailSemiFragment();
        Bundle args = new Bundle();
//        args.putString("1", param1);
//        args.putString("2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_detail_semi, container, false);
        activity = this.getActivity();
        id = dbManager.DATA_id;

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);

        includeSemi = layout.findViewById(R.id.fd_semi);
        layoutSemi = (LinearLayout) includeSemi.findViewById(R.id.semi_layout);
        rcvSemi = (RecyclerView) includeSemi.findViewById(R.id.semi_rcv);
        swipe = (SwipeRefreshLayout) includeSemi.findViewById(R.id.semi_swipe);
        fab = (FloatingActionButton) includeSemi.findViewById(R.id.semi_fab);

        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(getContext());
        rcvSemi.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, rcvLayoutManager.getOrientation());
        rcvSemi.addItemDecoration(dividerItemDecoration);
        semiRcvAdapter = new SemiListRcvAdapter(dbManager, activity, id);
        rcvSemi.setAdapter(semiRcvAdapter);

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
                us.updateSemi(id, activity, true);
            }
        });

        onRefresh();

        return layout;
    }

    @Override
    public void onRefresh() {
        semiRcvAdapter.refresh();
        swipe.setRefreshing(false);
    }

    public void setFab() {
        if (!Manager.modifyMode) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }
}