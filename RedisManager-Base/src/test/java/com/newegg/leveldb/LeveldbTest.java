package com.newegg.leveldb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.newegg.redis.leveldb.D_ComputerInfo;
import com.newegg.redis.leveldb.LevelTable;

public class LeveldbTest {
	
	static final String dbPath = "database/demo1";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		LevelTable.put("demo", D_ComputerInfo.class, Arrays.asList(
			createDemo(1),
			createDemo(2),
			createDemo(3)
		));
		ArrayList<D_ComputerInfo> list = new ArrayList<D_ComputerInfo>();
		int i = 10000;
		while (i-->0) {
			list.add(createDemo(i));
		}
		LevelTable.put("demo", D_ComputerInfo.class, list);
		
		LevelTable.deletePrev("demo", D_ComputerInfo.class, 1492065042070L);
		
		LevelTable.iterator("demo", D_ComputerInfo.class, t->{
			System.out.println(t);
		});
	}
	
	private static D_ComputerInfo createDemo(int i) {
		D_ComputerInfo info = new D_ComputerInfo();
		info.setDate(System.currentTimeMillis() + i);
		info.setIp("127.0.0.2");
		info.setFreeDisk(1546541651L);
		info.setHostname("EWXASDFSEDF" + i);
		return info;
	}
}
