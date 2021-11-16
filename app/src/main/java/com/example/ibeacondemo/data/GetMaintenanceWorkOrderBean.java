package com.example.ibeacondemo.data;

/**
 * desc   : 我的维保工单 查询获得的工单信息
 */
public class GetMaintenanceWorkOrderBean {
    //工单ID
    private String ID;

    //维护单ID
    private String MaintainInfoID;

    //维护单号
    private String MaintainNo;

    //公司名称
    private String CompanyName;

    //处理者姓名
    private String RealName;

    //设备名称
    private String DeviceName;

    //设备型号
    private String DeviceNumber;

    //计划维护日期
    private String CreateTime;

    //处理环节
    private String FlowNodeName;

    //处理状态   1-已提交  0-未提交
    private String IsSubmit;

    //处理动作    如为空，前端显示“暂无”
    private String ResultName;

    //提交时间   如为空，前端显示“暂无”
    private String SubmitTime;

    //处理节点ID
    private String FlowNodeID;

    //处理节点ID
    private String DeviceID;

    //是否有故障  （0无故障，1有故障，如为空，前端显示“暂无”）
    private String IsFault;

    public String getID() {
        return ID;
    }

    public String getMaintainInfoID() {
        return MaintainInfoID;
    }

    public String getMaintainNo() {
        return MaintainNo;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public String getRealName() {
        return RealName;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getDeviceNumber() {
        return DeviceNumber;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public String getFlowNodeName() {
        return FlowNodeName;
    }

    public String getIsSubmit() {
        return IsSubmit;
    }

    public String getResultName() {
        return ResultName;
    }

    public String getSubmitTime() {
        return SubmitTime;
    }

    public String getFlowNodeID() {
        return FlowNodeID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public String getIsFault() {
        return IsFault;
    }
}
