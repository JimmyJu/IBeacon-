package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 *  登入请求API
 */
public class LoginApi implements IRequestApi {
    @Override
    public String getApi() {
        return "user/Login?MobileNo=" + MobileNo + "&Password=" + Password;
    }

    private String MobileNo;

    private String Password;

    public LoginApi setMobileNo(String mobileNo) {
        this.MobileNo = mobileNo;
        return this;
    }

    public LoginApi setPassword(String password) {
        this.Password = password;
        return this;
    }
}
