package com.newegg.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapForEach {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", "a1");
		map.put("b", "b1");
		map.put("c", "c1");
		map.put("d", "d1");
		map.put("e", "e1");
		Iterator<Entry<String, String>> it = map.entrySet().iterator();  
        while(it.hasNext()){  
            Entry<String, String> entry=it.next();
            System.out.println(entry.getValue());
            it.remove();
        }  
		System.out.println(map.size());
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			map.remove(entry.getKey());
		}
	}
}
