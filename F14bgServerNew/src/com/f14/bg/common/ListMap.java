package com.f14.bg.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListMap<K, V> {
	protected Map<K, List<V>> map = new LinkedHashMap<K, List<V>>();
	
	public void clear(){
		this.map.clear();
	}
	
	/**
	 * 按照key取得list,如果不存在则会自动创建一个list
	 * 
	 * @param key
	 * @return
	 */
	public List<V> getList(K key){
		List<V> res = this.map.get(key);
		if(res==null){
			res = new ArrayList<V>();
			this.map.put(key, res);
		}
		return res;
	}
	
	/**
	 * 添加对象
	 * 
	 * @param key
	 * @param value
	 */
	public void add(K key, V value){
		List<V> list = this.getList(key);
		list.add(value);
	}
	
	/**
	 * 移除对象
	 * 
	 * @param value
	 */
	public void remove(V value){
		for(List<V> list : this.map.values()){
			list.remove(value);
		}
	}
	
	/**
	 * 取得键的合集
	 * 
	 * @return
	 */
	public Set<K> keySet(){
		return this.map.keySet();
	}
	
	/**
	 * 按照键值移除对象
	 * 
	 * @param key
	 */
	public void removeKey(K key){
		this.map.remove(key);
	}
	
	/**
	 * 取得所有包含value的key
	 * 
	 * @param value
	 * @return
	 */
	public List<K> getKeyByValue(V value){
		List<K> res = new ArrayList<K>();
		for(K k : this.keySet()){
			if(this.map.get(k).contains(value)){
				res.add(k);
			}
		}
		return res;
	}
}
