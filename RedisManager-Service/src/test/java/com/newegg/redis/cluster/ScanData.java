package com.newegg.redis.cluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import com.newegg.redis.model.ScanPage;
import redis.clients.jedis.HostAndPort;

public class ScanData {
	static int i = 0;
	
	public static void main(String[] args) throws IOException {
		Set<HostAndPort> nodes = new HashSet<HostAndPort>(){
			private static final long serialVersionUID = 1L;
			{
				add(new HostAndPort("192.168.3.50", 8200));
				//add(new HostAndPort("192.168.3.50", 8201));
				add(new HostAndPort("192.168.3.50", 8202));
				//add(new HostAndPort("192.168.3.50", 8203));
				add(new HostAndPort("192.168.3.50", 8204));
				//add(new HostAndPort("192.168.3.50", 8205));
				//add(new HostAndPort("192.168.3.50", 8206));
				//add(new HostAndPort("192.168.3.50", 8207));
				add(new HostAndPort("192.168.3.50", 8208));
				//add(new HostAndPort("192.168.3.50", 8209));
			}
		};
		
		RedisClusterScan scan = new RedisClusterScan(nodes);
		ScanPage page = new ScanPage();
		page.setQuery("abc*");
		while (page.getHasMore()) {
			page = scan.scan(page);
			page.getKeys().forEach(k->{
				System.out.println(k);
				i++;
			});
			page.setKeys(null);
		}
		System.out.println(i);
	}
}
