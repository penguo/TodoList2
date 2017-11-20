package com.pepg.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;

/**
 * Created by pengu on 2017-09-19.
 */

public class UpdateSemi implements View.OnClickListener {

    SemiListRcvAdapter semiRcvAdapter;
    DBManager dbManager;
    Context context;
    String weightNum;
    ImageView weight;
    Activity activity;
    int parentId;

    public UpdateSemi(SemiListRcvAdapter semiRcvAdapter, Activity activity, DBManager DBManager) {
        this.semiRcvAdapter = semiRcvAdapter;
        this.dbManager = DBManager;
        this.activity = activity;
    }

    public void updateSemi(final int id, Context cont, final boolean isNewSemi) {
        context = cont;

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.semi_update, null);
        final EditText title = (EditText) updateLayout.findViewById(R.id.semiup_et_title);
        final TextView date = (TextView) updateLayout.findViewById(R.id.semiup_tv_date);
        weight = (ImageView) updateLayout.findViewById(R.id.semiup_iv_weight);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;

        if (isNewSemi) {
            parentId = id;
            title.setText("");
            weightNum = "1";
            date.setText("Empty Data");
            weight.setImageResource(Manager.getDrawableResId("letter" + weightNum));
            builder.setTitle("세부todo 추가");
        } else {
            dbManager.getSemiValue("_id", id);
            title.setText(dbManager.DATA_semi_TITLE);
            weightNum = "" + dbManager.DATA_semi_WEIGHT;
            date.setText(dbManager.DATA_semi_DATE);
            weight.setImageResource(Manager.getDrawableResId("letter" + weightNum));
            builder.setTitle("세부todo 수정");
        }
        builder.setView(updateLayout);

        weight.setOnClickListener(this);
        date.setOnClickListener(this);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNewSemi) {
                    dbManager.semiInsert(parentId, title.getText().toString(), Integer.parseInt(weightNum), date.getText().toString());
                } else {
                    dbManager.semiUpdate(id, title.getText().toString(), Integer.parseInt(weightNum), " ~ 까지");
                }
                switch (activity.getLocalClassName()) {
                    case ("UpdateActivity"):
                        ((UpdateActivity) activity).onRefresh();
                        break;
                    case ("DetailItemActivity"):
                        ((DetailItemActivity) activity).refreshSemiRcv();
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
        switch (v.getId()) {
            case (R.id.semiup_iv_weight):
                setWeight();
                break;
        }
    }

    private void setWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("우선순위를 적어주세요. (0~9)");
        final EditText et = new EditText(context);
        et.setText(weightNum);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(et);
        builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weightNum = et.getText().toString();
                weight.setImageResource(Manager.getDrawableResId("letter" + weightNum));
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
