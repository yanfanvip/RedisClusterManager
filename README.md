本项目是Redis Cluster集群的管理工具;

项目地址 ： https://git.oschina.net/yanfanVIP/RedisClusterManager

环境要求：Java8+

系统部署方式：

* 下载最新版安装包 https://git.oschina.net/yanfanVIP/RedisClusterManager/releases
* 解压RedisManager-Web-1.0.0-SNAPSHOT.tar.gz到安装目录
* 运行相应脚本，启动服务器
* 若需要监控server信息，则将相应的monitor服务部署到Redis服务器中，并指定上报频率和服务器IP地址


基于Java开发，数据库采用了嵌入式Leveldb, 方便部署。

项目的主要功能有以下几点：

##1：集群监控功能##
可以同时对多个集群的状态进行监控，
![集群监控](https://git.oschina.net/uploads/images/2017/0419/170942_b0e86736_37113.jpeg "集群监控")

##2：集群状态查询##
图形化展示集群的主从关系，实时更新节点的请求量等数据，部署Monitor工具后，还可以监测到机器的硬件占用情况
![集群从属关系监控](https://git.oschina.net/uploads/images/2017/0419/171259_82ccbfa8_37113.jpeg "集群从属关系监控")
![集群数据化分析](https://git.oschina.net/uploads/images/2017/0419/171333_2e5a44a0_37113.jpeg "集群数据化分析")

##3: 集群节点管理功能##
树状结构展示集群的主从关系，并且可以实时修改集群节点关系
![集群主从关系](https://git.oschina.net/uploads/images/2017/0419/171531_da3fddf2_37113.jpeg "集群主从关系")
从节点重新设置主节点
![设置Slave](https://git.oschina.net/uploads/images/2017/0419/171830_099b87a2_37113.jpeg "设置Slave")
###主从切换###
![主从切换](https://git.oschina.net/uploads/images/2017/0419/171902_d8c8dc2b_37113.jpeg "主从切换")
###槽迁移###
![槽迁移](https://git.oschina.net/uploads/images/2017/0419/171930_a40b6533_37113.jpeg "槽迁移")

##4：集群数据管理##
集群数据查询
![scan](https://git.oschina.net/uploads/images/2017/0419/172117_a7256d26_37113.jpeg "scan")
![get data](https://git.oschina.net/uploads/images/2017/0419/172133_94c81f28_37113.jpeg "get data")