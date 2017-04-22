package org.redis.manager.controller;

import java.util.List;
import org.redis.manager.model.RedisVersionInfo;
import org.redis.manager.service.RedisInstallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class RedisVersionController extends BaseController{
	
	@Autowired
	RedisInstallService redisInstallService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<RedisVersionInfo> list() throws Exception {
		return redisInstallService.getRedisVersion();
	}
	
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Object del(String name) throws Exception {
		redisInstallService.del(name);
		return SUCCESS();
	}
}
