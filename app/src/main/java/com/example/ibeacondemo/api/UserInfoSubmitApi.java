package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 用户信息提交
 */
public final class UserInfoSubmitApi implements IRequestApi {
    private String UserInfoID;
    private String RealName;
    private String Sex;
    private String Email;
    private String CompanyID;
    private String CompanyIDs;
    private String PositionIDs;

    @Override
    public String getApi() {
        return "user/UpdateUserInfo?UserInfoID=" + UserInfoID +
                "&RealName=" + RealName + "&Sex=" + Sex +
                "&Email=" + Email + "&CompanyIDs=" + CompanyIDs +
                "&PositionIDs=" + PositionIDs + "&CompanyID=" + CompanyID;
    }

    public UserInfoSubmitApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }

    public UserInfoSubmitApi setRealName(String realName) {
        this.RealName = realName;
        return this;
    }

    public UserInfoSubmitApi setSex(String sex) {
        this.Sex = sex;
        return this;
    }

    public UserInfoSubmitApi setEmail(String email) {
        this.Email = email;
        return this;
    }

    public UserInfoSubmitApi setCompanyID(String companyID) {
        this.CompanyID = companyID;
        return this;
    }

    public UserInfoSubmitApi setCompanyIDs(String companyIDs) {
        this.CompanyIDs = companyIDs;
        return this;
    }

    public UserInfoSubmitApi setPositionIDs(String positionIDs) {
        this.PositionIDs = positionIDs;
        return this;
    }
}