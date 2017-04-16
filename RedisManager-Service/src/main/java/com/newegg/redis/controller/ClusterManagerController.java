package com.newegg.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.model.ClusterServerCache;
import com.newegg.redis.service.ClusterInfoService;

@RestController
@RequestMapping("/manager")
public class ClusterManagerController extends BaseController{

	@Autowired
	ClusterInfoService clusterInfoService;
	
	@RequestMapping(value = "/cluster/add", method = RequestMethod.POST)
	@ResponseBody
	public Object cluster_add(D_ClusterInfo clusterInfo) throws Exception {
		String id = clusterInfoService.addClusterInfo(clusterInfo);
		return SUCCESS(id);
	}
	
	@RequestMapping(value = "/cluster/delete/{cluster}", method = RequestMethod.POST)
	@ResponseBody
	public Object cluster_delete(@PathVariable String cluster) throws Exception {
		if(ClusterServerCache.clusterExist(cluster)){
			clusterInfoService.delete(cluster);
		}
		return SUCCESS();
	}
}
