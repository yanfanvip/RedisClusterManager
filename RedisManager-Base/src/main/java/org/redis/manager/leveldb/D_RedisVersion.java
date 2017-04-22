package org.redis.manager.leveldb;

import org.redis.manager.model.enums.ServerTypeEnum;

public class D_RedisVersion extends D_Level{
	private static final long serialVersionUID = -3130817906319917413L;
	
	private String name;
	private String version;
	private ServerTypeEnum type;
	
	@Override
	String key() {
		return name;
	}
	
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
