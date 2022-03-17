package com.example.ibeacondemo.ui.activity;


import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.other.AppConfig;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.tencent.bugly.beta.Beta;

import androidx.annotation.Nullable;

/**
 * desc   : 菜单首页（主）
 */
public final class FrontPageActivity extends AppActivity {
    private TitleBar mTitleBar;
    private TextView mVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.front_page_activity;
    }

    @Override
    protected void initView() {
        mVersion = findViewById(R.id.tv_login_ver);
        setOnClickListener(R.id.btn_visitor_system, R.id.btn_smart_garage,
                R.id.btn_conference_system, R.id.btn_om_system, R.id.rl_about, R.id.iv_update);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mTitleBar.setRightTitle("");
        mTitleBar.setRightIcon(R.mipmap.icon_demo);
        mVersion.setText("Version" + AppConfig.getVersionName());
        
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {

            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
    }

    @Nullable
    @Override
    public TitleBar getTitleBar() {
        return mTitleBar = super.getTitleBar();
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_visitor_system) {
            startActivity(GuestLoginActivity.class);
        } else if (viewId == R.id.btn_smart_garage) {
            // TODO:
        } else if (viewId == R.id.btn_conference_system) {
            // TODO:
        } else if (viewId == R.id.btn_om_system) {

        } else if (viewId == R.id.rl_about) {
            startActivity(AboutActivity.class);
        } else if (viewId == R.id.iv_update) {
            //检查更新
            Beta.checkUpgrade();
        }
    }
}