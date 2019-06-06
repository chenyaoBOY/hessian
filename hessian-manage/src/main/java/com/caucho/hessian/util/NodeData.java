package com.caucho.hessian.util;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author chenyao
 * @date 2019/5/28 16:00
 * @description
 */
public class NodeData implements Serializable {

    private long count;
    private String application;
    private Date date;
    private Map<String,Integer> methodMap;

    public NodeData() {
    }

    public NodeData(long count) {
        this.count = count;
    }

    public NodeData(long count, Date date, String application) {
        this.count = count;
        this.date = date;
        this.application = application;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Map<String, Integer> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(Map<String, Integer> methodMap) {
        this.methodMap = methodMap;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "count=" + count +
                ", application='" + application + '\'' +
                ", date=" + date +
                ", methodMap=" + methodMap +
                '}';
    }
}
