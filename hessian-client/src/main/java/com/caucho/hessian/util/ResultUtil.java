package com.caucho.hessian.util;

import java.io.Serializable;

/**
 * @author chenyao
 * @date 2019/5/27 16:56
 * @description
 */
public class ResultUtil<T> implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 9153954845389485800L;

    /**
     * errcode 错误编码，默认 0 = 成功   1=失败
     */
    private Integer errcode = 0;

    /**
     * errmsg 错误信息
     */
    private String errmsg;

    /**
     * 异常信息
     */
    private Exception omsException;

    /**
     * 返回结果
     */
    private T data;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }



    public Exception getOmsException() {
        return omsException;
    }

    public void setOmsException(Exception omsException) {
        this.omsException = omsException;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static ResultUtil fail(String msg){
        ResultUtil r = new ResultUtil();
        r.setErrcode(1);
        r.setErrmsg(msg);
        return r;
    }
    public static ResultUtil success(){
        return new ResultUtil();
    }
    public static ResultUtil data(Object object){
        ResultUtil r = new ResultUtil();
        r.setData(object);
        return r;
    }

    public static ResultUtil success(String msg){
        ResultUtil r = new ResultUtil();
        r.setErrmsg(msg);
        return r;
    }


}
