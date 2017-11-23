package com.pepg.todolist;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    View incHead;
    TextView tvToolbarTitle, tvAch, tvTitle, tvCategory, tvDday;
    LinearLayout layoutHead, layoutData, layoutDataEditMode;
    RoundCornerProgressBar pbHead;
    int id;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    FragmentManager fragmentManager;
    DetailBodyFragment detailBodyFragment = new DetailBodyFragment();
    FrameLayout frameLayoutHead, frameLayoutFragment;
    Toolbar toolbar;
    ImageButton btnReturn, btnEditMode, btnOverflow, btnHeadEdit;
    EditText etTitle;
    Spinner spinnerCategory;
    List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        tvToolbarTitle = (TextView) findViewById(R.id.detail_tv_toolbar_title);

        frameLayoutHead = (FrameLayout) findViewById(R.id.info_framelayout_head);
        frameLayoutFragment = (FrameLayout) findViewById(R.id.info_framelayout_fragment);

        btnReturn = (ImageButton) findViewById(R.id.detail_btn_return);
        btnEditMode = (ImageButton) findViewById(R.id.detail_btn_edit);
        btnOverflow = (ImageButton) findViewById(R.id.detail_btn_overflow);

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

        fragmentManager = getFragmentManager();

        btnReturn.setOnClickListener(this);
        btnEditMode.setOnClickListener(this);
        btnOverflow.setOnClickListener(this);
        layoutHead.setOnClickListener(this);


        items = dbManager.getSettingList("category");
        items.add(0, getString(R.string.all));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_spinner_toolbar,
                items);
        spinnerCategory.setAdapter(spinnerAdapter);
        spinnerCategory.setSelection(0);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.white07));
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
                break;
            case (R.id.head_layout):
                if (Manager.editMode) {
                    layoutData.setVisibility(View.GONE);
                    layoutDataEditMode.setVisibility(View.VISIBLE);
                    btnHeadEdit.setImageResource(R.drawable.ic_save);
                    btnHeadEdit.setClickable(true);
                    btnHeadEdit.setOnClickListener(this);
                }
                break;
            case (R.id.head_btn_edit):
                DBManager.DATA_TITLE = etTitle.getText().toString();
                dbManager.getValue("_id", id);
                dbManager.updateSimply();
                resetHeadEdit();
                break;
        }
    }

    public void resetHeadEdit(){
        layoutData.setVisibility(View.VISIBLE);
        layoutDataEditMode.setVisibility(View.GONE);
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
        setDday();
        viewBody();
        Manager.editMode = false;
        btnEditMode.setImageResource(R.drawable.ic_edit);
    }

    private void setDday() {
        tvDday.setText(Manager.getDday(DBManager.DATA_DATE));
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
            layoutData.setVisibility(View.VISIBLE);
            layoutDataEditMode.setVisibility(View.GONE);
        }
        switch (Manager.viewState) {
            case (0):
                DetailBodyFragment dbf = (DetailBodyFragment) getFragmentManager().findFragmentById(R.id.info_framelayout_fragment);
                dbf.editMode();
                break;
            case (2):
                DetailSemiFragment dsf = (DetailSemiFragment) getFragmentManager().findFragmentById(R.id.info_framelayout_fragment);
                dsf.editMode();
            default:
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "" + Manager.viewState);
        }
    }

    public void updateAch() {
        dbManager.getValue("_id", DBManager.DATA_id);
        pbHead.setProgress(DBManager.DATA_ACH);
        pbHead.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
        tvAch.setText(DBManager.DATA_ACH + "%");
        switch (Manager.viewState) {
            case (2):
                DetailSemiFragment dsf = (DetailSemiFragment) getFragmentManager().findFragmentById(R.id.info_framelayout_fragment);
                dsf.updateAch();
                break;
            default:
                DetailBodyFragment dbf = (DetailBodyFragment) getFragmentManager().findFragmentById(R.id.info_framelayout_fragment);
                dbf.updateAch();
                break;
        }
    }

    public void viewBody() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.info_framelayout_fragment, detailBodyFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public float getElevation() {
        return frameLayoutHead.getElevation();
    }

    public void refreshRcv() {
        DetailSemiFragment dsf = (DetailSemiFragment) getFragmentManager().findFragmentById(R.id.info_framelayout_fragment);
        dsf.onRefresh();
    }

    @Override
    public void onBackPressed() {
        if (Manager.editMode && Manager.viewState == 0) {
            try {
                DetailBodyFragment dbf = (DetailBodyFragment) getFragmentManager().findFragmentById(R.id.info_framelayout_fragment);
                dbf.editMode();
            } catch (Exception e) {
                Log.e("ERROR", e.toString() + "");
            }

        } else {
            super.onBackPressed();
        }
    }
}
