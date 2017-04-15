package com.newegg.leveldb;

import java.io.IOException;

import com.newegg.redis.leveldb.D_ComputerInfo;
import com.newegg.redis.leveldb.LevelTable;

public class D_ComputerInfoTest {

	public static void main(String[] args) throws IOException {
		D_ComputerInfo a = LevelTable.last("data", D_ComputerInfo.class);
		System.out.println(a);
		
		D_ComputerInfo info = new D_ComputerInfo();
		info.setIp("7");
		info.setFreeDisk(1546541651L);
		info.setHostname("EWXASDFSEDF");
		LevelTable.put("data", info);
		
		D_ComputerInfo b = LevelTable.last("data", D_ComputerInfo.class);
		System.out.println(b);
	}
}
