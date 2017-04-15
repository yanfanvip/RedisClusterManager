package com.newegg.redis.shell;

import java.io.File;

import com.newegg.redis.notify.Notify;
import com.newegg.redis.shell.client.SftpInterface;
import com.newegg.redis.shell.client.ShellClient;

public class MonitorUtil extends LinuxUtil{

	static String monitorPath = "systemMonitor";
	static String jarFile = "systemMonitor.jar";
	static String sourceRule = "systemMonitor.gz";
	
	String workpath;
	String source;
	
	public MonitorUtil(SftpInterface ftp, ShellClient client, String workpath, String source, Notify notify) {
		super(ftp, client, notify);
		this.workpath = workpath;
		this.source = source;
	}
	
	/**
	 * 监控是否运行
	 * @throws Exception
	 */
	public boolean check() throws Exception{
		String pid = process(jarFile);
		message(">> monitor is running; pid:" + pid);
		if(pid != null && !"".equals(pid)){
			return true;
		}
		return false;
	}
	
	/**
	 * 安装监控
	 * @param source 监控系统
	 * @param site 服务地址
	 */
	public void init(String site) throws Exception {
		if(check()){return;}
		mkdirs(workpath);
		cd(workpath);
		if(!checkDir(workpath + "/" + monitorPath)){
			if(!checkFile(workpath + "/monitor.gz")){//如果没有安装服务
				File sourceFile = new File(source + "/" + sourceRule);
				message(">> upload file:" + sourceFile.getPath());
				ftp.upload(workpath, sourceFile, "monitor.gz");
			}
			message(">> unzip systemMonitor");
			untar(workpath + "/monitor.gz", workpath);
			remove(workpath + "/" + monitorPath + "/start.sh");
			message(">> init start file");
			write(workpath + "/" + monitorPath + "/start.sh", "nohup java -jar systemMonitor.jar --website=http://"+site+"/metric >debug.log 2>&1 &");
			chmod(workpath + "/" + monitorPath +"/start.sh");
		}
		message(">> start monitor");
		cd(workpath + "/" + monitorPath);
		client.exec(workpath + "/" + monitorPath +"/start.sh");
		if(check()){return;}
		throw new Exception("monitor is not running");
	}
	
	public static void main(String[] args) throws Exception {
		SftpInterface ftp = SftpFactory.create("10.16.236.133", "root", "12345678");
		ShellClient client = new ShellClient("10.16.236.133", "root", "12345678");
		MonitorUtil util = new MonitorUtil(ftp, client, "/opt/app/redisManager", "E:\\source", new Notify() {
			@Override
			public void terminal(String message) {
				System.out.println(message);
			}

			@Override
			public void close() {
				
			}
		});
		util.init("10.16.232.129:8080");
	}
	
}
