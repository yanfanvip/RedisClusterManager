package com.newegg.redis.shell.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.newegg.redis.notify.Notify;

public class ScpClient implements SftpInterface{
	
	private String hosts;
	private String user;
	private Session session;
	private ChannelShell exec;
	private InputStream input;
	private OutputStream output;
	private Notify notify;
	
	public ScpClient(String host,String user,String password) throws Exception{
		this(host, user, password, false, null);
	}
	
	public ScpClient(String host,String user,String password, Notify notify) throws Exception{
		this(host, user, password, false, notify);
	}

	public ScpClient(String host,String user,String password,boolean su,Notify notify) throws Exception{
		this.hosts = host;
		this.user = user;
		session = connect(host, user, password);
		exec = (ChannelShell) session.openChannel("shell");
		exec.setPtySize(160, 50, 1280, 800);
		input = exec.getInputStream();
		output = exec.getOutputStream();
		exec.connect();
		this.notify = notify;
		get();
		if(su){
			getSU(password);
		}
	}
	
    /**
     * 连接到服务器
     */
    private static Session connect(String host,String user,String pass) throws JSchException{
    	JSch sch = new JSch();
    	Session session = sch.getSession(user, host, 22);
    	session.setPassword(pass);
    	Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(60000);
        session.connect();
        return session;
    }
    
	public String exec(String cmd) throws Exception{
		put(cmd);
		return getContext();
	}
	
	public void getSU(String password) throws Exception {
		put("sudo su");
		String rets = get();
		if(!rets.endsWith("#")){
			put(password);
			get();
		}
	}
	
	private String getContext() throws Exception{
		String context = get();
		String[] lines = context.split("\n");
		if(lines.length >= 2){
			context = context.replace(lines[0]+"\n", "");
			context = context.replace(lines[lines.length - 1], "");
		}
		return context.trim();
	}
	
	private String get() throws Exception{
		String context = "";
		int time = 5 * 5;
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
				}
			}
			Thread.sleep(100);
		}
		if(notify != null){
			notify.terminal(context);
		}
		return context.trim();
	}
	
	public void put(String cmd) throws IOException{
		cmd = cmd + "\n";
		output.write(cmd.getBytes());
		output.flush();
	}
	
	public void close(){
		try {
			output.close();
			input.close();
		} catch (Exception e) {}
		exec.disconnect();
		session.disconnect();
	}

	public String getHosts() {
		return hosts;
	}

	public String getUser() {
		return user;
	}
	
	public boolean isExist(String directory) throws Exception{
		String rets = exec("ls -lt " + directory);
		if(rets == null || "".equals(rets.trim()) || rets.trim().indexOf("ls:") > 0){
			if(notify != null){
				notify.terminal("isExist " + directory + ":false");
			}
			return false;
		}else{
			if(notify != null){
				notify.terminal("isExist " + directory + ":true");
			}
			return true;
		}
	}
	
	public void mkdir(String directory) throws Exception{
		exec("mkdir -p " + directory);
	}
	
	public void upload(String directory, File file) throws Exception{
		upload(directory, file, file.getName());
	}
	
	public void upload(String directory, File file,String name) throws Exception{
		if(notify != null){
			notify.terminal("upload " + file.getPath());
		}
		boolean ptimestamp = true;
		String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + directory;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		try {
			channel.connect();
			if (checkAck(in) != 0) {
				return;
			}
			if (ptimestamp) {
				command = "T " + (file.lastModified() / 1000) + " 0";
				command += (" " + (file.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					return;
				}
			}
			long filesize = file.length();
			command = "C0644 " + filesize + " " + name + "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				return;
			}
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len);
			}
			fis.close();
			fis = null;
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				return;
			}
			if(notify != null){
				notify.terminal("upload success");
			}
		}finally{
			out.close();
			channel.disconnect();
		}
	}
	
	public void delete(String directory, String deleteFile)throws Exception{
		if(notify != null){
			notify.terminal("delete " + directory + "/" + deleteFile);
		}
		exec("rm -rf " + directory + "/" + deleteFile);
	}
	
	
	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		if (b == 0)
			return b;
		if (b == -1)
			return b;
		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
		}
		return b;
	}
}
