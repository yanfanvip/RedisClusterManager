package org.redis.manager.shell.client;

public interface LogMessage {
	public void sendMessage(String message);
	public void close();
}
