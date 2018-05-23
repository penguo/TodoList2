package com.pepg.todolist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pepg.todolist.AddguideActivity;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.R;

import java.util.List;

import static com.pepg.todolist.AddguideActivity.DATA_STATIC;

public class Step1Fragment extends Fragment {

    DBManager dbManager;
    Activity activity;
    TextView tvCategory;
    LinearLayout layoutBody, layoutTitle, layoutCategory, layoutCategory2;
    EditText etTitle, etCategory;

    public Step1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_step1, container, false);
        dbManager = new DBManager(this.getContext(), "todolist2.db", null, MainActivity.DBVERSION);
        activity = getActivity();
        layoutBody = (LinearLayout) layout.findViewById(R.id.fs1_layout);
        layoutTitle = (LinearLayout) layout.findViewById(R.id.fs1_layout_title);
        layoutCategory = (LinearLayout) layout.findViewById(R.id.fs1_layout_category);
        layoutCategory2 = (LinearLayout) layout.findViewById(R.id.fs1_layout_category2);
        tvCategory = (TextView) layout.findViewById(R.id.fs1_tv_category);
        etTitle = (EditText) layout.findViewById(R.id.fs1_et_title);
        etCategory = (EditText) layout.findViewById(R.id.fs1_et_category2);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle.setText(DATA_STATIC.getTitle());
        etTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    ((AddguideActivity) getActivity()).setData(1);
                    return true;
                }
                return false;
            }
        });
        tvCategory.setText(DATA_STATIC.getCategory());
        layoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTitle.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etTitle, 0);
            }
        });
        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSelectOption();
            }
        });
        layoutCategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTitle.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etTitle, 0);
            }
        });
        layoutBody.requestFocus();
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
                    DATA_STATIC.setCategory(items[which].toString());
                    tvCategory.setText(DATA_STATIC.getCategory());
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
                DATA_STATIC.setCategory(selected);
                tvCategory.setText(DATA_STATIC.getCategory());
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

    public String getEtTitle() {
        return etTitle.getText().toString();
    }
}