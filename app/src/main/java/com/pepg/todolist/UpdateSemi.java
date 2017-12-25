package com.pepg.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.DataBase.DataSemi;

/**
 * Created by pengu on 2017-09-19.
 */

public class UpdateSemi implements View.OnClickListener {

    SemiListRcvAdapter semiRcvAdapter;
    DBManager dbManager;
    Context context;
    Activity activity;

    public UpdateSemi(SemiListRcvAdapter semiRcvAdapter, Activity activity, DBManager DBManager) {
        this.semiRcvAdapter = semiRcvAdapter;
        this.dbManager = DBManager;
        this.activity = activity;
    }

    public void updateSemi(Context cont, final DataSemi data) {
        context = cont;

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.update_semi, null);
        final EditText title = (EditText) updateLayout.findViewById(R.id.semiup_et_title);
        final EditText memo = (EditText) updateLayout.findViewById(R.id.semiup_et_memo);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;

        title.setText(data.getTitle());
        memo.setText(data.getMemo());

        if (data.isNew()) {
            builder.setTitle("세부todo 추가");
        } else {
            builder.setTitle("세부todo 수정");
        }
        builder.setView(updateLayout);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (data.isNew()) {
                    dbManager.insertSemi(data);
                } else {
                    dbManager.updateSemi(data);
                }
                switch (activity.getLocalClassName()) {
                    case ("InfoActivity"):
                        ((InfoActivity) activity).refreshRcv();
                        break;
                    default:
                        semiRcvAdapter.notifyDataSetChanged();
                }
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
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
    }
}
