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
				add(new HostAndPort("192.168.3.50", 8200));
				add(new HostAndPort("192.168.3.50", 8201));
				add(new HostAndPort("192.168.3.50", 8202));
				add(new HostAndPort("192.168.3.50", 8203));
				add(new HostAndPort("192.168.3.50", 8204));
				add(new HostAndPort("192.168.3.50", 8205));
				add(new HostAndPort("192.168.3.50", 8206));
				add(new HostAndPort("192.168.3.50", 8207));
				add(new HostAndPort("192.168.3.50", 8208));
				add(new HostAndPort("192.168.3.50", 8209));
			}
		};
		
		int i=10000;
		while(i-->0){
			JedisCluster cluster = new JedisCluster(list.get(RandomUtil.nextInt(0, 9)));
			cluster.set("demo"+i, "demo:" + i);
			cluster.close();
		}
		
	}
	
}
