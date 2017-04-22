package org.redis.manager.notify;

public interface Notify{
	void terminal(final String message);
	void close();
}