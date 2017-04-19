package com.newegg.redis.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.newegg.redis.cluster.RedisClusterTerminal;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.leveldb.D_ClusterNode_Tree;
import com.newegg.redis.leveldb.D_ComputerInfo;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.leveldb.D_RedisInfo;
import com.newegg.redis.model.ClusterServerCache;
import com.newegg.redis.model.M_clusterInfo;
import com.newegg.redis.model.enums.RedisNodeStatus;
import com.newegg.redis.service.ClusterInfoService;
import com.newegg.redis.service.ClusterNodeService;
import com.newegg.redis.service.ComputerInfoService;
import com.newegg.redis.service.RedisInfoService;
import com.newegg.redis.util.ClusterTreeUtil;

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
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		D_ClusterInfo info = clusterInfoService.getClusterInfo(cluster);
		RedisClusterTerminal client = new RedisClusterTerminal(info.getLast_read_host(), info.getLast_read_port());
		try {
			M_clusterInfo redisClusterInfo = clusterInfoService.getClusterInfoByRedis(client);
			info = clusterInfoService.updateClusterInfoByRedis(cluster, redisClusterInfo);
		}catch (Exception e) { } finally {
			client.close();
		}
		return info;
	}
	
	@RequestMapping(value = "/cluster/nodes/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public List<D_RedisClusterNode> clusternodes(@PathVariable String cluster) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		List<D_RedisClusterNode> oldNodes = clusterNodeService.getAllClusterNodes(cluster);
		for (D_RedisClusterNode n : oldNodes) {
			if(n.getStatus() == RedisNodeStatus.CONNECT){
				RedisClusterTerminal client = null;
				try {
					client = new RedisClusterTerminal(n.getHost(), n.getPort());
					List<D_RedisClusterNode> list = clusterNodeService.getClusterNodesByRedis(cluster, client);
					clusterNodeService.addClusterNodes(cluster, list);
					return list;
				} catch (Exception e) { }finally {
					if(client != null){
						client.close();
					}
				}
			}
		}
		return oldNodes;
	}
	
	@RequestMapping(value = "/cluster/tree/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public D_ClusterNode_Tree clustetree(@PathVariable String cluster) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		List<D_RedisClusterNode> nodes = clusternodes(cluster);
		return ClusterTreeUtil.getLevelTree(nodes);
	}
	
	@RequestMapping(value = "/cluster/serverInfo/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public List<D_ComputerInfo> serverInfo(@PathVariable String cluster) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		return computerInfoService.getAll(cluster);
	}
	
	@RequestMapping(value = "/cluster/redisInfo/{cluster}", method = RequestMethod.GET)
	@ResponseBody
	public List<D_RedisInfo> redisInfo(@PathVariable String cluster) throws Exception {
		if(!ClusterServerCache.clusterExist(cluster)){
			return null;
		}
		return redisInfoService.getAll(cluster);
	}
}
