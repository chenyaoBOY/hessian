package org.study.hessianui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.study.hessianui.bean.InterfaceBean;
import org.study.hessianui.bean.ProjectBean;
import org.study.hessianui.bean.User;
import org.study.hessianui.service.HessianManageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenyao
 * @date 2019/6/11 10:39
 * @description
 */
@RestController
public class HessianManageController {

    @Autowired
    private HessianManageService service;

    @RequestMapping("/login")
    public boolean login(@RequestBody User user) {
        if ("admin".equals(user.getUsername()) && "123456".equals(user.getPassword())) {
            return true;
        }
        return false;
    }

    @RequestMapping("/getInterfaceList")
    public List<InterfaceBean> getInterfaceList(String interfaceName) {
        return service.getInterfaceList(interfaceName);
    }

    @RequestMapping("/project/getProjectList")
    public List<ProjectBean> getProjectList(String projectName) {
        Map<String, List<String>> map = service.getProjectList(projectName);
        List<ProjectBean> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String applicatinName = entry.getKey();
            List<ProjectBean> beans = new ArrayList<>();
            List<String> ipList = entry.getValue();
            for (String ipName : ipList) {
                ProjectBean bean = new ProjectBean();
                bean.setName(ipName);
                bean.setStatus(1);
                bean.setNum(1);
                beans.add(bean);
            }
            ProjectBean bean = new ProjectBean();
            bean.setName(applicatinName);
            bean.setChildren(beans);
            bean.setNum(beans.size());
            bean.setStatus(beans.size()> 0 ? 1 : 0);
            list.add(bean);
        }
        return list;
    }
}
