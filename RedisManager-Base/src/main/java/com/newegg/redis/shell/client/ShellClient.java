package com.newegg.redis.shell.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.newegg.redis.notify.Notify;

public class ShellClient {
	
	private String hosts;
	private String user;
	private String password;
	private Session session;
	private ChannelShell shell;
	private InputStream input;
	private OutputStream output;
	private Notify notify;
	
	
	public ShellClient(String host,String user,String password) throws Exception{
		this(host, user, password, null);
	}
	

	public ShellClient(String host, String user, String password, Notify notify) throws Exception{
		this.hosts = host;
		this.user = user;
		this.password = password;
		session = this.connect();
		shell = (ChannelShell) session.openChannel("shell");
		shell.setPtySize(160, 50, 1280, 800);
		input = shell.getInputStream();
		output = shell.getOutputStream();
		shell.connect();
		this.notify = notify;
		get(30);
	}
	
    /**
     * 连接到服务器
     */
    private Session connect() throws JSchException{
    	JSch sch = new JSch();
    	Session session = sch.getSession(user, this.hosts, 22);
    	session.setPassword(password);
    	Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(60000);
        session.connect();
        return session;
    }
    
    public String execBySU(String cmd) throws Exception{
    	put("sudo " + cmd);
    	String context = get(10);
    	if(!context.endsWith("#") && !context.endsWith("$")){
    		put(password);
    		context = getContext(10);
    	}
		if(check()){
			return context;
		}
		throw new Exception("cmd [" + cmd + "] not execute; by :" + context);
    }
    
	public String exec(String cmd) throws Exception{
		put(cmd);
		String context = getContext(10);
		if(check()){
			return context;
		}
		throw new Exception("cmd [" + cmd + "] not execute; by :" + context);
	}
	
	public String exec(String cmd, long sleep) throws Exception{
		put(cmd);
		String context = getContext(sleep);
		if(check()){
			return context;
		}
		throw new Exception("cmd [" + cmd + "] not execute; by :" + context);
	}
	
	private boolean check() throws Exception{
		put("echo $?");
		String code = getContext(10);
		if(code.trim().equals("0")){
			return true;
		}else{
			return false;
		}
	}
	
	private String getContext(long sleep) throws Exception{
		String context = get(sleep);
		String[] lines = context.split("\n");
		if(lines.length >= 2){
			context = context.replace(lines[0]+"\n", "");
			context = context.replace(lines[lines.length - 1], "");
		}
		return context.trim();
	}
	
	private String get(long sleep) throws Exception{
		String context = "";
		int time = 300;
		byte[] contents = new byte[1024];
		while(time > 0){
			time--;
			while(input.available() > 0) {
				int i = input.read(contents, 0, 1024);
				if(i > 0){
					context += new String(contents, 0, i);
					if(context.trim().endsWith("#") || context.trim().endsWith("~") || context.trim().endsWith(":")){
						time = 2;
					}
				}else{
					time = 300;
				}
			}
			if(sleep > 0){
				Thread.sleep(sleep);
			}
		}
		if(notify != null){
			notify.terminal(context);
		}
		return context.trim();
	}
	
	public void put(String cmd) throws IOException{
		output.write((cmd + "\n").getBytes());
		output.flush();
	}
	
	public void close() throws IOException {
		output.close();
		input.close();
		shell.disconnect();
		session.disconnect();
	}

	public String getHosts() {
		return hosts;
	}

	public String getUser() {
		return user;
	}
}
