package com.f14.TS.component.condition;

import java.util.ArrayList;
import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;


public class TSGameConditionGroup {
	/**
	 * 白名单条件
	 */
	protected List<TSGameCondition> wcs = new ArrayList<TSGameCondition>(0);
	/**
	 * 黑名单条件
	 */
	protected List<TSGameCondition> bcs = new ArrayList<TSGameCondition>(0);
	
	/**
	 * 添加白条件
	 * 
	 * @param c
	 */
	public void addWcs(TSGameCondition c){
		this.wcs.add(c);
	}
	
	/**
	 * 添加黑条件
	 * 
	 * @param c
	 */
	public void addBcs(TSGameCondition c){
		this.bcs.add(c);
	}
	
	public boolean test(TSGameMode o, TSPlayer player) {
		//白黑名单中的条件均为 "或" 关系
		boolean res = true;
		for(TSGameCondition c : this.bcs){
			if(c.test(o, player)){
				return false;
			}
		}
		for(TSGameCondition c : this.wcs){
			if(c.test(o, player)){
				return true;
			}else{
				res = false;
			}
		}
		return res;
	}
	
}
