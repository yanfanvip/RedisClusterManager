package com.newegg.redis.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.leveldb.D_RedisClusterNode;

public class ClusterServerCache {
	
	static Map<String, Set<String>> clusterServers = new HashMap<String, Set<String>>();
	
	public static Set<String> getClustersByServer(String host){
		Set<String> servers = new HashSet<String>();
		clusterServers.forEach((k,v)->{
			if(v.contains(host)){
				servers.add(k);
			}
		});
		return servers;
	}
	
	public static boolean clusterExist(String uuid){
		return clusterServers.containsKey(uuid);
	}
	
	public static void deleteCluster(String uuid){
		clusterServers.remove(uuid);
	}
	
	public static void updateServer(D_ClusterInfo c, List<D_RedisClusterNode> new_RedisClusterNodes){
		HashSet<String> servers = new HashSet<String>();
		if(new_RedisClusterNodes != null){
			for (D_RedisClusterNode n : new_RedisClusterNodes) {
				servers.add(n.getHost());
			}
		}
		clusterServers.put(c.getUuid(), servers);
	}
}
