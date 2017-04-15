package com.newegg.redis.leveldb;

import java.io.Serializable;
import java.util.List;

public class D_ClusterNode_Tree implements Serializable{
	private static final long serialVersionUID = -2642236716570649507L;
	
	List<D_ClusterNode_Master> masters;
	Boolean status;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public List<D_ClusterNode_Master> getMasters() {
		return masters;
	}

	public void setMasters(List<D_ClusterNode_Master> masters) {
		this.masters = masters;
	}

	@Override
	public String toString() {
		return "{masters : " + masters + " }";
	}
}
