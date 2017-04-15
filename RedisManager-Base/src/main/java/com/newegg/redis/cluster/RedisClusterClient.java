package com.newegg.redis.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.newegg.redis.model.M_clusterInfo;
import com.newegg.redis.model.M_clusterNode;
import com.newegg.redis.model.M_clusterNode_Tree;
import com.newegg.redis.model.M_info;
import com.newegg.redis.model.convert.ClusterNodeConvert;
import com.newegg.redis.util.ClusterTreeUtil;
import com.newegg.redis.util.RedisMessageUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster.Reset;

public class RedisClusterClient extends Jedis{
	
	public RedisClusterClient(HostAndPort hp) {
	    super(hp.getHost(), hp.getPort());
	}
	
	public RedisClusterClient(final String host, final int port) {
	    super(host, port);
	}
	
	public M_clusterInfo getClusterInfo() throws Exception {
		String clusterInfo = super.clusterInfo();
		M_clusterInfo info = RedisMessageUtil.convert(clusterInfo, M_clusterInfo.class);
		info.setLast_read_host(getClient().getHost());
		info.setLast_read_port(getClient().getPort());
		return info;
	}
	
	public Map<HostAndPort, M_clusterNode> getClusterNode_map() throws Exception {
		String clusterNodes = super.clusterNodes();
		List<M_clusterNode> list = new ClusterNodeConvert().convert(clusterNodes);
		Map<HostAndPort, M_clusterNode> map = new HashMap<HostAndPort, M_clusterNode>();
		for (M_clusterNode node : list) {
			map.put(new HostAndPort(node.getHost(), node.getPort()), node);
		}
		return map;
	}
	
	public List<M_clusterNode> getClusterNode_list() throws Exception{
		String clusterNodes = super.clusterNodes();
		return new ClusterNodeConvert().convert(clusterNodes);
	}
	
	public M_clusterNode_Tree getClusterNode_tree() throws Exception{
		List<M_clusterNode> list = getClusterNode_list();
		return ClusterTreeUtil.getTree(list);
	}
	
	/**
	 * 初始化节点
	 * @return 
	 */
	public RedisClusterClient reset() {
		try { clusterFlushSlots(); } catch (Exception e) { }
		try { flushDB(); } catch (Exception e) { }
		clusterReset(Reset.HARD);
		clusterSaveConfig();
		return this;
	}
	
	/**
	 * 加入节点
	 * @throws Exception 
	 */
	public void meet(List<RedisClusterClient> nodes) throws Exception{
		for (RedisClusterClient client : nodes) {
			if(client.hashCode() != this.hashCode()){
				clusterMeet(client.getClient().getHost(), client.getClient().getPort());
			}
		}
		clusterSaveConfig();
		boolean over = false;
		int size = nodes.size();
		while(!over){
			Thread.sleep(1000);
			over = true;
			for (RedisClusterClient client : nodes) {
				M_clusterInfo info = client.getClusterInfo();
				over = size == info.getCluster_known_nodes();
				if(!over){
					break;
				}
			}
		}
	}
	
	/**
	 * 将当前结点设置为指定节点的从节点
	 */
	public RedisClusterClient slaveOf(String node) {
		clusterReplicate(node);
		return this;
	}
	
	/**
	 * 从该节点删除某个节点
	 */
	public void forget(String node) throws Exception{
		clusterForget(node);
		clusterSaveConfig();
	}
	
	@Override
	public int hashCode() {
		return ("redis_cluster_client:" + super.getClient().getHost() + ":" + super.getClient().getPort()).hashCode();
	}
	
	public M_info getInfo() throws Exception {
		String str = super.info();
		M_info info = RedisMessageUtil.convert(str, M_info.class);
		info.setHostname(getClient().getHost());
		info.setPort(getClient().getPort());
		return info;
	}

}
