package com.pepg.todolist.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;

import java.util.List;

public class Step1Fragment extends Fragment {

    EditText etTitle;
    DBManager dbM;
    Spinner spinnerCategory;
    List<String> itemsCategory;

    public Step1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step1, container, false);
        dbM = new DBManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);

        etTitle = (EditText) layout.findViewById(R.id.fs1_et);
        spinnerCategory = (Spinner) layout.findViewById(R.id.fs1_spinner);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle.setText(dbM.DATA_TITLE);
        etTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    dbM.DATA_TITLE = etTitle.getText().toString();
                    ((AddguideActivity) getActivity()).setData(1);
                    return true;
                }
                return false;
            }
        });

        itemsCategory = dbM.getSettingList("category");
        itemsCategory.add(getString(R.string.new_category));
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.custom_spinner_toolbar,
                itemsCategory);
        spinnerCategory.setAdapter(spinnerAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DBManager.DATA_CATEGORY = itemsCategory.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String getDataTitle() {
        return etTitle.getText().toString();
    }
}