package com.newegg.redis.util;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SystemUtil {
	
	static InetAddress address;
	static{
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static String ip() {
		return address.getHostAddress();
	}
	
	public static String hostname(){
		return address.getHostName();
	}
	
	public static void main(String[] args) throws UnknownHostException, SocketException {
		System.out.println(ip());
		System.out.println(hostname());
    }

}
