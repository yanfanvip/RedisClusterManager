package org.redis.manager.leveldb;

public class D_RedisInstall extends D_Level{
	private static final long serialVersionUID = -3130817906319917413L;
	
	private String ip;
	private Integer port;
	private String resource;
	
	@Override
	String key() {
		return ip + ":" + port;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
}
