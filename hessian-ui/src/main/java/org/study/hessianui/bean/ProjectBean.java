package org.study.hessianui.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chenyao
 * @date 2019/6/11 10:41
 * @description
 */
public class ProjectBean implements Serializable {
    private String name;
    private Date date;
    private Integer num;
    private Integer status;
    private List<ProjectBean> children;


    public List<ProjectBean> getChildren() {
        return children;
    }

    public void setChildren(List<ProjectBean> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
