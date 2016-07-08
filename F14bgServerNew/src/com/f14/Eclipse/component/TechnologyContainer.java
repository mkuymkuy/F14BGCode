package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.Eclipse.consts.TechnologyCategory;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.bg.common.ListMap;

public class TechnologyContainer {
	protected ListMap<TechnologyCategory, Technology> techs = new ListMap<TechnologyCategory, Technology>();
	protected List<TechnologyType> techTypes = new ArrayList<TechnologyType>();
	
	public void clear(){
		this.techs.clear();
		this.techTypes.clear();
	}
	
	/**
	 * 添加科技
	 * 
	 * @param o
	 */
	public void addTechnology(Technology o){
		this.techs.add(o.category, o);
		this.techTypes.add(o.type);
	}
	
	/**
	 * 取得指定科技类型的所有科技
	 * 
	 * @param category
	 * @return
	 */
	public List<Technology> getTechnology(TechnologyCategory category){
		return this.techs.getList(category);
	}
	
	/**
	 * 取得所有科技
	 * 
	 * @return
	 */
	public List<TechnologyType> getTechnologyTypes(){
		return this.techTypes;
	}
	
	/**
	 * 取得所有科技按类型统计的数量
	 * 
	 * @return
	 */
	public Map<TechnologyCategory, Integer> getTechnologyCount(){
		Map<TechnologyCategory, Integer> res = new HashMap<TechnologyCategory, Integer>();
		for(TechnologyCategory c : TechnologyCategory.values()){
			res.put(c, this.getTechnology(c).size());
		}
		return res;
	}
	
	/**
	 * 取得指定种类的科技数量
	 * 
	 * @param category
	 * @return
	 */
	public int getTechnologyCount(TechnologyCategory category){
		return this.techs.getList(category).size();
	}
	
	/**
	 * 判断是否有该类型科技
	 * 
	 * @param type
	 * @return
	 */
	public boolean hasTechnology(TechnologyType type){
		return this.techTypes.contains(type);
	}
}
