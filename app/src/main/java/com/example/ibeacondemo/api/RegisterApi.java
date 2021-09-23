package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 用户注册API
 */
public final class RegisterApi implements IRequestApi {

//    user/RegWC?MobileNo=xxx&WechatNo=xxx&OpenID=xxx&Password=xxx&RealName=xxx&UserType=xxx&Sex=xxx&Email=xxx&CompanyIDs=xxx &PositionIDs=xxx&CompanyID& VerifyCode=xxx

    @Override
    public String getApi() {
        return "user/RegWC?MobileNo=" + MobileNo + "&WechatNo=" + WechatNo +
                "&OpenID=" + OpenID + "&Password=" + Password + "&RealName=" + RealName +
                "&UserType=" + UserType + "&Sex=" + Sex + "&Email=" + Email + "&CompanyIDs=" + CompanyIDs +
                "&PositionIDs=" + PositionIDs + "&CompanyID=" + CompanyID + "&VerifyCode=" + VerifyCode;
    }

    /**
     * 手机号
     */
    private String MobileNo;

    /**
     * 微信号
     */
    private String WechatNo;

    /**
     * openID
     */
    private String OpenID;

    /**
     * 密码
     */
    private String Password;

    /**
     * 姓名
     */
    private String RealName;

    /**
     * 用户类别
     */
    private String UserType;

    /**
     * 性别
     */
    private String Sex;
    private String Email;
    private String CompanyIDs;
    private String PositionIDs;
    private String CompanyID;
    private String VerifyCode;

    public RegisterApi setMobileNo(String mobileNo) {
        this.MobileNo = mobileNo;
        return this;
    }

    public RegisterApi setWechatNo(String wechatNo) {
        WechatNo = wechatNo;
        return this;
    }

    public RegisterApi setOpenID(String openID) {
        this.OpenID = openID;
        return this;
    }

    public RegisterApi setPassword(String password) {
        this.Password = password;
        return this;
    }

    public RegisterApi setRealName(String realName) {
        this.RealName = realName;
        return this;
    }

    public RegisterApi setUserType(String userType) {
        this.UserType = userType;
        return this;
    }

    public RegisterApi setSex(String sex) {
        this.Sex = sex;
        return this;
    }

    public RegisterApi setEmail(String email) {
        this.Email = email;
        return this;
    }

    public RegisterApi setCompanyIDs(String companyIDs) {
        this.CompanyIDs = companyIDs;
        return this;
    }

    public RegisterApi setPositionIDs(String positionIDs) {
        this.PositionIDs = positionIDs;
        return this;
    }

    public RegisterApi setCompanyID(String companyID) {
        this.CompanyID = companyID;
        return this;
    }

    public RegisterApi setVerifyCode(String verifyCode) {
        this.VerifyCode = verifyCode;
        return this;
    }
}