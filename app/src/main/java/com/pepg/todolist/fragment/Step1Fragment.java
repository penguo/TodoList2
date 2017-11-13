package com.pepg.todolist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;

public class Step1Fragment extends Fragment{

    EditText etTitle;
    dbManager dbM;

    public Step1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step1, container, false);

        dbM = new dbManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);
        etTitle = (EditText) layout.findViewById(R.id.fs1_et);
        etTitle.setText(dbM.DATA_TITLE);
        etTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etTitle.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    dbM.DATA_TITLE = etTitle.getText().toString();
                    ((AddguideActivity)getActivity()).setData(1);
                    return true;
                }
                return false;
            }
        });
        return layout;
    }

    public String getDataTitle(){
        return etTitle.getText().toString();
    }
}