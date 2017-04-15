package com.newegg.redis.leveldb;

public interface Deserialize {
	D_Level deserialize(byte[] value);
}
