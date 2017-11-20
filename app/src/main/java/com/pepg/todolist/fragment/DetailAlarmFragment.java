package com.pepg.todolist.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;
import com.pepg.todolist.UpdateSemi;
import com.pepg.todolist.DataBase.dbManager;

/**
 * Created by pengu on 2017-11-20.
 */

public class DetailAlarmFragment extends Fragment {

    Activity activity;
    int id;
    dbManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_detail_semi, container, false);
        activity = this.getActivity();
        id = dbManager.DATA_id;

        dbManager = new dbManager(activity, "todolist2.db", null, MainActivity.DBVERSION);

        return layout;
    }

}