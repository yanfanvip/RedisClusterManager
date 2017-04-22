package org.redis.manager.leveldb;

/**
 * Redis服务
 */
public class D_ServerInfo extends D_Level{
	private static final long serialVersionUID = 2813052912562286682L;

	@Override
	String key() {
		return ip;
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
}
