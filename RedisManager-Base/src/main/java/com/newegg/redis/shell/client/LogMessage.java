package com.newegg.redis.shell.client;

public interface LogMessage {
	public void sendMessage(String message);
	public void close();
}
