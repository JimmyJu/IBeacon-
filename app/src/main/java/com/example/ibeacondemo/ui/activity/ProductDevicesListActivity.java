package com.example.ibeacondemo.ui.activity;


import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.api.GetCompanyListApi;
import com.example.ibeacondemo.api.GetDevicesInfoListApi;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.data.GetCompanyListBean;
import com.example.ibeacondemo.data.GetDeviceInfoListBean;
import com.example.ibeacondemo.data.GetDeviceTypeListBean;
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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * desc   : 产品设备列表
 */
public final class ProductDevicesListActivity extends AppActivity {

    private TextView tv_title_company_name, tv_title_current_state, tv_title_equipment_type;

    private TextView mCompanyName, mCurrentState, mEquipmentType;
    private EditText mEquipmentName;
    private Button mInquire;
    //用户ID
    private String mUserInfoID;
    //公司ID
    private String mCompanyID;
    //设备类型ID
    private String mTypeId;

    @Override
    protected int getLayoutId() {
        return R.layout.product_deviceslist_activity;
    }

    @Override
    protected void initView() {
        tv_title_company_name = findViewById(R.id.tv_title_company_name);
        tv_title_current_state = findViewById(R.id.tv_title_current_state);
        tv_title_equipment_type = findViewById(R.id.tv_title_equipment_type);

        mCompanyName = findViewById(R.id.tv_company_name);
        mCurrentState = findViewById(R.id.tv_current_state);
        mEquipmentType = findViewById(R.id.tv_equipment_type);
        mEquipmentName = findViewById(R.id.equipment_name);
        mInquire = findViewById(R.id.btn_inquire);

        setOnClickListener(R.id.tv_company_name, R.id.tv_current_state, R.id.tv_equipment_type, R.id.btn_inquire);
        InputTextManager.with(this)
                .addView(mCompanyName)
                .addView(mCurrentState)
                .addView(mEquipmentType)
                .setMain(mInquire)
                .build();

    }

    @Override
    protected void initData() {
        //获取用户ID
        mUserInfoID = (String) SPUtils.get(this, "userInfoID", "");

        String titleName = "<font color='#FF0000'>* </font>" + "<font color='#000000'>公司名称</font>" + "<font color='#000000'>:</font>";
        String titleState = "<font color='#FF0000'>* </font>" + "<font color='#000000'>当前状态</font>" + "<font color='#000000'>:</font>";
        String titleEquipment = "<font color='#FF0000'>* </font>" + "<font color='#000000'>设备类型</font>" + "<font color='#000000'>:</font>";
        tv_title_company_name.setText(Html.fromHtml(titleName));
        tv_title_current_state.setText(Html.fromHtml(titleState));
        tv_title_equipment_type.setText(Html.fromHtml(titleEquipment));
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_company_name) {
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

                                    new SelectDialog.Builder(ProductDevicesListActivity.this)
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

        } else if (viewId == R.id.tv_current_state) {
            //当前状态
            new SelectDialog.Builder(this)
                    .setTitle("选择设备状态")
                    .setList("全部", "正常", "已报修")
                    .setSingleSelect()
                    .setSelect(0)
                    .setListener(new SelectDialog.OnListener() {
                        @Override
                        public void onSelected(BaseDialog dialog, HashMap data) {
                            String state = "";
                            for (Object value : data.values()) {
                                state = (String) value;
                            }
                            mCurrentState.setText(state);
                        }
                    }).show();

        } else if (viewId == R.id.tv_equipment_type) {
            //设备类型
            EasyHttp.post(this)
                    .api("Repair/GetDeviceTypeList")
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                String replaceJson = result.toString().replace("\\", "");
                                if (Util.getJSONType(replaceJson)) {
                                    Gson gson = new Gson();
                                    List<GetDeviceTypeListBean> getDeviceTypeListBeans = gson.fromJson(replaceJson, new TypeToken<List<GetDeviceTypeListBean>>() {
                                    }.getType());

                                    ArrayList dataSet = new ArrayList();
                                    Map<String, String> map = new HashMap<String, String>();
                                    for (GetDeviceTypeListBean bean : getDeviceTypeListBeans) {
                                        dataSet.add(bean.getTypeName());
                                        map.put(bean.getTypeName(), bean.getID());
                                    }

                                    new SelectDialog.Builder(ProductDevicesListActivity.this)
                                            .setTitle("请选择设备类型")
                                            .setList(dataSet)
                                            .setSingleSelect()
                                            .setSelect(0)
                                            .setListener(new SelectDialog.OnListener() {
                                                @Override
                                                public void onSelected(BaseDialog dialog, HashMap data) {
                                                    String type = "";
                                                    for (Object value : data.values()) {
                                                        mTypeId = map.get(value);
                                                        type = (String) value;
                                                    }
                                                    mEquipmentType.setText(type);
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
            EasyHttp.post(this)
                    .api(new GetDevicesInfoListApi()
                            .setUserInfoID(mUserInfoID)
                            .setCompanyID(mCompanyID)
                            .setStatus(mCurrentState.getText().toString())
                            .setDeviceTypeID(mTypeId)
                            .setDeviceName(mEquipmentName.getText().toString()))
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                if (StringUtils.isBlank(result.toString())) {
                                    ToastUtils.show("暂无产品设备信息");
                                    return;
                                }
                                String replaceJson = result.toString().replace("\\", "");
                                if (Util.getJSONType(replaceJson)) {
                                    Gson gson = new Gson();
                                    List<GetDeviceInfoListBean> getDeviceInfoListBeans = gson.fromJson(replaceJson, new TypeToken<List<GetDeviceInfoListBean>>() {
                                    }.getType());

                                    // TODO: 列表数据展示
                                    ToastUtils.show(getDeviceInfoListBeans.toString());

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
        }

    }
}