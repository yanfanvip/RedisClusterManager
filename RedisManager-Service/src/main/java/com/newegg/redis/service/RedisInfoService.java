package com.newegg.redis.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.newegg.redis.context.AppConfig;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.leveldb.D_RedisInfo;
import com.newegg.redis.leveldb.LevelTable;

@Service
@Scope("singleton")
public class RedisInfoService {
	static Log log = LogFactory.getLog(RedisInfoService.class);
	
	@Autowired 
	AppConfig appConfig;
	
	static Map<String, Vector<D_RedisInfo>> cache = new HashMap<String, Vector<D_RedisInfo>>();
	static long history_time = 72 * 60 * 60 * 1000;
	static Timer timer;
	
	@PostConstruct
	public void init(){
		RedisInfoService.history_time = appConfig.getMonitor_history_duration() * 60 * 60 * 1000;
		if(timer == null){
			timer = new Timer();
	        timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						flushCache();
					} catch (IOException e) {
						log.error("monitor redis info insert database error", e);
					}
				}
			}, 0, 5000);
		}
	}
	
	public List<D_RedisInfo> getAll(String cluster) throws IOException{
		return LevelTable.getAll(cluster, D_RedisInfo.class);
	}

	public static void addRedisInfo(D_ClusterInfo cluster, D_RedisInfo info) throws IOException{
		Vector<D_RedisInfo> cs = cache.get(cluster.getUuid());
		if(cs == null){
			cs = new Vector<D_RedisInfo>();
		}
		cs.add(info);
		cache.put(cluster.getUuid(), cs);
	}
	
	public static void flushCache() throws IOException{
		if(cache.size() > 0){
			cache.forEach((k,v)->{
				try {
					LevelTable.put(k, D_RedisInfo.class, v);
					LevelTable.deletePrev(k, D_RedisInfo.class, System.currentTimeMillis() - history_time);
				} catch (Exception e) {
					log.error("monitor redis ["+k+"] computer by [" + v + "] info insert database error", e);
				}
				v.clear();
			});
		}
	}
}
