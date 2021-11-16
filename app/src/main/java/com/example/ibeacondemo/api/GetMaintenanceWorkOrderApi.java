package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 我的维保工单-- 查询获取列表工单API
 */
public final class GetMaintenanceWorkOrderApi implements IRequestApi {

    /**
     * 用户信息ID
     */
    private String UserInfoID;

    /**
     * 公司ID  如查全部，则为0
     */
    private String CompanyID;

    /**
     * -1全部   0未提交  1已提交
     */
    private String IsSubmit;

    /**
     * 设备型号
     */
    private String DeviceNumber;

    /**
     * 设备名称
     */
    private String DeviceName;

    /**
     * 维护单号
     */
    private String MaintainNo;

    @Override
    public String getApi() {
        return "Maintain/GetTaskList?UserInfoID=" + UserInfoID +
                "&CompanyID=" + CompanyID + "&IsSubmit=" + IsSubmit +
                "&DeviceNumber=" + DeviceNumber + "&DeviceName=" + DeviceName +
                "&MaintainNo=" + MaintainNo;
    }

    public GetMaintenanceWorkOrderApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }

    public GetMaintenanceWorkOrderApi setCompanyID(String companyID) {
        this.CompanyID = companyID;
        return this;
    }

    public GetMaintenanceWorkOrderApi setIsSubmit(String isSubmit) {
        this.IsSubmit = isSubmit;
        return this;
    }

    public GetMaintenanceWorkOrderApi setDeviceNumber(String deviceNumber) {
        this.DeviceNumber = deviceNumber;
        return this;
    }

    public GetMaintenanceWorkOrderApi setDeviceName(String deviceName) {
        this.DeviceName = deviceName;
        return this;
    }

    public GetMaintenanceWorkOrderApi setMaintainNo(String maintainNo) {
        this.MaintainNo = maintainNo;
        return this;
    }
}