package org.redis.manager.controller.websocket;

import org.redis.manager.controller.websocket.handler.ObjectWebSocketHandler;
import org.redis.manager.model.W_RedisInstall;
import org.redis.manager.notify.Notify;
import org.redis.manager.service.RedisInstallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@Scope("singleton")
public class RedisInstallHandle extends ObjectWebSocketHandler<W_RedisInstall>{
	
	@Autowired
	RedisInstallService redisInstallService;
	
	@Override
	public void onMessage(WebSocketSession session, W_RedisInstall t) throws Exception {
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
	}
}