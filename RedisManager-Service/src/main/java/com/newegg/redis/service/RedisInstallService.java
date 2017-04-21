package com.newegg.redis.service;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.newegg.redis.context.AppConfig;
import com.newegg.redis.context.AppConstants;
import com.newegg.redis.leveldb.D_RedisVersion;
import com.newegg.redis.leveldb.LevelTable;

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
