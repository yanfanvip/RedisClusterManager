package com.newegg.redis.cluster;

import java.io.IOException;
import java.util.UUID;

import com.newegg.redis.context.AppConstants;
import com.newegg.redis.leveldb.D_ClusterInfo;
import com.newegg.redis.leveldb.LevelTable;

public class LevelLost {

	public static void main(String[] args) throws Exception {
		//init();
		checkData();
		LevelTable.close();
	}

	public static void init() throws IOException {
        D_ClusterInfo info = new D_ClusterInfo();
        info.setUuid(UUID.randomUUID().toString());
        info.setName("DEMO");
        info.setLast_read_host("10.16.236.133");
        info.setLast_read_port(8200);
        LevelTable.put(AppConstants.LEVEL_DATABASES_SYSTEM, info);
	}

	static int count = 0;
	private static void checkData() throws IOException {
		count = 0;
		LevelTable.iterator(AppConstants.LEVEL_DATABASES_SYSTEM, D_ClusterInfo.class, c ->{
			System.out.println("==============   " + c.getName() + "   ==============");
			count++;
		});
		System.out.println(count);
	}
}
