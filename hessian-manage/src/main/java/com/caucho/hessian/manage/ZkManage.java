package com.caucho.hessian.manage;

import com.caucho.hessian.util.AddressUtil;
import com.caucho.hessian.util.NodeData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
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
    private static String DEFAULT_NAME="default-project-"+IP;
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
            APPLICATIONAME = DEFAULT_NAME;
        }
        if (!ZK_CLIENT.exists("/" + APPLICATIONAME)) {
            try {
                //这里需要注意的是 如果多台机器同时启动并创建节点（几率很低）
                // 会抛出异常，如果不捕获的话临时节点就会创建失败
                ZK_CLIENT.createPersistent("/" + APPLICATIONAME);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
            ZK_CLIENT.createEphemeral("/" + APPLICATIONAME + "/" + IP);
        } else {
            ZK_CLIENT.createEphemeral("/" + APPLICATIONAME + "/" + IP);
        }
    }


    /**
     * @description //TODO 创建节点并计算请求次数
     * @author chenyao
     * @date  2019/6/6 17:51
     * @param methodName:
     * @param args:
     * @param url:
     * @param type:
     * @return void
     */
    public void createInterfaceNodeAndAdd(String methodName, Object[] args, URL url, Class<?> type) {
        String path = "/" + type.getName();
        Stat stat = new Stat();
        NodeData data = ZK_CLIENT.readData(path, stat);
        if (data == null) {//先创建接口节点
            try {
                logger.info("create interface node name={}", path);
                ZK_CLIENT.createPersistent(path);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
            createInterfaceChildNode(path, methodName);
        } else {
            createInterfaceChildNode(path, methodName);
        }
    }

    /**
     * @description //TODO 创建hessian接口下的消费集群节点
     * @author chenyao
     * @date  2019/6/6 17:51
     * @param path:
     * @param methodName:
     * @return void
     */
    public void createInterfaceChildNode(String path, String methodName) {
        Stat stat = new Stat();
        String childPath = path + "/" + IP;
        NodeData childData = ZK_CLIENT.readData(childPath, stat);
        if (childData == null) {//根据集群ip分组
            try {
                logger.info("create interface child node name={}", childPath);
                ZK_CLIENT.createEphemeral(path + "/" + IP, new NodeData(1, new Date(), APPLICATIONAME));
            } catch (ZkNodeExistsException e) {//若分组已创建，则计数+1
                childData = ZK_CLIENT.readData(childPath, stat);
                doAddCount(childData, stat, childPath, methodName);
            }
        } else {
            doAddCount(childData, stat, childPath, methodName);
        }
    }

    /**
     * @description //TODO 计算请求次数
     * @author chenyao
     * @date  2019/6/6 17:52
     * @param data:
     * @param stat:
     * @param path:
     * @param methodName:
     * @return void
     */
    private void doAddCount(NodeData data, Stat stat, String path, String methodName) {
        while (true) {
            Map<String, Integer> map = data.getMethodMap();
            if (map.get(methodName) == null) {
                map.put(methodName, 1);
            } else {
                map.put(methodName, map.get(methodName) + 1);
            }
            data.setCount(data.getCount() + 1);
            try {
                ZK_CLIENT.writeData(path, data, stat.getVersion());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
