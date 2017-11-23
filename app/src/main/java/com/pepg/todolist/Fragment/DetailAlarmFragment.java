package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;
import com.pepg.todolist.DataBase.DBManager;

/**
 * Created by pengu on 2017-11-20.
 */

public class DetailAlarmFragment extends Fragment {

    Activity activity;
    int id;
    DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_detail_alarm, container, false);
        activity = this.getActivity();
        id = dbManager.DATA_id;
        Manager.viewState=3;

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);

        return layout;
    }

}