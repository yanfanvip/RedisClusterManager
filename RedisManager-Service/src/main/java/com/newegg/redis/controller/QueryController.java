package com.newegg.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.model.ScanPage;
import com.newegg.redis.service.QueryService;

@RestController
@RequestMapping("/query")
public class QueryController {
	
	@Autowired
	QueryService queryService;

	@RequestMapping(value = "/scan/{cluster}", method = RequestMethod.POST)
	@ResponseBody
	public Object scan(@PathVariable String cluster, ScanPage page) throws Exception {
		return queryService.scan(cluster, page);
	}
	
}
