package com.newegg.redis.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newegg.redis.cluster.RedisClusterScan;
import com.newegg.redis.leveldb.D_ClusterNode_Master;
import com.newegg.redis.leveldb.D_ClusterNode_Tree;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.model.ScanPage;
import com.newegg.redis.model.enums.RedisNodeStatus;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Service
public class QueryService {

	@Autowired
	ClusterNodeService clusterNodeService;
	
	public ScanPage scan(String cluster, ScanPage scanPage) throws Exception {
		if(scanPage.getQuery().indexOf("*") != -1){
			return query(cluster, scanPage);
		}else{
			return exist(cluster, scanPage);
		}
	}
	
	private ScanPage exist(String cluster, ScanPage scanPage) throws Exception {
		scanPage.setHasMore(false);
		List<D_RedisClusterNode> nodes = clusterNodeService.getAllClusterNodes(cluster);
		Set<HostAndPort> masters = new HashSet<HostAndPort>();
		nodes.forEach(node->{
			masters.add(new HostAndPort(node.getHost(), node.getPort()));
		});
		JedisCluster jedis = new JedisCluster(masters);
		try {
			if(jedis.exists(scanPage.getQuery())){
				scanPage.setKeys(new HashSet<String>());
				scanPage.getKeys().add(scanPage.getQuery());
			}
		} finally {
			jedis.close();
		}
		return scanPage;
	}

	public ScanPage query(String cluster, ScanPage scanPage) throws Exception {
		D_ClusterNode_Tree tree = clusterNodeService.getClusterTree(cluster);
		Set<HostAndPort> masters = new HashSet<HostAndPort>();
		for (D_ClusterNode_Master nodes : tree.getMasters()) {//每一个分片获取一个节点
			D_RedisClusterNode node = nodes.getMaster();
			if(node.getStatus() == RedisNodeStatus.CONNECT){
				masters.add(new HostAndPort(node.getHost(), node.getPort()));
			}else{
				for (D_RedisClusterNode slave : nodes.getSlaves()) {
					if(slave.getStatus() == RedisNodeStatus.CONNECT){
						masters.add(new HostAndPort(slave.getHost(), slave.getPort()));
						break;
					}
				}
			}
		}
		RedisClusterScan scan = new RedisClusterScan(masters);
		scanPage.setKeys(null);
		return scan.scan(scanPage);
	}
	
	public Object get(String cluster, String key) throws Exception {
		List<D_RedisClusterNode> nodes = clusterNodeService.getAllClusterNodes(cluster);
		Set<HostAndPort> masters = new HashSet<HostAndPort>();
		nodes.forEach(node->{
			masters.add(new HostAndPort(node.getHost(), node.getPort()));
		});
		Object value = null;
		JedisCluster jedis = new JedisCluster(masters);
		try {
			String type = jedis.type(key);
			switch (type) {
			case "string":
				value = jedis.get(key);
				break;
			case "list":
				value = jedis.lrange(key, 0, -1);
				break;
			case "set":
				value = jedis.smembers(key);
				break;
			case "zset":
				value = jedis.zrange(key, 0, -1);
				break;
			case "hash":
				value = jedis.hgetAll(key);
				break;
			default:
				break;
			}
		} finally {
			jedis.close();
		}
		return value;
	}

	public void delete(String cluster, String key) throws Exception {
		List<D_RedisClusterNode> nodes = clusterNodeService.getAllClusterNodes(cluster);
		Set<HostAndPort> masters = new HashSet<HostAndPort>();
		nodes.forEach(node->{
			masters.add(new HostAndPort(node.getHost(), node.getPort()));
		});
		JedisCluster jedis = new JedisCluster(masters);
		try {
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}
}
