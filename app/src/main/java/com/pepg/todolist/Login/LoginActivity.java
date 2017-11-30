package com.pepg.todolist.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.pepg.todolist.ListActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivProfile;
    TextView tvName, tvEmail;
    Button btnLogout;
    ImageButton btnKakao;
    FrameLayout layoutKakao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivProfile = (ImageView) findViewById(R.id.login_iv_profile);
        tvName = (TextView) findViewById(R.id.login_tv_name);
        tvEmail = (TextView) findViewById(R.id.login_tv_email);
        btnLogout = (Button) findViewById(R.id.login_btn_logout);
        btnKakao = (ImageButton) findViewById(R.id.login_btn_kakao);
        layoutKakao = (FrameLayout) findViewById(R.id.login_layout_kakao);

        try {
            tvName.setText(Manager.userProfile.getNickname());
            tvEmail.setText(Manager.userProfile.getEmail()+"");
            Glide.with(this).load(Manager.userProfile.getProfileImagePath())
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(ivProfile);
        } catch (Exception e) {
            tvName.setText("로그인이 필요한 서비스입니다.");
            tvEmail.setText("");
        }

        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            btnLogout.setVisibility(View.VISIBLE);
            layoutKakao.setVisibility(View.GONE);
        } else {
            btnLogout.setVisibility(View.GONE);
            layoutKakao.setVisibility(View.VISIBLE);
        }

        btnLogout.setOnClickListener(this);
        btnKakao.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case(R.id.login_btn_kakao):
                Intent intent = new Intent(LoginActivity.this, KakaoLoginActivity.class);
                startActivity(intent);
                break;
            case(R.id.login_btn_logout):
                onClickLogout();
                break;
        }
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Manager.userProfile = null;
                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "카카오 로그아웃 완료.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
