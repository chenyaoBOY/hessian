package com.caucho.hessian.manage;

import com.caucho.hessian.client.HessianProxyFactory;

import java.net.URL;

/**
 * @author chenyao
 * @date 2019/5/27 18:24
 * @description
 */
public class RequestManage {

    private URL url;
    private HessianProxyFactory factory;
    private Class<?> type;

    public RequestManage(URL url, HessianProxyFactory factory, Class<?> type) {
        this.url = url;
        this.factory = factory;
        this.type = type;
    }

    public void doManageHessianRequest(String methodName, Object[] args) {

    }
}
