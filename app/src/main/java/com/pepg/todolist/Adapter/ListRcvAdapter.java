package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DataBase.DBSortManager;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.ListActivity;
import com.pepg.todolist.Manager;

import com.pepg.todolist.DataBase.DBManager;

import com.pepg.todolist.R;

import java.util.List;

/**
 * Created by pengu on 2017-08-10.
 */

public class ListRcvAdapter extends RecyclerView.Adapter<ListRcvAdapter.ViewHolder> {
    private Activity activity;

    DBManager dbManager;

    public ListRcvAdapter(DBManager dbManager, Activity activity) {
        this.dbManager = dbManager;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {

        dbManager.setPosition();
        return dbManager.getSize();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSchedule;
        TextView tvTitle, tvDate, tvCategory, tvAch, tvDday, tvSubTitle;
        RoundCornerProgressBar pb;
        LinearLayout layoutItem, layoutUpperMargin, layoutSubTitle;

        /**************************************************/
        /** TODO initialize view components in item view **/
        /**************************************************/
        public ViewHolder(View itemView) {
            super(itemView);
            ivSchedule = (ImageView) itemView.findViewById(R.id.todo_iv_schedule);
            tvTitle = (TextView) itemView.findViewById(R.id.todo_tv_title);
            tvDate = (TextView) itemView.findViewById(R.id.todo_tv_date);
            tvCategory = (TextView) itemView.findViewById(R.id.todo_tv_category);
            tvAch = (TextView) itemView.findViewById(R.id.todo_tv_ach);
            tvDday = (TextView) itemView.findViewById(R.id.todo_tv_dday);
            pb = (RoundCornerProgressBar) itemView.findViewById(R.id.todo_pb);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.todo_layout);
            layoutUpperMargin = (LinearLayout) itemView.findViewById(R.id.todo_layout_uppermargin);
            layoutSubTitle = (LinearLayout) itemView.findViewById(R.id.todo_layout_subtitle);
            tvSubTitle = (TextView) itemView.findViewById(R.id.todo_tv_subtitle);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        dbManager.getValue("_position", position);
        holder.layoutSubTitle.setVisibility(View.GONE);
        holder.layoutUpperMargin.setVisibility(View.GONE);

        if (Manager.isViewSubTitle) {
            for (int i = 0; i < DBSortManager.subtitlePosition.size(); i++)
                if (DBSortManager.subtitlePosition.get(i) == position) {
                    if (position != 0) {
                        holder.layoutUpperMargin.setVisibility(View.VISIBLE);
                    }
                    holder.layoutSubTitle.setVisibility(View.VISIBLE);
                    switch (i) {
                        case (0):
                            holder.tvSubTitle.setText("과거");
                            break;
                        case (1):
                            holder.tvSubTitle.setText("오늘");
                            break;
                        case (2):
                            holder.tvSubTitle.setText("이번 주");
                            break;
                        case (3):
                            holder.tvSubTitle.setText("그 외");
                            break;
                    }
                }
        }

        if (!dbManager.DATA_TITLE.equals(activity.getString(R.string.empty_data))) {
            holder.tvTitle.setText(DBManager.DATA_TITLE);
            holder.tvCategory.setText(DBManager.DATA_CATEGORY);
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvTitle.setText(DBManager.DATA_CATEGORY);
            holder.tvCategory.setVisibility(View.GONE);
        }
        holder.tvDate.setText("~ " + DBManager.DATA_DATE);
        holder.tvDday.setText(Manager.getDdayString(DBManager.DATA_DDAY));
        if (DBManager.DATA_DATE.equals(DBManager.DATA_CREATEDATE)) {
            holder.ivSchedule.setVisibility(View.VISIBLE);
            holder.tvAch.setVisibility(View.GONE);
            holder.pb.setVisibility(View.GONE);
            holder.layoutItem.setAlpha(1);
        } else {
            holder.tvAch.setText(DBManager.DATA_ACH + "%");
            holder.ivSchedule.setVisibility(View.GONE);
            holder.tvAch.setVisibility(View.VISIBLE);
            holder.pb.setVisibility(View.VISIBLE);
            if (!Manager.calculateisStart(DBManager.DATA_CREATEDATE)) {
                holder.layoutItem.setAlpha((float) 0.5);
            } else {
                holder.layoutItem.setAlpha(1);
            }
        }
        try {
            if (DBManager.DATA_DDAY >= 10 || DBManager.DATA_DDAY <= -10) {
                holder.tvDday.setTextSize(16);
            } else {
                holder.tvDday.setTextSize(21);
            }
        } catch (Exception e) {
            holder.tvDday.setTextSize(16);
        }
        holder.pb.setProgress(DBManager.DATA_ACH);
        holder.pb.setSecondaryProgress(Manager.getSuggestAch(DBManager.DATA_CREATEDATE, DBManager.DATA_DATE));
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoActivity.class);
                dbManager.getValue("_position", position);
                intent.putExtra("_id", DBManager.DATA_id);
                if (Manager.isAnimationActive) {
                    List<Pair<View, String>> pairs = ((ListActivity) activity).getPairs();
                    pairs.add(Pair.create((View) holder.layoutItem, "layout_head"));
                    Bundle options = ActivityOptions.makeSceneTransitionAnimation(activity,
                            pairs.toArray(new Pair[pairs.size()])).toBundle();
                    activity.startActivityForResult(intent, Manager.RC_LIST_TO_INFO, options);
                } else {
                    activity.startActivityForResult(intent, Manager.RC_LIST_TO_INFO);
                }
            }
        });
        holder.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("정말로 삭제하시겠습니까? 모든 내용이 삭제되여 복구되지 않습니다.");
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
        dbManager.getValue("_position", position);
        dbManager.delete(DBManager.DATA_id);
        notifyItemRemoved(position);
        dbManager.setPosition();
        notifyItemRangeChanged(position, dbManager.getSize()); // 지워진 만큼 다시 채워넣기.
    }

    public void refresh() {
        dbManager.setPosition();
        notifyDataSetChanged();
    }
}