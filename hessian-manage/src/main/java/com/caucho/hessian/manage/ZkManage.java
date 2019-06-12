package com.caucho.hessian.manage;

import com.caucho.hessian.util.AddressUtil;
import com.caucho.hessian.util.NodeData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author chenyao
 * @date 2019/5/28 14:42
 * @description
 */
public class ZkManage {
    private Logger logger = LoggerFactory.getLogger(ZkManage.class);

    private static String APPLICATIONAME;
    private static ZkClient ZK_CLIENT;
    private static final String IP = AddressUtil.getIP();
    private static String DEFAULT_NAME = "default-project-" + IP;
    private static String PROJECT_NODE = "/project";
    private static String HESSIAN_NODE = "/hessian";

    /**
     * zookeeper初始化
     */
    static {
        InputStream stream = ZkManage.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException("load config.properties encounters a exception", e);
        }
        String zkUrl = properties.getProperty("zkUrl");
        String zkConnectionTimeOut = properties.getProperty("zkConnectionTimeOut");
        APPLICATIONAME = properties.getProperty("applicationName");
        if (StringUtils.isBlank(zkUrl)) {
            throw new RuntimeException("file in [config.properties] ：zkUrl is blank");
        }
        ZK_CLIENT = new ZkClient(zkUrl, StringUtils.isBlank(zkConnectionTimeOut) ? 10000 : Integer.valueOf(zkConnectionTimeOut));

        /**
         * 创建主节点
         * 通过zk建立项目父节点，集群在父节点下建立临时节点
         * 以此判断集群是否正常宕机
         */
        if (StringUtils.isBlank(APPLICATIONAME)) {
            APPLICATIONAME = DEFAULT_NAME;//如果config没有配置applicationName 则使用 default-project-127.0.0.1命名
        }
        if (!ZK_CLIENT.exists(PROJECT_NODE)) {//创建project节点，所有项目都在该父节点下创建
            try {
                ZK_CLIENT.createPersistent(PROJECT_NODE);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
        }
        if (!ZK_CLIENT.exists(HESSIAN_NODE)) {//创建hessian节点，所有接口都在该父节点下创建
            try {
                ZK_CLIENT.createPersistent(HESSIAN_NODE);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
        }
        PROJECT_NODE += "/";//  /project/
        if (!ZK_CLIENT.exists(PROJECT_NODE + APPLICATIONAME)) {//创建project子节点，/project/applicationName 永久的
            try {
                //这里需要注意的是 如果多台机器同时启动并创建节点（几率很低）
                // 会抛出异常，如果不捕获的话临时节点就会创建失败
                ZK_CLIENT.createPersistent(PROJECT_NODE + APPLICATIONAME);
            } catch (ZkNodeExistsException e) {
                if(!ZK_CLIENT.exists(PROJECT_NODE + APPLICATIONAME + "/" + IP))
                ZK_CLIENT.createEphemeral(PROJECT_NODE + APPLICATIONAME + "/" + IP);//创建application的临时节点 /project/applicationName/127.0.0.1
            }
        } else {
            if(!ZK_CLIENT.exists(PROJECT_NODE + APPLICATIONAME + "/" + IP))
            ZK_CLIENT.createEphemeral(PROJECT_NODE + APPLICATIONAME + "/" + IP);
        }
    }


    /**
     * @param methodName:
     * @param args:
     * @param url:
     * @param type:
     * @return void
     * @description //TODO 创建节点并计算请求次数
     * @author chenyao
     * @date 2019/6/6 17:51
     */
    public void createInterfaceNodeAndAdd(String methodName, Object[] args, URL url, Class<?> type) {
        String path = HESSIAN_NODE + "/" + type.getName();//  /hessian/com.service.Demo
        if (!ZK_CLIENT.equals(path)) {//先创建接口节点
            try {
                logger.info("create interface node name={}", path);
                ZK_CLIENT.createPersistent(path);
            } catch (ZkNodeExistsException e) {
                createInterfaceChildNode(path, methodName);
            }
        } else {
            createInterfaceChildNode(path, methodName);
        }
    }

    /**
     * @param path:
     * @param methodName:
     * @return void
     * @description //TODO 创建hessian接口下的消费集群节点
     * @author chenyao
     * @date 2019/6/6 17:51
     */
    public void createInterfaceChildNode(String path, String methodName) {
        Stat stat = new Stat();
        String childPath = path + "/" + IP; //  /hessian/com.service.Demo/127.0.0.1
        NodeData childData = readData(childPath, stat);
        if (childData == null) {//根据集群ip分组
            try {
                logger.info("create interface child node name={}", childPath);
                if(!ZK_CLIENT.exists(childPath))
                ZK_CLIENT.createEphemeral(childPath, new NodeData(0, new Date(), APPLICATIONAME));
            } catch (ZkNodeExistsException e) {//若分组已创建，则计数+1
                childData = readData(childPath, stat);
                doAddCount(childData, stat, childPath, methodName);
            }
            childData = readData(childPath, stat);
            doAddCount(childData, stat, childPath, methodName);
        } else {
            doAddCount(childData, stat, childPath, methodName);
        }
    }

    private NodeData readData(String path, Stat stat) {
        try {
            return ZK_CLIENT.readData(path, stat);
        } catch (ZkNoNodeException e) {
            return null;
        }
    }

    /**
     * @param data:
     * @param stat:
     * @param path:
     * @param methodName:
     * @return void
     * @description //TODO 计算请求次数
     * @author chenyao
     * @date 2019/6/6 17:52
     */
    private void doAddCount(NodeData data, Stat stat, String path, String methodName) {
        while (true) {
            Map<String, Integer> map = data.getMethodMap()==null?new HashMap<>(0):data.getMethodMap();
            if (map.get(methodName) == null) {
                map.put(methodName, 1);
            } else {
                map.put(methodName, map.get(methodName) + 1);
            }
            data.setMethodMap(map);
            data.setCount(data.getCount() + 1);
            try {
                if(ZK_CLIENT.exists(path)){
                    ZK_CLIENT.writeData(path, data, stat.getVersion());
                    break;
                }else{
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
