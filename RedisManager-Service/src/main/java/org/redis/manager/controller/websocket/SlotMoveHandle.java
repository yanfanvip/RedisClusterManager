package org.redis.manager.controller.websocket;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redis.manager.controller.websocket.handler.ObjectWebSocketHandler;
import org.redis.manager.model.W_SlotMove;
import org.redis.manager.notify.Notify;
import org.redis.manager.service.SlotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@Scope("singleton")
public class SlotMoveHandle extends ObjectWebSocketHandler<W_SlotMove>{
	
	@Autowired
	SlotMoveService slotMoveService;
	
	@Override
	public void onMessage(WebSocketSession session, W_SlotMove t) throws Exception {
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
		try {
			slotMoveService.slot_move(t, notify);
			notify.terminal("=== done ===");
		} catch (Exception e) {
			notify.terminal("slot move error:" + ExceptionUtils.getStackTrace(e));
		}finally {
			close();
		}
		
	}
}