本项目是Redis Cluster集群的管理工具;

项目地址 ： https://git.oschina.net/yanfanVIP/RedisClusterManager

演示地址：http://59.110.161.181 (阿里云网速较慢，请多等待。请尽量不要将Redis集群弄坏，谢谢配合)

环境要求：Java8+

系统部署方式：

* 下载最新版安装包 https://git.oschina.net/yanfanVIP/RedisClusterManager/releases
* 解压RedisManager-Web-1.0.0-SNAPSHOT.tar.gz到安装目录
* 运行相应脚本，启动服务器
* 若需要监控server信息，则将相应的monitor服务部署到Redis服务器中，并指定上报频率和服务器IP地址
* 配置Resource包含一下内容

```
.
├── jre.1.7.x64.gz
├── jre.1.7.x86.gz
├── redis.3.0.6.x64.gz
├── redis.3.0.6.x86.gz
├── redis.conf.template
└── systemMonitor-release.tar.gz

```

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


#2017-4-22 update#
* 新增集群创建功能
* 新增自动化RedisCluster部署功能

![自动化创建集群](https://git.oschina.net/uploads/images/2017/0422/161332_269f2649_37113.jpeg "自动化创建集群")

![输入图片说明](https://git.oschina.net/uploads/images/2017/0422/161407_826e6953_37113.jpeg "在这里输入图片标题")

#创建集群方法#
##1：打开Manager首页，点击右上角的Create Cluster

##2：若没有在集群中注册redis打包文件，则点击Import Resource,并且上传Resource
![资源列表](https://git.oschina.net/uploads/images/2017/0422/162556_b7ed34f5_37113.jpeg "资源列表")

##3：若没有在集群中注册Server信息，则点击Import Server
![注册Server](https://git.oschina.net/uploads/images/2017/0422/162730_b7c95c8e_37113.jpeg "注册Server")

##4：点击Install Node, 进行Redis以及monitor的自动化安装
![Install Node](https://git.oschina.net/uploads/images/2017/0422/162900_83341e55_37113.jpeg "Install Node")

##5：安装完所有节点后，点击Create Cluster，填写Master数量，生成集群
![自动化创建集群](https://git.oschina.net/uploads/images/2017/0422/161332_269f2649_37113.jpeg "自动化创建集群")

##6：查询新建集群状态
![输入图片说明](https://git.oschina.net/uploads/images/2017/0422/163104_5c4f26ae_37113.jpeg "在这里输入图片标题")

#数据查询的方法#
##1：在Manager首页，点击集群进入集群操作界面

##2：点击右上角的Query按钮，进入查询界面

##3：在搜索框输入需要查询的key，或者使用通配符‘*’进行匹配
![Query](https://git.oschina.net/uploads/images/2017/0422/163334_df0daafb_37113.jpeg "Query")

##4：在查询的key列表中，点击查询数据详情
![输入图片说明](https://git.oschina.net/uploads/images/2017/0422/163505_02668e77_37113.jpeg "在这里输入图片标题")
