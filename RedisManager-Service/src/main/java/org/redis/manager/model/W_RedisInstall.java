package org.redis.manager.model;

import java.util.List;

public class W_RedisInstall extends WebSocketModel{
	private static final long serialVersionUID = 1894941362398915472L;

	private String ip;
	private List<Integer> ports;
	private String source;
	private Integer memory;
	private String config;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public List<Integer> getPorts() {
		return ports;
	}
	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}
	public Integer getMemory() {
		return memory;
	}
	public void setMemory(Integer memory) {
		this.memory = memory;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
