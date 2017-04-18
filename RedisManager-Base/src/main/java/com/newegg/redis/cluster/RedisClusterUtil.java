package com.newegg.redis.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.newegg.redis.model.M_clusterNode;
import com.newegg.redis.model.convert.ClusterNodeConvert;
import com.newegg.redis.notify.Notify;
import com.newegg.redis.util.AssignHelp;
import redis.clients.jedis.HostAndPort;

/**
 * 从Jedis中获取集群及Node节点的状态信息
 */
public class RedisClusterUtil {
	
	public static void create(List<HostAndPort> list,int masterSize, Notify notify) throws Exception {
		message(notify, ">> start create cluster; node size : " + list.size() + " master size :" + masterSize);
		Map<HostAndPort, RedisClusterTerminal> hrs = new HashMap<HostAndPort, RedisClusterTerminal>();
		message(notify, ">> reset nodes");
		List<RedisClusterTerminal> cs = new ArrayList<RedisClusterTerminal>(){
			private static final long serialVersionUID = 1L; {
				for (HostAndPort hp : list) {
					RedisClusterTerminal client = new RedisClusterTerminal(hp);
					client.reset();
					add(client);
					hrs.put(hp, client);
				}
			}
		};
		message(notify, ">> meet nodes");
		cs.get(0).meet(cs);
		String nodeMessage = cs.get(0).clusterNodes();
		message(notify, ">> node info:" + nodeMessage);
		Map<HostAndPort, M_clusterNode> nodes = new HashMap<HostAndPort, M_clusterNode>();
		for (M_clusterNode node : new ClusterNodeConvert().convert(nodeMessage)) {
			nodes.put(new HostAndPort(node.getHost(), node.getPort()), node);
		}
		AssignHelp assign = new AssignHelp(list, masterSize);
		Map<HostAndPort, List<HostAndPort>> map = assign.get();
		message(notify, ">> assign:" + map);
		for (Entry<HostAndPort, List<HostAndPort>> entry : map.entrySet()) {
			HostAndPort masterHP = entry.getKey();
			String mastNodeId = nodes.get(masterHP).getNode();
			List<HostAndPort> hps = entry.getValue();
			message(notify, ">> master [" + masterHP + "] set slave " + hps);
			for (HostAndPort hostAndPort : hps) {
				hrs.get(hostAndPort).slaveOf(mastNodeId);
			}
		}
		cs.get(0).clusterSaveConfig();
		message(notify, ">> create cluster success!");
		assignSlot(new ArrayList<HostAndPort>(map.keySet()), hrs, notify);
		cs.get(0).clusterSaveConfig();
		message(notify, ">> all done!");
		notify.close();
	}
	
	/**
	 * 分配槽
	 */
	public static void assignSlot(List<HostAndPort> masters, Map<HostAndPort, RedisClusterTerminal> clients, Notify notify) {
		message(notify, ">> AssignSlot Start ... ");
		int slotSize = 16384 / masters.size();
		for (int i = 0; i < masters.size(); i++) {
			int start = i * slotSize;
			int end = (i + 1) * slotSize;
			if((i + 1) == masters.size()){
				end = 16384;
			}
			HostAndPort master = masters.get(i);
			RedisClusterTerminal cluster = clients.get(master);
			message(notify, ">> assignSlot from " + start + " to " + end + " with " + master);
			for(;start < end; start++) {
				if(start % 1000 == 0){
					message(notify, ">> " + start);
				}
				cluster.clusterAddSlots(start);
			}
		}
		message(notify, ">> assignSlot Done!");
	}
	
	static void message(Notify notify, String msg){
		if(notify != null){
			notify.terminal(msg);
		}
	}
}
