package com.newegg.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.model.ClusterServerCache;
import com.newegg.redis.monitor.MonitorRedis;
import com.newegg.redis.service.ClusterInfoService;
import com.newegg.redis.service.ClusterNodeService;

@RestController
@RequestMapping("/manager")
public class ClusterManagerController extends BaseController{

	@Autowired
	ClusterInfoService clusterInfoService;
	
	@Autowired
	ClusterNodeService clusterNodeService;
	
	@Autowired
	MonitorRedis monitor;
	
	@RequestMapping(value = "/cluster/add", method = RequestMethod.POST)
	@ResponseBody
	public Object cluster_add(D_ClusterInfo clusterInfo) throws Exception {
		clusterInfo = clusterInfoService.addClusterInfo(clusterInfo);
		ClusterServerCache.updateServer(clusterInfo, null);
		monitor.updateCluster(clusterInfo);
		return SUCCESS(clusterInfo.getUuid());
	}
	
	@RequestMapping(value = "/cluster/delete/{cluster}", method = RequestMethod.POST)
	@ResponseBody
	public Object cluster_delete(@PathVariable String cluster) throws Exception {
		if(ClusterServerCache.clusterExist(cluster)){
			clusterInfoService.delete(cluster);
			ClusterServerCache.deleteCluster(cluster);
		}
		return SUCCESS();
	}
	
	@RequestMapping(value = "/cluster/addNode/{cluster}", method = RequestMethod.POST)
	@ResponseBody
	public Object addNode(@PathVariable String cluster, @RequestParam String host, @RequestParam int port) throws Exception {
		if(ClusterServerCache.clusterExist(cluster)){
			clusterInfoService.addNode(cluster, host, port);
		}
		return SUCCESS();
	}
	
	@RequestMapping(value = "/cluster/{cluster}/tomaster/{node}", method = RequestMethod.POST)
	@ResponseBody
	public Object tomaster(@PathVariable String cluster, @PathVariable String node) throws Exception {
		if(ClusterServerCache.clusterExist(cluster)){
			clusterNodeService.toMaster(cluster, node);
		}
		return SUCCESS();
	}
	
	@RequestMapping(value = "/cluster/{cluster}/forget/{node}", method = RequestMethod.POST)
	@ResponseBody
	public Object forget(@PathVariable String cluster, @PathVariable String node) throws Exception {
		if(ClusterServerCache.clusterExist(cluster)){
			clusterNodeService.forget(cluster, node);
		}
		return SUCCESS();
	}
	
	@RequestMapping(value = "/cluster/{cluster}/slaveof/{master}/{node}", method = RequestMethod.POST)
	@ResponseBody
	public Object slaveof(@PathVariable String cluster, @PathVariable String master, @PathVariable String node) throws Exception {
		if(ClusterServerCache.clusterExist(cluster)){
			clusterNodeService.slaveof(cluster, master, node);
		}
		return SUCCESS();
	}
}
