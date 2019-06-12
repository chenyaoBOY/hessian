package org.study.hessianui.bean;

import java.util.Date;
import java.util.List;

/**
 * @author chenyao
 * @date 2019/6/12 10:41
 * @description
 */
public class InterfaceBean {

    private String interfaceName;
    private Date date;
    private Integer num;
    private Integer status;

    private List<IpInfo> ipInfoList;


    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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

    public List<IpInfo> getIpInfoList() {
        return ipInfoList;
    }

    public void setIpInfoList(List<IpInfo> ipInfoList) {
        this.ipInfoList = ipInfoList;
    }
}
