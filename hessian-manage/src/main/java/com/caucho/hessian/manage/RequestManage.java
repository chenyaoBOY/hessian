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

    private ConcurrentHashMap<String, Class<?>> url2Class;

    private ZkManage zkManage = new ZkManage();
    private static final ThreadPoolExecutor executors = new ThreadPoolExecutor(4, 8,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public void doManageHessianRequest(String methodName, Object[] args, URL url, Class<?> type) {
        url2Class.put(url.toString(),type);
        executors.execute(() -> {
            zkManage.createInterfaceNodeAndAdd(methodName,args,url,type);
        });
    }

    public ConcurrentHashMap<String, Class<?>> getUrl2Class() {
        return url2Class;
    }
}
