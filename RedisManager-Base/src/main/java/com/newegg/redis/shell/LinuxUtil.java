package com.newegg.redis.shell;

import com.newegg.redis.notify.Notify;
import com.newegg.redis.shell.client.SftpInterface;
import com.newegg.redis.shell.client.ShellClient;

public class LinuxUtil extends ShellUtil{
	
	public LinuxUtil(ShellClient client, Notify notify){
		super(client, notify);
	}
	
	public LinuxUtil(SftpInterface ftp, ShellClient client, Notify notify) {
		super(ftp, client, notify);
	}

	public boolean x64() throws Exception {
		String str = client.exec("file /bin/ls");
		if(str.indexOf("64-bit") != -1){
			return true;
		}else{
			return false;
		}
	}
	
	public void untar(String tarFile, String workpath) throws Exception{
		client.exec("tar -xvf " + tarFile + " -C " + workpath, 100);
	}
	
	public void mkdirs(String path) throws Exception{
		try {
			client.exec("mkdir -p " + path);
		} catch (Exception e) {//若没有权限
			client.execBySU("mkdir -p " + path);
			chown(path);
		}
	}
	
	public boolean checkDir(String path) throws Exception{
		String code = client.exec("if [ -d '"+ path +"' ]; then echo 0; else echo -1; fi");
		return code.trim().equals("0");
	}
	
	public boolean checkFile(String path) throws Exception{
		String code = client.exec("if [ -f '"+ path +"' ]; then echo 0; else echo -1; fi");
		return code.trim().equals("0");
	}
	
	public void rename(String oldName,String newName) throws Exception {
		client.exec("mv " + oldName + " " + newName);
	}
	
	public void remove(String path) throws Exception {
		client.exec("rm -rf " + path);
	}
	
	public String process(String process) throws Exception {
		return client.exec("ps -ef|grep '" + process + "' |grep -v grep|awk '{print $2}'");
	}
	
	public void kill(String process) throws Exception {
		client.exec("ps -ef|grep '" + process + "' |grep -v grep|awk '{print $2}'|xargs kill");
	}
	
	public void write(String file, String message) throws Exception{
		client.exec("echo '"+message+"' > " + file);
	}
	
	public void cd(String path) throws Exception {
		client.exec("cd "+ path);
	}
	
	public void chmod(String path) throws Exception {
		client.execBySU("chmod 777 "+ path);
	}
	
	public void chown(String path) throws Exception {
		client.execBySU("chown "+ path + " " + client.getUser());
	}
}
