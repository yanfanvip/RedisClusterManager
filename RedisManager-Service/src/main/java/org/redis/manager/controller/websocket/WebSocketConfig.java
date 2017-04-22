package org.redis.manager.controller.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends SpringBootServletInitializer implements WebSocketConfigurer{

	@Autowired
	SlotMoveHandle slotMoveHandle;
	
	@Autowired
	RedisInstallHandle redisInstallHandle;
	
	@Autowired
	CreateClusterHandle createClusterHandle;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(slotMoveHandle, "/slot_move");
		registry.addHandler(redisInstallHandle, "/install");
		registry.addHandler(createClusterHandle, "/create");
	}
}