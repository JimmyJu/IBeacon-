package com.example.ibeacondemo.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.Log;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.api.GuestLoginApi;
import com.example.ibeacondemo.app.AppActivity;
import com.example.ibeacondemo.manager.InputTextManager;
import com.example.ibeacondemo.other.IntentKey;
import com.example.ibeacondemo.other.KeyboardWatcher;
import com.example.ibeacondemo.util.SPUtils;
import com.example.ibeacondemo.util.Util;
import com.google.gson.Gson;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.widget.view.SubmitButton;
import com.tamsiree.rxkit.view.RxToast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * desc   : 访客登录页
 */
public final class GuestLoginActivity extends AppActivity
        implements KeyboardWatcher.SoftKeyboardStateListener,
        TextView.OnEditorActionListener {

    @Log
    public static void start(Context context, String phone, String password) {
        Intent intent = new Intent(context, GuestLoginActivity.class);
        intent.putExtra(IntentKey.PHONE, phone);
        intent.putExtra(IntentKey.PASSWORD, password);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private ImageView mLogoView;

    private ViewGroup mBodyLayout;
    private EditText mPhoneView;
    private EditText mPasswordView;

    private View mForgetView;
    private SubmitButton mCommitView;
    private CheckBox mRememberPsw;
    //记住密码标识
    private boolean flag;


    /**
     * logo 缩放比例
     */
    private final float mLogoScale = 0.8f;
    /**
     * 动画时间
     */
    private final int mAnimTime = 300;

    @Override
    protected int getLayoutId() {
        return R.layout.guest_login_activity;
    }

    @Override
    protected void initView() {
        mLogoView = findViewById(R.id.iv_login_logo);
        mBodyLayout = findViewById(R.id.ll_login_body);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        mForgetView = findViewById(R.id.tv_login_forget);
        mCommitView = findViewById(R.id.btn_login_commit);
        mRememberPsw = findViewById(R.id.login_checkBox);


        setOnClickListener(mForgetView, mCommitView);

        mPasswordView.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mPasswordView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        postDelayed(() -> {
            KeyboardWatcher.with(GuestLoginActivity.this)
                    .setListener(GuestLoginActivity.this);
        }, 500);

        // 自动填充手机号和密码
        mPhoneView.setText(getString(IntentKey.PHONE));
        mPasswordView.setText(getString(IntentKey.PASSWORD));


        flag = (Boolean) SPUtils.get(GuestLoginActivity.this, "GuestRemember", false);
        mRememberPsw.setChecked(flag);

        //显示手机号
        String phone = (String) SPUtils.get(GuestLoginActivity.this, "GuestPhone", "");
        if (flag) {
            mPhoneView.setText(phone);
        }

        mRememberPsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    GuestLoginActivity.this.flag = isChecked;
                    SPUtils.put(GuestLoginActivity.this, "GuestRemember", isChecked);
                } else {
                    GuestLoginActivity.this.flag = isChecked;
                    SPUtils.put(GuestLoginActivity.this, "GuestRemember", isChecked);
                    SPUtils.remove(GuestLoginActivity.this, "GuestPhone");
                }

            }
        });
    }

    @Override
    public void onRightClick(View view) {
        // 跳转到注册界面
        GuestRegisterActivity.start(this, mPhoneView.getText().toString(), mPasswordView.getText().toString(), (phone, password) -> {
            // 如果已经注册成功，就执行登录操作
            mPhoneView.setText(phone);
            mPasswordView.setText(password);
            mPasswordView.requestFocus();
            mPasswordView.setSelection(mPasswordView.getText().length());
            onClick(mCommitView);
        });
    }

    @SingleClick
    @Override
    public void onClick(View view) {
//        if (view == mForgetView) {
//            startActivity(PasswordForgetActivity.class);
//            return;
//        }


        if (view == mCommitView) {
            if (mPhoneView.getText().toString().length() != 11) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (flag) {
                //判断是否保存手机号
                SPUtils.put(GuestLoginActivity.this, "GuestPhone", mPhoneView.getText().toString());
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

//       ToastUtils.show("此功能正在开发中! 请到微信小程序登录");
//       mCommitView.showError(2000);

            EasyHttp.post(GuestLoginActivity.this)
                    .api(new GuestLoginApi()
                            .setMobileNo(mPhoneView.getText().toString())
                            .setPassword(mPasswordView.getText().toString())
                            .setLastLoginSource("3"))
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
                                String json = result.toString();
                               /* String s = result.toString().replace("\\", "");
                                String replaceJson = s.substring(1, s.length() - 1);
                                if (Util.getJSONType(replaceJson)) {
                                    postDelayed(() -> {
                                        mCommitView.showSucceed();
//                                        Log.e("TAG", "onSucceed: " + replaceJson);
                                        Gson gson = new Gson();
                                        LoginBean loginBean = gson.fromJson(replaceJson, LoginBean.class);
                                        //获取用户名
                                        String realName = loginBean.getRealName();
                                        //用户ID
                                        String id = loginBean.getID();
                                        if (StringUtils.isNotBlank(id)) {
                                            SPUtils.put(GuestLoginActivity.this, "userInfoID", id);
                                        }
                                        if (StringUtils.isNotBlank(realName)) {
                                            SPUtils.put(GuestLoginActivity.this, "realName", realName);

                                        }
                                        postDelayed(() -> {
                                            //
//                                            startActivity(OpsMainActivity.class);
//                                            finish();
                                        }, 1000);
                                    }, 1000);
                                } else {
                                    postDelayed(() -> {
                                        mCommitView.showError(3000);
                                        RxToast.warning(s);
                                    }, 1000);
                                }*/

                                /*{
                                    "User_UserInfo": {
                                    "ID": 143,
                                            "RealName": "王先生",
                                            "MobileNo": "13901559812",
                                            "WechatNo": "13901559812",
                                            "UserType": 4,
                                            "Sex": "男",
                                            "Email": "2322x234@qq.com",
                                            "CreateTime": "2021-12-30 18:12:57",
                                            "LastUpdateTime": "",
                                            "LastLoginTime": "2021-12-30 18:12:57",
                                            "RegSource": 1,
                                            "LastLoginSource": 1
                                }
                                }*/
                                if (Util.getJSONType(json)) {
                                    postDelayed(() -> {
                                        mCommitView.showSucceed();
                                        try {
                                            JSONObject jObject = new JSONObject(json);
                                            JSONObject user_userInfo = jObject.getJSONObject("User_UserInfo");
                                            String s = user_userInfo.toString();
                                            Gson gson = new Gson();
                                            GuestLoginApi.LoginBean loginBean = gson.fromJson(s, GuestLoginApi.LoginBean.class);
                                            //获取用户名、用户ID
                                            String realName = loginBean.getRealName();
                                            String id = String.valueOf(loginBean.getID());

                                            if (StringUtils.isNotBlank(id)) {
                                                SPUtils.put(GuestLoginActivity.this, "guestID", id);
                                            }
                                            if (StringUtils.isNotBlank(realName)) {
                                                SPUtils.put(GuestLoginActivity.this, "guestName", realName);

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        postDelayed(() -> {
                                            //用户页面
//                                            startActivity(OpsMainActivity.class);
//                                            finish();
                                        }, 1000);

                                    }, 1000);
                                } else {
                                    postDelayed(() -> {
                                        mCommitView.showError(3000);
                                        RxToast.warning(json);
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
        }
    }


    /**
     * {@link KeyboardWatcher.SoftKeyboardStateListener}
     */

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", 0, -mCommitView.getHeight());
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        // 执行缩小动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", 1f, mLogoScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", 1f, mLogoScale);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", 0f, -mCommitView.getHeight());
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    @Override
    public void onSoftKeyboardClosed() {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", mBodyLayout.getTranslationY(), 0f);
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        if (mLogoView.getTranslationY() == 0) {
            return;
        }

        // 执行放大动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", mLogoScale, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", mLogoScale, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", mLogoView.getTranslationY(), 0f);
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击登录按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}