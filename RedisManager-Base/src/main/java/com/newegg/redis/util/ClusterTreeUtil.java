package com.newegg.redis.util;

import java.util.ArrayList;
import java.util.List;
import com.newegg.redis.leveldb.D_ClusterNode_Master;
import com.newegg.redis.leveldb.D_ClusterNode_Tree;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.model.M_clusterNode;
import com.newegg.redis.model.M_clusterNode_Master;
import com.newegg.redis.model.M_clusterNode_Tree;
import com.newegg.redis.model.enums.RedisClusterRole;
import com.newegg.redis.model.enums.RedisNodeStatus;

public class ClusterTreeUtil {
	
	public static D_ClusterNode_Tree getLevelTree(List<D_RedisClusterNode> list) throws Exception {
		boolean status = true;
		List<D_ClusterNode_Master> masters = new ArrayList<D_ClusterNode_Master>();
		for (D_RedisClusterNode n1 : list) {
			if(n1.getRole() == RedisClusterRole.MASTER){
				List<D_RedisClusterNode> slaves = new ArrayList<D_RedisClusterNode>();
				for (D_RedisClusterNode n2 : list) {
					if(n1.getNode().equals(n2.getMaster())){
						slaves.add(n2);
					}
				}
				D_ClusterNode_Master master = new D_ClusterNode_Master();
				master.setMaster(n1);
				master.setSlaves(slaves);
				masters.add(master);
				if(n1.getStatus() != RedisNodeStatus.CONNECT){
					status = false;
				}
			}
		}
		D_ClusterNode_Tree tree = new D_ClusterNode_Tree();
		tree.setMasters(masters);
		tree.setStatus(status);
		return tree;
	}
	
	public static M_clusterNode_Tree getTree(List<M_clusterNode> list) throws Exception {
		boolean status = true;
		List<M_clusterNode_Master> masters = new ArrayList<M_clusterNode_Master>();
		for (M_clusterNode n1 : list) {
			if(n1.getRole() == RedisClusterRole.MASTER){
				List<M_clusterNode> slaves = new ArrayList<M_clusterNode>();
				for (M_clusterNode n2 : list) {
					if(n1.getNode().equals(n2.getMaster())){
						slaves.add(n2);
					}
				}
				M_clusterNode_Master master = new M_clusterNode_Master();
				master.setMaster(n1);
				master.setSlaves(slaves);
				masters.add(master);
				if(n1.getStatus() != RedisNodeStatus.CONNECT){
					status = false;
				}
			}
		}
		M_clusterNode_Tree tree = new M_clusterNode_Tree();
		tree.setMasters(masters);
		tree.setStatus(status);
		return tree;
	}
}
