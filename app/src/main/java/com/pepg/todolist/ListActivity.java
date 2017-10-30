package com.pepg.todolist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;

import com.pepg.todolist.Adapter.SimpleRcvAdapter;

import com.pepg.todolist.Adapter.ListRcvAdapter;
import com.pepg.todolist.DataBase.dbManager;

import com.pepg.todolist.R;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnCategory, btnSetting;
    TextView tvSortEqual;
    RecyclerView rcvTodo;
    ListRcvAdapter listRcvAdapter;
    FloatingActionButton fabAdd;
    RecyclerView rcvDialog;
    AlertDialog.Builder builder;
    Toolbar toolbar;

    final dbManager dbManager = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        btnCategory = (ImageButton) findViewById(R.id.listA_btn_category);
        btnSetting = (ImageButton) findViewById(R.id.listA_btn_setting);
        fabAdd = (FloatingActionButton) findViewById(R.id.listA_fab);
        toolbar = (Toolbar) findViewById(R.id.listA_toolbar_sort);
        tvSortEqual = (TextView) findViewById(R.id.listA_tv_sortequal);

        rcvTodo = (RecyclerView) findViewById(R.id.listA_rcv_todo);
        rcvTodo.setLayoutManager(new LinearLayoutManager(this));

        listRcvAdapter = new ListRcvAdapter(dbManager, this);
        rcvTodo.setAdapter(listRcvAdapter);
//        btnSetting.setOnClickListener(this);
        btnCategory.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        setToolbar();
    }

    public void setToolbar(){
        if(dbManager.DATA_SORTTYPE.equals("DEFAULT")){
            toolbar.setVisibility(View.GONE);
        }else{
            toolbar.setVisibility(View.VISIBLE);
            tvSortEqual.setText(dbManager.DATA_SORTTYPEEQUAL);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case (R.id.listA_btn_setting):
//                intent = new Intent(ListActivity.this, SettingsFragment.class);
//                startActivity(intent);
                break;
            case (R.id.listA_btn_category):
                DialogOption();
                break;
            case (R.id.listA_fab):
                intent = new Intent(ListActivity.this, AddguideActivity.class);
                intent.putExtra("_id", 0);
                startActivityForResult(intent, Manager.RC_LIST_TO_ADDGUIDE);
                break;
//            case (R.id.listA_btn_add):
//                intent = new Intent(ListActivity.this, UpdateActivity.class);
//                intent.putExtra("_id", 0);
//                startActivityForResult(intent, Manager.RC_LIST_TO_UPDATE);
//                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setToolbar();
        if (requestCode == Manager.RC_LIST_TO_UPDATE) {
            if (resultCode == RESULT_OK) {
                dbManager.setPosition();
                listRcvAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_DETAIL) {
            if (resultCode == RESULT_OK) {
                dbManager.setPosition();
                listRcvAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_ADDGUIDE) {
            if (resultCode == RESULT_OK) {
                dbManager.setPosition();
                listRcvAdapter.notifyDataSetChanged();
            }
        }
        if(requestCode == Manager.RC_DETAIL){
            if (resultCode == RESULT_OK) {
                dbManager.setPosition();
                listRcvAdapter.notifyDataSetChanged();
            }
        }
    }

    private void DialogOption() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View content = inflater.inflate(R.layout.dialog_layout, null);

        rcvDialog = (RecyclerView) content.findViewById(R.id.dialog_rcv);
        rcvDialog.setLayoutManager(new LinearLayoutManager(this));
        SimpleRcvAdapter simpleRcvAdapter = new SimpleRcvAdapter(dbManager, this, "category", "forSort");
        rcvDialog.setAdapter(simpleRcvAdapter);
        AlertDialog dialog;

        builder.setView(content);

        // Dialog
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.show();
    }

    public void refresh(){
        finish();
        startActivity(getIntent());
    }
}
