package com.newegg.redis.leveldb;

import java.io.Serializable;
import java.util.List;

public class D_ClusterNode_Master implements Serializable{
	private static final long serialVersionUID = -3549873933785150605L;
	private D_RedisClusterNode master;
	private List<D_RedisClusterNode> slaves;
	public D_RedisClusterNode getMaster() {
		return master;
	}
	public void setMaster(D_RedisClusterNode master) {
		this.master = master;
	}
	public List<D_RedisClusterNode> getSlaves() {
		return slaves;
	}
	public void setSlaves(List<D_RedisClusterNode> slaves) {
		this.slaves = slaves;
	}
	
	@Override
	public String toString() {
		return "{master : " + master.getHost() + ":" + master.getPort() + " , slaves : " + slaves + " }";
	}
}