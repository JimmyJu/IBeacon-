package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 管理员主页--获取维保工单的数量
 */
public final class GetMaintenanceWorkOrderQuantityApi implements IRequestApi {

    private String UserInfoID;

    @Override
    public String getApi() {
        return "Maintain/GetTaskCount?UserInfoID=" + UserInfoID;
    }

    public GetMaintenanceWorkOrderQuantityApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }
}