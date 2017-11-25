package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

public class DetailBodyFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    View includePB, borderAch, borderAlarm;
    int id;
    RoundCornerProgressBar pbBody;
    DBManager dbManager;
    LinearLayout layoutDate, layoutDateBg, layoutAch, layoutAlarm, layoutMemo, layoutAchBg, layoutAlarmBg, layoutMemoBg, layoutBody;
    ImageView ivZoomAch, ivZoomAlarm, ivZoomMemo;
    TextView tvDate, tvDateHead, tvAch, tvMemo, tvAchHead, tvAlarmHead, tvGone, tvStartDate;
    DetailDateFragment detailDateFragment;
    DetailSemiFragment detailSemiFragment;
    DetailAlarmFragment detailAlarmFragment;
    FragmentManager fragmentManager;
    EditText etMemo;
    final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_body, container, false);

        includePB = view.findViewById(R.id.detail_pb);
        pbBody = (RoundCornerProgressBar) includePB.findViewById(R.id.progressBar);

        borderAch = (View) view.findViewById(R.id.detail_border_ach);
        borderAlarm = (View) view.findViewById(R.id.detail_border_alarm);
        ivZoomAch = (ImageView) view.findViewById(R.id.detail_iv_zoom_ach);
        ivZoomAlarm = (ImageView) view.findViewById(R.id.detail_iv_zoom_alarm);
        ivZoomMemo = (ImageView) view.findViewById(R.id.detail_iv_zoom_memo);

        layoutBody = (LinearLayout) view.findViewById(R.id.detail_layout_body);

        tvDate = (TextView) view.findViewById(R.id.detail_tv_date);
        tvMemo = (TextView) view.findViewById(R.id.detail_tv_memo);
        tvAch = (TextView) view.findViewById(R.id.detail_tv_ach);
        tvDateHead = (TextView) view.findViewById(R.id.detail_tv_ach_head);
        tvAchHead = (TextView) view.findViewById(R.id.detail_tv_ach_head);
        tvAlarmHead = (TextView) view.findViewById(R.id.detail_tv_alarm_head);
        tvStartDate = (TextView) view.findViewById(R.id.detail_tv_startdate);
        layoutDate = (LinearLayout) view.findViewById(R.id.detail_layout_date);
        layoutDateBg = (LinearLayout) view.findViewById(R.id.detail_layout_date_bg);
        layoutAch = (LinearLayout) view.findViewById(R.id.detail_layout_ach);
        layoutAchBg = (LinearLayout) view.findViewById(R.id.detail_layout_ach_bg);
        layoutAlarm = (LinearLayout) view.findViewById(R.id.detail_layout_alarm);
        layoutAlarmBg = (LinearLayout) view.findViewById(R.id.detail_layout_alarm_bg);
        layoutMemo = (LinearLayout) view.findViewById(R.id.detail_layout_memo);
        layoutMemoBg = (LinearLayout) view.findViewById(R.id.detail_layout_memo_bg);

        etMemo = (EditText) view.findViewById(R.id.detail_et_memo);

        tvGone = (TextView) view.findViewById(R.id.detail_tv_gone);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = this.getActivity();
        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);
        id = dbManager.DATA_id;

        layoutDate.setOnClickListener(this);
        layoutAch.setOnClickListener(this);
        layoutAlarm.setOnClickListener(this);

        setData();
    }

    public void setData() {
        if (Manager.viewState != 0) {
            checkViewState();
        }
        Manager.viewState = 0;
        tvDate.setText(DBManager.DATA_DATE);
        tvStartDate.setText(DBManager.DATA_CREATEDATE);
        tvMemo.setText(DBManager.DATA_MEMO);
        etMemo.setText(DBManager.DATA_MEMO);
        fragmentManager = getFragmentManager();
        detailDateFragment = new DetailDateFragment();
        detailSemiFragment = new DetailSemiFragment();
        detailAlarmFragment = new DetailAlarmFragment();
        if (Manager.isAnimationActive) {
            setAnimation();
        }
        updateAch();
    }

    public void editMode() {
        if (!Manager.editMode) {
            etMemo.setVisibility(View.GONE);
            DBManager.DATA_MEMO = etMemo.getText().toString();
            tvMemo.setVisibility(View.VISIBLE);
            dbManager.updateSimply();
        } else {
            etMemo.setVisibility(View.VISIBLE);
            tvMemo.setVisibility(View.GONE);
        }
        setData();
    }

    public void updateAch() {
        tvAch.setText((DBManager.DATA_ACH_FINISH / 100) + " / " + (DBManager.DATA_ACH_MAX / 100));
        pbBody.setProgress(DBManager.DATA_ACH);
        pbBody.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
    }

    public void checkViewState() {
        if (Manager.isAnimationActive) {
            final int currentTextColor = tvGone.getCurrentTextColor();
            switch (Manager.viewState) {
                case (1):
                    layoutDateBg.setBackgroundResource(R.color.colorPrimaryDark);
                    tvDateHead.setTextColor(getResources().getColor(R.color.white07));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutDateBg.setBackgroundResource(R.drawable.xml_item);
                            tvDateHead.setTextColor(currentTextColor);
                        }
                    }, 300);
                    break;
                case (2):
                    layoutAchBg.setBackgroundResource(R.color.colorPrimaryDark);
                    tvAchHead.setTextColor(getResources().getColor(R.color.white07));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutAchBg.setBackgroundResource(R.drawable.xml_item);
                            tvAchHead.setTextColor(currentTextColor);
                        }
                    }, 300);
                    break;
                case (3):
                    layoutAlarmBg.setBackgroundResource(R.color.colorPrimaryDark);
                    tvAlarmHead.setTextColor(getResources().getColor(R.color.white07));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutAlarmBg.setBackgroundResource(R.drawable.xml_item);
                            tvAlarmHead.setTextColor(currentTextColor);
                        }
                    }, 300);
                    break;
            }
        }
    }

    public void setAnimation() {
        detailDateFragment.setSharedElementEnterTransition(Manager.getChangeBounds());
        detailDateFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
//        detailDateFragment.setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        detailDateFragment.setAllowEnterTransitionOverlap(false);
        detailSemiFragment.setSharedElementEnterTransition(Manager.getChangeBounds());
        detailSemiFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        detailSemiFragment.setAllowEnterTransitionOverlap(false);
        detailAlarmFragment.setSharedElementEnterTransition(Manager.getChangeBounds());
        detailAlarmFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        detailAlarmFragment.setAllowEnterTransitionOverlap(false);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction;
        switch (view.getId()) {
            case (R.id.detail_layout_date):
                layoutDate.setElevation(((InfoActivity) activity).getElevation());
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addSharedElement(layoutDate, "layout_date");
                fragmentTransaction.addSharedElement(pbBody, "progressbar");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.info_linearlayout_fragment, detailDateFragment);
                fragmentTransaction.commit();
                fragmentManager.executePendingTransactions();
                break;
            case (R.id.detail_layout_ach):
                layoutAch.setElevation(((InfoActivity) activity).getElevation());
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addSharedElement(layoutAch, "layout_ach");
                fragmentTransaction.addSharedElement(pbBody, "progressbar");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.info_linearlayout_fragment, detailSemiFragment);
                fragmentTransaction.commit();
                fragmentManager.executePendingTransactions();
                break;
            case (R.id.detail_layout_alarm):
                layoutAch.setElevation(((InfoActivity) activity).getElevation());
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addSharedElement(layoutAlarm, "layout_alarm");
                fragmentTransaction.addSharedElement(pbBody, "progressbar");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.info_linearlayout_fragment, detailAlarmFragment);
                fragmentTransaction.commit();
                fragmentManager.executePendingTransactions();
                break;
        }
    }
}