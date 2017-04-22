package org.redis.manager.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.redis.manager.context.AppConfig;
import org.redis.manager.context.AppConstants;
import org.redis.manager.leveldb.D_RedisInstall;
import org.redis.manager.leveldb.D_ServerInfo;
import org.redis.manager.leveldb.LevelTable;
import org.redis.manager.model.RedisVersionInfo;
import org.redis.manager.model.enums.ServerTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class RedisInstallService {

	@Autowired 
	AppConfig appConfig;
	
	static List<RedisVersionInfo> lists = null;
	
	public List<RedisVersionInfo> getRedisVersion(){
		synchronized (this) {
			if(lists == null){
				List<RedisVersionInfo> infos = new ArrayList<RedisVersionInfo>();
				File workpath = new File(appConfig.getResource());
				File[] list = workpath.listFiles();
				for (File file : list) {
					if(file.isFile() && file.getName().startsWith("redis.") && file.getName().endsWith(".gz")){
						RedisVersionInfo info = new RedisVersionInfo();
						info.setName(file.getName());
						String name = file.getName().substring(0, file.getName().length() - 3);
						String version = name.substring(6, name.lastIndexOf("."));
						info.setVersion(version);
						String type = name.substring(name.lastIndexOf(".") + 1);
						info.setType(ServerTypeEnum.valueOf(type));
						infos.add(info);
					}
				}
				RedisInstallService.lists = infos;
			}
			return RedisInstallService.lists;
		}
	}

	public void del(String name) throws IOException {
		synchronized (this) {
			File f = new File(appConfig.getResource() + "/" + name);
			if(f.exists() && f.isFile()){
				f.delete();
			}
			RedisInstallService.lists = null;
		}
	}
	
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
	public D_ServerInfo getServer(String ip) throws Exception {
		return LevelTable.get(AppConstants.LEVEL_DATABASES_SYSTEM, D_ServerInfo.class, ip);
	}
	
	/**
	 * 删除机器信息 
	 */
	public void delete(String ip) throws IOException {
		LevelTable.delete(AppConstants.LEVEL_DATABASES_SYSTEM, D_ServerInfo.class, ip);
	}

	/**
	 * 获取所有已安装列表
	 */
	public List<D_RedisInstall> getAllInstalled() throws IOException {
		return LevelTable.getAll(AppConstants.LEVEL_DATABASES_SYSTEM, D_RedisInstall.class);
	}
	
	/**
	 * 增加已安装列表
	 */
	public void addInstalled(D_RedisInstall install) throws IOException {
		LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, install);
	}

	/**
	 * 删除已安装节点
	 */
	public void delInstall(String ip, Integer port) throws IOException {
		LevelTable.delete(AppConstants.LEVEL_DATABASES_SYSTEM, D_RedisInstall.class, ip + ":" + port);
	}

	/**
	 * 清除已安装节点
	 */
	public void deleteAllInstall() throws IOException {
		LevelTable.destroy(AppConstants.LEVEL_DATABASES_SYSTEM, D_RedisInstall.class);
	}
}
