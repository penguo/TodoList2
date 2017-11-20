package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.ListActivity;
import com.pepg.todolist.Manager;

import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.DetailActivity;

import com.pepg.todolist.R;

import java.util.List;

/**
 * Created by pengu on 2017-08-10.
 */

public class ListRcvAdapter extends RecyclerView.Adapter<ListRcvAdapter.ViewHolder> {
    private Activity activity;

    dbManager dbManager;

    public ListRcvAdapter(dbManager dbManager, Activity activity) {
        this.dbManager = dbManager;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return dbManager.getSize();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvTitle, tvDate, tvCategory, tvAch, tvDday;
        RoundCornerProgressBar pb;

        /**************************************************/
        /** TODO initialize view components in item view **/
        /**************************************************/
        public ViewHolder(View itemView) {
            super(itemView);
            ivLogo = (ImageView) itemView.findViewById(R.id.todo_iv_logo);
            tvTitle = (TextView) itemView.findViewById(R.id.todo_tv_title);
            tvDate = (TextView) itemView.findViewById(R.id.todo_tv_date);
            tvCategory = (TextView) itemView.findViewById(R.id.todo_tv_category);
            tvAch = (TextView) itemView.findViewById(R.id.todo_tv_ach);
            tvDday = (TextView) itemView.findViewById(R.id.todo_tv_dday);
            pb = (RoundCornerProgressBar) itemView.findViewById(R.id.todo_pb);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        dbManager.setPosition();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        dbManager.getValue("_position", position);

        holder.tvTitle.setText(dbManager.DATA_TITLE);
        holder.tvDate.setText("~ " + dbManager.DATA_DATE);
        holder.tvCategory.setText(dbManager.DATA_CATEGORY);
        holder.tvAch.setText(dbManager.DATA_ACH + "%");
        holder.tvDday.setText(Manager.getDday(dbManager.DATA_DATE));
        try {
            if (dbManager.DATA_DDAY >= 10 || dbManager.DATA_DDAY <= -10) {
                holder.tvDday.setTextSize(16);
            } else {
                holder.tvDday.setTextSize(21);
            }
        } catch (Exception e) {
            holder.tvDday.setTextSize(16);
        }
        holder.pb.setProgress(dbManager.DATA_ACH);
        holder.pb.setSecondaryProgress(Manager.getSuggestAch(dbManager.DATA_CREATEDATE, dbManager.DATA_DATE));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailActivity.class);
                dbManager.getValue("_position", position);
                intent.putExtra("_id", dbManager.DATA_id);

                /**
                 * Shared Element Animation
                 * 스테이터스바, 네비게이션바, 툴바 와 함께 움직이는게 좋다.
                 */
                List<Pair<View, String>> pairs = ((ListActivity) activity).getPairs();
                pairs.add(Pair.create((View) holder.itemView, "layout_head"));
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(activity,
                        pairs.toArray(new Pair[pairs.size()])).toBundle();
                activity.startActivityForResult(intent, Manager.RC_LIST_TO_DETAIL, options);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("정말로 삭제하시겠습니까? 이 todo의 모든 내용이 삭제됩니다.");
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
        dbManager.delete(dbManager.DATA_id);
        notifyItemRemoved(position);
        dbManager.setPosition();
        notifyItemRangeChanged(position, dbManager.getSize()); // 지워진 만큼 다시 채워넣기.
    }
}