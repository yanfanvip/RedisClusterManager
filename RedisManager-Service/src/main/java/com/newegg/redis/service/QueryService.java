package com.newegg.redis.service;

import java.util.HashSet;
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

@Service
public class QueryService {

	@Autowired
	ClusterNodeService clusterNodeService;
	
	public ScanPage scan(String cluster, ScanPage scanPage) throws Exception {
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
}
