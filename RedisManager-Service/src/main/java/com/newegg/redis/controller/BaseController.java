package com.newegg.redis.controller;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

	public Object SUCCESS() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		return map;
	}
	
	public Object SUCCESS(Object data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("data", data);
		return map;
	}
	
	public Object SUCCESS(Object data, String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		map.put("data", data);
		map.put("message", message);
		return map;
	}
	
	public Object FAIL(String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", false);
		map.put("message", message);
		return map;
	}
}
