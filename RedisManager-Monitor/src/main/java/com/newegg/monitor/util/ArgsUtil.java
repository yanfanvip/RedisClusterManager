package com.newegg.monitor.util;

import java.util.HashMap;

public class ArgsUtil {
	HashMap<String,String> map;
	private ArgsUtil(HashMap<String,String> map){
		this.map = map;
	}
	
	public static ArgsUtil parser(String[] args){
		HashMap<String,String> map = new HashMap<String, String>();
		if(args.length > 0){
			for (String arg : args) {
				if(arg == null || "".equals(arg = arg.trim()) || ! arg.startsWith("--") || arg.indexOf("=") < 1) continue;
				String key = arg.substring(2, arg.indexOf("="));
				String value = arg.substring(arg.indexOf("=") + 1);
				map.put(key, value);
			}
		}
		return new ArgsUtil(map);
	}
	
	public String getString(String key) {
		return map.get(key);
	}
	
	public Long getLong(String key){
		if(map.containsKey(key)){
			return Long.valueOf(map.get(key));
		}
		return null;
	}
	
	public Integer getInt(String key){
		if(map.containsKey(key)){
			return Integer.valueOf(map.get(key));
		}
		return null;
	}
}
