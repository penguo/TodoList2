package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pepg.todolist.DataBase.AlarmData;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by pengu on 2017-08-10.
 */

public class AlarmRcvAdapter extends RecyclerView.Adapter<AlarmRcvAdapter.ViewHolder> {

    private Activity activity;
    DBManager dbManager;
    int id;
    ArrayList<AlarmData> list;
    String[] strings;
    Calendar cal, calNow;

    public AlarmRcvAdapter(DBManager dbManager, Activity activity, int id) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.id = id;
        list = dbManager.getAlarmOnesValue(id);
    }

    @Override
    public int getItemCount() {
        return dbManager.getAlarmSize(id);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvDetail;
        FrameLayout layoutEditMode;
        /**************************************************/
        /** TODO initialize view components in item view **/
        /**************************************************/
        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.alarm_tv_time);
            tvDetail = itemView.findViewById(R.id.alarm_tv_detail);
            layoutEditMode = itemView.findViewById(R.id.alarm_editmode);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        strings = list.get(position).getDate().split("\u002D");
        cal = Calendar.getInstance();
        calNow = Calendar.getInstance();
        cal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), 0);
        if (cal.getTimeInMillis() < calNow.getTimeInMillis()) { // 지났을 경우
            holder.itemView.setAlpha((float) 0.5);
        } else {
            holder.itemView.setAlpha(1);
        }

        holder.tvTime.setText(strings[0]+"-"+strings[1]+"-"+strings[2]+"   "+strings[3]+":"+strings[4]);
        if (list.get(position).getDetail().equals("")) {
            holder.tvDetail.setVisibility(View.GONE);
        } else {
            holder.tvDetail.setVisibility(View.VISIBLE);
            holder.tvDetail.setText(list.get(position).getDetail());
        }
        if (!Manager.editMode) {
            holder.layoutEditMode.setVisibility(View.GONE);
        } else {
            holder.layoutEditMode.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("정말로 삭제하시겠습니까?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeItemView(position);
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    private void removeItemView(int position) {
        dbManager.deleteAlarm(list.get(position).getAlarmId());
        notifyItemRemoved(position);
        refresh();
    }

    public void refresh() {
        list = dbManager.getAlarmOnesValue(id);
        notifyDataSetChanged();
    }
}