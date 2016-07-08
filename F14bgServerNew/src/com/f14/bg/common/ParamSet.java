package com.f14.bg.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的参数集合
 * 
 * @author F14eagle
 *
 */
public class ParamSet {
	protected Map<Object, Object> param = new HashMap<Object, Object>();
	
	/**
	 * 判断参数集是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		return param.isEmpty();
	}
	
	/**
	 * 设置参数
	 * 
	 * @param key
	 * @param value
	 */
	public void set(Object key, Object value){
		param.put(key, value);
	}
	
	/**
	 * 取得参数
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <C> C get(Object key){
		return (C)param.get(key);
	}
	
	/**
	 * 取得String类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public String getString(Object key){
		Object res = get(key);
		if(res==null){
			return null;
		}else{
			return res.toString();
		}
	}
	
	/**
	 * 取得Boolean类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public Boolean getBoolean(Object key){
		Object res = get(key);
		if(res==null){
			return null;
		}else{
			return (Boolean)res;
		}
	}
	
	/**
	 * 取得Double类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public Double getDouble(Object key){
		Object res = get(key);
		if(res==null){
			return null;
		}else{
			return (Double)res;
		}
	}
	
	/**
	 * 取得Integer类型的参数
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInteger(Object key){
		Object res = get(key);
		if(res==null){
			return null;
		}else{
			return (Integer)res;
		}
	}
	
	/**
	 * 清除所有参数
	 */
	public void clear(){
		this.param.clear();
	}
}
