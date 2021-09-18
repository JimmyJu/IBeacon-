package com.example.ibeacondemo.ui.fragment;


import android.graphics.Color;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.ibeacondemo.R;
import com.example.ibeacondemo.aop.SingleClick;
import com.example.ibeacondemo.api.ChangePawApi;
import com.example.ibeacondemo.app.AppFragment;
import com.example.ibeacondemo.manager.InputTextManager;
import com.example.ibeacondemo.ui.activity.InfoManageActivity;
import com.example.ibeacondemo.ui.dialog.HintDialog;
import com.example.ibeacondemo.util.SPUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.view.RxToast;

/**
 * desc   : 密码修改
 */
public final class ChangePasswordFragment extends AppFragment<InfoManageActivity> {

    private EditText mEt_original_psw, mEt_new_psw, mEt_new_psw_again;
    private Button mCommitView;
    private String mUserInfoID;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void initView() {
        mEt_original_psw = findViewById(R.id.et_original_psw);
        mEt_new_psw = findViewById(R.id.et_new_psw);
        mEt_new_psw_again = findViewById(R.id.et_new_psw_again);
        mCommitView = findViewById(R.id.btn_confirm_to_modify);

        setOnClickListener(mCommitView);
        InputTextManager.with(getAttachActivity())
                .addView(mEt_original_psw)
                .addView(mEt_new_psw)
                .addView(mEt_new_psw_again)
                .setMain(mCommitView)
                .build();

    }

    @Override
    protected void initData() {
        //获取用户ID
        mUserInfoID = (String) SPUtils.get(getAttachActivity(), "userInfoID", "");
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        //提交
        if (view == mCommitView) {
            if (!mEt_new_psw.getText().toString().equals(mEt_new_psw_again.getText().toString())) {
                mEt_new_psw.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mEt_new_psw_again.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_input_unlike);
                return;
            }
            // 隐藏软键盘
            hideKeyboard(getAttachActivity().getCurrentFocus());

            EasyHttp.post(this)
                    .api(new ChangePawApi()
                            .setUserInfoID(mUserInfoID)
                            .setOldPassword(mEt_original_psw.getText().toString())
                            .setNewPassword(mEt_new_psw.getText().toString()))
                    .request(new OnHttpListener<Object>() {
                        @Override
                        public void onSucceed(Object result) {
                            if (result != null) {
                                if (result.equals("True")) {
                                    new HintDialog.Builder(getAttachActivity())
                                            .setIcon(HintDialog.ICON_FINISH)
                                            .setMessage("修改成功")
                                            .show();
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

    @Override
    public void onResume() {
        super.onResume();
        InfoManageActivity.mInfo_edit.setTextColor(Color.rgb(119, 119, 119));
        InfoManageActivity.mChange_pws.setTextColor(Color.rgb(2, 87, 160));
    }
}