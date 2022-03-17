package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 访客系统-用户登录
 */
public final class GuestLoginApi implements IRequestApi {


    @Override
    public String getApi() {
        return "Visit/Login";
    }

    /**
     * 手机号码
     */
    private String MobileNo;

    /**
     * 密码
     */
    private String Password;

    /**
     * 登录来源 1微信小程序， 2 IOS， 3 安卓
     */
    private String LastLoginSource;


    public GuestLoginApi setMobileNo(String mobileNo) {
        this.MobileNo = mobileNo;
        return this;
    }

    public GuestLoginApi setPassword(String password) {
        this.Password = password;
        return this;
    }

    public GuestLoginApi setLastLoginSource(String lastLoginSource) {
        this.LastLoginSource = lastLoginSource;
        return this;
    }

    public final static class LoginBean {
        private int ID;
        private String RealName;
        private String MobileNo;
        private String WechatNo;
        private String UserType;
        private String Sex;
        private String Email;
        private String CreateTime;
        private String LastUpdateTime;
        private String LastLoginTime;
        private String RegSource;
        private String LastLoginSource;

        public int getID() {
            return ID;
        }

        public String getRealName() {
            return RealName;
        }

        public String getMobileNo() {
            return MobileNo;
        }

        public String getWechatNo() {
            return WechatNo;
        }

        public String getUserType() {
            return UserType;
        }

        public String getSex() {
            return Sex;
        }

        public String getEmail() {
            return Email;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public String getLastUpdateTime() {
            return LastUpdateTime;
        }

        public String getLastLoginTime() {
            return LastLoginTime;
        }

        public String getRegSource() {
            return RegSource;
        }

        public String getLastLoginSource() {
            return LastLoginSource;
        }
    }

}