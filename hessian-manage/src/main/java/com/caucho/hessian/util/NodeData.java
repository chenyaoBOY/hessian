package com.caucho.hessian.util;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenyao
 * @date 2019/5/28 16:00
 * @description
 */
public class NodeData implements Serializable {

    private long count;
    private String application;
    private Date date;

    public NodeData() {
    }

    public NodeData(long count) {
        this.count = count;
    }

    public NodeData(long count, Date date) {
        this.count = count;
        this.date = date;
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

    @Override
    public String toString() {
        return "NodeData{" +
                "count=" + count +
                ", application='" + application + '\'' +
                ", date=" + date +
                '}';
    }
}
