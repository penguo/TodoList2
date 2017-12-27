package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.DataBase.DataToday;

import com.pepg.todolist.DataBase.DBManager;

import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.Optional.SmoothCheckBox;
import com.pepg.todolist.R;

import java.util.ArrayList;

/**
 * Created by pengu on 2017-08-10.
 */

public class TodayRcvAdapter extends RecyclerView.Adapter<TodayRcvAdapter.ViewHolder> {

    private Activity activity;

    DBManager dbManager;
    ArrayList<DataToday> dataList;
    String date;
    boolean isViewPastST, isViewTodayST, isViewFutureST, isFirstSubTitle, isToday;

    public TodayRcvAdapter(DBManager dbManager, Activity activity, String date, boolean isToday) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.date = date;
        this.isToday = isToday;
        dataList = dbManager.getTodayList(date, isToday);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        LinearLayout layoutUpperMargin, layoutSubTitle, layoutItem;
        TextView tvTodoTitle, tvSemiTitle, tvSubTitle;
        SmoothCheckBox scb;
        boolean isSet;
        /**************************************************/
        /** TODO initialize view components in item view **/
        /**************************************************/
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.today_cardview);
            tvTodoTitle = (TextView) itemView.findViewById(R.id.today_tv_todo_title);
            tvSemiTitle = (TextView) itemView.findViewById(R.id.today_tv_semi_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.today_tv_subtitle);
            scb = (SmoothCheckBox) itemView.findViewById(R.id.today_scb);
            layoutUpperMargin = (LinearLayout) itemView.findViewById(R.id.today_layout_uppermargin);
            layoutSubTitle = (LinearLayout) itemView.findViewById(R.id.today_layout_subtitle);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.today_layout_item);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todaycard, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.isSet = false;
        holder.layoutSubTitle.setVisibility(View.GONE);
        holder.layoutUpperMargin.setVisibility(View.GONE);
//        if (!isFirstSubTitle) {
//            holder.layoutUpperMargin.setVisibility(View.VISIBLE);
//            isFirstSubTitle = true;
//        }
//        switch (dataList.get(position).getStatus()) {
//            case (0):
//                if (!isViewPastST) {
//                    holder.layoutSubTitle.setVisibility(View.VISIBLE);
//                    holder.tvSubTitle.setText("지난 " + 2 + "일 이내");
//                    isViewPastST = true;
//                }
//                break;
//            case (1):
//                if (!isViewTodayST) {
//                    holder.layoutSubTitle.setVisibility(View.VISIBLE);
//                    holder.tvSubTitle.setText("오늘");
//                    isViewTodayST = true;
//                }
//                break;
//            case (2):
//                if (!isViewFutureST) {
//                    holder.layoutSubTitle.setVisibility(View.VISIBLE);
//                    holder.tvSubTitle.setText("앞으로 " + 2 + "일 이내");
//                    isViewFutureST = true;
//                }
//                break;
//        }
        holder.scb.setClickable(false);
        if (dataList.get(position).getDataSemi().getAch() == 100) {
            holder.scb.setChecked(true);
        } else {
            holder.scb.setChecked(false);
        }
        holder.isSet = true;


        holder.tvTodoTitle.setText(dataList.get(position).getDataTodo().getTitle());
        holder.tvSemiTitle.setText(dataList.get(position).getDataSemi().getTitle());

        holder.scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (holder.isSet) {
                    if (holder.scb.isChecked()) {
                        dataList.get(position).getDataSemi().setAch(100);
                    } else {
                        dataList.get(position).getDataSemi().setAch(0);
                    }
                    dbManager.updateSemi(dataList.get(position).getDataSemi());
                }
            }
        });
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.scb.toggleAnimation();
            }
        });
    }

    private void removeItemView(int position) {
//        dbManager.deleteTodo(dataList.get(position).getId());
        dataList = dbManager.getTodayList(date, isToday);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size()); // 지워진 만큼 다시 채워넣기.
    }

    public void refresh() {
        dataList = dbManager.getTodayList(date, isToday);
        notifyDataSetChanged();
    }
}
