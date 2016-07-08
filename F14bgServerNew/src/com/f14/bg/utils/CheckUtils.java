package com.f14.bg.utils;

import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;

public class CheckUtils {

	/**
	 * 检查o是否为null,为null则抛出异常信息msg
	 * 
	 * @param o
	 * @param msg
	 * @throws BoardGameException
	 */
	public static void checkNull(Object o, String msg) throws BoardGameException{
		if(o==null){
			throw new BoardGameException(msg);
		}else{
			if(o instanceof String && StringUtils.isEmpty((String)o)){
				throw new BoardGameException(msg);
			}
		}
	}
	
	/**
	 * 判断obj是否在array中
	 * 
	 * @param array
	 * @param obj
	 * @return
	 */
	public static boolean inArray(Object[] array, Object obj){
		for(Object o : array){
			if(o==obj){
				return true;
			}
		}
		return false;
	}
}
