package com.pepg.todolist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pepg.todolist.Adapter.ListRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    TextView tvToolbarTitle;
    RecyclerView rcvTodo;
    ListRcvAdapter listRcvAdapter;
    FloatingActionButton fabAdd;
    Animation viewSlideOut, viewSlideIn;
    ImageView ivDropdown;
    DividerItemDecoration dividerItemDecoration;
    Toolbar toolbar;
    SwipeRefreshLayout swipe;
    Spinner spinnerSort;
    List<String> items;
    ImageButton btnSetting, btnSort;
    boolean isSortView;

    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Manager.setSetting(this);

        toolbar = (Toolbar) findViewById(R.id.listA_toolbar);
        setSupportActionBar(toolbar);

        spinnerSort = (Spinner) findViewById(R.id.listA_spinner);
        fabAdd = (FloatingActionButton) findViewById(R.id.listA_fab);
        tvToolbarTitle = (TextView) findViewById(R.id.listA_tv_toolbar_title);
        ivDropdown = (ImageView)findViewById(R.id.listA_iv_dropdown);
        btnSort = (ImageButton)findViewById(R.id.listA_btn_sort);
        btnSetting = (ImageButton)findViewById(R.id.listA_btn_setting);

        swipe = (SwipeRefreshLayout) findViewById(R.id.listA_swipe);
        swipe.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light
        );
        swipe.setOnRefreshListener(this);
        viewSlideOut = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_up);
        viewSlideIn = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_up);

        rcvTodo = (RecyclerView) findViewById(R.id.listA_rcv_todo);
        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(this);
        rcvTodo.setLayoutManager(rcvLayoutManager);
        dividerItemDecoration = new DividerItemDecoration(this, rcvLayoutManager.getOrientation());
        rcvTodo.addItemDecoration(dividerItemDecoration);

        listRcvAdapter = new ListRcvAdapter(dbManager, this);
        rcvTodo.setAdapter(listRcvAdapter);
        fabAdd.setOnClickListener(this);
        tvToolbarTitle.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        dataSet();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case (R.id.listA_fab):
                dbManager.deleteDummyData_Semi();
                dbManager.resetPublicData();
                dbManager.DATA_DATE = getString(R.string.unregistered);
                dbManager.DATA_CATEGORY = getString(R.string.unregistered);
                intent = new Intent(ListActivity.this, AddguideActivity.class);
                intent.putExtra("_id", 0);
                startActivityForResult(intent, Manager.RC_LIST_TO_ADDGUIDE);
                break;
            case(R.id.listA_tv_toolbar_title):
                if(isSortView){
                    spinnerSort.performClick();
                }
                break;
            case(R.id.listA_btn_setting):
                intent = new Intent(ListActivity.this, SettingsActivity.class);
                startActivityForResult(intent, Manager.RC_LIST_TO_SETTINGS);
                break;
            case(R.id.listA_btn_sort):
                spinnerSort.performClick();
                break;
        }
    }

    private void dataSet() {
        ivDropdown.setVisibility(View.GONE);
        items = dbManager.getSettingList("category"); // date, category 일 때 items에 추가.
        items.add(0, getString(R.string.all));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_spinner_toolbar,
                items);
        spinnerSort.setAdapter(spinnerAdapter);
        spinnerSort.setSelection(0);
        spinnerSort.setOnItemSelectedListener(this);
    }

    public void refreshSort() {
        listRcvAdapter = new ListRcvAdapter(dbManager, this);
        rcvTodo.setAdapter(listRcvAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Manager.RC_LIST_TO_UPDATE) {
            if (resultCode == RESULT_OK) {
                dbManager.DATA_SORTTYPE = "DEFAULT";
                onRefresh();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_INFO) {
            onRefresh();
        }
        if (requestCode == Manager.RC_LIST_TO_ADDGUIDE) {
            if (resultCode == RESULT_OK) {
                dbManager.DATA_SORTTYPE = "DEFAULT";
                onRefresh();
            }
        }
        if(requestCode == Manager.RC_LIST_TO_SETTINGS){
            onRefresh();
        }
    }

    public List<Pair<View, String>> getPairs() {
        List<Pair<View, String>> pairs = new ArrayList<>();
//        pairs.add(Pair.create((View) toolbar, "toolbar"));
        return pairs;
    }

    @Override
    public void onRefresh() {
        listRcvAdapter.refresh();
        swipe.setRefreshing(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (!items.get(position).equals(getString(R.string.all))) {
            DBManager.DATA_SORTTYPE = "CATEGORY";
            DBManager.DATA_SORTTYPEEQUAL = items.get(position);
            tvToolbarTitle.setText(items.get(position));
            ivDropdown.setVisibility(View.VISIBLE);
            isSortView = true;
        } else {
            resetSort();
        }
        refreshSort();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void resetSort(){
        DBManager.DATA_SORTTYPE = "DEFAULT";
        DBManager.DATA_SORTTYPEEQUAL = "";
        tvToolbarTitle.setText("TodoList");
        ivDropdown.setVisibility(View.GONE);
        isSortView = false;
        onRefresh();
    }

    @Override
    public void onBackPressed() {
        if(isSortView){
            resetSort();
        }else{
            finish();
        }
    }
}
