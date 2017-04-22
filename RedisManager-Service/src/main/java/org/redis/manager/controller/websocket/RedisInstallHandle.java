package org.redis.manager.controller.websocket;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redis.manager.context.AppConfig;
import org.redis.manager.controller.websocket.handler.ObjectWebSocketHandler;
import org.redis.manager.leveldb.D_RedisInstall;
import org.redis.manager.leveldb.D_ServerInfo;
import org.redis.manager.model.W_RedisInstall;
import org.redis.manager.notify.Notify;
import org.redis.manager.service.RedisInstallService;
import org.redis.manager.shell.JavaRuntimeUtil;
import org.redis.manager.shell.JavaRuntimeUtil.JavaVersion;
import org.redis.manager.shell.MonitorUtil;
import org.redis.manager.shell.RedisInstallUtil;
import org.redis.manager.shell.SftpFactory;
import org.redis.manager.shell.client.SftpInterface;
import org.redis.manager.shell.client.ShellClient;
import org.redis.manager.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@Scope("singleton")
public class RedisInstallHandle extends ObjectWebSocketHandler<W_RedisInstall>{
	
	@Autowired
	RedisInstallService redisInstallService;
	
	@Autowired 
	AppConfig appConfig;
	
	@Override
	public void onMessage(WebSocketSession session, W_RedisInstall install) throws Exception {
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
		
		D_ServerInfo info = redisInstallService.getServer(install.getIp());
		SftpInterface ftp = null;
		ShellClient client = null;
		try {
			if(info == null){
				throw new Exception("not find server:" + install.getIp());
			}
			ftp = SftpFactory.create(info.getIp(), info.getUserName(), info.getPassword());
			client = new ShellClient(info.getIp(), info.getUserName(), info.getPassword());
			JavaRuntimeUtil javaRuntimeUtil = new JavaRuntimeUtil(ftp, client, info.getWorkhome(), appConfig.getResource(), notify);
			javaRuntimeUtil.init(JavaVersion.Version1_6);
			MonitorUtil monitorUtil = new MonitorUtil(ftp, client, info.getWorkhome(), appConfig.getResource(), notify);
			monitorUtil.init(SystemUtil.ip());
			RedisInstallUtil util = new RedisInstallUtil(ftp, client, info.getWorkhome(), appConfig.getResource(), notify);
			util.install(install.getSource(), install.getPorts(), install.getMemory(), install.getConfig());
			for (Integer port : install.getPorts()) {
				D_RedisInstall redisInstall = new D_RedisInstall();
				redisInstall.setIp(install.getIp());
				redisInstall.setPort(port);
				redisInstall.setResource(install.getSource());
				redisInstallService.addInstalled(redisInstall);
			}
			notify.terminal("=== done ===");
		}catch (Exception e) {
			notify.terminal("redis install error:" + ExceptionUtils.getStackTrace(e));
		} finally {
			if(ftp != null){
				ftp.close();
			}
			if(client != null){
				client.close();
			}
			close();
		}
	}
}