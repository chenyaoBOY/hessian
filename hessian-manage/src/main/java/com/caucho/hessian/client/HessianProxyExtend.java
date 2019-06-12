package com.caucho.hessian.client;

import com.caucho.hessian.manage.RequestManage;
import com.caucho.hessian.manage.ZkManage;
import com.caucho.hessian.util.ResultUtil;
import com.caucho.hessian.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chenyao
 * @date 2019/5/27 15:47
 * @description 多例 xml中配置的接口列表都会创建一次
 */
public class HessianProxyExtend extends HessianProxy {
    private static final Logger log = Logger.getLogger(HessianProxyExtend.class.getName());

    private WeakHashMap<Method, String> mangleMap = new WeakHashMap<>();
    private URL url;
    private RequestManage requestManage;
    private Class<?> type;

    protected HessianProxyExtend(URL url, HessianProxyFactory factory) {
        super(url, factory);
    }

    protected HessianProxyExtend(URL url, HessianProxyFactoryExtend factory, Class<?> type) {
        super(url, factory, type);
        this.url = url;
        this.type = type;
        this.requestManage = factory.getRequestManage();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**缓存方法 若方法已经缓存 表示该方法不是getObj4SpecifiedMethod中的特定方法*/
        String mangleName;
        synchronized (this.mangleMap) {
            mangleName = this.mangleMap.get(method);
        }
        if (mangleName == null) {
            ResultUtil resultUtil = getObj4SpecifiedMethod(method, proxy, args);
            if (resultUtil.getErrcode() == 0) {
                return resultUtil.getData();
            }
            if (!this._factory.isOverloadEnabled()) {
                mangleName = method.getName();
            } else {
                mangleName = this.mangleName(method);
            }
            synchronized (this.mangleMap) {
                this.mangleMap.put(method, mangleName);
            }
        }

        /**远程连接的逻辑*/
        InputStream is = null;
        HessianConnection conn = null;
        try {
            conn = this.sendRequest(mangleName, args);
            //通过HessianURLConnection中的URLConnection 获取输入流
            is = this.getInputStream(conn);
            setInputStreamWithLogLevel(is);
            int code = is.read();
            if (code == 72) {
                int read = is.read();
                read = is.read();
                AbstractHessianInput in = this._factory.getHessian2Input(is);
                return in.readReply(method.getReturnType());//当服务出现异常时，读取的时候消费者才会抛异常
            } else if (code == 114) {
                is.read();
                is.read();
                AbstractHessianInput in = this._factory.getHessianInput(is);
                in.startReplyBody();
                Object value = in.readObject(method.getReturnType());
                if (value instanceof InputStream) {
                    value = new HessianProxy.ResultInputStream(conn, is, in, (InputStream) value);
                    is = null;
                    conn = null;
                } else {
                    in.completeReply();
                }
                return value;
            } else {
                throw new HessianProtocolException("'" + (char) code + "' is an unknown code");
            }
        } catch (HessianProtocolException var30) {
            throw new HessianRuntimeException(var30);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception var27) {
                log.log(Level.FINE, var27.toString(), var27);
            }
            try {
                if (conn != null) {
                    conn.destroy();
                }
            } catch (Exception var26) {
                log.log(Level.FINE, var26.toString(), var26);
            }
        }

    }


    @Override
    protected HessianConnection sendRequest(String methodName, Object[] args) throws IOException {
        //建立连接 这里需要设置超时时间
        HessianConnection conn = this._factory.getConnectionFactory().open(this.url);
        this.addRequestHeaders(conn);//添加请求头信息
        boolean isValid = false;
        OutputStream os;
        try {
            try {
                //此处内部源码通过HttpClient建立连接
                //hessian连接入口
                os = conn.getOutputStream();//此处若URL有误 异常提示连接失败 Connection refused: connect
                //此处没有异常 表明远程hessianURL地址可以访问
                requestManage.doManageHessianRequest(methodName, args,url,type);
            } catch (Exception e) {
                throw new HessianRuntimeException(e);
            }
            setOutputStreamWithLogLevel(os);//没啥用

            //将请求参数 放入输出流
            AbstractHessianOutput out = this._factory.getHessianOutput(os);
            out.call(methodName, args);
            out.flush();
            //发送HTTP请求
            conn.sendRequest();
            //hessian远程调用完成

            isValid = true;
            return conn;
        } finally {
            if (!isValid && conn != null) {
                conn.destroy();
            }
        }

    }


    private ResultUtil getObj4SpecifiedMethod(Method method, Object proxy, Object[] args) {
        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();
        if (methodName.equals("equals") && params.length == 1 && params[0].equals(Object.class)) {
            Object value = args[0];
            if (value != null && Proxy.isProxyClass(value.getClass())) {
                Object proxyHandler = Proxy.getInvocationHandler(value);
                if (!(proxyHandler instanceof HessianProxy)) {
                    return ResultUtil.data(Boolean.FALSE);
                }

                HessianProxy handler = (HessianProxy) proxyHandler;
                return ResultUtil.data(new Boolean(this.url.equals(handler.getURL())));
            }
            return ResultUtil.data(Boolean.FALSE);
        }

        if (methodName.equals("hashCode") && params.length == 0) {
            return ResultUtil.data(new Integer(this.url.hashCode()));
        }

        if (methodName.equals("getHessianType")) {
            return ResultUtil.data(proxy.getClass().getInterfaces()[0].getName());
        }

        if (methodName.equals("getHessianURL")) {
            return ResultUtil.data(this.url.toString());
        }

        if (methodName.equals("toString") && params.length == 0) {
            return ResultUtil.data("HessianProxy[" + this.url + "]");
        }
        return ResultUtil.fail("");
    }

    private void setInputStreamWithLogLevel(InputStream is) {
        if (log.isLoggable(Level.FINEST)) {
            PrintWriter dbg = new PrintWriter(new HessianProxy.LogWriter(log));
            HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
            dIs.startTop2();
            is = dIs;

        }
    }

    private void setOutputStreamWithLogLevel(OutputStream os) {
        if (log.isLoggable(Level.FINEST)) {
            PrintWriter dbg = new PrintWriter(new HessianProxy.LogWriter(log));
            HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
            dOs.startTop2();
            os = dOs;
        }
    }

}
