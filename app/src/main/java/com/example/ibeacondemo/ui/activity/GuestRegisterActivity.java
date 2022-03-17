package com.example.ibeacondemo.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.Log;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.api.GetCodeApi;
import com.example.ibeacondemo.api.RegisterApi;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.data.BelongCompany;
import com.example.ibeacondemo.data.JobBean;
import com.example.ibeacondemo.data.ServiceObjectBean;
import com.example.ibeacondemo.manager.InputTextManager;
import com.example.ibeacondemo.other.IntentKey;
import com.example.ibeacondemo.ui.dialog.SelectDialog;
import com.example.ibeacondemo.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.CountdownView;
import com.hjq.widget.view.SubmitButton;
import com.tamsiree.rxkit.view.RxToast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.Call;

/**
 * desc   : 访客注册
 */
public final class GuestRegisterActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private String userType = "1";

    @Log
    public static void start(BaseActivity activity, String phone, String password, OnRegisterListener listener) {
        Intent intent = new Intent(activity, GuestRegisterActivity.class);
        intent.putExtra(IntentKey.PHONE, phone);
        intent.putExtra(IntentKey.PASSWORD, password);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(IntentKey.PHONE), data.getStringExtra(IntentKey.PASSWORD));
            } else {
                listener.onCancel();
            }
        });
    }

    private EditText mPhoneView;
    private CountdownView mCountdownView;

    private EditText mCodeView;

    private EditText mFirstPassword;
    private EditText mSecondPassword;

    private SubmitButton mCommitView;

    private EditText mUserName, mEmail;
    private TextView mBelongingCompany, mjob, mServiceObject, mUser_gender;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioAdmin, mRadioUser;
    private LinearLayout mCompany2Job;
    //职位ID
    private String[] mJobID;
    //所属公司ID
    private String[] mBelongingCompanyID;
    //服务对象公司ID
    private String[] mServiceObjectID;

//    private List<BelongCompany> belongCompanyList;
//    private List<JobBean> mJobBeans;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_register_phone);
        mCountdownView = findViewById(R.id.cv_register_countdown);
        mCodeView = findViewById(R.id.et_register_code);
        mFirstPassword = findViewById(R.id.et_register_password1);
        mSecondPassword = findViewById(R.id.et_register_password2);
        mCommitView = findViewById(R.id.btn_register_commit);

        //--------user-----
        mUserName = findViewById(R.id.et_user_name);
        mEmail = findViewById(R.id.et_e_mail);
        mBelongingCompany = findViewById(R.id.tv_belonging_company);
        mjob = findViewById(R.id.tv_job);
        mServiceObject = findViewById(R.id.tv_service_object);
        mRadioGroup = findViewById(R.id.rg_radio_group);
        mRadioAdmin = findViewById(R.id.radio_admin);
        mRadioUser = findViewById(R.id.radio_user);
        mCompany2Job = findViewById(R.id.ll_company_job);
        mUser_gender = findViewById(R.id.tv_user_gender);


        setOnClickListener(mCountdownView, mCommitView, mBelongingCompany, mjob, mServiceObject, mUser_gender);

        mSecondPassword.setOnEditorActionListener(this);

//        belongCompanyList = new ArrayList<>();
//        mJobBeans = new ArrayList<>();

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));
        //通过管理多个 TextView 输入是否为空来启用或者禁用按钮的点击事件
        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .addView(mUserName)
                .addView(mUser_gender)
                .addView(mEmail)
//                .addView(mBelongingCompany)
//                .addView(mjob)
                .addView(mServiceObject)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        // 自动填充手机号和密码
        mPhoneView.setText(getString(IntentKey.PHONE));
        mFirstPassword.setText(getString(IntentKey.PASSWORD));
        mSecondPassword.setText(getString(IntentKey.PASSWORD));

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                //1-设备管理员  2-业主
                switch (checkId) {
                    case R.id.radio_admin:
                        userType = "1";
                        mCompany2Job.setVisibility(View.VISIBLE);

                        break;
                    case R.id.radio_user:
                        userType = "2";
                        mCompany2Job.setVisibility(View.GONE);

                        mBelongingCompanyID = null;
                        mJobID = null;
                        mBelongingCompany.setText("");
                        mjob.setText("");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @SingleClick
    @Override
    public void onClick(View view) {

        if (view == mCountdownView) {
            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            /*if (true) {
                toast(R.string.common_code_send_hint);
                mCountdownView.start();
                return;
            }*/

            // 获取验证码
            EasyHttp.post(this)
                    .api(new GetCodeApi()
                            .setMobileNo(mPhoneView.getText().toString()))

                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                if (result.equals("True")) {
                                    toast(R.string.common_code_send_hint);
                                    mCountdownView.start();
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
           /* EasyHttp.post(this)
                    .api(new GetCodeApi()
                            .setPhone(mPhoneView.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();
                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            mCountdownView.start();
                        }
                    });*/
        } else if (view == mUser_gender) {
            new SelectDialog.Builder(this)
                    .setTitle("请选择你的性别")
                    .setList("男", "女")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(0)
                    .setListener(new SelectDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
//                            toast("确定了：" + data.toString());
                            Collection<String> values = data.values();
                            mUser_gender.setText(values.toString().substring(1, values.toString().length() - 1));
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
//                            toast("取消了");
                        }
                    })
                    .show();

        } else if (view == mBelongingCompany) {
            EasyHttp.post(this)
                    .api("user/GetCompanyListOwn")
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                String replaceJson = result.toString().replace("\\", "");
                                if (Util.getJSONType(replaceJson)) {
                                    Gson gson = new Gson();
                                    List<BelongCompany> belongCompanyList = gson.fromJson(replaceJson, new TypeToken<List<BelongCompany>>() {
                                    }.getType());

                                    ArrayList dataSet = new ArrayList();
                                    Map<String, String> map = new HashMap<String, String>();
                                    for (BelongCompany bean : belongCompanyList) {
                                        dataSet.add(bean.getCompanyName());
                                        map.put(bean.getCompanyName(), bean.getID());
                                    }
                                    new SelectDialog.Builder(GuestRegisterActivity.this)
                                            .setTitle("请选择所属公司")
                                            .setList(dataSet)
                                            .setListener(new SelectDialog.OnListener<String>() {
                                                @Override
                                                public void onSelected(BaseDialog dialog, HashMap data) {
                                                    mBelongingCompanyID = new String[data.size()];
                                                    int i = 0;
                                                    String belongingCompanyData = "";
                                                    for (Object value : data.values()) {
                                                        mBelongingCompanyID[i++] = map.get(value);
                                                        belongingCompanyData = belongingCompanyData + (String) value + ",";
                                                    }
                                                    android.util.Log.e("TAG", "所属公司ID : " + Arrays.toString(mBelongingCompanyID));
                                                    mBelongingCompany.setText(belongingCompanyData.substring(0, belongingCompanyData.length() - 1));
                                                }
                                            })
                                            .show();
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


        } else if (view == mjob) {
            EasyHttp.post(this)
                    .api("user/GetPositionList")
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                String replaceJson = result.toString().replace("\\", "");
//                                Log.e("TAG", "onSucceed: " + replaceJson);
                                if (Util.getJSONType(replaceJson)) {
                                    Gson gson = new Gson();
                                    List<JobBean> jobBeans = gson.fromJson(replaceJson, new TypeToken<List<JobBean>>() {
                                    }.getType());
                                    ArrayList dataSet = new ArrayList();
                                    Map<String, String> map = new HashMap<String, String>();
                                    for (JobBean bean : jobBeans) {
//                                        Log.e("TAG", "onSucceed: " + bean.getPositionName());
                                        dataSet.add(bean.getPositionName());
                                        map.put(bean.getPositionName(), bean.getID());
                                    }
                                    new SelectDialog.Builder(GuestRegisterActivity.this)
                                            .setTitle("请选择岗位")
                                            .setList(dataSet)
                                            .setListener(new SelectDialog.OnListener<String>() {
                                                @Override
                                                public void onSelected(BaseDialog dialog, HashMap data) {
                                                    mJobID = new String[data.size()];
                                                    int i = 0;
                                                    String jobData = "";
                                                    for (Object value : data.values()) {
                                                        mJobID[i++] = map.get(value);
                                                        jobData = jobData + (String) value + ",";
                                                    }
                                                    android.util.Log.e("TAG", "岗位ID : " + Arrays.toString(mJobID));
                                                    mjob.setText(jobData.substring(0, jobData.length() - 1));
                                                }
                                            })
                                            .show();

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

        } else if (view == mServiceObject) {


            EasyHttp.post(this)
                    .api("user/GetCompanyList")
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                String replaceJson = result.toString().replace("\\", "");
                                android.util.Log.e("TAG", "onSucceed: " + replaceJson);
                                if (Util.getJSONType(replaceJson)) {
                                    Gson gson = new Gson();
                                    List<ServiceObjectBean> serviceObjectBeans = gson.fromJson(replaceJson, new TypeToken<List<ServiceObjectBean>>() {
                                    }.getType());
                                    ArrayList dataSet = new ArrayList();
                                    Map<String, String> map = new HashMap<String, String>();
                                    for (ServiceObjectBean bean : serviceObjectBeans) {
//                                        Log.e("TAG", "onSucceed: " + bean.getPositionName());
                                        dataSet.add(bean.getCompanyName());
                                        map.put(bean.getCompanyName(), bean.getID());
                                    }
                                    new SelectDialog.Builder(GuestRegisterActivity.this)
                                            .setTitle("请选择服务对象公司")
                                            .setList(dataSet)
                                            .setListener(new SelectDialog.OnListener<String>() {
                                                @Override
                                                public void onSelected(BaseDialog dialog, HashMap data) {
                                                    //记录服务对象公司ID
                                                    mServiceObjectID = new String[data.size()];
                                                    int i = 0;
                                                    String ServiceObjectData = "";
                                                    for (Object value : data.values()) {
                                                        mServiceObjectID[i++] = map.get(value);
                                                        ServiceObjectData = ServiceObjectData + (String) value + ",";
                                                    }
                                                    android.util.Log.e("TAG", "服务对象公司ID : " + Arrays.toString(mServiceObjectID));
                                                    mServiceObject.setText(ServiceObjectData.substring(0, ServiceObjectData.length() - 1));
                                                }
                                            })
                                            .show();

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


        } else if (view == mCommitView) {
            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_code_error_hint);
                return;
            }

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_password_input_unlike);
                return;
            }
            if (!Util.isEmailAddress(mEmail.getText().toString())) {
                mEmail.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_emil);
                return;
            }
            //所属公司
            String mBelongingCompanyID2Str = StringUtils.join(mBelongingCompanyID, ",");
            //岗位
            String mJobID2Str = StringUtils.join(mJobID, ",");
            //服务对象公司
            String mServiceObjectID2Str = StringUtils.join(mServiceObjectID, ",");

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

           /* if (true) {
                mCommitView.showProgress();
                postDelayed(() -> {
                    mCommitView.showSucceed();
                    postDelayed(() -> {
                        setResult(RESULT_OK, new Intent()
                                .putExtra(IntentKey.PHONE, mPhoneView.getText().toString())
                                .putExtra(IntentKey.PASSWORD, mFirstPassword.getText().toString()));
                        finish();
                    }, 1000);
                }, 2000);
                return;
            }*/

            if (userType.equals("1")) {
                if (mBelongingCompanyID2Str == null) {
                    toast(R.string.common_owned_company);
                    mCommitView.showError(3000);
                    return;
                }
                if (mJobID2Str == null) {
                    toast(R.string.common_job);
                    mCommitView.showError(3000);
                    return;
                }
            } else if (userType.equals("2")) {
                if (mBelongingCompanyID2Str == null) {
                    mBelongingCompanyID2Str = "0";
                }
                if (mJobID2Str == null) {
                    mJobID2Str = "";
                }
            }

            android.util.Log.e("TAG", "注册信息: " +
                    " 所属公司ID " + mBelongingCompanyID2Str +
                    " 职位ID " + mJobID2Str +
                    " 服务对象公司 " + mServiceObjectID2Str +
                    " 用户类型 " + userType);

            // 提交注册
            EasyHttp.post(this)
                    .api(new RegisterApi()
                            .setMobileNo(mPhoneView.getText().toString())
                            .setWechatNo(mPhoneView.getText().toString())
                            .setOpenID("")
                            .setPassword(mFirstPassword.getText().toString())
                            .setRealName(mUserName.getText().toString())
                            .setUserType(userType)
                            .setSex(mUser_gender.getText().toString())
                            .setEmail(mEmail.getText().toString())
                            .setCompanyIDs(mServiceObjectID2Str)
                            .setPositionIDs(mJobID2Str)
                            .setCompanyID(mBelongingCompanyID2Str)
                            .setVerifyCode(mCodeView.getText().toString()))
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onStart(Call call) {
                            mCommitView.showProgress();
                        }

                        @Override
                        public void onEnd(Call call) {
                        }

                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                if (result.equals("True")) {

                                    postDelayed(() -> {
                                        mCommitView.showSucceed();
                                        postDelayed(() -> {
                                            setResult(RESULT_OK, new Intent()
                                                    .putExtra(IntentKey.PHONE, mPhoneView.getText().toString())
                                                    .putExtra(IntentKey.PASSWORD, mFirstPassword.getText().toString()));
                                            finish();
                                        }, 1000);
                                    }, 1000);

                                } else {
                                    postDelayed(() -> {
                                        mCommitView.showError(3000);
                                        ToastUtils.show(result);
                                    }, 1000);
                                }
                            } else {
                                postDelayed(() -> {
                                    mCommitView.showError(3000);
                                    RxToast.info("服务异常");
                                }, 1000);
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            postDelayed(() -> {
                                mCommitView.showError(3000);
                                RxToast.error("网络服务异常");
                            }, 1000);
                        }
                    });

          /*  EasyHttp.post(this)
                    .api(new RegisterApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString())
                            .setPassword(mFirstPassword.getText().toString()))
                    .request(new HttpCallback<HttpData<RegisterBean>>(this) {

                        @Override
                        public void onStart(Call call) {
                            mCommitView.showProgress();
                        }

                        @Override
                        public void onEnd(Call call) {}

                        @Override
                        public void onSucceed(HttpData<RegisterBean> data) {
                            postDelayed(() -> {
                                mCommitView.showSucceed();
                                postDelayed(() -> {
                                    setResult(RESULT_OK, new Intent()
                                            .putExtra(IntentKey.PHONE, mPhoneView.getText().toString())
                                            .putExtra(IntentKey.PASSWORD, mFirstPassword.getText().toString()));
                                    finish();
                                }, 1000);
                            }, 1000);
                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            postDelayed(() -> {
                                mCommitView.showError(3000);
                            }, 1000);
                        }
                    });*/
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击注册按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    /**
     * 注册监听
     */
    public interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone    手机号
         * @param password 密码
         */
        void onSucceed(String phone, String password);

        /**
         * 取消注册
         */
        default void onCancel() {
        }
    }
}