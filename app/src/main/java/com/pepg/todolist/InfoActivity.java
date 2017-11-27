package com.pepg.todolist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.DetailBodyFragment;
import com.pepg.todolist.Fragment.DetailSemiFragment;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    View incHead;
    TextView tvToolbarTitle, tvAch, tvTitle, tvCategory, tvDday;
    LinearLayout layoutHead, layoutData, layoutDataEditMode, layoutFragment;
    RoundCornerProgressBar pbHead;
    int id;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    FragmentManager fragmentManager;
    DetailBodyFragment detailBodyFragment = new DetailBodyFragment();
    FrameLayout frameLayoutHead;
    Toolbar toolbar;
    ImageButton btnReturn, btnEditMode, btnOverflow, btnHeadEdit;
    EditText etTitle;
    Spinner spinnerCategory, spinnerMenu;
    List<String> itemsCategory, itemsMenu;
    AlertDialog.Builder dialog;
    Activity activity;
    String selectedItem;
    CoordinatorLayout layoutAch;
    boolean cancelEditMode;
    ImageView ivSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        activity = this;

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        tvToolbarTitle = (TextView) findViewById(R.id.detail_tv_toolbar_title);

        frameLayoutHead = (FrameLayout) findViewById(R.id.info_framelayout_head);
        layoutFragment = (LinearLayout) findViewById(R.id.info_linearlayout_fragment);

        btnReturn = (ImageButton) findViewById(R.id.detail_btn_return);
        btnEditMode = (ImageButton) findViewById(R.id.detail_btn_edit);
        btnOverflow = (ImageButton) findViewById(R.id.detail_btn_overflow);
        spinnerMenu = (Spinner) findViewById(R.id.detail_spinner);

        incHead = findViewById(R.id.info_include_head);
        pbHead = incHead.findViewById(R.id.head_pb);
        tvAch = incHead.findViewById(R.id.head_tv_ach);
        tvTitle = incHead.findViewById(R.id.head_tv_title);
        tvCategory = incHead.findViewById(R.id.head_tv_category);
        tvDday = incHead.findViewById(R.id.head_tv_dday);
        btnHeadEdit = incHead.findViewById(R.id.head_btn_edit);
        layoutHead = incHead.findViewById(R.id.head_layout);
        layoutData = incHead.findViewById(R.id.head_layout_data);
        layoutDataEditMode = incHead.findViewById(R.id.head_layout_data_editmode);
        etTitle = incHead.findViewById(R.id.head_et_title);
        spinnerCategory = incHead.findViewById(R.id.head_spinner);
        layoutAch = incHead.findViewById(R.id.head_layout_ach);
        ivSchedule = incHead.findViewById(R.id.head_iv_schedule);

        fragmentManager = getFragmentManager();

        btnReturn.setOnClickListener(this);
        btnEditMode.setOnClickListener(this);
        btnOverflow.setOnClickListener(this);
        layoutHead.setOnClickListener(this);

        itemsCategory = dbManager.getSettingList("category");
        itemsCategory.add(getString(R.string.new_category));
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_spinner_toolbar,
                itemsCategory);
        spinnerCategory.setAdapter(spinnerAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = itemsCategory.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        itemsMenu = new ArrayList<>();
        itemsMenu.add("상태창에 고정");
        itemsMenu.add("btn1");
        itemsMenu.add("btn2");
        itemsMenu.add("항목 삭제");
        itemsMenu.add("취소");
        ArrayAdapter<String> spinnerMenuAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_spinner_toolbar,
                itemsMenu);
        spinnerMenu.setAdapter(spinnerMenuAdapter);
        spinnerMenu.setSelection(4);
        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case (0):
                        Manager.notificationInfo(activity, getResources(), DBManager.DATA_id, dbManager);
                        break;
                    case (1):
                        break;
                    case (3):
                        dialog = new AlertDialog.Builder(activity);
                        dialog.setMessage("정말로 삭제하시겠습니까?");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.delete(DBManager.DATA_id);

                                supportFinishAfterTransition();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setData();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.detail_btn_return):
                onBackPressed();
                break;
            case (R.id.detail_btn_edit):
                onEditModePressed();
                break;
            case (R.id.detail_btn_overflow):
                spinnerMenu.performClick();
                break;
            case (R.id.head_layout):
                if (Manager.editMode) {
                    layoutData.setVisibility(View.GONE);
                    layoutDataEditMode.setVisibility(View.VISIBLE);
                    layoutAch.setVisibility(View.GONE);
                    btnHeadEdit.setImageResource(R.drawable.ic_save);
                    btnHeadEdit.setClickable(true);
                    btnHeadEdit.setOnClickListener(this);
                }
                break;
            case (R.id.head_btn_edit):
                resetHeadEdit();
                break;
        }
    }

    public void resetHeadEdit() {
        if (cancelEditMode) {
            DBManager.DATA_TITLE = etTitle.getText().toString();
            DBManager.DATA_CATEGORY = selectedItem;
            dbManager.updateSimply();
            dbManager.getValue("_id", id);
        }
        layoutData.setVisibility(View.VISIBLE);
        layoutDataEditMode.setVisibility(View.GONE);
        layoutAch.setVisibility(View.VISIBLE);
        btnHeadEdit.setImageResource(R.drawable.ic_edit);
        btnHeadEdit.setOnClickListener(null);
        btnHeadEdit.setClickable(false);
        if (!DBManager.DATA_TITLE.equals(getString(R.string.empty_data))) {
            tvTitle.setText(DBManager.DATA_TITLE);
            tvCategory.setText(DBManager.DATA_CATEGORY);
        } else {
            tvTitle.setText(DBManager.DATA_CATEGORY);
            tvCategory.setVisibility(View.GONE);
        }
    }

    public void setData() {
        id = getIntent().getIntExtra("_id", -1);
        dbManager.getValue("_id", id);
        if (!DBManager.DATA_TITLE.equals(getString(R.string.empty_data))) {
            tvTitle.setText(DBManager.DATA_TITLE);
            tvCategory.setText(DBManager.DATA_CATEGORY);
        } else {
            tvTitle.setText(DBManager.DATA_CATEGORY);
            tvCategory.setVisibility(View.GONE);
        }
        etTitle.setText(DBManager.DATA_TITLE);
        for (int i = 0; i < itemsCategory.size(); i++) {
            if (itemsCategory.get(i).equals(DBManager.DATA_CATEGORY)) {
                selectedItem = DBManager.DATA_CATEGORY;
                spinnerCategory.setSelection(i);
            }
        }
        setDday();
        viewBody();
        Manager.editMode = false;
        cancelEditMode = false;
        btnEditMode.setImageResource(R.drawable.ic_edit);
    }

    private void setDday() {
        tvDday.setText(Manager.getDdayString(DBManager.DATA_DDAY));
        try {
            if (DBManager.DATA_DDAY >= 10 || DBManager.DATA_DDAY <= -10) {
                tvDday.setTextSize(16);
            } else {
                tvDday.setTextSize(21);
            }
        } catch (Exception e) {
            tvDday.setTextSize(16);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAch();
    }

    public void onEditModePressed() {
        if (!Manager.editMode) {
            Manager.editMode = true;
            btnEditMode.setImageResource(R.drawable.ic_save);
            tvToolbarTitle.setText("Detail  EditMode");
            btnHeadEdit.setVisibility(View.VISIBLE);
            tvDday.setVisibility(View.GONE);
        } else {
            Manager.editMode = false;
            btnEditMode.setImageResource(R.drawable.ic_edit);
            tvToolbarTitle.setText("Detail");
            btnHeadEdit.setVisibility(View.GONE);
            tvDday.setVisibility(View.VISIBLE);
            resetHeadEdit();
        }
        switch (Manager.viewState) {
            case (0):
                DetailBodyFragment dbf = (DetailBodyFragment) getFragmentManager().findFragmentById(R.id.info_linearlayout_fragment);
                dbf.editMode();
                break;
            case (2):
                DetailSemiFragment dsf = (DetailSemiFragment) getFragmentManager().findFragmentById(R.id.info_linearlayout_fragment);
                dsf.checkEditMode();
                break;
            default:
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "" + Manager.viewState);
                break;
        }
    }

    public void updateAch() {
        dbManager.getValue("_id", DBManager.DATA_id);
        pbHead.setProgress(DBManager.DATA_ACH);
        pbHead.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
        if(DBManager.DATA_DATE.equals(DBManager.DATA_CREATEDATE)){
            ivSchedule.setVisibility(View.VISIBLE);
            tvAch.setVisibility(View.GONE);
        }else{
            tvAch.setText(DBManager.DATA_ACH + "%");
            ivSchedule.setVisibility(View.GONE);
            tvAch.setVisibility(View.VISIBLE);
        }
        switch (Manager.viewState) {
            case (0):
                DetailBodyFragment dbf = (DetailBodyFragment) getFragmentManager().findFragmentById(R.id.info_linearlayout_fragment);
                dbf.updateAch();
                break;
            case (2):
                DetailSemiFragment dsf = (DetailSemiFragment) getFragmentManager().findFragmentById(R.id.info_linearlayout_fragment);
                dsf.updateAch();
                break;
        }
    }

    public void viewBody() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.info_linearlayout_fragment, detailBodyFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public float getElevation() {
        return frameLayoutHead.getElevation();
    }

    public void refreshRcv() {
        updateAch();
        DetailSemiFragment dsf = (DetailSemiFragment) getFragmentManager().findFragmentById(R.id.info_linearlayout_fragment);
        dsf.onRefresh();
    }

    @Override
    public void onBackPressed() {
        if (Manager.editMode && Manager.viewState == 0) {
            try {
                cancelEditMode = true;
                onEditModePressed();
            } catch (Exception e) {
                Log.e("ERROR", e.toString() + "");
            }
            Log.e("", Manager.viewState + "");
        } else {
            super.onBackPressed();
        }
    }
}
