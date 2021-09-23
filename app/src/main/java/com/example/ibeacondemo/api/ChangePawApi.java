package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestType;
import com.hjq.http.model.BodyType;

/**
 * desc   : 用户信息管理--修改密码API
 */
public final class ChangePawApi implements IRequestApi, IRequestType {
    private String UserInfoID;
    private String OldPassword;
    private String NewPassword;


    @Override
    public String getApi() {
        return "user/UpdatePassword?UserInfoID=" + UserInfoID +
                "&OldPassword=" + OldPassword +
                "&NewPassword=" + NewPassword;
    }

    @Override
    public BodyType getType() {
        //某个接口进行单独配置
        return BodyType.FORM;
    }

    public ChangePawApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }

    public ChangePawApi setOldPassword(String oldPassword) {
        this.OldPassword = oldPassword;
        return this;
    }

    public ChangePawApi setNewPassword(String newPassword) {
        this.NewPassword = newPassword;
        return this;
    }
}