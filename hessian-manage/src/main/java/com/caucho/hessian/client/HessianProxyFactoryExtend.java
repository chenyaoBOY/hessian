package com.caucho.hessian.client;

import com.caucho.hessian.io.HessianRemoteObject;
import com.caucho.hessian.util.AddressUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;

/**
 * @author chenyao
 * @date 2019/5/27 15:48
 * @description
 */
public class HessianProxyFactoryExtend extends HessianProxyFactory {


    private final String applicationName;

    public HessianProxyFactoryExtend(String applicationName) {
        super();
        this.applicationName = applicationName;
    }

    @Override
    public Object create(Class<?> api, URL url, ClassLoader loader) {
        if (api == null) {
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        } else {
            HessianProxyExtend handler = new HessianProxyExtend(url, this, api);
            handler.setApplicationName(applicationName);
            return Proxy.newProxyInstance(loader, new Class[]{api, HessianRemoteObject.class}, handler);
        }
    }
}
