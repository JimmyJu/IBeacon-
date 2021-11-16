package com.example.ibeacondemo.api;

import com.hjq.http.config.IRequestApi;

/**
 * desc   : 产品设备列表--查询设备列表信息API
 */
public final class GetDevicesInfoListApi implements IRequestApi {
    private String UserInfoID;
    private String CompanyID;
    private String Status;
    private String DeviceTypeID;
    private String DeviceName;

    @Override
    public String getApi() {
        return "Repair/GetDeviceInfoList?UserInfoID=" + UserInfoID +
                "&CompanyID=" + CompanyID + "&Status=" + Status +
                "&DeviceTypeID=" + DeviceTypeID + "&DeviceName=" + DeviceName;
    }

    public GetDevicesInfoListApi setUserInfoID(String userInfoID) {
        this.UserInfoID = userInfoID;
        return this;
    }

    public GetDevicesInfoListApi setCompanyID(String companyID) {
        this.CompanyID = companyID;
        return this;
    }

    public GetDevicesInfoListApi setStatus(String status) {
        this.Status = status;
        return this;
    }

    public GetDevicesInfoListApi setDeviceTypeID(String deviceTypeID) {
        this.DeviceTypeID = deviceTypeID;
        return this;
    }

    public GetDevicesInfoListApi setDeviceName(String deviceName) {
        this.DeviceName = deviceName;
        return this;
    }
}