package com.newegg.redis.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.leveldb.D_ClusterNode_Tree;
import com.newegg.redis.leveldb.D_ComputerInfo;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.leveldb.D_RedisInfo;
import com.newegg.redis.service.ClusterInfoService;
import com.newegg.redis.service.ClusterNodeService;
import com.newegg.redis.service.ComputerInfoService;
import com.newegg.redis.service.RedisInfoService;

@RestController
@RequestMapping("/info")
public class SystemInfoController extends BaseController{
	
	@Autowired
	ClusterInfoService clusterInfoService;
	
	@Autowired
	ClusterNodeService clusterNodeService;
	
	@Autowired
	ComputerInfoService computerInfoService;
	
	@Autowired
	RedisInfoService redisInfoService;
	
	@RequestMapping(value = "/clusters", method = RequestMethod.GET)
	@ResponseBody
	public List<D_ClusterInfo> clusters() throws Exception {
		return clusterInfoService.getAll();
	}
	
	@RequestMapping(value = "/cluster/info/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public D_ClusterInfo cluster(@PathVariable String cluster) throws Exception {
		return clusterInfoService.getClusterInfo(cluster);
	}
	
	@RequestMapping(value = "/cluster/nodes/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public List<D_RedisClusterNode> clusternodes(@PathVariable String cluster) throws Exception {
		return clusterNodeService.getAllClusterNodes(cluster);
	}
	
	@RequestMapping(value = "/cluster/tree/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public D_ClusterNode_Tree clustetree(@PathVariable String cluster) throws Exception {
		return clusterNodeService.getClusterTree(cluster);
	}
	
	@RequestMapping(value = "/cluster/serverInfo/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public List<D_ComputerInfo> serverInfo(@PathVariable String cluster) throws Exception {
		return computerInfoService.getAll(cluster);
	}
	
	@RequestMapping(value = "/cluster/redisInfo/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public List<D_RedisInfo> redisInfo(@PathVariable String cluster) throws Exception {
		return redisInfoService.getAll(cluster);
	}
	
}
