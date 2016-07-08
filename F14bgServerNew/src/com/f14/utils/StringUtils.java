package com.f14.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringUtils {

	/**
	 * 判断字符串是否为空或null
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str==null || str.length()==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 将list转换成string
	 * 
	 * @param coll
	 * @return
	 */
	public static String list2String(Collection<?> coll){
		String res = "";
		if(!coll.isEmpty()){
			for(Object o : coll){
				res += o + ",";
			}
			res = res.substring(0, res.length()-1);
		}
		return res;
	}
	
	/**
	 * 将string转换成list
	 * 
	 * @param string
	 * @return
	 */
	public static List<String> string2List(String string){
		List<String> res = new ArrayList<String>();
		if(!isEmpty(string)){
			String[] strs = string.split(",");
			for(String s : strs){
				res.add(s);
			}
		}
		return res;
	}
	
	/**
	 * 将array转换成string
	 * 
	 * @param array
	 * @return
	 */
	public static String array2String(Object[] array){
		String res = "";
		if(array!=null && array.length>0){
			for(Object o : array){
				res += o + ",";
			}
			res = res.substring(0, res.length()-1);
		}
		return res;
	}
	
	/**
	 * 将array转换成string
	 * 
	 * @param array
	 * @return
	 */
	public static String array2String(int[] array){
		String res = "";
		if(array!=null && array.length>0){
			for(Object o : array){
				res += o + ",";
			}
			res = res.substring(0, res.length()-1);
		}
		return res;
	}
	
	/**
	 * 返回str在strs中的序列,如果没找到则返回-1
	 * 
	 * @param strs
	 * @param str
	 * @return
	 */
	public static int indexOfArray(String[] strs, String str){
		for(int i=0;i<strs.length;i++){
			if(strs[i].equals(str)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 返回in在ints中的序列,如果没找到则返回-1
	 * 
	 * @param ints
	 * @param in
	 * @return
	 */
	public static int indexOfArray(int[] ints, int in){
		for(int i=0;i<ints.length;i++){
			if(ints[i]==in){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * string转换成int数组
	 * 
	 * @param str
	 * @return
	 */
	public static int[] string2int(String str){
		if(isEmpty(str)){
			return new int[0];
		}
		String[] ss = str.split(",");
		int[] res = new int[ss.length];
		for(int i=0;i<ss.length;i++){
			res[i] = Integer.valueOf(ss[i]);
		}
		return res;
	}
}
