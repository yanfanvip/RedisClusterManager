package com.newegg.redis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.newegg.redis.context.AppConfig;
import com.newegg.redis.leveldb.D_ComputerInfo;
import com.newegg.redis.leveldb.LevelTable;
import com.newegg.redis.model.ClusterServerCache;

@Service
@Scope("singleton")
public class ComputerInfoService {
	static Log log = LogFactory.getLog(ComputerInfoService.class);
	
	@Autowired
	AppConfig appConfig;
	
	static Map<String, List<D_ComputerInfo>> cache = new HashMap<String, List<D_ComputerInfo>>();
	static long history_time = 48 * 60 * 60 * 1000;
	static Timer timer;
	
	@PostConstruct
	public void init(){
		ComputerInfoService.history_time = appConfig.getMonitor_history_duration() * 60 * 60 * 1000;
		if(timer == null){
			timer = new Timer();
	        timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						flushCache();
					} catch (IOException e) {
						log.error("monitor computer info insert database error", e);
					}
				}
			}, 0, 5000);
		}
	}
	
	/**
	 * 获取监控数据
	 */
	public List<D_ComputerInfo> getAll(String cluster) throws IOException{
		return LevelTable.getAll(cluster, D_ComputerInfo.class);
	}

	public static void addComputerInfo(D_ComputerInfo info) throws Exception{
		Set<String> clusters = ClusterServerCache.getClustersByServer(info.getIp());
		for (String c : clusters) {
			addComputerInfo(c, info);
		}
	}

	public static synchronized void addComputerInfo(String cluster, D_ComputerInfo info) throws IOException{
		List<D_ComputerInfo> cs = cache.get(cluster);
		if(cs == null){
			cs = new ArrayList<D_ComputerInfo>();
		}
		cs.add(info);
		cache.put(cluster, cs);
	}
	
	public static synchronized void flushCache() throws IOException{
		if(cache.size() > 0){
			cache.forEach((k,v)->{
				try {
					LevelTable.put(k, D_ComputerInfo.class, v);
					LevelTable.deletePrev(k, D_ComputerInfo.class, System.currentTimeMillis() - history_time);
				} catch (Exception e) {
					log.error("monitor cluster ["+k+"] computer by [" + v + "] info insert database error", e);
				}
			});
			cache.clear();
		}
	}
}
