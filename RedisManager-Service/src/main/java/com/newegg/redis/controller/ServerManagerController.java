package com.newegg.redis.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.leveldb.D_ServerInfo;
import com.newegg.redis.service.ServerInfoService;
import com.newegg.redis.util.BeanUtils;

@RestController
@RequestMapping("/server")
public class ServerManagerController extends BaseController{
	
	@Autowired
	ServerInfoService serverInfoService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<D_ServerInfo> list() throws Exception {
		List<D_ServerInfo> list = serverInfoService.getAllService();
		for (D_ServerInfo s : list) {
			s.setPassword(null);
		}
		return list;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(D_ServerInfo serverInfo) throws Exception {
		D_ServerInfo info = serverInfoService.get(serverInfo.getIp());
		if(serverInfo.getPassword()!= null && serverInfo.equals("")){
			serverInfo.setPassword(null);
		}
		if(info != null){
			BeanUtils.copyNotNullProperties(info, serverInfo);
		}else{
			info = serverInfo;
		}
		serverInfoService.addService(info);
		return SUCCESS();
	}
	
	@RequestMapping(value = "/del/{ip:.+}", method = RequestMethod.POST)
	@ResponseBody
	public Object del(@PathVariable String ip) throws Exception {
		serverInfoService.delete(ip);
		return SUCCESS();
	}
}
