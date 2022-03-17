package com.example.ibeacondemo.ui.activity;


import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.app.AppActivity;

/**
 * desc   : 关于界面
 */
public final class AboutActivity extends AppActivity {
    private TextView tv_about;

    @Override
    protected int getLayoutId() {
        return R.layout.about_activity;
    }

    @Override
    protected void initView() {
        tv_about = findViewById(R.id.tv_about);
        tv_about.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    @Override
    protected void initData() {
    }
}