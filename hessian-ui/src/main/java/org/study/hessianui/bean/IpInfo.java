package org.study.hessianui.bean;

import java.util.Date;
import java.util.List;

/**
 * @author chenyao
 * @date 2019/6/12 10:44
 * @description
 */
public class IpInfo{
    private String ip;
    private String applicationName;
    private Long totalNum;
    private Date date;
    private Integer methodNum;
    private List<MethodInfo> methodInfoList;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getMethodNum() {
        return methodNum;
    }

    public void setMethodNum(Integer methodNum) {
        this.methodNum = methodNum;
    }

    public List<MethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<MethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }
}
