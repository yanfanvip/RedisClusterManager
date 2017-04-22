package com.newegg;

import java.lang.reflect.InvocationTargetException;

import org.redis.manager.leveldb.D_ServerInfo;
import org.redis.manager.util.BeanUtils;

public class CopyTest {
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
		D_ServerInfo s1 = new D_ServerInfo();
		D_ServerInfo s2 = new D_ServerInfo();
		
		s1.setHostname("s1");
		s1.setIp("10.16.236.133");
		s1.setUserName("root");
		s1.setWorkhome("/a");
		
		
		s2.setHostname("s2");
		s2.setWorkhome("/b");
		
		System.out.println("s1:" + s1);
		System.out.println("s2:" + s2);
		
		D_ServerInfo s3 = new D_ServerInfo();
		BeanUtils.copyNotNullProperties(s3, s1);
		BeanUtils.copyNotNullProperties(s3, s2);
		
		
		System.out.println("s1:" + s1);
		System.out.println("s2:" + s2);
		System.out.println("s3:" + s3);
	}
}
