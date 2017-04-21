package com.newegg.redis.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.leveldb.D_RedisVersion;
import com.newegg.redis.service.RedisInstallService;

@RestController
@RequestMapping("/version")
public class RedisVersionController extends BaseController{
	
	@Autowired
	RedisInstallService redisInstallService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<D_RedisVersion> list() throws Exception {
		return redisInstallService.getRedisVersion();
	}
	
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public Object del(String name) throws Exception {
		redisInstallService.del(name);
		return SUCCESS();
	}
}
