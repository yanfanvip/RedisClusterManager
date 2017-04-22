package org.redis.manager.shell;

import org.redis.manager.notify.Notify;
import org.redis.manager.shell.client.SftpInterface;
import org.redis.manager.shell.client.ShellClient;

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
