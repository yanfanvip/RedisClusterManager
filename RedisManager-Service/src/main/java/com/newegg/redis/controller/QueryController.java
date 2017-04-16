package com.newegg.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.newegg.redis.model.ClusterServerCache;
import com.newegg.redis.model.ScanPage;
import com.newegg.redis.service.QueryService;

@RestController
@RequestMapping("/query")
public class QueryController extends BaseController{
	
	@Autowired
	QueryService queryService;

	@RequestMapping(value = "/scan/{cluster}", method = RequestMethod.POST)
	@ResponseBody
	public Object scan(@PathVariable String cluster, ScanPage page) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		return queryService.scan(cluster, page);
	}
	
	@RequestMapping(value = "/get/{cluster}/{key}", method = RequestMethod.GET)
	@ResponseBody
	public Object get(@PathVariable String cluster, @PathVariable String key) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		return SUCCESS(queryService.get(cluster, key));
	}
	
	@RequestMapping(value = "/delete/{cluster}/{key}", method = RequestMethod.POST)
	@ResponseBody
	public Object delete(@PathVariable String cluster, @PathVariable String key) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		queryService.delete(cluster, key);
		return SUCCESS();
	}
}
