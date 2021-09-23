package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 产品设备列表--获取公司名称API
 */
public final class GetCompanyListApi implements IRequestApi {
    private String UserInfoID;

    @Override
    public String getApi() {
        return "Repair/GetCompanyList?UserInfoID=" + UserInfoID;
    }

    public GetCompanyListApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }
}