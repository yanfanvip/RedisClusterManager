package com.newegg.monitor.util;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

public class MonitorUtil {
	static{
		File libPath = new File(System.getProperty("user.dir") + "/lib");
		libPath.mkdirs();
		System.setProperty("java.io.tmpdir", libPath.getPath());
	}
	private static Sigar sigar = new Sigar();
	
	static class staticResource{
		public static Long totalMem;//总内存数
		public static Long totalDisk;//磁盘
		public static Long totalSwap;//交换区
		public static String ips;//全部内网IP地址
		public static String hostname;//机器名称
	}
	
	public static Map<String,String> getSystemInfo(String ip) throws Exception{
		Map<String, String> form = new HashMap<String, String>();
		form.put("hostname", MonitorUtil.getLocalHostName());
		if(ip != null && !"".equals(ip)){
			form.put("ip", ip);
		}else{
			form.put("ip", MonitorUtil.getAllIp());
		}
		form.put("totalMem", MonitorUtil.totalMem() + "");
		form.put("freeMem", MonitorUtil.freeMem() + "");
		form.put("combinedMem",MonitorUtil.combinedMem() + "");
		form.put("combinedCpu",MonitorUtil.combinedCpu() + "");
		form.put("combinedDisk",MonitorUtil.combinedDisk() + "");
		form.put("totalDisk", MonitorUtil.totalDisk() + "");
		form.put("freeDisk", MonitorUtil.freeDisk() + "");
		form.put("totalSwap",MonitorUtil.totalSwap() + "");
		form.put("freeSwap",MonitorUtil.freeSwap() + "");
		form.put("combinedSwap",MonitorUtil.combinedSwap() + "");
		return form;
	}
	
	/**
	 * 获取总内存
	 */
	public static long totalMem() throws SigarException{
		if(staticResource.totalMem == null){
			Mem mem = sigar.getMem();
			staticResource.totalMem = (long)(mem.getTotal()/1048576);
		}
		return staticResource.totalMem;
	}
	
	/**
	 * 获取剩余内存
	 */
	public static long freeMem() throws SigarException{
		Mem mem = sigar.getMem();
		long used = (long)(mem.getFree()/1048576);
		return used;
	}
	
	/**
	 * 获取内存占用率
	 */
	public static double combinedMem() throws SigarException{
		long total = totalMem();
		long free = freeMem();
		long used = total - free;
		return ((long)(used * 10000 / total)) / 100.00;
	}
	
	/**
	 * 获取CPU占用率
	 */
	public static double combinedCpu() throws SigarException{
		CpuPerc cpuPerc = sigar.getCpuPerc();
		return ((long)(cpuPerc.getCombined() * 10000)) / 100.00;
	}
	
	/**
	 * 磁盘总量
	 */
	public static long totalDisk() throws SigarException {
		if(staticResource.totalDisk == null){
			FileSystem[] fs = sigar.getFileSystemList();
			long total = 0L;
			for (FileSystem fileSystem : fs) {
				try {
					if(fileSystem.getType() == 2){
		    			FileSystemUsage usage = sigar.getFileSystemUsage(fileSystem.getDirName());
		    			total += usage.getTotal();
		    		}
				} catch (Exception e) {}
			}
			if(total == 0){
				return 1;
			}
			staticResource.totalDisk = total;
		}
		return staticResource.totalDisk;
	}
	
	/**
	 * 磁盘剩余量
	 */
	public static long freeDisk() throws SigarException {
		FileSystem[] fs = sigar.getFileSystemList();
		long free = 0L;
		for (FileSystem fileSystem : fs) {
			try {
				if(fileSystem.getType() == 2){
	    			FileSystemUsage usage = sigar.getFileSystemUsage(fileSystem.getDirName());
	    			free += usage.getFree();
	    		}
			}catch (Exception e) {}
		}
		return free;
	}
	
	/**
	 * 磁盘占用率
	 */
	public static double combinedDisk() throws SigarException{
		long total = totalDisk();
		long free = freeDisk();
		long used = total - free;
		return ((long)used * 10000 / total) / 100.00;
	}
	
	/**
	 * 获取交换区占用率
	 */
	public static double combinedSwap() throws SigarException{
		long total = totalSwap();
		long free = freeSwap();
		long used = total - free;
		return ((long)(used * 10000 / total)) / 100.00;
	}
	
	/**
	 * 获取交换区总大小
	 */
	public static long totalSwap() throws SigarException{
		if(staticResource.totalSwap == null){
			Swap swap = sigar.getSwap();
			staticResource.totalSwap = (long)(swap.getTotal()/1048576);
		}
		return staticResource.totalSwap;
	}
	
	/**
	 * 获取交换区剩余
	 */
	public static long freeSwap() throws SigarException{
		Swap swap = sigar.getSwap();
		return (long)(swap.getFree()/1048576);
	}
	
	/**
	 * 获取全部内网IP
	 * @throws SocketException 
	 */
	public static String getAllIp() throws UnknownHostException, SocketException {
		if(staticResource.ips == null){
			Set<String> ips = new HashSet<String>();
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
	        while (netInterfaces.hasMoreElements()) {
	            NetworkInterface ni = netInterfaces.nextElement();
	            Enumeration<InetAddress> address = ni.getInetAddresses();
	            while (address.hasMoreElements()) {
	            	InetAddress inetAddress = address.nextElement();
	            	if(!inetAddress.isLoopbackAddress() 
	            			&& !inetAddress.isMulticastAddress()
	            			&& !inetAddress.isMCGlobal()
	            			&& !inetAddress.isMCLinkLocal()
	            			&& !inetAddress.isMCNodeLocal()
	            			&& !inetAddress.isMCOrgLocal()
	            			&& !inetAddress.isMCSiteLocal()
	            			&& !inetAddress.isLinkLocalAddress()
	            			&& !inetAddress.isAnyLocalAddress()){
//	            		System.out.println("===================================");
//		            	System.out.println("isLinkLocalAddress:" + inetAddress.isLinkLocalAddress());//本地连接地址
//		            	System.out.println("isAnyLocalAddress:" + inetAddress.isAnyLocalAddress());//任何本地地址
//		            	System.out.println("isLoopbackAddress:" + inetAddress.isLoopbackAddress());//本地地址
//		            	System.out.println("isMCGlobal:" + inetAddress.isMCGlobal());//全球广播地址
//		            	System.out.println("isMCLinkLocal:" + inetAddress.isMCLinkLocal());//子网广播
//		            	System.out.println("isMCNodeLocal:" + inetAddress.isMCNodeLocal());//本地接口广播地址
//		            	System.out.println("isMCOrgLocal:" + inetAddress.isMCOrgLocal());//组织范围的广播地址
//		            	System.out.println("isMCSiteLocal:" + inetAddress.isMCSiteLocal());//站点范围的广播
//		            	System.out.println("isMulticastAddress:" + inetAddress.isMulticastAddress());//广播地址
//		            	System.out.println("isSiteLocalAddress:" + inetAddress.isSiteLocalAddress());
	                    ips.add(inetAddress.getHostAddress());
//	                    System.out.println(inetAddress.getHostAddress());
//	                    System.out.println(inetAddress.getHostName());
	            	}
	            }
	        }
	        StringBuffer buffer = new StringBuffer();
	        for (String ip : ips) {
	        	buffer.append(ip).append(";");
			}
	        String realIp = buffer.substring(0, buffer.lastIndexOf(";"));
	        staticResource.ips = realIp;
		}
		return staticResource.ips;
    }
	
	/**
	 * 获取主机名称
	 * @throws SigarException 
	 */
    public static String getLocalHostName() throws SigarException{
    	if(staticResource.hostname == null){
    		staticResource.hostname = sigar.getFQDN();
    	}
    	return staticResource.hostname;
    }
}