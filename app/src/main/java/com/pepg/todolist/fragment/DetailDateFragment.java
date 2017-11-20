package com.pepg.todolist.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;

public class DetailDateFragment extends Fragment {

    Activity activity;
    int id;
    com.pepg.todolist.DataBase.dbManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_detail_date, container, false);
        activity = this.getActivity();
        id = dbManager.DATA_id;

        dbManager = new dbManager(activity, "todolist2.db", null, MainActivity.DBVERSION);



        return layout;
    }

}