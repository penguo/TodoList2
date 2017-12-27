package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.DataSemi;
import com.pepg.todolist.DataBase.DataTodo;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;
import com.pepg.todolist.UpdateSemi;
import com.pepg.todolist.DataBase.DBManager;

import static com.pepg.todolist.InfoActivity.DATA_INFO;

/**
 * Created by pengu on 2017-11-20.
 */

public class DetailSemiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View includeSemi, includePB;
    SwipeRefreshLayout swipe;
    LinearLayout layoutSemi, layoutHead;
    RecyclerView rcvSemi;
    SemiListRcvAdapter semiRcvAdapter;
    UpdateSemi us;
    Activity activity;
    DBManager dbManager;
    FloatingActionButton fab;
    TextView tvAch;
    RoundCornerProgressBar pb;
    ImageButton btnLibraryAdd;

    public DetailSemiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_detail_semi, container, false);
        includeSemi = view.findViewById(R.id.fdsemi_include);
        layoutSemi = (LinearLayout) includeSemi.findViewById(R.id.semi_layout);
        rcvSemi = (RecyclerView) includeSemi.findViewById(R.id.semi_rcv);
        swipe = (SwipeRefreshLayout) includeSemi.findViewById(R.id.semi_swipe);
        fab = (FloatingActionButton) includeSemi.findViewById(R.id.semi_fab);
        btnLibraryAdd = (ImageButton) includeSemi.findViewById(R.id.semi_btn_libraryadd);

        layoutHead = (LinearLayout) view.findViewById(R.id.fdsemi_layout_head);
        tvAch = (TextView) view.findViewById(R.id.fdsemi_tv_ach);

        includePB = view.findViewById(R.id.fdsemi_pb);
        pb = includePB.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = this.getActivity();
        Manager.viewState = 2;

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);

        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(activity);
        rcvSemi.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, rcvLayoutManager.getOrientation());
        rcvSemi.addItemDecoration(dividerItemDecoration);
        semiRcvAdapter = new SemiListRcvAdapter(dbManager, activity, DATA_INFO);
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
                us.updateSemi(activity, new DataSemi(DATA_INFO.getId()));
            }
        });
        btnLibraryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Manager.callSemiLibraryAddLayout(activity, dbManager, DATA_INFO.getId(), semiRcvAdapter);
            }
        });

        onRefresh();
        setData();
    }

    public void setData() {
        ((InfoActivity) activity).resetHeadEdit();
        pb.setSecondaryProgress(Manager.getSuggestAch(DATA_INFO));
        updateAch();
        checkEditMode();
    }

    public void updateAch() {
        tvAch.setText((DATA_INFO.getAch_finish() / 100) + " / " + (DATA_INFO.getAch_max() / 100));
        pb.setProgress(DATA_INFO.getAch());
    }

    @Override
    public void onRefresh() {
        semiRcvAdapter.refresh();
        swipe.setRefreshing(false);
    }

    public void checkEditMode() {
        if (!Manager.editMode) {
            fab.setVisibility(View.GONE);
            btnLibraryAdd.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
            btnLibraryAdd.setVisibility(View.VISIBLE);
        }
        onRefresh();
    }
}