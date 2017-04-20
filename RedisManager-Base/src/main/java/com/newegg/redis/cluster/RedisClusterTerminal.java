package com.newegg.redis.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.newegg.redis.model.M_ClusterSlots;
import com.newegg.redis.model.M_clusterInfo;
import com.newegg.redis.model.M_clusterNode;
import com.newegg.redis.model.M_clusterNode_Tree;
import com.newegg.redis.model.M_info;
import com.newegg.redis.model.convert.ClusterNodeConvert;
import com.newegg.redis.notify.Notify;
import com.newegg.redis.util.ClusterTreeUtil;
import com.newegg.redis.util.RedisMessageUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster.Reset;

public class RedisClusterTerminal extends Jedis{
	
	Notify notify;
	
	public RedisClusterTerminal(HostAndPort hp) {
	    super(hp.getHost(), hp.getPort());
	}
	
	public RedisClusterTerminal(final String host, final int port) {
	    super(host, port);
	}
	
	public RedisClusterTerminal(HostAndPort hp,Notify notify) {
	    super(hp.getHost(), hp.getPort());
	    this.notify = notify;
	}
	
	public RedisClusterTerminal(final String host, final int port,Notify notify) {
	    super(host, port);
	    this.notify = notify;
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
	public RedisClusterTerminal reset() {
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
	public void meet(List<RedisClusterTerminal> nodes) throws Exception{
		for (RedisClusterTerminal client : nodes) {
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
			for (RedisClusterTerminal client : nodes) {
				M_clusterInfo info = client.getClusterInfo();
				over = size == info.getCluster_known_nodes();
				if(!over){
					break;
				}
			}
		}
	}
	
	/**
	 * 将当前槽迁移到该节点
	 */
	public void reshard(int start,int end)throws Exception {
		message("start move slot from " + start + " to " + end);
		List<M_clusterNode> all = getClusterNode_list();
		Map<HostAndPort, M_clusterNode> hpNodes = new HashMap<HostAndPort, M_clusterNode>();
		M_clusterNode myself = null;
		for (M_clusterNode n : all) {
			if(n.getMaster() == null || "".equals(n.getMaster())){
				hpNodes.put(new HostAndPort(n.getHost(), n.getPort()), n);
			}
			if(n.getMyself()!= null && n.getMyself()){
				myself = n;
			}
		}
		M_ClusterSlots clusterSlots = new M_ClusterSlots(clusterSlots(), hpNodes);
		int i = start;
		while(i <= end){
			M_clusterNode sourceNode = clusterSlots.getNodeBySlot(i);
			if(sourceNode == null || !sourceNode.getNode().equals(myself.getNode())){
				message(">> move :" + i);
				moveSlot(myself, sourceNode, i);
			}
			i++;
		}
		message(">> all done!");
	}
	
	/**
	 * 迁移指定的槽
	 */
	private void moveSlot(M_clusterNode myself, M_clusterNode sourceNode, int slot) throws Exception{
		if(sourceNode != null){//设置为待迁移状态
			RedisClusterTerminal source = new RedisClusterTerminal(sourceNode.getHost(), sourceNode.getPort());
			try {
				clusterSetSlotImporting(slot, sourceNode.getNode());
				source.clusterSetSlotMigrating(slot, myself.getNode());
				message(">>start move data");
				List<String> keys;
				do {
					keys = source.clusterGetKeysInSlot(slot, 100);
					for (String key : keys) {
						source.migrate(myself.getHost(), myself.getPort(), key, 0, 60000);
					}
				} while(keys.size() > 0);
			}catch(Exception e){
				clusterSetSlotStable(slot);
				source.clusterSetSlotStable(slot);
				throw e;
			} finally {
				source.close();
			}
		}
		clusterSetSlotNode(slot, myself.getNode());
	}

	/**
	 * 将当前结点设置为指定节点的从节点
	 */
	public RedisClusterTerminal slaveOf(String node) {
		clusterReplicate(node);
		return this;
	}
	
	/**
	 * 从该节点删除某个节点
	 */
	public void forget(String node) {
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

	void message(String msg){
		if(notify != null){
			notify.terminal(msg);
		}
	}
}
