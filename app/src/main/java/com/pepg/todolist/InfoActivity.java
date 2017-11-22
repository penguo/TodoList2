package com.pepg.todolist;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.DetailBodyFragment;
import com.pepg.todolist.Fragment.DetailSemiFragment;

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
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

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
        selectItem(0);
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
    }

    public void selectItem(int item) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (item) {
            case (0):
                bundle = new Bundle();
                detailBodyFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.info_framelayout_fragment, detailBodyFragment);
                fragmentTransaction.commit();
                break;
            case (1): // DATE
                break;
            case (2): // ACH-SEMI
//                bundle = new Bundle();
//                detailSemiFragment.setArguments(bundle);
//
//                Slide slideTransition = new Slide(Gravity.RIGHT);
//                slideTransition.setDuration(250);
//
//                ChangeBounds changeBoundsTransition = new ChangeBounds();
//                changeBoundsTransition.setDuration(250);
//
//                detailSemiFragment.setEnterTransition(slideTransition);
//                detailSemiFragment.setSharedElementEnterTransition(changeBoundsTransition);
//
//                fragmentTransaction.addSharedElement(detailBodyFragment.getSharedElement(item), "layout_ach");
//                fragmentTransaction.add(R.id.info_framelayout_fragment, detailSemiFragment);
//                fragmentTransaction.addToBackStack("TEST");
//                fragmentTransaction.commit();
                break;
            case (3): // ALARM
                break;
        }
    }
}
