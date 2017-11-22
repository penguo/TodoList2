package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;

public class DetailBodyFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    View includePB, borderAch, borderAlarm;
    int id;
    RoundCornerProgressBar pbBody;
    DBManager dbManager;
    LinearLayout layoutDate, layoutAch, layoutAlarm, layoutMemo, layoutAchBg, layoutAlarmBg, layoutMemoBg, layoutBody;
    ImageView ivZoomAch, ivZoomAlarm, ivZoomMemo;
    TextView tvDate, tvAch, tvMemo, tvAchHead, tvAlarmHead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_detail_body, container, false);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = this.getActivity();
        id = dbManager.DATA_id;

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
        tvAchHead = (TextView) view.findViewById(R.id.detail_tv_ach_head);
        tvAlarmHead = (TextView) view.findViewById(R.id.detail_tv_alarm_head);
        layoutDate = (LinearLayout) view.findViewById(R.id.detail_layout_date);
        layoutAch = (LinearLayout) view.findViewById(R.id.detail_layout_ach);
        layoutAchBg = (LinearLayout) view.findViewById(R.id.detail_layout_ach_bg);
        layoutAlarm = (LinearLayout) view.findViewById(R.id.detail_layout_alarm);
        layoutAlarmBg = (LinearLayout) view.findViewById(R.id.detail_layout_alarm_bg);
        layoutMemo = (LinearLayout) view.findViewById(R.id.detail_layout_memo);
        layoutMemoBg = (LinearLayout) view.findViewById(R.id.detail_layout_memo_bg);

        dbManager = new DBManager(activity, "todolist2.db", null, MainActivity.DBVERSION);

        layoutDate.setOnClickListener(this);
        layoutAch.setOnClickListener(this);
        layoutAlarm.setOnClickListener(this);

        setData();
    }

    public void setData() {
        tvDate.setText(DBManager.DATA_DATE);
        tvMemo.setText(DBManager.DATA_MEMO);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.detail_layout_date):
                break;
            case (R.id.detail_layout_ach):
//                ((InfoActivity) activity).selectItem(2);

                DetailSemiFragment detailSemiFragment = DetailSemiFragment.newInstance();
//                Slide slideTransition = new Slide(Gravity.RIGHT);
//                slideTransition.setDuration(250);

                ChangeBounds changeBoundsTransition = new ChangeBounds();
                changeBoundsTransition.setDuration(250);

//                detailSemiFragment.setEnterTransition(slideTransition);
                detailSemiFragment.setSharedElementEnterTransition(changeBoundsTransition);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addSharedElement(layoutBody, "layout_body");
                fragmentTransaction.addToBackStack("TEST");
                fragmentTransaction.add(R.id.info_framelayout_fragment, detailSemiFragment);
                fragmentTransaction.commit();
                break;
            case (R.id.detail_layout_alarm):
                break;
        }
    }

    public View getSharedElement(int item) {
        switch (item) {
            case (1):
                break;
            case (2):
                return layoutAch;
            case (3):
                break;
            default:
                return layoutAch;
        }
        return null;
    }
}