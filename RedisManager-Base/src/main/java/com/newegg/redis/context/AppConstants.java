package com.newegg.redis.context;

public final class AppConstants {
	
	/**
	 * 集群状态
	 */
	public static final String CLUSTER_STATE = "cluster_state";
	public static final String OK = "ok";
	public static final String FAIL = "fail";
	
	/**
	 * 槽点信息
	 */
	public static final String CLUSTER_SLOTS_ASSIGNED = "cluster_slots_assigned";
	public static final String CLUSTER_SLOTS_OK = "cluster_slots_ok";
	
	/**
	 * 节点角色 
	 */
	public static final String NODE_ROLE_MASTER = "master";
	public static final String NODE_ROLE_SLAVE = "slave";
	
	
	/**
	 * 软件安装进度
	 * -1:未安装
	 * 1:已安装，未生成集群
	 * 2:生成集群中
	 * 3:安装成功，在集群中
	 */
	public static final int REDIS_INSTALL_NOT = -1;
	public static final int REDIS_INSTALL_YES = 1;
	public static final int REDIS_INSTALL_ING = 2;
	public static final int REDIS_INSTALL_END = 3;
	
	
	public static final String LEVEL_DATABASES_SYSTEM = "SYSTEM";
}