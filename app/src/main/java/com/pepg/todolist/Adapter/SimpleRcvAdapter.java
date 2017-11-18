package com.pepg.todolist.Adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.ListActivity;

import com.pepg.todolist.R;

/**
 * Created by pengu on 2017-08-10.
 */

public class SimpleRcvAdapter extends RecyclerView.Adapter<SimpleRcvAdapter.ViewHolder> {
    private Activity activity;

    dbManager dbManager;
    List<String> items, itemsDetail;
    String type, option;
    Date date;
    Calendar cal;
    SimpleDateFormat CurDateFormat;
    CharSequence[] itemsCS, itemsDetailCS;
    int selectedPosition = -1;

    public SimpleRcvAdapter(dbManager dbManager, Activity activity, String type) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.type = type;
        this.option = "";
    }

    public SimpleRcvAdapter(dbManager dbManager, Activity activity, String type, String option) {
        this.dbManager = dbManager;
        this.activity = activity;
        this.type = type;
        this.option = option;
    }

    @Override
    public int getItemCount() {
        return dbManager.getSettingSize(type) + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetail;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.simple_tv_title);
            tvDetail = (TextView) itemView.findViewById(R.id.simple_tv_detail);
            layout = (LinearLayout) itemView.findViewById(R.id.simple_layout);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        dataSet();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvTitle.setText(itemsCS[position].toString());
        if (type.equals("date")) {
            holder.tvDetail.setVisibility(View.VISIBLE);
            holder.tvDetail.setText(itemsDetailCS[position].toString());
            if (position == itemsCS.length - 1) { //직접생성의 경우
                if (selectedPosition == itemsCS.length - 1) {
                    holder.tvDetail.setText(dbManager.DATA_DATE);
                } else {
                    holder.tvDetail.setVisibility(View.GONE);
                }
            }
        } else if (type.equals("category")) {
            holder.tvDetail.setVisibility(View.GONE);
        }

        if (selectedPosition == position) {
            holder.layout.setBackgroundResource(R.drawable.xml_border_line_selected);
        } else {
            holder.layout.setBackgroundResource(R.drawable.xml_border_line);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (type) {
                    case ("date"):
                        if (!itemsCS[position].toString().equals(activity.getString(R.string.new_date))) {
                            dbManager.DATA_DATE = itemsDetailCS[position].toString();
                            ((AddguideActivity) activity).setData(3);
                            selectedPosition = position;
                            notifyDataSetChanged();
                        } else {
                            SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
                            SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
                            SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
                            DatePickerDialog dpDialog = new DatePickerDialog(activity, listener, Integer.parseInt(CurYearFormat.format(date).toString()), Integer.parseInt(CurMonthFormat.format(date).toString()) - 1, Integer.parseInt(CurDayFormat.format(date).toString()));
                            dpDialog.show();
                        }
                        break;
                    case ("category"):
                        switch (option) {
                            case ("forSort"):
                                if (!itemsCS[position].toString().equals(activity.getString(R.string.all))) {
                                    dbManager.DATA_SORTTYPE = "CATEGORY";
                                    dbManager.DATA_SORTTYPEEQUAL = itemsCS[position].toString();
                                } else {
                                    dbManager.DATA_SORTTYPE = "DEFAULT";
                                    dbManager.DATA_SORTTYPEEQUAL = "";
                                }
                                selectedPosition = position;
                                ((ListActivity) activity).refresh();
                                notifyDataSetChanged();
                                break;
                            case ("forEdit"): //TODO
                                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                                dialog.setMessage("어떤 행동을 하시겠습니까?");
                                dialog.setCancelable(true);
                                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
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
                                break;
                            default:
                                if (!itemsCS[position].toString().equals(activity.getString(R.string.new_category))) {
                                    dbManager.DATA_CATEGORY = itemsCS[position].toString();
                                    ((AddguideActivity) activity).setData(2);
                                } else {
                                    DialogAdd();
                                }
                                break;
                        }
                        notifyDataSetChanged();
                }
            }
        });
    }

    private void dataSet() {
        items = dbManager.getSettingList(type); // date, category 일 때 items에 추가.
        switch (type) {
            case ("date"):
                items.add(activity.getString(R.string.new_date));
                long now = System.currentTimeMillis();
                date = new Date(now);
                CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                itemsDetail = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    cal = Calendar.getInstance();
                    switch (i) {
                        case (0):
                            cal.add(Calendar.DATE, 1);
                            break;
                        case (1):
                            cal.add(Calendar.DATE, 7);
                            break;
                        case (2):
                            cal.add(Calendar.MONTH, 1);
                            break;
                        case (3):
                            cal.add(Calendar.MONTH, 6);
                            break;
                        case (4):
                            itemsDetail.add(activity.getString(R.string.date_forever));
                            break;
                    }
                    if (i != 4) {
                        itemsDetail.add(CurDateFormat.format(cal.getTime())+"");
                    }
                }
                itemsDetail.add("");
                itemsDetailCS = itemsDetail.toArray(new String[itemsDetail.size()]);
                break;
            case ("category"):
                items.add(activity.getString(R.string.new_category));
                break;
        }
        if (option.equals("forSort")) {
            items.add(0, activity.getString(R.string.all));
        }
        itemsCS = items.toArray(new String[items.size()]);
        setSelectedPosition();
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
            dbManager.DATA_DATE = sb.toString();
            selectedPosition = itemsCS.length - 1;
            notifyDataSetChanged();
            ((AddguideActivity) activity).setData(3);
        }
    };

    private void DialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("추가할 카테고리의 이름을 입력해주세요.");
        final EditText title = new EditText(activity);
        builder.setView(title);
        builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = title.getText().toString();
                if (!dbManager.isAlreadyResCategory(selected)) {
                    dbManager.addSetting("category", selected);
                } else {
                    Toast.makeText(activity, "이미 존재하는 카테고리입니다. 해당 카테고리로 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
                dbManager.DATA_CATEGORY = selected;
                ((AddguideActivity) activity).setData(2);
                dataSet();
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Dialog
        AlertDialog dialog;
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private boolean setSelectedPosition() {
        int i = 0;
        switch (type) {
            case ("date"):
                for (i = 0; i < itemsDetailCS.length - 1; i++) {
                    if (itemsDetailCS[i].equals(dbManager.DATA_DATE)) {
                        selectedPosition = i;
                        return true;
                    }
                }
                break;
            case ("category"):
                if (option.equals("forSort")) {
                    selectedPosition = 0;
                    for (i = 1; i < itemsCS.length - 1; i++) {
                        if (itemsCS[i].equals(dbManager.DATA_SORTTYPEEQUAL)) {
                            selectedPosition = i;
                            return true;
                        }
                    }
                } else {
                    for (i = 0; i < itemsCS.length - 1; i++) {
                        if (itemsCS[i].equals(dbManager.DATA_CATEGORY)) {
                            selectedPosition = i;
                            return true;
                        }
                    }
                }
                break;
        }
        return false;
    }
}