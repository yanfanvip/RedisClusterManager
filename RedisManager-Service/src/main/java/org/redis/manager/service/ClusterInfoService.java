package org.redis.manager.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.redis.manager.cluster.RedisClusterTerminal;
import org.redis.manager.context.AppConstants;
import org.redis.manager.exceptions.ParameterException;
import org.redis.manager.leveldb.D_ClusterInfo;
import org.redis.manager.leveldb.D_ComputerInfo;
import org.redis.manager.leveldb.D_RedisClusterNode;
import org.redis.manager.leveldb.D_RedisInfo;
import org.redis.manager.leveldb.LevelTable;
import org.redis.manager.model.M_clusterInfo;
import org.redis.manager.monitor.MonitorRedis;
import org.redis.manager.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ClusterInfoService {
	@Autowired
	MonitorRedis monitor;
	
	/**
	 * 添加一个集群到数据库中
	 * @return 
	 */
	public D_ClusterInfo addClusterInfo(D_ClusterInfo info) throws Exception{
		if(StringUtils.isBlank(info.getName())){
			throw new ParameterException("cluster name can not empty");
		}
		LevelTable.iterator(AppConstants.LEVEL_DATABASES_SYSTEM, D_ClusterInfo.class, d->{
			if(d.getName().equals(info.getName())){
				throw new ParameterException("cluster name ["+ info.getName() +"] has exist");
			}
		});
		info.setUuid(UUID.randomUUID().toString());
		LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, info);
		monitor.updateCluster(info);
		return info;
	}
	
	/**
	 * 更新一个集群到数据库中
	 */
	public void updateClusterInfo(D_ClusterInfo info) throws Exception{
		if(StringUtils.isBlank(info.getName())){
			throw new ParameterException("cluster name can not empty");
		}
		if(StringUtils.isBlank(info.getUuid())){
			throw new ParameterException("cluster id can not empty");
		}
		LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, info);
	}
	
	/**
	 * 删除指定集群
	 * @param cluster
	 * @throws IOException 
	 */
	public void delete(String cluster) throws IOException {
		LevelTable.delete(AppConstants.LEVEL_DATABASES_SYSTEM, D_ClusterInfo.class, cluster);
		LevelTable.destroy(cluster, D_RedisInfo.class);
		LevelTable.destroy(cluster, D_ComputerInfo.class);
		LevelTable.destroy(cluster, D_RedisClusterNode.class);
		String path = LevelTable.path(cluster, D_RedisClusterNode.class);
		File home = new File(path).getParentFile();
		home.delete();
	}
	
	/**
	 * 从Redis中查询一个集群的信息
	 * @return 
	 */
	public M_clusterInfo getClusterInfoByRedis(RedisClusterTerminal client) throws Exception{
		return client.getClusterInfo();
	}
	
	/**
	 * 从数据库中查询cluster信息
	 */
	public D_ClusterInfo getClusterInfo(String id) throws Exception{
		return LevelTable.get(AppConstants.LEVEL_DATABASES_SYSTEM, D_ClusterInfo.class, id);
	}
	
	/**
	 * 根据从Redis中查询到的数据更新数据库
	 * @return 
	 */
	public D_ClusterInfo updateClusterInfoByRedis(String id, M_clusterInfo info) throws Exception {
		D_ClusterInfo clusterInfo = new D_ClusterInfo();
		D_ClusterInfo old_ClusterInfo = getClusterInfo(id);
		BeanUtils.copyNotNullProperties(clusterInfo, old_ClusterInfo);
		BeanUtils.copyNotNullProperties(clusterInfo, info);
		updateClusterInfo(clusterInfo);
		return clusterInfo;
	}

	/**
	 * 获取所有集群
	 */
	public List<D_ClusterInfo> getAll() throws IOException {
		return LevelTable.getAll(AppConstants.LEVEL_DATABASES_SYSTEM, D_ClusterInfo.class);
	}

	/**
	 * 向集群中添加节点
	 */
	public void addNode(String cluster, String host, int port) throws Exception {
		D_ClusterInfo clusterInfo = getClusterInfo(cluster);
		RedisClusterTerminal clusterTerminal = new RedisClusterTerminal(host, port);
		try {
			clusterTerminal.clusterMeet(clusterInfo.getLast_read_host(), clusterInfo.getLast_read_port());
			clusterTerminal.clusterSaveConfig();
		} finally {
			clusterTerminal.close();
		}
	}
}