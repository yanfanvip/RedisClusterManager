package com.newegg.redis.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.leveldb.D_ComputerInfo;
import com.newegg.redis.model.convert.Convert;
import com.newegg.redis.service.ComputerInfoService;

@RestController
@RequestMapping
public class MonitorController extends BaseController{
	
	@RequestMapping(value = "metric", method = RequestMethod.POST)
	public void metric(@RequestParam Map<String, String> metric) throws Exception {
		D_ComputerInfo info = Convert.convert(metric, D_ComputerInfo.class);
		ComputerInfoService.addComputerInfo(info);
	}
	
}
