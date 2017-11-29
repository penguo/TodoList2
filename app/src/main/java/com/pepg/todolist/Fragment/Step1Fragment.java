package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

import java.util.List;

public class Step1Fragment extends Fragment {

    DBManager dbManager;
    Activity activity;
    TextView tvTitle, tvCategory;
    LinearLayout layoutTitle, layoutCategory;
    int id;

    public Step1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step1, container, false);
        id = DBManager.DATA_id;
        dbManager = new DBManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);
        activity = getActivity();
        tvTitle = (TextView) layout.findViewById(R.id.fs1_tv_title);
        layoutTitle = (LinearLayout) layout.findViewById(R.id.fs1_layout_title);
        layoutCategory = (LinearLayout) layout.findViewById(R.id.fs1_layout_category);
        tvCategory = (TextView) layout.findViewById(R.id.fs1_tv_category);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle.setText(dbManager.DATA_TITLE);
        tvCategory.setText(DBManager.DATA_CATEGORY);
        layoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Manager.callSetTitleLayout(activity, dbManager, id, tvTitle);
            }
        });
        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSelectOption();
            }
        });
    }

    private void DialogSelectOption() {
        final List<String> Items = dbManager.getSettingList("category");
        Items.add(getString(R.string.new_category));
        final CharSequence[] items = Items.toArray(new String[Items.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!items[which].toString().equals(getString(R.string.new_category))) {
                    DBManager.DATA_CATEGORY = items[which].toString();
                    tvCategory.setText(DBManager.DATA_CATEGORY);
                } else {
                    DialogAdd();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

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
                    if (selected.equals("")) {
                        Toast.makeText(activity, "분류 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        selected = activity.getString(R.string.unregistered);
                    } else {
                        dbManager.addSetting("category", selected);
                    }
                } else {
                    Toast.makeText(activity, "이미 존재하는 카테고리입니다. 해당 카테고리로 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
                DBManager.DATA_CATEGORY = selected;
                tvCategory.setText(DBManager.DATA_CATEGORY);
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

}