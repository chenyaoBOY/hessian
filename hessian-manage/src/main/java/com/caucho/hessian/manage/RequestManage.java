package com.caucho.hessian.manage;

import com.caucho.hessian.client.HessianProxyFactory;

import java.net.URL;
import java.util.concurrent.*;

/**
 * @author chenyao
 * @date 2019/5/27 18:24
 * @description
 */
public class RequestManage {


    private ZkManage zkManage = new ZkManage();
    private static final ThreadPoolExecutor executors = new ThreadPoolExecutor(4, 8,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public RequestManage(URL url, HessianProxyFactory factory, Class<?> type) {
        zkManage.setUrl(url);
        zkManage.setFactory(factory);
        zkManage.setType(type);
    }

    public void doManageHessianRequest(String methodName, Object[] args, String applicationName) {
        zkManage.setApplicationName(applicationName);
        executors.execute(() -> {
            zkManage.createMasterNode();
            zkManage.createInterfaceNodeAndAdd();
        });
    }
}
