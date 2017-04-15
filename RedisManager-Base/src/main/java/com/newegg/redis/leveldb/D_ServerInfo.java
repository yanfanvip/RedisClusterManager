package com.newegg.redis.leveldb;

import com.newegg.redis.model.enums.RedisServerStatus;

/**
 * Redis服务
 */
public class D_ServerInfo extends D_Level{
	private static final long serialVersionUID = 2813052912562286682L;

	@Override
	String key() {
		return ip + ":" + port;
	}

	/**
	 * IP地址
	 */
	private String ip;
	/**
	 * hostname
	 */
	private String hostname;
	/**
	 * port
	 */
	private String port;
	/**
	 * 机器登陆名称
	 */
	private String userName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 工作空间
	 */
	private String workhome;
	/**
	 * 服务状态
	 */
	private RedisServerStatus status;

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getWorkhome() {
		return workhome;
	}
	public void setWorkhome(String workhome) {
		this.workhome = workhome;
	}
	public RedisServerStatus getStatus() {
		return status;
	}
	public void setStatus(RedisServerStatus status) {
		this.status = status;
	}
	
}
