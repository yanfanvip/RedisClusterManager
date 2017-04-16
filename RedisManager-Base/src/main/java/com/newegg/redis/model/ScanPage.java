package com.newegg.redis.model;

import java.util.Set;

public class ScanPage {
	
	private String query;
	private Integer pageSize= 20;
	private Integer client = 0; 
	private String cursor = "0";
	private Set<String> keys;
	private Boolean hasMore = true;
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getClient() {
		return client;
	}
	public void setClient(Integer client) {
		this.client = client;
	}
	public String getCursor() {
		return cursor;
	}
	public void setCursor(String cursor) {
		this.cursor = cursor;
	}
	public Set<String> getKeys() {
		return keys;
	}
	public void setKeys(Set<String> keys) {
		this.keys = keys;
	}
	public Boolean getHasMore() {
		return hasMore;
	}
	public void setHasMore(Boolean hasMore) {
		this.hasMore = hasMore;
	}
	
}
