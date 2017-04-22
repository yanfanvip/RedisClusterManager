package org.redis.manager.service;

import java.util.Map;
import org.redis.manager.cluster.RedisClusterTerminal;
import org.redis.manager.leveldb.D_RedisClusterNode;
import org.redis.manager.model.ClusterServerCache;
import org.redis.manager.model.W_SlotMove;
import org.redis.manager.notify.Notify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
			}finally {
				terminal.close();
			}
		}
	}
}
