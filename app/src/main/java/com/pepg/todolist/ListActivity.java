package com.pepg.todolist;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.pepg.todolist.Adapter.ListRcvAdapter;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Login.KakaoLoginActivity;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ListActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    TextView tvToolbarTitle, tvNavHeaderName, tvNavHeaderEmail;
    RecyclerView rcvTodo;
    ListRcvAdapter listRcvAdapter;
    FloatingActionButton fabAdd;
    Animation viewSlideOut, viewSlideIn;
    ImageView ivDropdown, ivProfile;
    DividerItemDecoration dividerItemDecoration;
    Toolbar toolbar;
    SwipeRefreshLayout swipe;
    Spinner spinnerSort;
    List<String> items;
    ImageButton btnSetting, btnSort;
    boolean isSortView;
    DrawerLayout drawer;

    final DBManager dbManager = new DBManager(this, "todolist2.db", null, MainActivity.DBVERSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Manager.setSetting(this);

        toolbar = (Toolbar) findViewById(R.id.listA_toolbar);
        setSupportActionBar(toolbar);

        spinnerSort = (Spinner) findViewById(R.id.listA_spinner);
        fabAdd = (FloatingActionButton) findViewById(R.id.listA_fab);
        tvToolbarTitle = (TextView) findViewById(R.id.listA_tv_toolbar_title);
        ivDropdown = (ImageView) findViewById(R.id.listA_iv_dropdown);
        btnSort = (ImageButton) findViewById(R.id.listA_btn_sort);
        btnSetting = (ImageButton) findViewById(R.id.listA_btn_setting);

        swipe = (SwipeRefreshLayout) findViewById(R.id.listA_swipe);
        swipe.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light
        );
        swipe.setOnRefreshListener(this);
        viewSlideOut = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_up);
        viewSlideIn = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_up);

        drawer = (DrawerLayout) findViewById(R.id.listA_drawerlayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View viewNavHeader = navigationView.getHeaderView(0);

        tvNavHeaderName = (TextView) viewNavHeader.findViewById(R.id.navheader_tv_name);
        tvNavHeaderEmail = (TextView) viewNavHeader.findViewById(R.id.navheader_tv_email);
        ivProfile = (ImageView) viewNavHeader.findViewById(R.id.navheader_iv_profile);

        rcvTodo = (RecyclerView) findViewById(R.id.listA_rcv_todo);
        LinearLayoutManager rcvLayoutManager = new LinearLayoutManager(this);
        rcvTodo.setLayoutManager(rcvLayoutManager);
        dividerItemDecoration = new DividerItemDecoration(this, rcvLayoutManager.getOrientation());
        rcvTodo.addItemDecoration(dividerItemDecoration);

        listRcvAdapter = new ListRcvAdapter(dbManager, this);
        rcvTodo.setAdapter(listRcvAdapter);
        fabAdd.setOnClickListener(this);
        tvToolbarTitle.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        dataSet();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case (R.id.listA_fab):
                dbManager.deleteDummyData_Semi();
                dbManager.deleteDummyData_Alarm();
                dbManager.resetPublicData();
                DBManager.DATA_DATE = getString(R.string.unregistered);
                DBManager.DATA_CREATEDATE = getString(R.string.unregistered);
                DBManager.DATA_CATEGORY = getString(R.string.unregistered);
                intent = new Intent(ListActivity.this, AddguideActivity.class);
//                intent.putExtra("_id", 0);
//                List<Pair<View, String>> pairs = new ArrayList<>();
//                pairs.add(Pair.create((View) fabAdd, "list_fab"));
//                Bundle options = ActivityOptions.makeSceneTransitionAnimation(this,
//                        pairs.toArray(new Pair[pairs.size()])).toBundle();
//                startActivity(intent, options);

                intent.putExtra("_id", 0);
                startActivityForResult(intent, Manager.RC_LIST_TO_ADDGUIDE);
                break;
            case (R.id.listA_tv_toolbar_title):
                if (isSortView) {
                    spinnerSort.performClick();
                }
                break;
            case (R.id.listA_btn_setting):
                intent = new Intent(ListActivity.this, SettingsActivity.class);
                startActivityForResult(intent, Manager.RC_LIST_TO_SETTINGS);
                break;
            case (R.id.listA_btn_sort):
                drawer.openDrawer(Gravity.LEFT);
                break;
        }
    }

    private void dataSet() {
        ivDropdown.setVisibility(View.GONE);
        items = dbManager.getSettingList("category"); // date, category 일 때 items에 추가.
        items.add(0, getString(R.string.all));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_spinner_toolbar,
                items);
        spinnerSort.setAdapter(spinnerAdapter);
        spinnerSort.setSelection(0);
        spinnerSort.setOnItemSelectedListener(this);
        try {
            tvNavHeaderName.setText(Manager.userProfile.getNickname());
            if(Manager.userProfile.getEmail()!=null){
                tvNavHeaderEmail.setText(Manager.userProfile.getEmail()+"");
            }else{
                tvNavHeaderEmail.setVisibility(View.GONE);
            }
            Glide.with(this).load(Manager.userProfile.getProfileImagePath())
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(ivProfile);
        } catch (Exception e) {
            tvNavHeaderName.setText("로그인이 필요한 서비스입니다.");
            tvNavHeaderEmail.setText("");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Manager.RC_LIST_TO_UPDATE) {
            if (resultCode == RESULT_OK) {
                dbManager.DATA_SORTTYPE = "DEFAULT";
                onRefresh();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_INFO) {
            onRefresh();
        }
        if (requestCode == Manager.RC_LIST_TO_ADDGUIDE) {
            if (resultCode == RESULT_OK) {
                dbManager.DATA_SORTTYPE = "DEFAULT";
                onRefresh();
            }
        }
        if (requestCode == Manager.RC_LIST_TO_SETTINGS) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        listRcvAdapter.refresh();
        swipe.setRefreshing(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (!items.get(position).equals(getString(R.string.all))) {
            DBManager.DATA_SORTTYPE = "CATEGORY";
            DBManager.DATA_SORTTYPEEQUAL = items.get(position);
            tvToolbarTitle.setText(items.get(position));
            ivDropdown.setVisibility(View.VISIBLE);
            isSortView = true;
        } else {
            resetSort();
        }
        onRefresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void resetSort() {
        DBManager.DATA_SORTTYPE = "DEFAULT";
        DBManager.DATA_SORTTYPE2 = "";
        DBManager.DATA_SORTTYPEEQUAL = "";
        tvToolbarTitle.setText("TodoList");
        ivDropdown.setVisibility(View.GONE);
        isSortView = false;
        onRefresh();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isSortView) {
                resetSort();
            } else {
                finish();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.nav_sort):
                CategorySelectOption();
                break;
            case (R.id.nav_settings):
                intent = new Intent(ListActivity.this, SettingsActivity.class);
                startActivityForResult(intent, Manager.RC_LIST_TO_SETTINGS);
                break;
            case(R.id.nav_sortschedule):
                DBManager.DATA_SORTTYPE2 = "SCHEDULE";
                tvToolbarTitle.setText("일정");
                isSortView = true;
                onRefresh();
                break;
            case(R.id.nav_sorttodo):
                DBManager.DATA_SORTTYPE2 = "TODO";
                tvToolbarTitle.setText("할 일");
                isSortView = true;
                onRefresh();
                break;
            case(R.id.nav_clear):
                dbManager.reset();
                onRefresh();
                break;
            case(R.id.nav_analysis):
                Toast.makeText(this, "준비 중 입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void CategorySelectOption() {
        final List<String> Items = dbManager.getSettingList("category");
        final CharSequence[] items = Items.toArray(new String[Items.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBManager.DATA_SORTTYPE = "CATEGORY";
                DBManager.DATA_SORTTYPEEQUAL = items[which].toString();
                tvToolbarTitle.setText(items[which] + "");
                ivDropdown.setVisibility(View.VISIBLE);
                isSortView = true;
                onRefresh();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
