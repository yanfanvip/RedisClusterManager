package com.newegg.redis.cluster;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.redis.manager.context.AppConstants;
import org.redis.manager.leveldb.D_ClusterInfo;
import org.redis.manager.leveldb.D_ClusterNode_Tree;
import org.redis.manager.leveldb.D_ComputerInfo;
import org.redis.manager.leveldb.D_RedisClusterNode;
import org.redis.manager.leveldb.D_RedisInfo;
import org.redis.manager.leveldb.LevelTable;
import org.redis.manager.util.ClusterTreeUtil;

import com.alibaba.fastjson.JSON;

public class LevelScan {

	public static void main(String[] args) throws IOException {
		//init();
		scanCluster();
		LevelTable.close();
	}
	
	public static void init() throws IOException {
        D_ClusterInfo info = new D_ClusterInfo();
        info.setUuid(UUID.randomUUID().toString());
        info.setName("DEMO");
        info.setLast_read_host("10.16.236.133");
        info.setLast_read_port(8200);
        LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, info);
	}

	public static void scanCluster() throws IOException{
		LevelTable.iterator(AppConstants.LEVEL_DATABASES_SYSTEM, D_ClusterInfo.class, c ->{
			System.out.println("==============   " + c.getName() + "   ==============");
			try {
				scanNode(c);
				scanRedisInfo(c);
				scanServerInfo(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void scanNode(D_ClusterInfo c) throws Exception{
		System.out.println(">> scanNode");
		List<D_RedisClusterNode> list = LevelTable.getAll(c.getUuid(), D_RedisClusterNode.class);
		D_ClusterNode_Tree tree = ClusterTreeUtil.getLevelTree(list);
		String json = JSON.toJSONString(tree);
		System.out.println(json);
	}
	
	public static void scanRedisInfo(D_ClusterInfo c) throws IOException{
		System.out.println(">> scanRedisInfo");
		List<D_RedisInfo> redisInfos = LevelTable.getAll(c.getUuid(), D_RedisInfo.class);
		String json = JSON.toJSONString(redisInfos);
		System.out.println(json);
	}
	
	public static void scanServerInfo(D_ClusterInfo c) throws IOException{
		System.out.println(">> scanServerInfo");
		List<D_ComputerInfo> redisInfos = LevelTable.getAll(c.getUuid(), D_ComputerInfo.class);
		String json = JSON.toJSONString(redisInfos);
		System.out.println(json);
	}
}
