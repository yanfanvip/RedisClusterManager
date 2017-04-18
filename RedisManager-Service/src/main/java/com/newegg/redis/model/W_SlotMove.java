package com.newegg.redis.model;

public class W_SlotMove extends WebSocketModel{
	private static final long serialVersionUID = -7912841637412914532L;

	private String cluster;
	private String node;
	private Integer start;
	private Integer end;
	
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
}
