package com.caucho.hessian.client;

import com.caucho.hessian.io.HessianRemoteObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;

/**
 * @author chenyao
 * @date 2019/5/27 15:48
 * @description
 */
public class HessianProxyFactoryExtend extends HessianProxyFactory {

    public HessianProxyFactoryExtend() {
        super();
    }

    @Override
    public Object create(Class<?> api, URL url, ClassLoader loader) {
        if (api == null) {
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        } else {
            InvocationHandler handler = new HessianProxyExtend(url, this, api);
            return Proxy.newProxyInstance(loader, new Class[]{api, HessianRemoteObject.class}, handler);
        }
    }
}
