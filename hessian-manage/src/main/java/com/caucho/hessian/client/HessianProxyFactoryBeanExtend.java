package com.caucho.hessian.client;

import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import java.net.MalformedURLException;

/**
 * @author chenyao
 * @date 2019/6/6 15:32
 * @description  多例
 */
public class HessianProxyFactoryBeanExtend extends HessianProxyFactoryBean {

    private HessianProxyFactoryExtend hessianProxyFactoryExtend = new HessianProxyFactoryExtend();

    @Override
    protected Object createHessianProxy(HessianProxyFactory proxyFactory) throws MalformedURLException {
        return super.createHessianProxy(hessianProxyFactoryExtend);
    }
}
