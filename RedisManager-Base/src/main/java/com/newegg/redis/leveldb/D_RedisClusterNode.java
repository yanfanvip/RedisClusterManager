package com.newegg.redis.leveldb;

import java.util.List;
import com.newegg.redis.model.M_Slot;
import com.newegg.redis.model.enums.RedisClusterRole;
import com.newegg.redis.model.enums.RedisNodeStatus;

public class D_RedisClusterNode extends D_Level{
	private static final long serialVersionUID = 6720963927382832155L;

	@Override
	String key() {
		return host + ":" + port;
	}

	private String node;
	private String host;
	private Integer port;
	private RedisClusterRole role;
	private String master;
	private RedisNodeStatus status;
	private Boolean myself;
	private List<M_Slot> slots;

	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public RedisClusterRole getRole() {
		return role;
	}
	public void setRole(RedisClusterRole role) {
		this.role = role;
	}
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public RedisNodeStatus getStatus() {
		return status;
	}
	public void setStatus(RedisNodeStatus status) {
		this.status = status;
	}
	public Boolean getMyself() {
		return myself;
	}
	public void setMyself(Boolean myself) {
		this.myself = myself;
	}
	public List<M_Slot> getSlots() {
		return slots;
	}
	public void setSlots(List<M_Slot> slots) {
		this.slots = slots;
	}
}
