package com.newegg.redis;

import java.util.ArrayList;
import java.util.List;

import com.newegg.redis.cluster.RedisClusterClient;
import com.newegg.redis.cluster.RedisClusterUtil;
import com.newegg.redis.notify.Notify;

import redis.clients.jedis.HostAndPort;

public class RedisClusterUtilTest {
	
	public static void main(String[] args) throws Exception {
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
		RedisClusterUtil.create(list, 4, new Notify() {
			@Override
			public void terminal(String message) {
				System.out.println(message);
			}
			
			@Override
			public void close() {
				
			}
		});
		
		RedisClusterClient client = new RedisClusterClient(list.get(0));
		System.out.println(client.clusterNodes());
		client.close();
	}
}
