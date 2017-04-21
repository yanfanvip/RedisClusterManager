package com.newegg.redis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.HostAndPort;

public class M_ClusterSlots implements Serializable{
	private static final long serialVersionUID = 5418141610136816170L;
	
	private Map<HostAndPort, List<Slot>> hpSlots;
	private Map<M_clusterNode, List<Slot>> idSlots;
	
	@SuppressWarnings("unchecked")
	public M_ClusterSlots(List<Object> clusterSlot, Map<HostAndPort, M_clusterNode> hpNodes){
		hpSlots = new HashMap<HostAndPort, List<Slot>>();
		idSlots = new HashMap<M_clusterNode, List<Slot>>();
		
		for (Object object : clusterSlot) {
			List<Object> tmp = (List<Object>) object;
			Long start = (Long) tmp.get(0);
			Long end = (Long) tmp.get(1);
			for (int i = 2; i < tmp.size(); i++) {
				List<Object> nodeSlot = (List<Object>)tmp.get(i);
				HostAndPort hp = new HostAndPort(new String((byte[]) nodeSlot.get(0)), ((Long)nodeSlot.get(1)).intValue());
				M_clusterNode node = hpNodes.get(hp);
				
				List<Slot> slot;
				if(!hpSlots.containsKey(hp)){
					slot = new ArrayList<Slot>();
				}else{
					slot = hpSlots.get(hp);
				}
				slot.add(new Slot(start, end));
				hpSlots.put(hp, slot);
				idSlots.put(node, slot);
			}
		}
	}
	
	public M_clusterNode getNodeBySlot(int slot){
		for (M_clusterNode key : idSlots.keySet()) {
			List<Slot> tslots = idSlots.get(key);
			for (Slot tslot : tslots) {
				if(tslot.end >= slot && tslot.start <= slot){
					return key;
				}
			}
		}
		return null;
	}
	
	public HostAndPort getHostBySlot(int slot){
		for (HostAndPort key : hpSlots.keySet()) {
			List<Slot> tslots = hpSlots.get(key);
			for (Slot tslot : tslots) {
				if(tslot.end >= slot && tslot.start <= slot){
					return key;
				}
			}
		}
		return null;
	}
	
}
class Slot{
	int start;
	int end;
	
	public Slot(int start,int end){
		this.start = start;
		this.end = end;
	}
	
	public Slot(long start, long end) {
		this.start = (int)start;
		this.end = (int)end;
	}
}