package com.pepg.todolist;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.DetailBodyFragment;
import com.pepg.todolist.Fragment.DetailSemiFragment;

import static com.pepg.todolist.Manager.viewState;

public class InfoActivity extends AppCompatActivity {

    View incToolbar, incHead;
    ImageButton btnReturn, btnEdit;
    TextView tvToolbarTitle, tvHeadAch, tvTitle, tvCategory, tvDday;
    RoundCornerProgressBar pbHead;
    int id;
    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);
    FragmentManager fragmentManager;
    DetailBodyFragment detailBodyFragment = new DetailBodyFragment();
    DetailSemiFragment detailSemiFragment = new DetailSemiFragment();
    FrameLayout frameLayoutToolbar, frameLayoutHead, frameLayoutFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        frameLayoutToolbar = (FrameLayout) findViewById(R.id.info_framelayout_toolbar);
        frameLayoutHead = (FrameLayout) findViewById(R.id.info_framelayout_head);
        frameLayoutFragment = (FrameLayout) findViewById(R.id.info_framelayout_fragment);


        incToolbar = findViewById(R.id.info_include_toolbar);
        btnReturn = incToolbar.findViewById(R.id.detail_btn_return);
        btnEdit = incToolbar.findViewById(R.id.detail_btn_edit);
        tvToolbarTitle = incToolbar.findViewById(R.id.detail_tv_toolbar_title);

        incHead = findViewById(R.id.info_include_head);
        pbHead = incHead.findViewById(R.id.detail_pb_head);
        tvHeadAch = incHead.findViewById(R.id.detail_tv_head_ach);
        tvTitle = incHead.findViewById(R.id.detail_tv_title);
        tvCategory = incHead.findViewById(R.id.detail_tv_category);
        tvDday = incHead.findViewById(R.id.detail_tv_dday);

        fragmentManager = getFragmentManager();


        setData();
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
        setDday();
        viewBody();
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

    public void updateAch() {
        dbManager.getValue("_id", DBManager.DATA_id);
        pbHead.setProgress(DBManager.DATA_ACH);
        pbHead.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
        tvHeadAch.setText(DBManager.DATA_ACH + "%");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
