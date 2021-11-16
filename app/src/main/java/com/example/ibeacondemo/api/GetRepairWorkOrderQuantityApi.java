package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 管理员主页--获取维修工单的数量
 */
public final class GetRepairWorkOrderQuantityApi implements IRequestApi {

    private String UserInfoID;

    @Override
    public String getApi() {
        return "Repair/GetTaskCount?UserInfoID=" + UserInfoID;
    }

    public GetRepairWorkOrderQuantityApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }
}