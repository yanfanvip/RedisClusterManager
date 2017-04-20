package com.newegg.redis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.newegg.redis.cluster.RedisClusterTerminal;
import com.newegg.redis.leveldb.D_ClusterNode_Tree;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.leveldb.LevelTable;
import com.newegg.redis.model.M_clusterNode;
import com.newegg.redis.model.enums.RedisNodeStatus;
import com.newegg.redis.util.ClusterTreeUtil;

@Service
@Scope("singleton")
public class ClusterNodeService {
	
	/**
	 * 更新集群中所有节点信息
	 */
	public void addClusterNodes(String cluster, List<D_RedisClusterNode> clusterNodes) throws Exception {
		LevelTable.replace(cluster, D_RedisClusterNode.class, clusterNodes);
	}
	
	/**
	 * 根据Redis节点查询集群的所有Node节点
	 */
	public List<D_RedisClusterNode> getClusterNodesByRedis(String cluster, RedisClusterTerminal client) throws Exception{
		List<M_clusterNode> nodes = client.getClusterNode_list();
		List<D_RedisClusterNode> clusterNodes = new ArrayList<D_RedisClusterNode>();
		for (M_clusterNode n : nodes) {
			D_RedisClusterNode clusterNode = new D_RedisClusterNode();
			BeanUtils.copyProperties(clusterNode, n);
			clusterNodes.add(clusterNode);
		}
		return clusterNodes;
	}
	
	/**
	 * 查询指定集群的所有node节点
	 * @return 
	 */
	public List<D_RedisClusterNode> getAllClusterNodes(String cluster) throws Exception{
		return LevelTable.getAll(cluster, D_RedisClusterNode.class);
	}
	
	/**
	 * 查询指定集群的所有node节点
	 * @return 
	 */
	public Map<String, D_RedisClusterNode> getAllClusterNodeMap(String cluster) throws Exception{
		List<D_RedisClusterNode> all = getAllClusterNodes(cluster);
		Map<String, D_RedisClusterNode> map = new HashMap<String, D_RedisClusterNode>();
		all.forEach(n->{
			map.put(n.getNode(), n);
		});
		return map;
	}
	
	/**
	 * 查询指定集群的树
	 */
	public D_ClusterNode_Tree getClusterTree(String cluster) throws Exception{
		List<D_RedisClusterNode> list = LevelTable.getAll(cluster, D_RedisClusterNode.class);
		return ClusterTreeUtil.getLevelTree(list);
	}
	
	/**
	 * 将一个集群中的master slave节点交换位置
	 */
	public void toMaster(String cluster, String node) throws Exception{
		Map<String, D_RedisClusterNode> nodes = getAllClusterNodeMap(cluster);
		D_RedisClusterNode old_Slave = nodes.get(node);
		RedisClusterTerminal client = new RedisClusterTerminal(old_Slave.getHost(), old_Slave.getPort());
		try {
			client.clusterFailover();
		} finally {
			client.close();
		}
	}

	/**
	 * 从集群中删除节点
	 */
	public void forget(String cluster, String node) throws Exception {
		Map<String, D_RedisClusterNode> nodes = getAllClusterNodeMap(cluster);
		D_RedisClusterNode fnode = nodes.get(node);
		if(fnode != null){
			new RedisClusterTerminal(fnode.getHost(), fnode.getPort()).reset().close();
		}
		for (D_RedisClusterNode n : nodes.values()) {
			if(!node.equals(n.getNode()) && n.getStatus() == RedisNodeStatus.CONNECT){
				RedisClusterTerminal client = new RedisClusterTerminal(n.getHost(), n.getPort());
				try {
					client.forget(node);
					client.clusterSaveConfig();
				}catch(Exception e){} finally {
					client.close();
				}
			}
		}
	}

	/**
	 * 设置一个从节点的主节点
	 * @throws Exception 
	 */
	public void slaveof(String cluster, String master, String node) throws Exception {
		Map<String, D_RedisClusterNode> nodes = getAllClusterNodeMap(cluster);
		D_RedisClusterNode slave_node = nodes.get(node);
		RedisClusterTerminal client = new RedisClusterTerminal(slave_node.getHost(), slave_node.getPort());
		try {
			client.slaveOf(master);
		} finally {
			client.close();
		}
	}
}
