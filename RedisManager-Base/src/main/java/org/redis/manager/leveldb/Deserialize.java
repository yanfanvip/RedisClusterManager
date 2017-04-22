package org.redis.manager.leveldb;

public interface Deserialize {
	D_Level deserialize(byte[] value);
}
