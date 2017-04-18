package com.newegg.leveldb;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import com.newegg.redis.cluster.RedisClusterTerminal;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.leveldb.LevelTable;
import com.newegg.redis.model.M_clusterNode;

public class D_RedisClusterNodeTest {

	public static void main(String[] args) throws Exception {
		RedisClusterTerminal client = new RedisClusterTerminal("10.16.236.133", 8200);
		List<M_clusterNode> info = client.getClusterNode_list();
		List<D_RedisClusterNode> list = new ArrayList<D_RedisClusterNode>();
		for (M_clusterNode n1 : info) {
			D_RedisClusterNode n2 = new D_RedisClusterNode();
			BeanUtils.copyProperties(n2, n1);
			list.add(n2);
		}
		client.close();
		
		LevelTable.put("demo", D_RedisClusterNode.class, list);
		
		LevelTable.iterator("demo", D_RedisClusterNode.class, n ->{
			System.out.println(n);
		});
	}
	
}
