package com.pepg.todolist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;

import com.pepg.todolist.Adapter.SimpleRcvAdapter;

import com.pepg.todolist.Adapter.ListRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnCategory, btnSetting;
    TextView tvSortEqual;
    RecyclerView rcvTodo;
    ListRcvAdapter listRcvAdapter;
    FloatingActionButton fabAdd;
    LinearLayout layoutSort;
    RecyclerView rcvDialog;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    Animation viewSlideOut, viewSlideIn;
    boolean isSortViewing;
    DividerItemDecoration dividerItemDecoration;
    Toolbar toolbar;

    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        btnCategory = (ImageButton) findViewById(R.id.listA_btn_category);
        btnSetting = (ImageButton) findViewById(R.id.listA_btn_setting);
        fabAdd = (FloatingActionButton) findViewById(R.id.listA_fab);
        layoutSort = (LinearLayout) findViewById(R.id.listA_layout_sort);
        tvSortEqual = (TextView) findViewById(R.id.listA_tv_sortequal);
        toolbar = (Toolbar) findViewById(R.id.listA_toolbar);

        viewSlideOut = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_up);
        viewSlideIn = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_up);

        rcvTodo = (RecyclerView) findViewById(R.id.listA_rcv_todo);
        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(this);
        rcvTodo.setLayoutManager(rcvLayoutManager);
        dividerItemDecoration = new DividerItemDecoration(this, rcvLayoutManager.getOrientation());
        rcvTodo.addItemDecoration(dividerItemDecoration);

        listRcvAdapter = new ListRcvAdapter(dbManager, this);
        rcvTodo.setAdapter(listRcvAdapter);
//        btnSetting.setOnClickListener(this);
        btnCategory.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        layoutSort.setOnClickListener(this);
        setSortView();
    }

    public void setSortView() {
        if (dbManager.DATA_SORTTYPE.equals("DEFAULT")) {
            if (!isSortViewing) {
                layoutSort.setVisibility(View.GONE);
            } else {
                layoutSort.setVisibility(View.GONE);
                layoutSort.startAnimation(viewSlideIn);
                isSortViewing = false;
            }
        } else {
            if (!isSortViewing) {
                layoutSort.setVisibility(View.VISIBLE);
                layoutSort.startAnimation(viewSlideOut);
                isSortViewing = true;
            }
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
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
//                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                break;
//            case (R.id.listA_btn_add):
//                intent = new Intent(ListActivity.this, UpdateActivity.class);
//                intent.putExtra("_id", 0);
//                startActivityForResult(intent, Manager.RC_LIST_TO_UPDATE);
//                break;
            case (R.id.listA_layout_sort):
                DialogOption();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setSortView();
        if (requestCode == Manager.RC_LIST_TO_UPDATE) {
            if (resultCode == RESULT_OK) {
                dbManager.DATA_SORTTYPE = "DEFAULT";
                refresh();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_DETAIL) {
            if (resultCode == RESULT_OK) {
                refresh();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_ADDGUIDE) {
            if (resultCode == RESULT_OK) {
                dbManager.DATA_SORTTYPE = "DEFAULT";
                refresh();
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
        rcvDialog.addItemDecoration(dividerItemDecoration);

        builder.setView(content);

        // Dialog
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.show();
    }

    public void reSet() {
        listRcvAdapter = new ListRcvAdapter(dbManager, this);
        rcvTodo.setAdapter(listRcvAdapter);
        setSortView();
    }

    public void refresh(){
        listRcvAdapter.notifyDataSetChanged();
        setSortView();
    }

    public void dialogDismiss() {
        refresh();
        dialogDismiss();
    }

    public List<Pair<View, String>> getPairs() {

        List<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(Pair.create((View) toolbar, "toolbar"));
        return pairs;
    }
}
