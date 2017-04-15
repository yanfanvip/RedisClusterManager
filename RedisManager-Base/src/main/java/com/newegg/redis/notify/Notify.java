package com.newegg.redis.notify;

public interface Notify{
	void terminal(final String message);
	void close();
}