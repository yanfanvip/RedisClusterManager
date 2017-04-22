package org.redis.manager.service;

import java.io.IOException;
import java.util.List;

import org.redis.manager.context.AppConfig;
import org.redis.manager.context.AppConstants;
import org.redis.manager.leveldb.D_RedisVersion;
import org.redis.manager.leveldb.LevelTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class RedisInstallService {

	@Autowired 
	AppConfig appConfig;
	
	public List<D_RedisVersion> getRedisVersion() throws IOException {
		return LevelTable.getAll(AppConstants.LEVEL_DATABASES_SYSTEM, D_RedisVersion.class);
	}
	
	public void addRedisVersion(D_RedisVersion version) throws IOException{
		LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, version);
	}

	public void del(String name) throws IOException {
		LevelTable.delete(AppConstants.LEVEL_DATABASES_SYSTEM, D_RedisVersion.class, name);
	}
}
