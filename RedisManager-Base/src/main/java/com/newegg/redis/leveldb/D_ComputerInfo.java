package com.newegg.redis.leveldb;


/**
 * 机器监控信息存储
 */
public class D_ComputerInfo extends D_TimeLine{
	private static final long serialVersionUID = 7859547698765261008L;

	@Override
	public String id() {
		return ip;
	}
	
	/**
	 * IP地址
	 */
	private String ip;
	/**
	 * 机器名
	 */
	private String hostname;
	/**
	 * 总内存
	 */
	private Long totalMem;
	/**
	 * 剩余内存
	 */
	private Long freeMem;
	/**
	 * 内存占比
	 */
	private Double combinedMem;
	/**
	 * CPU占比
	 */
	private Double combinedCpu;
	/**
	 * 磁盘总量
	 */
	private Long totalDisk;
	/**
	 * 磁盘剩余量
	 */
	private Long freeDisk;
	/**
	 * 磁盘占比
	 */
	private Double combinedDisk;
	/**
	 * 交换区总量
	 */
	private Long totalSwap;
	/**
	 * 空余交换区
	 */
	private Long freeSwap;
	/**
	 * 交换区占比
	 */
	private Double combinedSwap;
	
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
	public Long getTotalMem() {
		return totalMem;
	}
	public void setTotalMem(Long totalMem) {
		this.totalMem = totalMem;
	}
	public Long getFreeMem() {
		return freeMem;
	}
	public void setFreeMem(Long freeMem) {
		this.freeMem = freeMem;
	}
	public Double getCombinedMem() {
		return combinedMem;
	}
	public void setCombinedMem(Double combinedMem) {
		this.combinedMem = combinedMem;
	}
	public Double getCombinedCpu() {
		return combinedCpu;
	}
	public void setCombinedCpu(Double combinedCpu) {
		this.combinedCpu = combinedCpu;
	}
	public Long getTotalDisk() {
		return totalDisk;
	}
	public void setTotalDisk(Long totalDisk) {
		this.totalDisk = totalDisk;
	}
	public Long getFreeDisk() {
		return freeDisk;
	}
	public void setFreeDisk(Long freeDisk) {
		this.freeDisk = freeDisk;
	}
	public Double getCombinedDisk() {
		return combinedDisk;
	}
	public void setCombinedDisk(Double combinedDisk) {
		this.combinedDisk = combinedDisk;
	}
	public Long getTotalSwap() {
		return totalSwap;
	}
	public void setTotalSwap(Long totalSwap) {
		this.totalSwap = totalSwap;
	}
	public Long getFreeSwap() {
		return freeSwap;
	}
	public void setFreeSwap(Long freeSwap) {
		this.freeSwap = freeSwap;
	}
	public Double getCombinedSwap() {
		return combinedSwap;
	}
	public void setCombinedSwap(Double combinedSwap) {
		this.combinedSwap = combinedSwap;
	}
}
