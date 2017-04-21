package com.newegg.redis.controller.websocket.handler;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.alibaba.fastjson.JSON;

public abstract class ObjectWebSocketHandler<T> extends TextWebSocketHandler implements Closeable{
	private WebSocketSession session;
	
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public ObjectWebSocketHandler() {
		Class<?> clz = this.getClass();
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T>) types[0];
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.session = session;
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String json = message.getPayload();
		T t = JSON.parseObject(json, clazz);
		onMessage(session, t);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		closeSession();
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		closeSession();
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
	
	public abstract void onMessage(WebSocketSession session , T t) throws Exception;
	
	public void send(Object object) {
		if(this.session != null && this.session.isOpen()){
			TextMessage msg;
			if(object instanceof String){
				msg = new TextMessage((String)object);
			}else{
				msg = new TextMessage(JSON.toJSONString(object));
			}
			try { this.session.sendMessage(msg); } catch (IOException e) {}
		}
	}
	
	@Override
	public void close() throws IOException {
		closeSession();
	}
	
	public void closeSession(){
		try {
			this.session.close();
		} catch (Exception e) { }
		this.session = null;
	}
}
