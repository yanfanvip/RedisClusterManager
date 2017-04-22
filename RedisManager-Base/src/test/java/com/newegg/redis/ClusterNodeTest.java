package com.newegg.redis;

import java.util.List;

import org.redis.manager.cluster.RedisClusterTerminal;
import org.redis.manager.model.M_clusterNode;

public class ClusterNodeTest {
	public static void main(String[] args) throws Exception {
		RedisClusterTerminal client = new RedisClusterTerminal("10.16.236.59", 8200);
		try {
			List<M_clusterNode> list = client.getClusterNode_list();
			System.out.println(list);
		} finally {
			client.close();
		}
	}
}
