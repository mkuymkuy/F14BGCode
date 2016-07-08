package com.f14.bg.component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class PartPool implements Cloneable {
	private LinkedHashMap<Object, Part> pool = new LinkedHashMap<Object, Part>();
	
	/**
	 * 取得配件对象
	 * 
	 * @param partName
	 * @return
	 */
	private Part getPart(Object part){
		Part p = pool.get(part);
		if(p==null){
			p = new Part(part, 0);
			pool.put(part, p);
		}
		return p;
	}
	
	/**
	 * 设置配件数量
	 * 
	 * @param card
	 * @param num
	 */
	public void setPart(Object part, int num){
		Part p = this.getPart(part);
		p.num = num;
	}
	
	/**
	 * 拿取1个配件
	 * 
	 * @param partName
	 * @return
	 */
	public int takePart(Object part){
		return takePart(part, 1);
	}
	
	/**
	 * 拿取配件
	 * 
	 * @param cardId
	 * @param num
	 * @return
	 */
	public int takePart(Object part, int num){
		Part p = this.getPart(part);
		if(p==null || p.num<=0){
			return 0;
		}else{
			int res = Math.min(num, p.num);
			p.num -= res;
			return res;
		}
	}
	
	/**
	 * 取得配件的可用数量
	 * 
	 * @param partName
	 * @return
	 */
	public int getAvailableNum(Object part){
		Part p = this.getPart(part);
		if(p==null || p.num<=0){
			return 0;
		}else{
			return p.num;
		}
	}
	
	/**
	 * 放入1个配件
	 * 
	 * @param partName
	 * @param payNum
	 */
	public void putPart(Object part){
		this.putPart(part, 1);
	}
	
	/**
	 * 放入配件
	 * 
	 * @param partName
	 * @param num
	 */
	public void putPart(Object part, int num){
		Part p = this.getPart(part);
		p.num += num;
	}
	
	/**
	 * 取出所有指定的配件
	 * 
	 * @param part
	 * @return
	 */
	public int takePartAll(Object part){
		Part p = this.getPart(part);
		if(p==null || p.num<=0){
			return 0;
		}else{
			int res = p.num;
			p.num = 0;
			return res;
		}
	}
	
	/**
	 * 清理所有的配件
	 */
	public void clear(){
		pool.clear();
	}
	
	/**
	 * 判断配件池是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		for(Object key : this.pool.keySet()){
			if(this.getAvailableNum(key)>0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 取得配件池中所有配件类型
	 * 
	 * @return
	 */
	public Set<Object> getParts(){
		return this.pool.keySet();
	}
	
	/**
	 * 取得所有非空配件类型的总数
	 * 
	 * @return
	 */
	public int getPartNum(){
		int res = 0;
		for(Object key : this.pool.keySet()){
			if(this.getAvailableNum(key)>0){
				res += 1;
			}
		}
		return res;
	}
	
	/**
	 * 取得所有配件的总数
	 * 
	 * @return
	 */
	public int getTotalNum(){
		int res = 0;
		for(Object key : this.pool.keySet()){
			res += this.getAvailableNum(key);
		}
		return res;
	}
	
	/**
	 * 将parts中的配件放入配件堆中
	 * 
	 * @param parts
	 */
	public void putParts(PartPool parts){
		for(Object part : parts.getParts()){
			this.putPart(part, parts.getAvailableNum(part));
		}
	}
	
	/**
	 * 从配件堆中取出parts中的配件
	 * 
	 * @param parts
	 * @return
	 */
	public PartPool takeParts(PartPool parts){
		PartPool res = new PartPool();
		for(Object part : parts.getParts()){
			res.putPart(part, this.takePart(part, parts.getAvailableNum(part)));
		}
		return res;
	}
	
	/**
	 * 判断配件池中是否拥有parts中的所有配件
	 * 
	 * @param parts
	 * @return
	 */
	public boolean hasParts(PartPool parts){
		for(Object key : parts.getParts()){
			if(this.getAvailableNum(key)<parts.getAvailableNum(key)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 取得所有配件的数量
	 * 
	 * @return
	 */
	public Map<Object, Integer> getAllPartsNumber(){
		Map<Object, Integer> res = new HashMap<Object, Integer>();
		for(Object key : this.getParts()){
			res.put(key, this.getAvailableNum(key));
		}
		return res;
	}
	
	@Override
	public PartPool clone() throws CloneNotSupportedException {
		PartPool res = (PartPool)super.clone();
		res.pool = new LinkedHashMap<Object, Part>();
		for(Object k : this.pool.keySet()){
			res.pool.put(k, this.pool.get(k).clone());
		}
		return res;
	}
	
	private class Part implements Cloneable{
		Object part;
		int num;
		
		Part(Object part, int num){
			this.part = part;
			this.num = num;
		}
		
		@Override
		protected Part clone() throws CloneNotSupportedException {
			return (Part)super.clone();
		}
	}
}
