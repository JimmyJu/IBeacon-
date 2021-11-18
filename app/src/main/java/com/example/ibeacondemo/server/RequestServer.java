package com.example.ibeacondemo.server;

import com.example.ibeacondemo.other.AppConfig;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

public class RequestServer implements IRequestServer {
    @Override
    public String getHost() {
        return AppConfig.getHostUrl();
//        return "https://fms.yinxinht.com/";
    }

//    @Override
//    public String getPath() {
//        return "";
//    }

    @Override
    public BodyType getType() {
        // 以JSON的形式提交参数
        return BodyType.JSON;
    }
}
