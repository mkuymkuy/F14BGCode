package com.f14.utils;

import java.util.HashMap;
import java.util.Map;

public class SequenceUtils {
	private static Map<Class<?>, Integer> map = new HashMap<Class<?>, Integer>();
	
	/**
	 * 生成id
	 * 
	 * @param clazz
	 * @return
	 */
	public synchronized static String generateId(Class<?> clazz){
		Integer i = map.get(clazz);
		if(i==null){
			i = 0;
		}
		i++;
		map.put(clazz, i);
		return i+"";
	}
}
