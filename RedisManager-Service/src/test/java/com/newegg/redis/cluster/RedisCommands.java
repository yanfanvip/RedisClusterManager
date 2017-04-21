package com.newegg.redis.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.newegg.redis.util.RandomUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisCommands {

	public static void main(String[] args) throws IOException {
		List<HostAndPort> list = new ArrayList<HostAndPort>(){
			private static final long serialVersionUID = 1L;
			{
				add(new HostAndPort("10.16.236.133", 8200));
				add(new HostAndPort("10.16.236.133", 8201));
				add(new HostAndPort("10.16.236.133", 8202));
				add(new HostAndPort("10.16.236.133", 8203));
				add(new HostAndPort("10.16.236.133", 8204));
				add(new HostAndPort("10.16.236.133", 8205));
				add(new HostAndPort("10.16.236.133", 8206));
				add(new HostAndPort("10.16.236.133", 8207));
				add(new HostAndPort("10.16.236.133", 8208));
				add(new HostAndPort("10.16.236.133", 8209));
			}
		};
		
		int i=80;
		while(i-->0){
			JedisCluster cluster = new JedisCluster(list.get(RandomUtil.nextInt(0, 9)));
			cluster.set("sg"+i, "aaabc:" + i);
			cluster.close();
		}
		
	}
	
}
