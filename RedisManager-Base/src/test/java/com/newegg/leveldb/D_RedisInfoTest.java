package com.newegg.leveldb;

import org.redis.manager.cluster.RedisClusterTerminal;
import org.redis.manager.leveldb.D_RedisInfo;
import org.redis.manager.leveldb.LevelTable;
import org.redis.manager.model.M_info;
import org.redis.manager.util.BeanUtils;

public class D_RedisInfoTest {

	public static void main(String[] args) throws Exception {
		RedisClusterTerminal client = new RedisClusterTerminal("10.16.236.133", 8200);
		M_info info = client.getInfo();
		D_RedisInfo redisInfo = new D_RedisInfo();
		BeanUtils.copyNotNullProperties(redisInfo, info);
		client.close();
		
		D_RedisInfo last = LevelTable.last("demo", D_RedisInfo.class);
		System.out.println(last);
		
		LevelTable.put("demo", redisInfo);
		
		D_RedisInfo now = LevelTable.last("demo", D_RedisInfo.class);
		System.out.println(now);
	}
	
}
