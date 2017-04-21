package com.newegg.redis.service;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.newegg.redis.cluster.RedisClusterTerminal;
import com.newegg.redis.leveldb.D_RedisClusterNode;
import com.newegg.redis.model.ClusterServerCache;
import com.newegg.redis.model.W_SlotMove;
import com.newegg.redis.notify.Notify;

@Service
@Scope("singleton")
public class SlotMoveService {
	
	@Autowired
	ClusterNodeService clusterNodeService;

	public void slot_move(W_SlotMove slotMove, Notify notify) throws Exception {
		if(ClusterServerCache.clusterExist(slotMove.getCluster())){
			Map<String, D_RedisClusterNode> map = clusterNodeService.getAllClusterNodeMap(slotMove.getCluster());
			D_RedisClusterNode target = map.get(slotMove.getNode());
			RedisClusterTerminal terminal = new RedisClusterTerminal(target.getHost(), target.getPort(), notify);
			try {
				terminal.reshard(slotMove.getStart(), slotMove.getEnd());
			}catch (Exception e) {
				if(notify != null){
					notify.terminal("slot move error:" + ExceptionUtils.getStackTrace(e));
				}
			} finally {
				terminal.close();
				notify.close();
			}
		}
	}
}
