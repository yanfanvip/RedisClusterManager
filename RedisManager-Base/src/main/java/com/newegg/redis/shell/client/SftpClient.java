package com.newegg.redis.shell.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Vector;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.newegg.redis.notify.Notify;


public class SftpClient implements SftpInterface{

	private String hosts;
	private Session session;
	private ChannelSftp sftp;
	private Notify notify;
	
    public SftpClient(String host,String user,String pass,Notify notify) throws JSchException {
    	this.hosts = host;
    	session = connect(host, user, pass);
    	sftp = (ChannelSftp) session.openChannel("sftp");
    	this.notify = notify;
    	sftp.connect();
	}
    
    public SftpClient(String host,String user,String pass) throws JSchException {
    	this.hosts = host;
    	session = connect(host, user, pass);
    	sftp = (ChannelSftp) session.openChannel("sftp");
    	sftp.connect();
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

    /**
     * 路径是否存在
     */
    @SuppressWarnings("rawtypes")
	public boolean isExist(String directory) throws SftpException{
    	try {
    		Vector content = sftp.ls(directory);   
    	    if(content == null) {
    	    	return false;
    	    }else{
    	    	return true;
    	    }
		} catch (Exception e) {
			return false;
		}
    }
    /**
     * 创建路径文件夹
     */
    public void mkdir(String directory) throws SftpException{
    	String[] dirs = directory.split("/");
    	String mkdir = "/";
    	for (String line : dirs) {
    		if(!"".equals(line)){
    			mkdir += line + "/";
    			if(!isExist(mkdir)){
        			sftp.mkdir(mkdir);
        			if(notify != null){
        				notify.terminal("create " + directory);
        			}
        		}
    		}
		}
    }
    /** 
     * 上传文件 
     */  
    public void upload(String directory, File file) throws FileNotFoundException, SftpException {
    	if(notify != null){
    		notify.terminal("upload " + directory + "/" + file.getName());
		}
    	if(isExist(directory)){
    		sftp.cd(directory);
    	}else{
    		mkdir(directory);
    		sftp.cd(directory);
    	}
        sftp.put(new FileInputStream(file), file.getName());  
    }
    /** 
     * 上传文件 
     */  
    public void upload(String directory, File file,String name) throws FileNotFoundException, SftpException {
    	if(notify != null){
    		notify.terminal("upload " + directory + "/" + name);
		}
    	if(isExist(directory)){
    		sftp.cd(directory);
    	}else{
    		mkdir(directory);
    		sftp.cd(directory);
    	}
        sftp.put(new FileInputStream(file), name);
    }
  
    /** 
     * 删除文件 
     */  
    public void delete(String directory, String deleteFile)throws SftpException{
    	if(notify != null){
    		notify.terminal("remove " + directory + "/" + deleteFile);
		}
    	sftp.cd(directory);  
        sftp.rm(deleteFile);  
    }
  
    /**
     * 断开连接
     */
    public void close(){
    	sftp.disconnect();
    	session.disconnect();
    }

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
}
