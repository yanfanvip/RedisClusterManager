package com.newegg.redis.model;

import java.util.List;

public class M_clusterNode_Master{
	private M_clusterNode master;
	private List<M_clusterNode> slaves;
	public M_clusterNode getMaster() {
		return master;
	}
	public void setMaster(M_clusterNode master) {
		this.master = master;
	}
	public List<M_clusterNode> getSlaves() {
		return slaves;
	}
	public void setSlaves(List<M_clusterNode> slaves) {
		this.slaves = slaves;
	}
	
	@Override
	public String toString() {
		return "{master : " + master.getHost() + ":" + master.getPort() + " , slaves : " + slaves + " }";
	}
}