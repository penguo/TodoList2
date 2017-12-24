package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetailBodyFragment extends Fragment implements View.OnClickListener {

    Activity activity;
    View includePB, borderAch, borderAlarm;
    int id, step;
    RoundCornerProgressBar pbBody;
    DBManager dbManager;
    LinearLayout layoutDate, layoutAch, layoutAlarm, layoutMemo, layoutBody;
    ImageView ivZoomAch, ivZoomAlarm, ivEditDate, ivEditMemo;
    TextView tvDate, tvDateHead, tvAch, tvMemo, tvAchHead, tvAlarmHead, tvGone, tvStartDate, tvDateMiddle, tvAlarmSize;
    DetailSemiFragment detailSemiFragment;
    DetailAlarmFragment detailAlarmFragment;
    FragmentManager fragmentManager;
    final Handler handler = new Handler();
    String result;
    String[] items, strings;

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
        ivEditDate = (ImageView) view.findViewById(R.id.detail_iv_edit_date);
        ivEditMemo = (ImageView) view.findViewById(R.id.detail_iv_edit_memo);

        layoutBody = (LinearLayout) view.findViewById(R.id.detail_layout_body);

        tvDate = (TextView) view.findViewById(R.id.detail_tv_date);
        tvMemo = (TextView) view.findViewById(R.id.detail_tv_memo);
        tvAch = (TextView) view.findViewById(R.id.detail_tv_ach);
        tvDateHead = (TextView) view.findViewById(R.id.detail_tv_ach_head);
        tvAchHead = (TextView) view.findViewById(R.id.detail_tv_ach_head);
        tvAlarmHead = (TextView) view.findViewById(R.id.detail_tv_alarm_head);
        tvStartDate = (TextView) view.findViewById(R.id.detail_tv_startdate);
        tvDateMiddle = (TextView) view.findViewById(R.id.detail_tv_datemiddle);
        layoutDate = (LinearLayout) view.findViewById(R.id.detail_layout_date);
        layoutAch = (LinearLayout) view.findViewById(R.id.detail_layout_ach);
        layoutAlarm = (LinearLayout) view.findViewById(R.id.detail_layout_alarm);
        layoutMemo = (LinearLayout) view.findViewById(R.id.detail_layout_memo);
        tvAlarmSize = (TextView) view.findViewById(R.id.detail_tv_alarmsize);

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
        layoutMemo.setOnClickListener(this);

        setData();
        editMode();
    }

    public void setData() {
        if (Manager.viewState != 0) {
            checkViewState();
        }
        Manager.viewState = 0;
        tvDate.setText(DBManager.DATA_DATE);
        switch (DBManager.DATA_TYPE) {
            case (1):
                tvStartDate.setText(DBManager.DATA_CREATEDATE);
                break;
            case (2):
                tvStartDate.setVisibility(View.GONE);
                tvDateMiddle.setVisibility(View.GONE);
                break;
        }
        tvMemo.setText(DBManager.DATA_MEMO);
        tvAlarmSize.setText(dbManager.getAlarmSize(DBManager.DATA_id) + "");
        fragmentManager = getFragmentManager();
        detailSemiFragment = new DetailSemiFragment();
        detailAlarmFragment = new DetailAlarmFragment();
        if (Manager.isAnimationActive) {
            setAnimation();
        }
        updateAch();
    }

    public void editMode() {
        if (!Manager.editMode) {
            ivEditMemo.setVisibility(View.GONE);
            tvMemo.setVisibility(View.VISIBLE);
            dbManager.updateSimply();
            ivEditDate.setVisibility(View.GONE);
        } else {
            ivEditMemo.setVisibility(View.VISIBLE);
            tvMemo.setVisibility(View.GONE);
            ivEditDate.setVisibility(View.VISIBLE);
        }
        setData();
    }

    public void updateAch() {
        tvAch.setText((DBManager.DATA_ACH_FINISH / 100) + " / " + (DBManager.DATA_ACH_MAX / 100));
        pbBody.setProgress(DBManager.DATA_ACH);
        pbBody.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
    }

    public void checkViewState() {
        int[] attrs = new int[]{R.attr.selectableItemBackground}; // Ripple Effect
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
        final int backgroundResource = typedArray.getResourceId(0, 0);
        if (Manager.isAnimationActive) {
            final int currentTextColor = tvGone.getCurrentTextColor();
            switch (Manager.viewState) {
                case (1):
                    layoutDate.setBackgroundResource(R.color.colorPrimaryDark);
                    tvDateHead.setTextColor(getResources().getColor(R.color.white07));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutDate.setBackgroundResource(backgroundResource);
                            tvDateHead.setTextColor(currentTextColor);
                        }
                    }, 300);
                    break;
                case (2):
                    layoutAch.setBackgroundResource(R.color.colorPrimaryDark);
                    tvAchHead.setTextColor(getResources().getColor(R.color.white07));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutAch.setBackgroundResource(backgroundResource);
                            tvAchHead.setTextColor(currentTextColor);
                        }
                    }, 300);
                    break;
                case (3):
                    layoutAlarm.setBackgroundResource(R.color.colorPrimaryDark);
                    tvAlarmHead.setTextColor(getResources().getColor(R.color.white07));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutAlarm.setBackgroundResource(backgroundResource);
                            tvAlarmHead.setTextColor(currentTextColor);
                        }
                    }, 300);
                    break;
            }
        }
        typedArray.recycle();
    }

    public void setAnimation() {
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
                if (Manager.editMode) {
                    DateEdit();
                }
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
            case (R.id.detail_layout_memo):
                if (Manager.editMode) {
                    Manager.callMemoLayout(activity, tvMemo);
                }
                break;
        }
    }

    public void DateEdit() {
        step = 0;
        final String[] items = {"할 일(~부터 ~까지)", "일정(선택한 날짜에)", "버킷 리스트(언젠가)", "꾸준하게(반복적으로)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("항목의 유형을 선택해주세요.");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                which++;
                DBManager.DATA_TYPE = which;
                DateSelectOption();
            }
        });
        builder.show();
    }

    public void DateSelectOption() {
        step++;
        DatePickerDialog dpDialog;
        Calendar cal = Calendar.getInstance();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

        dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));

        switch (DBManager.DATA_TYPE) {
            case (1):
                if (step == 1) {
                    dpDialog.setTitle("할 일의 시작 날짜를 선택해주세요.");
                } else if (step == 2) {
                    dpDialog.setTitle("할 일의 종료 날짜를 선택해주세요.");
                    strings = DBManager.DATA_CREATEDATE.split("\u002D");
                    dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                    cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                    cal.add(Calendar.DATE, 1);
                    dpDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                }
                dpDialog.show();
                break;
            case (2):
                dpDialog.setTitle("일정 날짜를 선택해주세요.");
                dpDialog.show();
                break;
            case (3):
                DBManager.DATA_CREATEDATE = activity.getString(R.string.date_forever);
                DBManager.DATA_DATE = activity.getString(R.string.date_forever);
                refresh();
                break;
            case (4):
                if (step == 1) {
                    dpDialog.setTitle("할 일의 시작 날짜를 선택해주세요.");
                } else if (step == 2) {
                    dpDialog.setTitle("할 일의 종료 날짜를 선택해주세요.");
                    strings = DBManager.DATA_CREATEDATE.split("\u002D");
                    dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                    cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
                    cal.add(Calendar.DATE, 1);
                    dpDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                }
                dpDialog.show();
                break;
        }
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
            result = sb.toString();
            switch (DBManager.DATA_TYPE) {
                case (1):
                    if (step == 1) {
                        DBManager.DATA_CREATEDATE = result;
                        DateSelectOption();
                    } else if (step == 2) {
                        DBManager.DATA_DATE = result;
                        dbManager.updateSimply();
                        refresh();
                    }
                    break;
                case (2):
                    DBManager.DATA_CREATEDATE = result;
                    DBManager.DATA_DATE = result;
                    dbManager.updateSimply();
                    refresh();
                    break;
                case (3):
                    // 여기로 넘어올 일 없다.
                    break;
                case (4):
                    if (step == 1) {
                        DBManager.DATA_CREATEDATE = result;
                        DateSelectOption();
                    } else if (step == 2) {
                        DBManager.DATA_DATE = result;
                        dbManager.updateSimply();
                        refresh();
                    }
                    break;
            }
        }
    };

    public void refresh() {
        dbManager.getValue("_id", id);
        setData();
        ((InfoActivity) activity).updateAch();
        ((InfoActivity) activity).setDday();
    }
}