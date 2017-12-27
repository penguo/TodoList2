package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.DataBase.DataSemi;
import com.pepg.todolist.DataBase.DataTodo;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.Optional.SmoothCheckBox;

import com.pepg.todolist.R;

import com.pepg.todolist.UpdateSemi;

import java.util.ArrayList;

import static com.pepg.todolist.Manager.editMode;

/**
 * Created by pengu on 2017-08-10.
 */

public class SemiListRcvAdapter extends RecyclerView.Adapter<SemiListRcvAdapter.ViewHolder> {
    private Activity activity;

    DBManager dbManager;
    int parentId;
    String currentClassName;
    ArrayList<DataSemi> semiList;

    public SemiListRcvAdapter(DBManager dbManager, Activity activity, DataTodo parentData) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.parentId = parentId;
        currentClassName = activity.getLocalClassName();
        parentId = parentData.getId();
        semiList = dbManager.getSemiValueList(parentId);
    }

    public SemiListRcvAdapter(DBManager dbManager, Activity activity, int parentId, boolean isEditMode) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.parentId = parentId;
        Manager.editMode = isEditMode;
        currentClassName = activity.getLocalClassName();
        semiList = dbManager.getSemiValueList(parentId);
    }

    @Override
    public int getItemCount() {
        return semiList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMemo, tvDate;
        SmoothCheckBox scb;
        LinearLayout layoutTop;
        boolean isSet;
        FrameLayout layoutEditMode;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.semi_tv_title);
            tvMemo = (TextView) itemView.findViewById(R.id.semi_tv_memo);
            tvDate = (TextView) itemView.findViewById(R.id.semi_tv_date);
            scb = (SmoothCheckBox) itemView.findViewById(R.id.semi_scb);
            layoutTop = (LinearLayout) itemView.findViewById(R.id.semi_layout);
            layoutEditMode = (FrameLayout) itemView.findViewById(R.id.semi_editmode);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semi, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.isSet = false;
        holder.tvTitle.setText(semiList.get(position).getTitle());
        if (semiList.get(position).getDate().equals("")) {
            holder.tvDate.setVisibility(View.GONE);
        } else {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(semiList.get(position).getDate());
        }
        if (semiList.get(position).getMemo().equals("")) {
            holder.tvMemo.setVisibility(View.GONE);
        } else {
            holder.tvMemo.setVisibility(View.VISIBLE);
            holder.tvMemo.setText(semiList.get(position).getMemo());
        }
        holder.scb.setClickable(false);
        if (semiList.get(position).getAch() == 100) {
            holder.scb.setChecked(true);
        } else {
            holder.scb.setChecked(false);
        }
        holder.isSet = true;
        if (!editMode) {
            holder.layoutEditMode.setVisibility(View.GONE);
            holder.scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    if (holder.isSet) {
                        if (holder.scb.isChecked()) {
                            semiList.get(position).setAch(100);
                        } else {
                            semiList.get(position).setAch(0);
                        }
                        dbManager.updateSemi(semiList.get(position));
                        if (currentClassName.equals("InfoActivity")) {
                            ((InfoActivity) activity).updateAch();
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
            holder.layoutEditMode.setVisibility(View.VISIBLE);
            holder.scb.setClickable(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateSemi us = new UpdateSemi(SemiListRcvAdapter.this, activity, dbManager);
                    us.updateSemi(activity, semiList.get(position));
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
        dbManager.deleteSemi(semiList.get(position).getId());
        if (parentId != 0) { // parentId=0 : 더미데이터 -> 더미데이터가 아닐 때
            switch (currentClassName) {
                case ("InfoActivity"):
                    ((InfoActivity) activity).updateAch();
                    break;
            }
        }

        semiList = dbManager.getSemiValueList(parentId);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, semiList.size()); // 지워진 만큼 다시 채워넣기.
    }

    public void refresh() {
        semiList = dbManager.getSemiValueList(parentId);
        notifyDataSetChanged();
    }
}