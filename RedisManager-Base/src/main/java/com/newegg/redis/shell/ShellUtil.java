package com.newegg.redis.shell;

import com.newegg.redis.notify.Notify;
import com.newegg.redis.shell.client.SftpInterface;
import com.newegg.redis.shell.client.ShellClient;

public abstract class ShellUtil{
	SftpInterface ftp;
	ShellClient client;
	Notify notify;
	
	public ShellUtil(ShellClient client, Notify notify){
		this.client = client;
		this.notify = notify;
	}
	
	public ShellUtil(SftpInterface ftp, ShellClient client, Notify notify){
		this.ftp = ftp;
		this.client = client;
		this.notify = notify;
	}
	
	void message(String message) {
		if(notify != null){ notify.terminal(message); }
	}
}
