package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * 获取用户信息
 */
public final class getUserInfoApi implements IRequestApi {

    private String UserInfoID;

    @Override
    public String getApi() {
        return "user/GetUserInfo?UserInfoID=" + UserInfoID;
    }

    public getUserInfoApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }
}