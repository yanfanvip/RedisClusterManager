package org.redis.manager.controller.websocket;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redis.manager.cluster.RedisClusterUtil;
import org.redis.manager.context.AppConfig;
import org.redis.manager.controller.websocket.handler.ObjectWebSocketHandler;
import org.redis.manager.leveldb.D_ClusterInfo;
import org.redis.manager.leveldb.D_RedisInstall;
import org.redis.manager.model.W_CreateCluster;
import org.redis.manager.notify.Notify;
import org.redis.manager.service.ClusterInfoService;
import org.redis.manager.service.RedisInstallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import redis.clients.jedis.HostAndPort;

@Component
@Scope("singleton")
public class CreateClusterHandle extends ObjectWebSocketHandler<W_CreateCluster>{
	
	@Autowired
	RedisInstallService redisInstallService;
	
	@Autowired
	ClusterInfoService clusterInfoService;
	
	@Autowired 
	AppConfig appConfig;
	
	@Override
	public void onMessage(WebSocketSession session, W_CreateCluster cluster) throws Exception {
		Notify notify = new Notify() {
			@Override
			public void terminal(String message) {
				send(message);
			}
			@Override
			public void close() {
				close();
			}
		};
		List<D_RedisInstall> installs = redisInstallService.getAllInstalled();
		try {
			if(installs.size() == 0){
				throw new Exception("no installed node");
			}
			List<HostAndPort> list = new ArrayList<HostAndPort>();
			for (D_RedisInstall i : installs) {
				list.add(new HostAndPort(i.getIp(), i.getPort()));
			}
			RedisClusterUtil.create(list, cluster.getMaster(), notify);
			redisInstallService.deleteAllInstall();
			D_ClusterInfo info = new D_ClusterInfo();
			info.setName(cluster.getName());
			HostAndPort hp = list.get(0);
			info.setLast_read_host(hp.getHost());
			info.setLast_read_port(hp.getPort());
			clusterInfoService.addClusterInfo(info);
			notify.terminal("=== done ===");
		} catch (Exception e) {
			notify.terminal("create cluster error:" + ExceptionUtils.getStackTrace(e));
		}finally {
			close();
		}
	}
}