package org.study.hessianui.service;

import com.caucho.hessian.util.NodeData;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.study.hessianui.bean.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyao
 * @date 2019/6/11 10:42
 * @description
 */
@Service
public class HessianManageService {

    private String PROJECT_PATH = "/project";
    private String HESSIAN_PATH = "/hessian";
    private ZkClient client = new ZkClient("localhost");

    public List<InterfaceBean> getInterfaceList(String interfaceName) {
        List<InterfaceBean> result = new ArrayList<>();
        if (StringUtils.isEmpty(interfaceName)) {
            List<String> interfaceList = getChildren(HESSIAN_PATH);
            for (String name : interfaceList) {
                List<String> ipList = getChildren(HESSIAN_PATH + "/" + name);
                List<IpInfo> ipInfos = new ArrayList<>();
                for (String ip : ipList) {
                    NodeData node = client.readData(HESSIAN_PATH + "/" + name + "/" + ip);
                    if(node==null) continue;
                    Map<String/*methodName*/, Integer/*methodCount*/> methodMap = node.getMethodMap();
                    IpInfo ipInfo = new IpInfo();

                    List<MethodInfo> methodInfos = new ArrayList<>();
                    if(methodMap !=null){
                        for (String methodName : methodMap.keySet()) {
                            methodInfos.add(new MethodInfo(methodName,methodMap.get(methodName)));
                        }
                    }
                    ipInfo.setIp(ip);
                    ipInfo.setApplicationName(node.getApplication());
                    ipInfo.setMethodNum(methodInfos.size());
                    ipInfo.setTotalNum(node.getCount());
                    ipInfo.setMethodInfoList(methodInfos);
                    ipInfo.setDate(node.getDate());
                    ipInfos.add(ipInfo);
                }
                InterfaceBean bean = new InterfaceBean();
                bean.setInterfaceName(name);
                bean.setIpInfoList(ipInfos);
                bean.setNum(ipInfos.size());
                bean.setStatus(ipInfos.size()>0?1:0);
                result.add(bean);
            }
        } else {
        }
        return result;
    }

    private List<String> getChildren(String path) {
        List<String> children = client.getChildren(path);
        if (children == null) {
            return new ArrayList<>();
        }
        return children;
    }

    public Map<String, List<String>> getProjectList(String projectName) {
        Map<String, List<String>> map = new HashMap<>(16);
        if(StringUtils.isEmpty(projectName)){//全局搜索
            List<String> applicationList = getChildren(PROJECT_PATH);//项目名称
            for (String applicationName : applicationList) {
                List<String> ipList = getChildren(PROJECT_PATH + "/" + applicationName);
                map.put(applicationName,ipList);
            }
        }else{
            List<String> ipList = getChildren(PROJECT_PATH + "/" + projectName);
            map.put(projectName,ipList);
        }
        return map;
    }
}
