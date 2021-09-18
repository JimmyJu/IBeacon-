package com.example.ibeacondemo.data;

/**
 * 用户信息
 */
public class UserInfoBean {
    private String MobileNo;
    private String RealName;
    private String UserType;
    private String Sex;
    private String Email;
    //服务对象公司ID组（need）
    private String CompanyIDs;
    //职位ID组（need）
    private String PositionIDs;
    //个人所属公司ID（need）
    private String CompanyID;
    //个人所属公司名称
    private String CompanyName;
    //岗位名称
    private String PositionNamesWC;
    //岗位ID
    private String PositionIDsWC;
    //服务对象公司名称
    private String CompanyNamesWC;
    //服务对象公司ID
    private String CompanyIDsWC;

    public String getMobileNo() {
        return MobileNo;
    }

    public String getRealName() {
        return RealName;
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

    public String getCompanyIDs() {
        return CompanyIDs;
    }

    public String getPositionIDs() {
        return PositionIDs;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public String getPositionNamesWC() {
        return PositionNamesWC;
    }

    public String getPositionIDsWC() {
        return PositionIDsWC;
    }

    public String getCompanyNamesWC() {
        return CompanyNamesWC;
    }

    public String getCompanyIDsWC() {
        return CompanyIDsWC;
    }
}
