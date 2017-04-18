package com.newegg.leveldb;

import org.apache.commons.beanutils.BeanUtils;
import com.newegg.redis.cluster.RedisClusterTerminal;
import com.newegg.redis.leveldb.D_RedisInfo;
import com.newegg.redis.leveldb.LevelTable;
import com.newegg.redis.model.M_info;

public class D_RedisInfoTest {

	public static void main(String[] args) throws Exception {
		RedisClusterTerminal client = new RedisClusterTerminal("10.16.236.133", 8200);
		M_info info = client.getInfo();
		D_RedisInfo redisInfo = new D_RedisInfo();
		BeanUtils.copyProperties(redisInfo, info);
		client.close();
		
		D_RedisInfo last = LevelTable.last("demo", D_RedisInfo.class);
		System.out.println(last);
		
		LevelTable.put("demo", redisInfo);
		
		D_RedisInfo now = LevelTable.last("demo", D_RedisInfo.class);
		System.out.println(now);
	}
	
}
