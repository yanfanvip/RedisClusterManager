package com.newegg.redis.model.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.newegg.redis.model.M_Slot;
import com.newegg.redis.model.M_clusterNode;
import com.newegg.redis.model.enums.RedisClusterRole;
import com.newegg.redis.model.enums.RedisNodeStatus;

public class ClusterNodeConvert extends Convert<List<M_clusterNode>>{
	public static final Pattern DELIMITER = Pattern.compile("[ ]");
	
	@Override
	public List<M_clusterNode> convert(String message) throws Exception {
		List<M_clusterNode> list = new ArrayList<M_clusterNode>();
		List<String[]> lines = Convert.split(message, DELIMITER);
		for (String[] line : lines) {
			M_clusterNode clusterNode = new M_clusterNode();
			clusterNode.setNode(line[0]);
			
			String[] ipport = line[1].split(":");
			clusterNode.setHost(ipport[0]);
			clusterNode.setPort(Integer.valueOf(ipport[1]));
			if(line[2].startsWith("myself,")){
				clusterNode.setMyself(true);
				line[2] = line[2].replace("myself,", "");
			}else{
				clusterNode.setMyself(false);
			}
			clusterNode.setRole(RedisClusterRole.get(line[2].toUpperCase()));
			if(!"-".equals(line[3])){
				clusterNode.setMaster(line[3]);
			}
			if("connected".equals(line[7])){
				clusterNode.setStatus(RedisNodeStatus.CONNECT);
			}else{
				clusterNode.setStatus(RedisNodeStatus.DISCONNECT);
			}
			if(clusterNode.getRole() == RedisClusterRole.MASTER){
				List<M_Slot> slots = new ArrayList<M_Slot>();
				int i = 8;
				while (line.length > i) {
					M_Slot slot = new M_Slot();
					String[] slot_t = line[i].split("-");
					if(slot_t.length == 2){
						slot.setStart(Integer.valueOf(slot_t[0]));
						slot.setEnd(Integer.valueOf(slot_t[1]));
						slots.add(slot);
					}
					i++;
				}
				clusterNode.setSlots(slots);
			}
			list.add(clusterNode);
		}
		return list;
	}
}
