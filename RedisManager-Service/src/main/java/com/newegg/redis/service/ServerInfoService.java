package com.newegg.redis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.newegg.redis.context.AppConstants;
import com.newegg.redis.leveldb.D_ServerInfo;
import com.newegg.redis.leveldb.LevelTable;
import com.newegg.redis.model.enums.RedisServerStatus;

@Service
@Scope("singleton")
public class ServerInfoService {
	
	/**
	 * 添加机器信息
	 */
	public void addService(D_ServerInfo server) throws Exception {
		LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, server);
	}
	
	/**
	 * 获取所有机器信息
	 * @return 
	 */
	public List<D_ServerInfo> getAllService() throws Exception {
		return LevelTable.getAll(AppConstants.LEVEL_DATABASES_SYSTEM, D_ServerInfo.class);
	}
	
	/**
	 * 获取指定机器信息
	 */
	public D_ServerInfo getService(String ip, int port) throws Exception{
		return LevelTable.get(AppConstants.LEVEL_DATABASES_SYSTEM, D_ServerInfo.class, ip + ":" + port);
	}
	
	/**
	 * 获取指定状态的所有机器信息
	 * @return 
	 */
	public List<D_ServerInfo> getAllService(RedisServerStatus status) throws Exception {
		List<D_ServerInfo> data = new ArrayList<D_ServerInfo>();
		if(status == null){
			return data;
		}
		List<D_ServerInfo> list = LevelTable.getAll(AppConstants.LEVEL_DATABASES_SYSTEM, D_ServerInfo.class);
		for (D_ServerInfo d : list) {
			if(d.getStatus() == status){
				data.add(d);
			}
		}
		return data;
	}
}
