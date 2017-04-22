package org.redis.manager.model;

public class W_CreateCluster extends WebSocketModel{
	private static final long serialVersionUID = 1894941362398915472L;

	private String name;
	private Integer master;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getMaster() {
		return master;
	}
	public void setMaster(Integer master) {
		this.master = master;
	}
}
