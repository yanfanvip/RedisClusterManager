package org.redis.manager.controller;

import java.util.List;

import org.redis.manager.context.AppConfig;
import org.redis.manager.leveldb.D_RedisInstall;
import org.redis.manager.leveldb.D_ServerInfo;
import org.redis.manager.service.RedisInstallService;
import org.redis.manager.shell.RedisInstallUtil;
import org.redis.manager.shell.SftpFactory;
import org.redis.manager.shell.client.SftpInterface;
import org.redis.manager.shell.client.ShellClient;
import org.redis.manager.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
public class ServerManagerController extends BaseController{
	
	@Autowired
	RedisInstallService redisInstallService;

	@Autowired 
	AppConfig appConfig;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<D_ServerInfo> list() throws Exception {
		List<D_ServerInfo> list = redisInstallService.getAllService();
		for (D_ServerInfo s : list) {
			s.setPassword(null);
		}
		return list;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(D_ServerInfo serverInfo) throws Exception {
		D_ServerInfo info = redisInstallService.getServer(serverInfo.getIp());
		if(serverInfo.getPassword()!= null && serverInfo.equals("")){
			serverInfo.setPassword(null);
		}
		if(info != null){
			BeanUtils.copyNotNullProperties(info, serverInfo);
		}else{
			info = serverInfo;
		}
		redisInstallService.addService(info);
		return SUCCESS();
	}
	
	@RequestMapping(value = "/del/{ip:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Object del(@PathVariable String ip) throws Exception {
		redisInstallService.delete(ip);
		return SUCCESS();
	}
	
	@RequestMapping(value = "/installed", method = RequestMethod.GET)
	@ResponseBody
	public List<D_RedisInstall> installed() throws Exception {
		return redisInstallService.getAllInstalled();
	}
	
	@RequestMapping(value = "/delInstall", method = RequestMethod.POST)
	@ResponseBody
	public Object delInstalled(D_RedisInstall install) throws Exception {
		D_ServerInfo info = redisInstallService.getServer(install.getIp());
		if(info == null){
			return FAIL("not find install");
		}
		SftpInterface ftp = null;
		ShellClient client = null;
		try {
			ftp = SftpFactory.create(info.getIp(), info.getUserName(), info.getPassword());
			client = new ShellClient(info.getIp(), info.getUserName(), info.getPassword());
			RedisInstallUtil util = new RedisInstallUtil(ftp, client, info.getWorkhome(), appConfig.getResource(), null);
			util.stop(install.getPort());
		}finally {
			if(ftp != null){
				ftp.close();
			}
			if(client != null){
				client.close();
			}
		}
		redisInstallService.delInstall(install.getIp(), install.getPort());
		return SUCCESS();
	}
}
