package org.redis.manager.controller;

import java.util.Map;

import org.redis.manager.leveldb.D_ComputerInfo;
import org.redis.manager.model.convert.Convert;
import org.redis.manager.service.ComputerInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MonitorController extends BaseController{
	
	@RequestMapping(value = "metric", method = RequestMethod.POST)
	public void metric(@RequestParam Map<String, String> metric) throws Exception {
		D_ComputerInfo info = Convert.convert(metric, D_ComputerInfo.class);
		ComputerInfoService.addComputerInfo(info);
	}
	
}
