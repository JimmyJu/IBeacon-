package com.example.ibeacondemo.ui.activity;


import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.api.GetCompanyListApi;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.data.GetCompanyListBean;
import com.example.ibeacondemo.manager.InputTextManager;
import com.example.ibeacondemo.ui.dialog.SelectDialog;
import com.example.ibeacondemo.util.SPUtils;
import com.example.ibeacondemo.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.view.RxToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * desc   : 报修工单查询
 */
public final class RepairWorkOrderQueryActivity extends AppActivity {
    private TextView tv_title_company_name;
    private TextView mCompanyName, mEquipmentName, mDeviceModel, mRepairNum;
    private Button mInquire;

    //用户ID
    private String mUserInfoID;
    //公司ID
    private String mCompanyID;

    @Override
    protected int getLayoutId() {
        return R.layout.repair_workorder_query_activity;
    }

    @Override
    protected void initView() {
        tv_title_company_name = findViewById(R.id.tv_title_company_name);

        mCompanyName = findViewById(R.id.tv_company_name);
        mEquipmentName = findViewById(R.id.equipment_name);
        mDeviceModel = findViewById(R.id.tv_device_model);
        mRepairNum = findViewById(R.id.tv_repair_num);
        mInquire = findViewById(R.id.btn_inquire);

        setOnClickListener(R.id.tv_company_name, R.id.btn_inquire);

        InputTextManager.with(this)
                .addView(mCompanyName)
                .setMain(mInquire)
                .build();
    }

    @Override
    protected void initData() {
        //获取用户ID
        mUserInfoID = (String) SPUtils.get(this, "userInfoID", "");

        String titleName = "<font color='#FF0000'>* </font>" + "<font color='#000000'>公司名称</font>" + "<font color='#000000'>:</font>";
        tv_title_company_name.setText(Html.fromHtml(titleName));
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_company_name) {
            //公司名称
            //获取公司名称
            EasyHttp.post(this)
                    .api(new GetCompanyListApi()
                            .setUserInfoID(mUserInfoID))
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                String replaceJson = result.toString().replace("\\", "");
                                if (Util.getJSONType(replaceJson)) {
                                    Gson gson = new Gson();
                                    List<GetCompanyListBean> getCompanyListBeans = gson.fromJson(replaceJson, new TypeToken<List<GetCompanyListBean>>() {
                                    }.getType());

                                    ArrayList dataSet = new ArrayList();
                                    Map<String, String> map = new HashMap<String, String>();
                                    for (GetCompanyListBean bean : getCompanyListBeans) {
                                        dataSet.add(bean.getCompanyName());
                                        map.put(bean.getCompanyName(), bean.getCompanyID());
                                    }

                                    new SelectDialog.Builder(RepairWorkOrderQueryActivity.this)
                                            .setTitle("选择公司")
                                            .setSingleSelect()
                                            .setList(dataSet)
                                            .setSelect(0)
                                            .setListener(new SelectDialog.OnListener() {
                                                @Override
                                                public void onSelected(BaseDialog dialog, HashMap data) {
                                                    String companyName = "";
                                                    for (Object value : data.values()) {
                                                        mCompanyID = map.get(value);
                                                        companyName = (String) value;
                                                    }
                                                    mCompanyName.setText(companyName);
                                                }
                                            }).show();
                                } else {
                                    ToastUtils.show(replaceJson);
                                }
                            } else {
                                RxToast.info("服务异常");
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            RxToast.error("网络服务异常");
                        }
                    });

        } else if (viewId == R.id.btn_inquire) {
            //查询

        }
    }
}