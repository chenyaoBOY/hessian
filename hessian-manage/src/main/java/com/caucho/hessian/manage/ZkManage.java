package com.caucho.hessian.manage;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.util.AddressUtil;
import com.caucho.hessian.util.NodeData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author chenyao
 * @date 2019/5/28 14:42
 * @description
 */
public class ZkManage {
    private Logger logger = LoggerFactory.getLogger(ZkManage.class);

    private String applicationName;
    private URL url;
    private HessianProxyFactory factory;
    private Class<?> type;

    private static final ZkClient client = new ZkClient("localhost", 10000);
    private boolean ifHasNode;

    /**
     * 创建主节点
     * 通过zk建立项目父节点，集群在父节点下建立临时节点
     * 以此判断集群是否正常宕机
     */
    public void createMasterNode() {
        logger.info("-----------------------");
        if (ifHasNode) {
            return;
        }
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "default-project";
        }
        if (!client.exists("/" + applicationName)) {
            try {
                //这里需要注意的是 如果多台机器同时启动并创建节点（几率很低）
                // 会抛出异常，如果不捕获的话临时节点就会创建失败
                client.createPersistent("/" + applicationName);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
            client.createEphemeral("/" + applicationName + "/" + AddressUtil.getIP());
        } else {
            client.createEphemeral("/" + applicationName + "/" + AddressUtil.getIP());
        }
        ifHasNode = true;
    }

    public void createInterfaceNodeAndAdd() {
        String path = "/" + type.getName();
        if (!client.exists(path)) {//先创建接口节点
            try {
                client.createPersistent(path);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
            if (!client.exists(path + "/" + AddressUtil.getIP())) {//根据集群ip分组
                try {
                    client.createEphemeral(path + "/" + AddressUtil.getIP(), new NodeData(1, new Date()));
                } catch (ZkNodeExistsException e) {//若分组已创建，则计数+1
                    doAddCount(path);
                }
            }
        } else {
            doAddCount(path);
        }
    }

    private void doAddCount(String path) {
        while (true) {
            Stat stat = new Stat();
            NodeData data = client.readData(path, stat);
            data.setCount(data.getCount() + 1);
            try {
                client.writeData(path, data, stat.getVersion());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setFactory(HessianProxyFactory factory) {
        this.factory = factory;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
