package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.pepg.todolist.DetailItemActivity;
import com.pepg.todolist.Optional.SmoothCheckBox;
import com.pepg.todolist.UpdateActivity;

import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.DetailActivity;

import com.pepg.todolist.R;

import com.pepg.todolist.UpdateSemi;

/**
 * Created by pengu on 2017-08-10.
 */

public class SemiListRcvAdapter extends RecyclerView.Adapter<SemiListRcvAdapter.ViewHolder> {
    private Activity activity;

    dbManager dbManager;
    int parentId;
    String currentClassName;

    public SemiListRcvAdapter(dbManager dbManager, Activity activity, int parentId) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.parentId = parentId;
        this.dbManager.setSemiPosition(parentId);
    }

    @Override
    public int getItemCount() {
        return dbManager.getSemiSize(parentId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //        ImageView ivWeight;
        TextView tvTitle, tvDate;
        View includePB;
        RoundCornerProgressBar pb;
        SmoothCheckBox scb;
        LinearLayout layoutTop;
        boolean isSet;

        public ViewHolder(View itemView) {
            super(itemView);
//            ivWeight = (ImageView) itemView.findViewById(R.id.semi_iv_weight);
            tvTitle = (TextView) itemView.findViewById(R.id.semi_tv_title);
            tvDate = (TextView) itemView.findViewById(R.id.semi_tv_date);
            includePB = itemView.findViewById(R.id.semi_pb);
            pb = (RoundCornerProgressBar) includePB.findViewById(R.id.progressBar);
            scb = (SmoothCheckBox) itemView.findViewById(R.id.semi_scb);
            layoutTop = (LinearLayout) itemView.findViewById(R.id.semi_layout);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semi, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        currentClassName = activity.getLocalClassName();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        dbManager.getSemiValue("_position", position);

//        holder.ivWeight.setImageResource(Manager.getDrawableResId("letter" + dbManager.DATA_semi_WEIGHT));
        holder.isSet = false;
        holder.tvTitle.setText(dbManager.DATA_semi_TITLE);
        holder.tvDate.setText(dbManager.DATA_semi_DATE);
        holder.pb.setMax(dbManager.DATA_semi_ACHMAX);
        holder.pb.setProgress(dbManager.DATA_semi_ACH);
        holder.scb.setClickable(false);
        if (dbManager.DATA_semi_ACH == 100) {
            holder.scb.setChecked(true);
        } else {
            holder.scb.setChecked(false);
        }
        holder.isSet = true;
        if (currentClassName.equals("DetailActivity") || currentClassName.equals("DetailItemActivity")) {
            holder.scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    if (holder.isSet) {
                        dbManager.getSemiValue("_position", position);
                        if (holder.scb.isChecked()) {
                            dbManager.DATA_semi_ACH = 100;
                        } else {
                            dbManager.DATA_semi_ACH = 0;
                        }
                        dbManager.semiUpdateSimply();
                        holder.pb.setProgress(dbManager.DATA_semi_ACH);
                        dbManager.getValue("_id", parentId);
                        if(currentClassName.equals("DetailActivity")){
                            ((DetailActivity) activity).updateAch();
                        }else if(currentClassName.equals("DetailItemActivity")) {
                            ((DetailItemActivity) activity).updateAch();
                        }
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.scb.toggleAnimation();
                }
            });
        } else {
            holder.scb.setClickable(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.getLocalClassName().equals("UpdateActivity")) {
                        ((UpdateActivity) activity).clearFocus();
                    }
                    UpdateSemi us = new UpdateSemi(SemiListRcvAdapter.this, activity, dbManager);
                    dbManager.getSemiValue("_position", position);
                    us.updateSemi(dbManager.DATA_semi_id, activity, false);
                    notifyDataSetChanged();
                }
            });
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
                    return false;
                }
            });
        }
    }

    private void removeItemView(int position) {
        dbManager.getSemiValue("_position", position);
        dbManager.semiDelete(dbManager.DATA_semi_id);
        dbManager.setSemiPosition(parentId);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dbManager.getSemiSize(parentId)); // 지워진 만큼 다시 채워넣기.
    }

}