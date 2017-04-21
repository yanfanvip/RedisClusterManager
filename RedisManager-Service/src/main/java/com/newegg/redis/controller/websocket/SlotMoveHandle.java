package com.newegg.redis.controller.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.newegg.redis.controller.websocket.handler.ObjectWebSocketHandler;
import com.newegg.redis.model.W_SlotMove;
import com.newegg.redis.notify.Notify;
import com.newegg.redis.service.SlotMoveService;

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
		slotMoveService.slot_move(t, notify);
	}
}