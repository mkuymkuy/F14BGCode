package com.f14.PuertoRico.component;

import java.util.HashMap;
import java.util.Map;

import com.f14.PuertoRico.consts.GoodType;
import com.f14.bg.component.PartPool;

public class PrPartPool extends PartPool {

	/**
	 * 取得所有资源的数据
	 * 
	 * @return
	 */
	public Map<String, Object> getResources(){
		Map<String, Object> res = new HashMap<String, Object>();
		for(GoodType goodType : GoodType.values()){
			res.put(goodType.toString(), this.getAvailableNum(goodType));
		}
		return res;
	}
	
	/**
	 * 取得所有资源的数据,i为修正值,将乘上原始数据
	 * 
	 * @return
	 */
	public Map<String, Object> getResources(int i){
		Map<String, Object> res = new HashMap<String, Object>();
		for(GoodType goodType : GoodType.values()){
			res.put(goodType.toString(), i*this.getAvailableNum(goodType));
		}
		return res;
	}
	
	/**
	 * 取得所有配件的数据,i为修正值,将乘上原始数据
	 * 
	 * @return
	 */
	public Map<String, Object> getParts(int i){
		Map<String, Object> res = new HashMap<String, Object>();
		for(Object key : this.getParts()){
			int num = this.getAvailableNum(key);
			if(num!=0){
				res.put(key.toString(), i*num);
			}
		}
		return res;
	}
	
	/**
	 * 取得资源的字符串描述
	 * 
	 * @return
	 */
	public String getResourceDescr(){
		String res = "";
		for(GoodType goodType : GoodType.values()){
			int num = this.getAvailableNum(goodType);
			if(num>0){
				res += num + "个" + GoodType.getChinese(goodType) + ",";
			}
		}
		if(res.length()>0){
			res = res.substring(0, res.length()-1);
		}
		return res;
	}
	
}
