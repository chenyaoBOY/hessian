package com.caucho.hessian.client;

import com.caucho.hessian.io.HessianRemoteObject;
import com.caucho.hessian.manage.RequestManage;
import com.caucho.hessian.manage.ZkManage;

import java.lang.reflect.Proxy;
import java.net.URL;

/**
 * @author chenyao
 * @date 2019/5/27 15:48
 * @description 多例
 */
public class HessianProxyFactoryExtend extends HessianProxyFactory {

    private static RequestManage requestManage = new RequestManage() ;

    public HessianProxyFactoryExtend() {
        super();
    }

    /**
     * @description //TODO HessianProxyFactory.create(class,url)
     * @author chenyao
     * @date  2019/6/6 15:54
     * @return java.lang.Object
     */
    @Override
    public Object create(Class<?> api, URL url, ClassLoader loader) {
        if (api == null) {
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        } else {
            HessianProxyExtend handler = new HessianProxyExtend(url, this, api);
            return Proxy.newProxyInstance(loader, new Class[]{api, HessianRemoteObject.class}, handler);
        }
    }

    public RequestManage getRequestManage() {
        return requestManage;
    }
}
