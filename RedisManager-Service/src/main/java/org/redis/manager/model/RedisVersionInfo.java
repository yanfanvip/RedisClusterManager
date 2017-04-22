package org.redis.manager.model;

import org.redis.manager.model.enums.ServerTypeEnum;

public class RedisVersionInfo{
	
	private String name;
	private String version;
	private ServerTypeEnum type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public ServerTypeEnum getType() {
		return type;
	}
	public void setType(ServerTypeEnum type) {
		this.type = type;
	}
}
