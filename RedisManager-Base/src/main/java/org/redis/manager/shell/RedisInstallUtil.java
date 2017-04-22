package org.redis.manager.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.redis.manager.notify.Notify;
import org.redis.manager.shell.client.SftpInterface;
import org.redis.manager.shell.client.ShellClient;
import org.redis.manager.util.GzipUtil;


/**
 * Redis自动化部署
 * Redis结构如下
 * File : redis.3.0.6.x64.gz
  		redis3.0.6
		└── bin
		    ├── redis-benchmark
		    ├── redis-check-aof
		    ├── redis-check-dump
		    ├── redis-cli
		    ├── redis-sentinel -> redis-server
		    └── redis-server
 */
public class RedisInstallUtil extends LinuxUtil{

	
	static String redis_home = "redis";
	static String redis_data = "cluster";
	static String sourceRule = "redis.{version}.{system}.gz";
	static String config_template = "redis.conf.template";
	
	String workpath;
	String source;
	
	public RedisInstallUtil(SftpInterface ftp, ShellClient client, String workpath, String source, Notify notify) {
		super(ftp, client, notify);
		this.workpath = workpath;
		this.source = source;
	}
	
	/**
	 * 安装Redis服务
	 * @param name 需要安装的redis文件
	 * @param ports
	 * @throws Exception 
	 */
	public void install(String name, List<Integer> ports, Integer memory, String config) throws Exception {
		mkdirs(workpath);
		cd(workpath);
		if(!checkDir(workpath + "/" + redis_home)){//Redis没有部署
			if(!checkFile(workpath + "/redis.gz")){//没有源文件
				File sourceFile = new File(source + "/" + name);
				message(">> upload file:" + sourceFile.getPath());
				ftp.upload(workpath, sourceFile, "redis.gz");
			}
			String currentVersion = GzipUtil.getTarFiles(source + "/" + name).get(0);
			message(">> unzip redis");
			untar(workpath + "/redis.gz", workpath);
			rename(workpath + "/" + currentVersion, workpath + "/redis");
		}
		cd(workpath);
		for (Integer port : ports) {
			mkdirs(workpath + "/" + redis_data + "/" + port);
			File configFile = replaceFile(port, memory, config);
			message(">> upload config file:" + configFile.getPath());
			ftp.upload(workpath + "/" + redis_data + "/" + port, configFile, "redis.conf");
			configFile.delete();
			message(">> start redis port:" + port);
			if(checkFile(workpath + "/" + redis_home + "/bin/redis-server")){
				client.exec("./"+redis_home+"/bin/redis-server ./"+redis_data+"/"+port+"/redis.conf");
			}else{
				client.exec("./"+redis_home+"/src/redis-server ./"+redis_data+"/"+port+"/redis.conf");
			}
			Thread.sleep(3000);
			if(check(port) == null){
				throw new Exception("start redis fail");
			}
		}
		message(">> all node is running");
	}
	
	public void restart(List<Integer> ports) throws Exception {
		for (Integer port : ports) {
			restart(port);
		}
		message(">> all node is running");
	}
	
	public void restart(int port) throws Exception{
		String pid = check(port);
		if(pid != null){
			message(">> kill redis port:" + port);
			kill(pid);
		}
		message(">> restart redis port:" + port);
		if(checkFile(workpath + "/" + redis_home + "/bin/redis-server")){
			client.exec("./"+redis_home+"/bin/redis-server ./"+redis_data+"/"+port+"/redis.conf");
		}else{
			client.exec("./"+redis_home+"/src/redis-server ./"+redis_data+"/"+port+"/redis.conf");
		}
		Thread.sleep(3000);
		if(check(port) == null){
			throw new Exception("restart redis fail");
		}
	}
	
	public void stop(int port) throws Exception{
		String pid = check(port);
		if(pid != null){
			message(">> kill redis port:" + port);
			kill(pid);
		}
	}
	
	public File replaceFile(int port, Integer memory,String config) throws IOException{
		message(">> Make config file with port: "+ port);
		File file = new File(source + "/redis-" + port + ".conf");
		File template = new File(source + "/" + config_template);
		if(file.exists()){
			file.delete();
		}
		HashMap<String,String> configMap = new HashMap<String,String>();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(template));
		BufferedReader bufferedReader = new BufferedReader(reader);
		{
			String line = null;
	        while((line = bufferedReader.readLine()) != null){
	        	if(line.length() > 2 && line.indexOf(" ") > -1 && (line.length() > line.indexOf(" ")+1)){
	        		String key = line.substring(0,line.indexOf(" "));
	        		String value = line.substring(line.indexOf(" ")+1);
	        		configMap.put(key, value);
	        	}
	        }
	        bufferedReader.close();
	        reader.close();
		}
        configMap.put("port", port + "");
        configMap.put("dir",  workpath + "/" + redis_data + "/" + port + "/");
        configMap.put("cluster-config-file", "nodes.conf");
        if(memory != null){
        	configMap.put("maxmemory", memory+"mb");
        }
        if(config != null && !"".equals(config)){
        	String[] lines = config.split(";");
        	for (String line : lines) {
        		if(line!= null && line.indexOf("=")>0){
        			String key = line.substring(0,line.indexOf("="));
            		String value = line.substring(line.indexOf("=")+1);
            		configMap.put(key, value);
        		}
			}
        }
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        List<String> keys = new ArrayList<String>(configMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
        	String value = configMap.get(key);
        	if(value != null){
        		writer.write(key + " " + value + "\n\n");
        	}
		}
        writer.flush();
        writer.close();
		return file;
	}
	
	public String check(int port) throws Exception{
		String processNum = client.exec("ps -ef | grep *:"+ port +" | grep redis | grep -v grep | awk '{print $2}'");
		if(processNum != null && !"".equals(processNum)){
			return processNum;
		}
		return null;
	}
}
