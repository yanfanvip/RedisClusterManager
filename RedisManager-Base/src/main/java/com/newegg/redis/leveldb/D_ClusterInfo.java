package com.newegg.redis.leveldb;

public class D_ClusterInfo extends D_Level{
	private static final long serialVersionUID = 8364230072739348763L;

	@Override
	String key() {
		return uuid;
	}

	/**
	 * 主键
	 */
	private String uuid;
	
	private String name;
	
	private String cluster_state;
	private Integer cluster_slots_assigned;
	private Integer cluster_slots_ok;
	private Integer cluster_slots_pfail;
	private Integer cluster_slots_fail;
	private Integer cluster_known_nodes;
	private Integer cluster_size;
	private Integer cluster_current_epoch;
	private Integer cluster_my_epoch;
	private Integer cluster_stats_messages_sent;
	private Integer cluster_stats_messages_received;
	private String last_read_host;
	private Integer last_read_port;

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCluster_state() {
		return cluster_state;
	}
	public void setCluster_state(String cluster_state) {
		this.cluster_state = cluster_state;
	}
	public Integer getCluster_slots_assigned() {
		return cluster_slots_assigned;
	}
	public void setCluster_slots_assigned(Integer cluster_slots_assigned) {
		this.cluster_slots_assigned = cluster_slots_assigned;
	}
	public Integer getCluster_slots_ok() {
		return cluster_slots_ok;
	}
	public void setCluster_slots_ok(Integer cluster_slots_ok) {
		this.cluster_slots_ok = cluster_slots_ok;
	}
	public Integer getCluster_slots_pfail() {
		return cluster_slots_pfail;
	}
	public void setCluster_slots_pfail(Integer cluster_slots_pfail) {
		this.cluster_slots_pfail = cluster_slots_pfail;
	}
	public Integer getCluster_slots_fail() {
		return cluster_slots_fail;
	}
	public void setCluster_slots_fail(Integer cluster_slots_fail) {
		this.cluster_slots_fail = cluster_slots_fail;
	}
	public Integer getCluster_known_nodes() {
		return cluster_known_nodes;
	}
	public void setCluster_known_nodes(Integer cluster_known_nodes) {
		this.cluster_known_nodes = cluster_known_nodes;
	}
	public Integer getCluster_size() {
		return cluster_size;
	}
	public void setCluster_size(Integer cluster_size) {
		this.cluster_size = cluster_size;
	}
	public Integer getCluster_current_epoch() {
		return cluster_current_epoch;
	}
	public void setCluster_current_epoch(Integer cluster_current_epoch) {
		this.cluster_current_epoch = cluster_current_epoch;
	}
	public Integer getCluster_my_epoch() {
		return cluster_my_epoch;
	}
	public void setCluster_my_epoch(Integer cluster_my_epoch) {
		this.cluster_my_epoch = cluster_my_epoch;
	}
	public Integer getCluster_stats_messages_sent() {
		return cluster_stats_messages_sent;
	}
	public void setCluster_stats_messages_sent(Integer cluster_stats_messages_sent) {
		this.cluster_stats_messages_sent = cluster_stats_messages_sent;
	}
	public Integer getCluster_stats_messages_received() {
		return cluster_stats_messages_received;
	}
	public void setCluster_stats_messages_received(Integer cluster_stats_messages_received) {
		this.cluster_stats_messages_received = cluster_stats_messages_received;
	}
	public String getLast_read_host() {
		return last_read_host;
	}
	public void setLast_read_host(String last_read_host) {
		this.last_read_host = last_read_host;
	}
	public Integer getLast_read_port() {
		return last_read_port;
	}
	public void setLast_read_port(Integer last_read_port) {
		this.last_read_port = last_read_port;
	}
}
