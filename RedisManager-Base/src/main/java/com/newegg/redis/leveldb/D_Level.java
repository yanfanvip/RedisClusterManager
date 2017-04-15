package com.newegg.redis.leveldb;

import java.io.Serializable;
import org.apache.commons.compress.utils.Charsets;
import com.alibaba.fastjson.JSON;

/**
 * LevelDB 存储对象
 */
public abstract class D_Level implements Deserialize, Serializable{
	private static final long serialVersionUID = 1603324825319527467L;
	
	abstract String key();
	
	public byte[] value(){
		String json = JSON.toJSONString(this);
		return json.getBytes();
	}
	
	@Override
	public String toString() {
		String json = JSON.toJSONString(this);
		return json;
	}
	
	@Override
	public D_Level deserialize(byte[] value) {
		return deserialize(this.getClass(), value);
	}
	
	public static D_Level deserialize(Class<? extends D_Level> clazz,byte[] value){
		String json = new String(value, Charsets.UTF_8);
		return JSON.parseObject(json, clazz);
	}
}