package com.newegg.redis.cluster;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.newegg.redis.model.ScanPage;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisClusterScan{

	HostAndPort[] nodes;
	
	public RedisClusterScan(Set<HostAndPort> masters) {
		HostAndPort[] nodes = masters.toArray(new HostAndPort[masters.size()]);
		Arrays.sort(nodes, (o1, o2)->{
			return (o1.getHost() + ":" + o1.getPort()).hashCode() - (o2.getHost() + ":" + o2.getPort()).hashCode();
		});
		this.nodes = nodes;
	}
	
	public ScanPage scan(ScanPage scan) {
		Set<String> keys = new HashSet<String>();
		boolean flag = false;//是否满足当前页面的个数
		while(!flag && scan.getHasMore()){//如果没有满足查询个数，则循环下一个节点继续查询
			HostAndPort hp = nodes[scan.getClient()];
			Jedis jedis = new Jedis(hp.getHost(), hp.getPort());
			try {
				ScanResult<String>  result = jedis.scan(scan.getCursor(), new ScanParams().count(scan.getPageSize() - keys.size()).match(scan.getQuery()));
				scan.setCursor(result.getStringCursor());
				if(scan.getCursor() == null || "".equals(scan.getCursor()) || "0".equals(scan.getCursor())){//当前结点没有数据了
					scan.setClient(scan.getClient() + 1);
					if(scan.getClient() >= nodes.length){
						scan.setHasMore(false);
					}
					scan.setCursor("0");
				}else{
					scan.setHasMore(true);
				}
				keys.addAll(result.getResult());
			} finally {
				jedis.close();
			}
			if(keys.size() >= scan.getPageSize()){
				flag = true;
			}
		}
		scan.setKeys(keys);
		return scan;
	}
}