package com.example.ibeacondemo.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.api.UserInfoSubmitApi;
import com.example.ibeacondemo.api.getUserInfoApi;
import com.example.ibeacondemo.app.AppFragment;
import com.example.ibeacondemo.data.UserInfoBean;
import com.example.ibeacondemo.manager.InputTextManager;
import com.example.ibeacondemo.ui.activity.InfoManageActivity;
import com.example.ibeacondemo.ui.activity.LoginActivity;
import com.example.ibeacondemo.ui.dialog.HintDialog;
import com.example.ibeacondemo.ui.dialog.MessageDialog;
import com.example.ibeacondemo.util.LiveDataBus;
import com.example.ibeacondemo.util.SPUtils;
import com.example.ibeacondemo.util.Util;
import com.google.gson.Gson;
import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.view.RxToast;

import org.apache.commons.lang3.StringUtils;

/**
 * 信息编辑页面
 */
public final class InfoEditorFragment extends AppFragment<InfoManageActivity> {

    private TextView mTv_owned_company, mTv_service_target_company, mTv_job, mBoy, mGirl, tv_tittle_name, tv_tittle_sex, tv_tittle_mail;
    private EditText mName, mMail;
    private String mSex;
    private Button mCommitView;
    //个人所属公司id
    private String mCompanyID;
    //服务对象公司IDs
    private String mCompanyIDs;
    //职位IDs
    private String mPositionIDs;
    //用户ID
    private String mUserInfoID;

    public static InfoEditorFragment newInstance() {
        return new InfoEditorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_info_editor;
    }

    @Override
    protected void initView() {
        //tittle 加星部分
        tv_tittle_name = findViewById(R.id.tv_tittle_name);
        tv_tittle_sex = findViewById(R.id.tv_tittle_sex);
        tv_tittle_mail = findViewById(R.id.tv_tittle_mail);


        mTv_owned_company = findViewById(R.id.tv_owned_company);
        mTv_service_target_company = findViewById(R.id.tv_service_target_company);
        mTv_job = findViewById(R.id.tv_job);
        mName = findViewById(R.id.name);
        mBoy = findViewById(R.id.boy);
        mGirl = findViewById(R.id.girl);
        mMail = findViewById(R.id.et_mail);
        mCommitView = findViewById(R.id.btn_save_commit);

        InputTextManager.with(getAttachActivity())
                .addView(mName)
                .addView(mMail)
                .setMain(mCommitView)
                .build();

        setOnClickListener(R.id.btn_signout, R.id.boy, R.id.girl, R.id.btn_save_commit);

    }

    @Override
    protected void initData() {
        String tittleName = "<font color='#FF0000'>* </font>" + "<font color='#000000'>姓名</font>" + "<font color='#000000'>:</font>";
        String tittleSex = "<font color='#FF0000'>* </font>" + "<font color='#000000'>性别</font>" + "<font color='#000000'>:</font>";
        String tittleMail = "<font color='#FF0000'>* </font>" + "<font color='#000000'>电子邮箱</font>" + "<font color='#000000'>:</font>";
        tv_tittle_name.setText(Html.fromHtml(tittleName));
        tv_tittle_sex.setText(Html.fromHtml(tittleSex));
        tv_tittle_mail.setText(Html.fromHtml(tittleMail));

        //获取用户ID
        mUserInfoID = (String) SPUtils.get(getAttachActivity(), "userInfoID", "");

        if (StringUtils.isNoneBlank(mUserInfoID)) {
            EasyHttp.post(InfoEditorFragment.this)
                    .api(new getUserInfoApi()
                            .setUserInfoID(mUserInfoID))
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                String s = result.toString().replace("\\", "");
                                String replaceJson = s.substring(1, s.length() - 1);
                                //判断是否为json
                                if (Util.getJSONType(replaceJson)) {
//                                    Log.e("TAG", "onSucceed: " + replaceJson);
                                    Gson gson = new Gson();
                                    UserInfoBean userInfoBean = gson.fromJson(replaceJson, UserInfoBean.class);
                                    mCompanyID = userInfoBean.getCompanyID();
                                    mCompanyIDs = userInfoBean.getCompanyIDs();
                                    mPositionIDs = userInfoBean.getPositionIDs();

                                    String companyName = userInfoBean.getCompanyName();
                                    String companyNamesWC = userInfoBean.getCompanyNamesWC();
                                    String positionNamesWC = userInfoBean.getPositionNamesWC();
                                    String realName = userInfoBean.getRealName();
                                    String sex = userInfoBean.getSex();
                                    String email = userInfoBean.getEmail();

                                    mTv_owned_company.setText(companyName);
                                    mTv_service_target_company.setText(companyNamesWC);
                                    mTv_job.setText(positionNamesWC);
                                    mName.setText(realName);
                                    setSex(sex);
                                    mMail.setText(email);

                                } else {
                                    RxToast.warning(s);
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


    @Override
    public void onResume() {
        super.onResume();
        InfoManageActivity.mInfo_edit.setTextColor(Color.rgb(2, 87, 160));
        InfoManageActivity.mChange_pws.setTextColor(Color.rgb(119, 119, 119));
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        //此状态已隐藏
        if (viewId == R.id.btn_signout) {
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
                    //.setAutoDismiss(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            postDelayed(() -> {
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                finish();
                            }, 300);
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                        }
                    })
                    .show();


        } else if (viewId == R.id.boy) {
            if (mBoy.isClickable()) {
                mBoy.setEnabled(false);
                mGirl.setEnabled(true);
                mSex = "男";
            }

        } else if (viewId == R.id.girl) {
            if (mGirl.isClickable()) {
                mGirl.setEnabled(false);
                mBoy.setEnabled(true);
                mSex = "女";
            }

        } else if (viewId == R.id.btn_save_commit) {
            //保存用户信息
            EasyHttp.post(InfoEditorFragment.this)
                    .api(new UserInfoSubmitApi()
                            .setUserInfoID(mUserInfoID)
                            .setRealName(mName.getText().toString())
                            .setSex(mSex)
                            .setEmail(mMail.getText().toString())
                            .setCompanyID(mCompanyID)
                            .setCompanyIDs(mCompanyIDs)
                            .setPositionIDs(mPositionIDs))
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                if (result.equals("True")) {
                                    new HintDialog.Builder(getAttachActivity())
                                            .setIcon(HintDialog.ICON_FINISH)
                                            .setMessage("保存成功")
                                            .show();
                                    LiveDataBus.get().with("userName").postValue(mName.getText().toString());
                                } else {
                                    ToastUtils.show(result);
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

    public String isSex(String sex) {
        if (StringUtils.isNoneBlank(sex)) {
            mSex = sex;
        } else {
            mSex = "女";
        }
        return mSex;
    }

    public void setSex(String sex) {
        String mSex = isSex(sex);
        if (mSex.equals("男")) {
            mBoy.setClickable(true);
            mBoy.setEnabled(false);
            mGirl.setEnabled(true);

        } else if (mSex.equals("女")) {
            mGirl.setClickable(true);
            mGirl.setEnabled(false);
            mBoy.setEnabled(true);
        }
    }
}