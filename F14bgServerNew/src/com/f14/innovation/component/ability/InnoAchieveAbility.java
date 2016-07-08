package com.f14.innovation.component.ability;

import java.util.ArrayList;
import java.util.List;

import com.f14.innovation.consts.InnoAchieveTrigType;

/**
 * 成就牌的能力
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveAbility {
	public List<InnoAchieveTrigType> trigTypes = new ArrayList<InnoAchieveTrigType>();
	public String achieveClass;
	
	public List<InnoAchieveTrigType> getTrigTypes() {
		return trigTypes;
	}
	public void setTrigTypes(List<InnoAchieveTrigType> trigTypes) {
		for(Object o : trigTypes){
			this.trigTypes.add(InnoAchieveTrigType.valueOf(o.toString()));
		}
	}
	public String getAchieveClass() {
		return achieveClass;
	}
	public void setAchieveClass(String achieveClass) {
		this.achieveClass = achieveClass;
	}
	
	/**
	 * 判断是否包含指定的触发类型
	 * 
	 * @param types
	 * @return
	 */
	public boolean contains(InnoAchieveTrigType...types){
		if(this.trigTypes!=null){
			for(InnoAchieveTrigType o : types){
				if(this.trigTypes.contains(o)){
					return true;
				}
			}
		}
		return false;
	}
}
