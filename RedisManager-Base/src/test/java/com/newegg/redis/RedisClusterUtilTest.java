package com.newegg.redis;

import java.util.ArrayList;
import java.util.List;

import org.redis.manager.cluster.RedisClusterTerminal;
import org.redis.manager.cluster.RedisClusterUtil;
import org.redis.manager.notify.Notify;

import redis.clients.jedis.HostAndPort;

public class RedisClusterUtilTest {
	
	public static void main(String[] args) throws Exception {
		List<HostAndPort> list = new ArrayList<HostAndPort>(){
			private static final long serialVersionUID = 1L;
			{
				add(new HostAndPort("10.16.236.59", 8200));
				add(new HostAndPort("10.16.236.59", 8201));
				add(new HostAndPort("10.16.236.59", 8202));
				add(new HostAndPort("10.16.236.59", 8203));
				add(new HostAndPort("10.16.236.59", 8204));
				add(new HostAndPort("10.16.236.59", 8205));
				add(new HostAndPort("10.16.236.59", 8206));
				add(new HostAndPort("10.16.236.59", 8207));
				add(new HostAndPort("10.16.236.59", 8208));
				add(new HostAndPort("10.16.236.59", 8209));
			}
		};
		RedisClusterUtil.create(list, 4, new Notify() {
			@Override
			public void terminal(String message) {
				System.out.println(message);
			}
			
			@Override
			public void close() {
				
			}
		});
		
		RedisClusterTerminal client = new RedisClusterTerminal(list.get(0));
		System.out.println(client.clusterNodes());
		client.close();
	}
}
