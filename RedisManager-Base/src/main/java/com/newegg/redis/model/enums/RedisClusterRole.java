package com.newegg.redis.model.enums;

public enum RedisClusterRole {
	MASTER,
	SLAVE,
	FAIL;

	public static RedisClusterRole get(String key) {
		RedisClusterRole ret = RedisClusterRole.FAIL;
		if(key != null){
			for (RedisClusterRole role : RedisClusterRole.values()) {
				if(role.name().equalsIgnoreCase(key)){
					ret = role;
				}
			}
		}
		return ret;
	}
}
