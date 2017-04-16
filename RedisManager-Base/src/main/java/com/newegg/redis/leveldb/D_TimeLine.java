package com.newegg.redis.leveldb;

/**
 * LevelDB 时间序列存储对象
 */
public abstract class D_TimeLine extends D_Level{
	private static final long serialVersionUID = 1603324825319527467L;
	
	/**
	 * 时间
	 */
	public Long date = System.currentTimeMillis();
	
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	
	abstract public String id();
	
	public String key(){
		return date + ":" + id();
	}
}