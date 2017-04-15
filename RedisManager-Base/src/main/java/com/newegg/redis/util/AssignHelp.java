package com.newegg.redis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.HostAndPort;

/**
 * 
 */
public class AssignHelp {

	private Map<HostAndPort,List<HostAndPort>> tree;
	private int hostsSize;
	private int masterSize;
	private int allSize;
	
	public Map<HostAndPort, List<HostAndPort>> get(){
		return tree;
	}
	
	/**
	 * list : {ip:port,ip:port}
	 * master : master Size
	 * salve : Slave Size
	 */
	public AssignHelp(List<HostAndPort> all, int masterSize) {
		if (all.size() < masterSize) { // 抛异常
			throw new IllegalArgumentException("master sum salve can't equals list size.");
		}
		List<HostAndPort> list = new ArrayList<HostAndPort>(all);
		this.masterSize = masterSize;
		this.allSize = list.size();
		tree = new HashMap<HostAndPort, List<HostAndPort>>();
		Map<String,Set<Integer>> hosts = getHostGroup(list);
		this.hostsSize = hosts.size();
		selectMaster(tree,list, hosts, masterSize);
		selectSlave(tree, list);
	}
	
	/**
	 * 根据host分组
	 */
	private Map<String,Set<Integer>> getHostGroup(List<HostAndPort> list){
		Map<String,Set<Integer>> hosts = new HashMap<String, Set<Integer>>();
		Collections.shuffle(list);
		for (HostAndPort ipPort : list) {
			Set<Integer> ports = hosts.get(ipPort.getHost());
			if(ports == null){
				ports = new HashSet<Integer>();
			}
			ports.add(ipPort.getPort());
			hosts.put(ipPort.getHost(), ports);
		}
		return hosts;
	}
	
	/**
	 * 选取Master
	 */
	private void selectMaster(Map<HostAndPort,List<HostAndPort>> tree,List<HostAndPort> list,Map<String,Set<Integer>> hosts,int masterSize){
		if(list.size() == masterSize){
			for (HostAndPort master : list) {
				tree.put(master, new ArrayList<HostAndPort>());
			}
			list.clear();
			return;
		}
		int portCount = 0;
		int existMaster = 0;
		List<String> keys = new ArrayList<String>(hosts.keySet());
		while (existMaster < masterSize) {
			Collections.shuffle(keys);
			for (String host : keys) {
				if(existMaster < masterSize){
					Set<Integer> ports = hosts.get(host);
					if(ports.size() > portCount){
						Integer port = (Integer) ports.toArray()[portCount];
						ports.remove(port);
						hosts.put(host, ports);
						HostAndPort master = new HostAndPort(host, port);
						list.remove(master);
						tree.put(master, new ArrayList<HostAndPort>());
						existMaster++;
					}
				}
			}
			portCount++;
		}
	}
	/**
	 * 选取Slave节点
	 */
	private void selectSlave(Map<HostAndPort, List<HostAndPort>> tree, List<HostAndPort> list) {
		List<HostAndPort> keySets = new ArrayList<HostAndPort>(tree.keySet());
		while (list.size() > 0) {
			Collections.shuffle(list);
			Collections.shuffle(keySets);
			for (HostAndPort ipPort : keySets) {
				List<HostAndPort> slaves = tree.get(ipPort);
				List<HostAndPort> exist = new ArrayList<HostAndPort>(slaves);
				exist.add(ipPort);
				HostAndPort tmp = getOtherHost(list, exist);
				if(tmp != null){
					slaves.add(tmp);
					tree.put(ipPort, slaves);
				}
			}
		}
	}
	
	private HostAndPort getOtherHost(List<HostAndPort> list,List<HostAndPort> exist){
		if(list.size() == 0){
			return null;
		}
		List<String> hosts = new ArrayList<String>();
		for (HostAndPort ipPort : exist) {
			hosts.add(ipPort.getHost());
		}
		HostAndPort ret = null;
		Collections.shuffle(list);
		for (HostAndPort ipPort : list) {
			if(hosts.contains(ipPort.getHost())){
				continue;
			}
			ret = ipPort;
		}
		if(ret != null){
			list.remove(ret);
		}else if(hostsSize > allSize / masterSize){
			throw new AssignNotExcellent();
		}else{
			ret = list.get(0);
			list.remove(ret);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) {
		List<HostAndPort> list = new ArrayList<HostAndPort>(){
			private static final long serialVersionUID = 1L;
			{
				add(new HostAndPort("10.16.236.133", 8201));
				add(new HostAndPort("10.16.236.133", 8202));
				add(new HostAndPort("10.16.236.133", 8203));
			}
		};
		AssignHelp assign = new AssignHelp(list, 2);
		Map<HostAndPort, List<HostAndPort>> map = assign.get();
		System.out.println(map);
	}
}

class AssignNotExcellent extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
}
