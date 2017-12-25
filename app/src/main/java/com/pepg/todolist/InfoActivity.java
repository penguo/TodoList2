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
import android.view.WindowManager;
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
import com.pepg.todolist.DataBase.DataTodo;
import com.pepg.todolist.Fragment.DetailAlarmFragment;
import com.pepg.todolist.Fragment.DetailBodyFragment;
import com.pepg.todolist.Fragment.DetailSemiFragment;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    View incHead;
    TextView tvToolbarTitle, tvAch, tvTitle, tvCategory, tvDday, tvTitleEdit, tvCategoryEdit;
    LinearLayout layoutHead, layoutData, layoutDataEditMode, layoutFragment, layoutCategoryEdit, layoutTitleEdit;
    RoundCornerProgressBar pbHead;
    int id;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    FragmentManager fragmentManager;
    DetailBodyFragment detailBodyFragment = new DetailBodyFragment();
    FrameLayout frameLayoutHead;
    Toolbar toolbar;
    ImageButton btnReturn, btnEditMode, btnOverflow, btnHeadEdit;
    Spinner spinnerMenu;
    List<String> itemsMenu;
    AlertDialog.Builder dialog;
    Activity activity;
    CoordinatorLayout layoutAch;
    boolean cancelEditMode;
    ImageView ivSchedule;
    DataTodo data;

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
        layoutAch = incHead.findViewById(R.id.head_layout_ach);
        ivSchedule = incHead.findViewById(R.id.head_iv_schedule);
        layoutCategoryEdit = incHead.findViewById(R.id.head_layout_category_edit);
        layoutTitleEdit = incHead.findViewById(R.id.head_layout_title_edit);
        tvCategoryEdit = incHead.findViewById(R.id.head_tv_category_edit);
        tvTitleEdit = incHead.findViewById(R.id.head_tv_title_edit);

        fragmentManager = getFragmentManager();

        btnReturn.setOnClickListener(this);
        btnEditMode.setOnClickListener(this);
        btnOverflow.setOnClickListener(this);
        layoutHead.setOnClickListener(this);
        layoutCategoryEdit.setOnClickListener(this);
        layoutTitleEdit.setOnClickListener(this);

        itemsMenu = new ArrayList<>();
        itemsMenu.add("상태창에 고정");
        itemsMenu.add("항목 삭제");
        itemsMenu.add("취소");
        ArrayAdapter<String> spinnerMenuAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_toolbar,
                itemsMenu);
        spinnerMenu.setAdapter(spinnerMenuAdapter);
        spinnerMenu.setSelection(2);
        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case (0):
                        Manager.notificationInfo(activity, getResources(), id, dbManager);
                        break;
                    case (1):
                        dialog = new AlertDialog.Builder(activity);
                        dialog.setMessage("정말로 삭제하시겠습니까?");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.deleteTodo(data.getId());
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
            case (R.id.head_layout_category_edit):
                DialogSelectOption();
                break;
            case (R.id.head_layout_title_edit):
                Manager.callSetTitleLayout(activity, dbManager, data, tvTitleEdit);
                break;
        }
    }

    public void resetHeadEdit() {
        if (!cancelEditMode) {
            dbManager.updateTodo(data);
            data = dbManager.getValue(id);
        }
        layoutData.setVisibility(View.VISIBLE);
        layoutDataEditMode.setVisibility(View.GONE);
        layoutAch.setVisibility(View.VISIBLE);
        btnHeadEdit.setImageResource(R.drawable.ic_edit);
        btnHeadEdit.setOnClickListener(null);
        btnHeadEdit.setClickable(false);
        if (!data.getTitle().equals(getString(R.string.empty_data))) {
            tvTitle.setText(data.getTitle());
            tvCategory.setText(data.getCategory());
            tvCategory.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText(data.getCategory());
            tvCategory.setVisibility(View.GONE);
        }
    }

    public void setData() {
        id = getIntent().getIntExtra("_id", -1);
        data = dbManager.getValue(id);
        if (!data.getTitle().equals(getString(R.string.empty_data))) {
            tvTitle.setText(data.getTitle());
            tvCategory.setText(data.getCategory());
            tvCategory.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText(data.getCategory());
            tvCategory.setVisibility(View.GONE);
        }
        tvTitleEdit.setText(data.getTitle());
        tvCategoryEdit.setText(data.getCategory());
        setDday();
        viewBody();
        Manager.editMode = false;
        cancelEditMode = false;
        btnEditMode.setImageResource(R.drawable.ic_edit);
    }

    public void setDday() {
        tvDday.setText(Manager.getDdayString(data.getDday()));
        try {
            if (data.getDday() >= 10 || data.getDday() <= -10) {
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
            case (3):
                DetailAlarmFragment daf = (DetailAlarmFragment) getFragmentManager().findFragmentById(R.id.info_linearlayout_fragment);
                daf.checkEditMode();
                break;
            default:
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "" + Manager.viewState);
                break;
        }
    }

    public void updateAch() {
        data = dbManager.getValue(id);
        pbHead.setProgress(data.getAch());
        pbHead.setSecondaryProgress(Manager.getSuggestAch(data));

        switch(data.getType()){
            case(1):
                tvAch.setText(data.getAch() + "%");
                ivSchedule.setVisibility(View.GONE);
                tvAch.setVisibility(View.VISIBLE);
                pbHead.setVisibility(View.VISIBLE);
                break;
            case(2):
                ivSchedule.setVisibility(View.VISIBLE);
                tvAch.setVisibility(View.GONE);
                pbHead.setVisibility(View.GONE);
                break;
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

    private void DialogSelectOption() {
        final List<String> Items = dbManager.getSettingList("category");
        Items.add(getString(R.string.new_category));
        final CharSequence[] items = Items.toArray(new String[Items.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!items[which].toString().equals(getString(R.string.new_category))) {
                    data.setCategory(items[which].toString());
                    dbManager.updateTodo(data);
                    tvCategoryEdit.setText(data.getCategory());
                } else {
                    DialogAdd();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void DialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("추가할 카테고리의 이름을 입력해주세요.");
        final EditText title = new EditText(activity);
        builder.setView(title);
        builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = title.getText().toString();
                if (!dbManager.isAlreadyResCategory(selected)) {
                    if (selected.equals("")) {
                        Toast.makeText(activity, "분류 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        selected = activity.getString(R.string.unregistered);
                    } else {
                        dbManager.addSetting("category", selected);
                    }
                } else {
                    Toast.makeText(activity, "이미 존재하는 카테고리입니다. 해당 카테고리로 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
                data.setCategory(selected);
                dbManager.updateTodo(data);
                tvCategoryEdit.setText(data.getCategory());
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
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public DataTodo getData(){
        return data;
    }
}