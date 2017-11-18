package com.pepg.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.dbManager;

import com.pepg.todolist.R;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    int id;
    String selected;
    EditText etTitle;
    ImageButton btnSave, btnReturn;
    TextView tvCategory, tvDate;
    FloatingActionButton fabSemiAdd;
    LinearLayout layoutDate, layoutCategory, layoutTop;
    final dbManager dbManager = new dbManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    UpdateSemi us;
    RecyclerView rcvSemi;
    SemiListRcvAdapter semiRcvAdapter;
    Activity activity;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        etTitle = (EditText) findViewById(R.id.update_et_title);
        btnSave = (ImageButton) findViewById(R.id.update_btn_save);
        btnReturn = (ImageButton) findViewById(R.id.update_btn_return);
        tvCategory = (TextView) findViewById(R.id.update_tv_category);
        tvDate = (TextView) findViewById(R.id.update_tv_date);
        layoutCategory = (LinearLayout) findViewById(R.id.update_layout_category);
        layoutDate = (LinearLayout) findViewById(R.id.update_layout_date);
        layoutTop = (LinearLayout) findViewById(R.id.update_layout_top);
        fabSemiAdd = (FloatingActionButton) findViewById(R.id.update_fab_semiadd);

        rcvSemi = (RecyclerView) findViewById(R.id.update_rcv_semi);
        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(this);
        rcvSemi.setLayoutManager(rcvLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, rcvLayoutManager.getOrientation());
        rcvSemi.addItemDecoration(dividerItemDecoration);

        semiRcvAdapter = new SemiListRcvAdapter(dbManager, this, id);
        rcvSemi.setAdapter(semiRcvAdapter);

        us = new UpdateSemi(semiRcvAdapter, this, dbManager);

        activity = this;

        btnSave.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        fabSemiAdd.setOnClickListener(this);
        layoutCategory.setOnClickListener(this);
        layoutDate.setOnClickListener(this);


        swipe = (SwipeRefreshLayout) findViewById(R.id.update_swipe);
        swipe.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light
        );
        swipe.setOnRefreshListener(this);

        etTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Manager.controlKeyboard(false, activity);
                    clearFocus();
                    return true;
                }
                return false;
            }
        });

        setData();
    }

    private void setData() {
        id = getIntent().getIntExtra("_id", 0);
        if (id == 0) { // NEW DATA
            etTitle.setText("");
            tvCategory.setText(getString(R.string.unregistered));
            tvDate.setText(getString(R.string.unregistered));
            etTitle.requestFocus();
        } else {
            dbManager.getValue("_id", id);
            etTitle.setText(dbManager.DATA_TITLE);
            tvCategory.setText(dbManager.DATA_CATEGORY);
            tvDate.setText(dbManager.DATA_DATE);
        }
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        clearFocus();
        switch (v.getId()) {
            case (R.id.update_layout_category):
                DialogSelectOption("category");
                break;
            case (R.id.update_layout_date):
                DialogSelectOption("date");
                break;
            case (R.id.update_btn_save):
                if (id == 0) {
                    dbManager.insert(etTitle.getText().toString(), tvCategory.getText().toString(), tvDate.getText().toString(), 0);
                    dbManager.DATA_SORTTYPE = "DEFAULT";
                } else {
                    dbManager.update(id, etTitle.getText().toString(), tvCategory.getText().toString(), tvDate.getText().toString(), 0);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case (R.id.update_fab_semiadd):
                us.updateSemi(id, this, true);
                semiRcvAdapter.notifyDataSetChanged();
                break;
            case (R.id.update_btn_return):
                onBackPressed();
                break;
        }
    }

    private void DialogSelectOption(final String type) {
        final List<String> Items = dbManager.getSettingList(type);
        if (type == "category") {
            Items.add(getString(R.string.new_category));
        } else if (type == "date") {
            Items.add(getString(R.string.new_date));
        }
        final CharSequence[] items = Items.toArray(new String[Items.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equals("category")) {
                    if (!items[which].toString().equals(getString(R.string.new_category))) {
                        tvCategory.setText(items[which].toString());
                    } else {
                        DialogAdd();
                    }
                } else if (type.equals("date")) {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    if (!items[which].toString().equals(getString(R.string.new_date))) {
                        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        switch (which) {
                            case (0):
                                cal.add(Calendar.DATE, 1);
                                break;
                            case (1):
                                cal.add(Calendar.DATE, 7);
                                break;
                            case (2):
                                cal.add(Calendar.MONTH, 1);
                                break;
                            case (3):
                                cal.add(Calendar.MONTH, 6);
                                break;
                            case (4):
                                tvDate.setText(getString(R.string.date_forever));
                                break;
                        }
                        if (which != 4) {
                            tvDate.setText(CurDateFormat.format(cal.getTime())+"");
                        }
                    } else {
                        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
                        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
                        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
                        DatePickerDialog dpDialog = new DatePickerDialog(UpdateActivity.this, listener, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));
                        dpDialog.show();
                    }
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void DialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("추가할 카테고리의 이름을 입력해주세요.");
        final EditText title = new EditText(this);
        builder.setView(title);
        builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = title.getText().toString();
                if (!dbManager.isAlreadyResCategory(selected)) {
                    dbManager.addSetting("category", selected);
                } else {
                    Toast.makeText(activity, "이미 존재하는 카테고리입니다. 해당 카테고리로 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
                tvCategory.setText(selected);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Dialog
        AlertDialog dialog;
        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            StringBuffer sb = new StringBuffer();
            sb.append(year + "-");
            monthOfYear++;
            if (monthOfYear < 10) {
                sb.append("0");
            }
            sb.append(monthOfYear + "-");
            if (dayOfMonth < 10) {
                sb.append("0");
            }
            sb.append(dayOfMonth + "");
            tvDate.setText(sb.toString());
        }
    };

    public void onBackPressed() {
        dbManager.deleteDummyData_Semi();
        setResult(RESULT_OK);
        finish();
    }

    public void clearFocus() {
        layoutTop.requestFocus();
    }


    @Override
    public void onRefresh() {
        semiRcvAdapter = new SemiListRcvAdapter(dbManager, this, id);
        rcvSemi.setAdapter(semiRcvAdapter);
        swipe.setRefreshing(false);
    }

}
