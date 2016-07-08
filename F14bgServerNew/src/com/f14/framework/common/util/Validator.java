package com.f14.framework.common.util;


public class Validator {

	public static boolean isEmpty(String str){
		if(str==null || str.length()==0) return true;
		return false;
	}
	
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	public static boolean isInArray(Object value, Object[] array){
		for(Object e : array){
			if(e.equals(value)){
				return true;
			}
		}
		return false;
	}
}
