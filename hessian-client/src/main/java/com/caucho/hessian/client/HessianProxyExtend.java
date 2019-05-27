package com.caucho.hessian.client;

import com.caucho.hessian.io.*;
import com.caucho.hessian.util.ResultUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chenyao
 * @date 2019/5/27 15:47
 * @description
 */
public class HessianProxyExtend extends HessianProxy {
    private static final Logger log = Logger.getLogger(HessianProxyExtend.class.getName());


    private URL _url;
    private WeakHashMap<Method, String> mangleMap = new WeakHashMap<>();

    protected HessianProxyExtend(URL url, HessianProxyFactory factory) {
        super(url, factory);
    }

    protected HessianProxyExtend(URL url, HessianProxyFactory factory, Class<?> type) {
        super(url, factory, type);
        this._url = url;
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
            //通过HessianURLConnection中的URLConnection 获取流
            is = this.getInputStream(conn);
            setInputStreamWithLogLevel(is);
            int code = is.read();
            if (code == 72) {
                int read = is.read();
                read = is.read();
                AbstractHessianInput in = this._factory.getHessian2Input(is);
                return in.readReply(method.getReturnType());
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
            HessianDebugOutputStream dOs = new HessianDebugOutputStream((OutputStream) os, dbg);
            dOs.startTop2();
            os = dOs;
        }
    }

    @Override
    protected HessianConnection sendRequest(String methodName, Object[] args) throws IOException {
        HessianConnection conn = null;
        conn = this._factory.getConnectionFactory().open(this._url);
        boolean isValid = false;
        try {
            this.addRequestHeaders(conn);
            OutputStream os;
            try {
                os = conn.getOutputStream();
            } catch (Exception var11) {
                throw new HessianRuntimeException(var11);
            }
            setOutputStreamWithLogLevel(os);
            AbstractHessianOutput out = this._factory.getHessianOutput(os);
            out.call(methodName, args);
            out.flush();
            conn.sendRequest();
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
                return ResultUtil.data(new Boolean(this._url.equals(handler.getURL())));
            }
            return ResultUtil.data(Boolean.FALSE);
        }

        if (methodName.equals("hashCode") && params.length == 0) {
            return ResultUtil.data(new Integer(this._url.hashCode()));
        }

        if (methodName.equals("getHessianType")) {
            return ResultUtil.data(proxy.getClass().getInterfaces()[0].getName());
        }

        if (methodName.equals("getHessianURL")) {
            return ResultUtil.data(this._url.toString());
        }

        if (methodName.equals("toString") && params.length == 0) {
            return ResultUtil.data("HessianProxy[" + this._url + "]");
        }
        return ResultUtil.fail("");
    }
}
