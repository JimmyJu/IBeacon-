package com.example.ibeacondemo.data;

/**
 * desc   : 产品设备列表--查询获得的设备列表信息
 */
public class GetDeviceInfoListBean {
    //设备ID
    private String ID;

    //设备型号
    private String DeviceNumber;

    //设备类型名称
    private String DeviceTypeName;

    //设备名称（可关键字模糊查询）
    private String DeviceName;

    //当前报修状态
    private String Status;

    public String getID() {
        return ID;
    }

    public String getDeviceNumber() {
        return DeviceNumber;
    }

    public String getDeviceTypeName() {
        return DeviceTypeName;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getStatus() {
        return Status;
    }
}
