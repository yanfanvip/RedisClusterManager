package com.newegg.redis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
	
	static Map<String, List<D_RedisInfo>> cache = new HashMap<String, List<D_RedisInfo>>();
	static long history_time = 48 * 60 * 60 * 1000;
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
		List<D_RedisInfo> lists = LevelTable.getAll(cluster, D_RedisInfo.class);
		final Map<String, D_RedisInfo> last_info = new HashMap<String, D_RedisInfo>();
		lists.forEach(d ->{
			if(last_info.containsKey(d.id())){
				D_RedisInfo last = last_info.get(d.id());
				long second = Math.abs(d.getDate() - last.getDate()) / 1000;
				long cmds = Math.abs(d.getTotal_commands_processed() - last.getTotal_commands_processed());
				long connects = Math.abs(d.getTotal_connections_received() - last.getTotal_connections_received());
				long inputs = Math.abs(d.getTotal_net_input_bytes() - last.getTotal_net_input_bytes());
				long outputs = Math.abs(d.getTotal_net_output_bytes() - last.getTotal_net_output_bytes());
				
				d.setCommands_processed_ops_by_sec(cmds / second);
				d.setConnections_received_ops_by_sec(connects / second);
				d.setNet_input_bytes_ops_by_sec(inputs / second);
				d.setNet_output_bytes_ops_by_sec(outputs / second);
			}
			last_info.put(d.id(), d);
		});
		return lists;
	}

	public static synchronized void addRedisInfo(D_ClusterInfo cluster, D_RedisInfo info) throws IOException{
		List<D_RedisInfo> cs = cache.get(cluster.getUuid());
		if(cs == null){
			cs = new ArrayList<D_RedisInfo>();
		}
		cs.add(info);
		cache.put(cluster.getUuid(), cs);
	}
	
	public static synchronized void flushCache() throws IOException{
		if(cache.size() > 0){
			cache.forEach((k,v)->{
				try {
					LevelTable.put(k, D_RedisInfo.class, v);
					LevelTable.deletePrev(k, D_RedisInfo.class, System.currentTimeMillis() - history_time);
				} catch (Exception e) {
					log.error("monitor redis ["+k+"] computer by [" + v + "] info insert database error", e);
				}
			});
			cache.clear();
		}
	}
}
