package com.newegg.redis.model;

import java.util.List;

public class M_clusterNode_Tree {
	List<M_clusterNode_Master> masters;
	Boolean status;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public List<M_clusterNode_Master> getMasters() {
		return masters;
	}

	public void setMasters(List<M_clusterNode_Master> masters) {
		this.masters = masters;
	}

	@Override
	public String toString() {
		return "{masters : " + masters + " }";
	}
}
