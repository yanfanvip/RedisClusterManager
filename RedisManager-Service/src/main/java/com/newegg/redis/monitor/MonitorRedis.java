package com.newegg.redis.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.newegg.redis.cluster.RedisClusterTerminal;
import com.newegg.redis.context.AppConfig;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.leveldb.D_RedisInfo;
import com.newegg.redis.model.ClusterServerCache;
import com.newegg.redis.model.M_clusterInfo;
import com.newegg.redis.model.M_info;
import com.newegg.redis.model.enums.RedisNodeStatus;
import com.newegg.redis.service.ClusterInfoService;
import com.newegg.redis.service.ClusterNodeService;
import com.newegg.redis.service.RedisInfoService;
import com.newegg.redis.util.RandomUtil;

@Component
@Scope("singleton")
public class MonitorRedis {
	static Log log = LogFactory.getLog(MonitorRedis.class);
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	ClusterNodeService clusterNodeService;
	
	@Autowired
	ClusterInfoService clusterInfoService;
	
	static Timer timer;
	
	@PostConstruct
	public void init(){
		if(timer == null){
			long period = appConfig.getMonitor_redis_period_second() * 1000;
			timer = new Timer();  
	        timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						startJob();
					} catch (Exception e) {
						log.error("redis monitor error", e);
					}
				}
			}, 0, period);
		}
	}
	
	/**
	 * 执行更新集群的任务
	 */
	public void startJob() throws Exception{
		List<D_ClusterInfo> clusters = clusterInfoService.getAll();
		clusters.forEach(c -> {
			try {
				updateCluster(c);
			} catch (Exception e) {
				log.error("redis monitor by cluster [" + c + "] error", e);
			}
		});
	}
	
	/**
	 * 更新集群所有状态
	 */
	public void updateCluster(D_ClusterInfo c)  throws Exception{
		List<D_RedisClusterNode> new_RedisClusterNodes = getClusterNodes(c);
		updateClusterInfo(c, new_RedisClusterNodes);
		updateServerCache(c, new_RedisClusterNodes);
		updateRedisInfoByClusterNodes(c, new_RedisClusterNodes);
	}
	
	/**
	 * 获取集群的最新状态
	 * @return 
	 * @throws Exception
	 */
	private void updateClusterInfo(D_ClusterInfo old_ClusterInfo , List<D_RedisClusterNode> nodes) throws Exception{
		D_RedisClusterNode node = randomOne(nodes);
		RedisClusterTerminal client = null;
		try {
			client = new RedisClusterTerminal(node.getHost(), node.getPort());
			M_clusterInfo info = client.getClusterInfo();
			D_ClusterInfo clusterInfo = new D_ClusterInfo();
			BeanUtils.copyProperties(clusterInfo, old_ClusterInfo);
			BeanUtils.copyProperties(clusterInfo, info);
			clusterInfoService.updateClusterInfo(clusterInfo);
			//cluster_change(old_ClusterInfo, clusterInfo);
		}catch(Exception e){
			log.error("redis monitor by cluster error", e);
		} finally {
			client.close();
		}
	}
	
	/**
	 * 更新redis缓存
	 */
	private void updateServerCache(D_ClusterInfo c, List<D_RedisClusterNode> new_RedisClusterNodes) {
		ClusterServerCache.updateServer(c, new_RedisClusterNodes);
	}
	
	/**
	 * 获取集群节点的最新状态
	 * @throws Exception
	 */
	private List<D_RedisClusterNode> getClusterNodes(D_ClusterInfo c) throws Exception{
		RedisClusterTerminal client = null;
		try {
			String cluster = c.getUuid();
			List<D_RedisClusterNode> old_RedisClusterNodes = clusterNodeService.getAllClusterNodes(cluster);
			List<D_RedisClusterNode> new_RedisClusterNodes = null;
			if(old_RedisClusterNodes.size() > 0){
				D_RedisClusterNode node = randomOne(old_RedisClusterNodes);
				client = new RedisClusterTerminal(node.getHost(), node.getPort());
				new_RedisClusterNodes = clusterNodeService.getClusterNodesByRedis(cluster, client);
				clusterNodeService.addClusterNodes(cluster, new_RedisClusterNodes);
				//cluster_node_change(c, old_RedisClusterNodes, new_RedisClusterNodes);
			}else{
				client = new RedisClusterTerminal(c.getLast_read_host(), c.getLast_read_port());
				new_RedisClusterNodes = clusterNodeService.getClusterNodesByRedis(cluster, client);
				clusterNodeService.addClusterNodes(cluster, new_RedisClusterNodes);
			}
			return new_RedisClusterNodes;
		} finally {
			if(client != null){
				client.close();
			}
		}
	}
	
	/**
	 * 更新redis状态
	 */
	private void updateRedisInfoByClusterNodes(D_ClusterInfo c, List<D_RedisClusterNode> new_RedisClusterNodes) {
		new_RedisClusterNodes.forEach(node->{
			if(node.getStatus() == RedisNodeStatus.CONNECT){
				RedisClusterTerminal client = null;
				try {
					client = new RedisClusterTerminal(node.getHost(), node.getPort());
					M_info m_info = client.getInfo();
					D_RedisInfo info = new D_RedisInfo();
					BeanUtils.copyProperties(info, m_info);
					RedisInfoService.addRedisInfo(c, info);
				} catch (Exception e) {
					log.error("redis monitor by node [" + node + "] error", e);
				} finally {
					if(client != null){
						client.close();
					}
				}
			}
		});
	}
	
//	/**
//	 * 集群变化
//	 */
//	private void cluster_change(D_ClusterInfo old_data, D_ClusterInfo new_data){
//		
//	}
//
//	/**
//	 * 集群节点变化
//	 */
//	private void cluster_node_change(D_ClusterInfo cluster, List<D_RedisClusterNode> old_data, List<D_RedisClusterNode> new_data){
//		
//	}

	/**
	 * 随机返回一个状态正常的节点
	 */
	static D_RedisClusterNode randomOne(List<D_RedisClusterNode> nodes){
		List<D_RedisClusterNode> list = new ArrayList<D_RedisClusterNode>();
		nodes.forEach(n ->{
			if(n.getStatus() == RedisNodeStatus.CONNECT){
				list.add(n);
			}
		});
		return RandomUtil.randomOne(list);
	}
}
