package com.example.ibeacondemo.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.manager.ActivityManager;
import com.example.ibeacondemo.ui.dialog.MessageDialog;
import com.example.ibeacondemo.util.DoubleClickHelper;
import com.example.ibeacondemo.util.LiveDataBus;
import com.example.ibeacondemo.util.SPUtils;
import com.hjq.base.BaseDialog;
import com.tamsiree.rxkit.view.RxToast;

import androidx.lifecycle.Observer;

/**
 * 运维主页面
 */
public class OpsMainActivity extends AppActivity {

    private TextView mRealName, mPromptMesg;

    @Override
    protected int getLayoutId() {
        return R.layout.ops_activity;
    }

    @Override
    protected void initView() {
        mRealName = findViewById(R.id.tv_userName);
        mPromptMesg = findViewById(R.id.prompt_message);
        mPromptMesg.setSelected(true);

        setOnClickListener(R.id.btn_xxgl, R.id.btn_sblb,
                R.id.btn_wbgd, R.id.btn_wbgdcx,
                R.id.btn_bxgd, R.id.btn_bxgdcx,
                R.id.btn_bxlr, R.id.btn_signout);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        //获取用户姓名
        String realName = (String) SPUtils.get(OpsMainActivity.this, "realName", "");
        mRealName.setText(getString(R.string.hint) + realName);

        //接收数据
        liveDataBus();

        //获取工单数目
        getTheNumberOfWorkOrders();

    }

    private void getTheNumberOfWorkOrders() {
        mPromptMesg.setText("您有0条故障信息需处理，详情请戳“我的报修工单”按钮");
    }

    private void liveDataBus() {
        LiveDataBus.get().with("userName", String.class).observe(this, new Observer<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(String name) {
                mRealName.setText(getString(R.string.hint) + name);
            }
        });
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_xxgl) {
            //用户信息管理
            startActivity(InfoManageActivity.class);

        } else if (viewId == R.id.btn_sblb) {
            //产品设备列表
            startActivity(ProductDevicesListActivity.class);

        } else if (viewId == R.id.btn_wbgd) {
            //我的维保工单
            startActivity(MaintenanceWorkOrderActivity.class);

        } else if (viewId == R.id.btn_wbgdcx) {
            //维保工单查询
            startActivity(MaintenanceWorkOrderQueryActivity.class);

        } else if (viewId == R.id.btn_bxgd) {
            //报修工单
            startActivity(RepairWorkOrderActivity.class);

        } else if (viewId == R.id.btn_bxgdcx) {
            //报修工单查询
            startActivity(RepairWorkOrderQueryActivity.class);

        } else if (viewId == R.id.btn_bxlr) {
            //报修录入
            startActivity(RepairEntryActivity.class);

        } else if (viewId == R.id.btn_signout) {
            //退出登录
            new MessageDialog.Builder(getActivity())
                    // 标题可以不用填写
                    .setTitle("温馨提示")
                    // 内容必须要填写
                    .setMessage("确定要退出账号吗？")
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
//                    .setAutoDismiss(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            startActivity(LoginActivity.class);
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                            //清空对应的realName值
                            SPUtils.remove(OpsMainActivity.this, "realName");
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
//                            toast("取消了");
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            RxToast.info("再按一次退出程序");
            return;
        }
        postDelayed(() -> {
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities();
            //清空对应的realName值
            SPUtils.remove(OpsMainActivity.this, "realName");
//            finish();
            // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
            // System.exit(0);
        }, 300);
    }
}
