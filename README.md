# hessian可视化管理项目v1.0(一期功能)

## 技术栈
### hessian-client hessian客户端
### hessian-server hessian服务端
### hessian-api hessian接口信息
### hessian-manage
* 重写HessianProxyFactoryBean
* 重写HessianProxy
* zookeeper管理hessian接口信息
### hessian-ui 
* springboot
* vue
* element-ui
* zookeeper获取hessian信息
## 1.项目管理
* 项目列表
* 项目集群列表
* 集群信息  


![image](https://github.com/chenyaoBOY/document/raw/master/image/1.png)



## 2.接口管理

 1. 通过hessian消费者获取消费的所有hessian接口信息  
 2. 可观察某个hessian接口下的消费集群机器信息  
 3. 可统计某个hessian接口的访问量  
 4. 可统计某个hessian接口中被调用方法的访问量  

![image](https://github.com/chenyaoBOY/document/raw/master/image/2.png)
