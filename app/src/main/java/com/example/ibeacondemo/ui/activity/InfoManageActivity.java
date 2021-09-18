package com.example.ibeacondemo.ui.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.ui.fragment.ChangePasswordFragment;
import com.example.ibeacondemo.ui.fragment.InfoEditorFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

/**
 * 用户信息管理
 */
public class InfoManageActivity extends AppActivity {
    private ViewPager2 mViewPager2;
    public static Button mInfo_edit, mChange_pws;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_manage;
    }

    @Override
    protected void initView() {
        mViewPager2 = findViewById(R.id.mainViewpager);
        mInfo_edit = findViewById(R.id.btn_info_edit);
        mChange_pws = findViewById(R.id.btn_change_pws);

        setOnClickListener(R.id.btn_info_edit, R.id.btn_change_pws);

    }

    @Override
    protected void initData() {
        initFragment();
    }

    private void initFragment() {
        final List<Fragment> list = new ArrayList<>();
        list.add(InfoEditorFragment.newInstance());
        list.add(ChangePasswordFragment.newInstance());


        //设置预加载的Fragment页面数量，可防止流式布局StaggeredGrid数组越界错误。
        mViewPager2.setOffscreenPageLimit(list.size() - 1);
        //设置是否可滑动
//        mViewPager2.setUserInputEnabled(false);
        FragmentStateAdapter adapter = new FragmentStateAdapter(InfoManageActivity.this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return list.get(position);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        };
        mViewPager2.setAdapter(adapter);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_info_edit) {
            //用户信息编辑
            mViewPager2.setCurrentItem(0, false);
            mInfo_edit.setTextColor(Color.rgb(2, 87, 160));
            mChange_pws.setTextColor(Color.rgb(119, 119, 119));

        } else if (viewId == R.id.btn_change_pws) {
            //密码修改
            mViewPager2.setCurrentItem(1, false);
            mInfo_edit.setTextColor(Color.rgb(119, 119, 119));
            mChange_pws.setTextColor(Color.rgb(2, 87, 160));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager2.setAdapter(null);
    }
}