package com.f14.bg.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件组
 * 
 * @author F14eagle
 *
 */
public abstract class AbstractConditionGroup<P> extends AbstractCondition<P> {
	/**
	 * 白名单条件
	 */
	protected List<AbstractCondition<P>> wcs = new ArrayList<AbstractCondition<P>>(0);
	/**
	 * 黑名单条件
	 */
	protected List<AbstractCondition<P>> bcs = new ArrayList<AbstractCondition<P>>(0);
	
	public List<AbstractCondition<P>> getWcs() {
		return wcs;
	}
	public void setWcs(List<AbstractCondition<P>> wcs) {
		this.wcs = wcs;
	}
	public List<AbstractCondition<P>> getBcs() {
		return bcs;
	}
	public void setBcs(List<AbstractCondition<P>> bcs) {
		this.bcs = bcs;
	}

	/**
	 * 清除所有条件
	 */
	public void clear(){
		this.wcs.clear();
		this.bcs.clear();
	}
	/**
	 * 添加白条件
	 * 
	 * @param c
	 */
	public void addWcs(AbstractCondition<P> c){
		this.wcs.add(c);
	}
	
	/**
	 * 添加黑条件
	 * 
	 * @param c
	 */
	public void addBcs(AbstractCondition<P> c){
		this.bcs.add(c);
	}
	
	@Override
	public boolean test(P o) {
		//白黑名单中的条件均为 "或" 关系
		boolean res = true;
		for(ICondition<P> c : this.bcs){
			if(c.test(o)){
				return false;
			}
		}
		for(ICondition<P> c : this.wcs){
			if(c.test(o)){
				return true;
			}else{
				res = false;
			}
		}
		return res;
	}
	
	@Override
	public AbstractCondition<P> clone() {
		AbstractConditionGroup<P> res = (AbstractConditionGroup<P>)super.clone();
		res.wcs = new ArrayList<AbstractCondition<P>>();
		for(AbstractCondition<P> o : this.wcs){
			res.wcs.add(o.clone());
		}
		res.bcs = new ArrayList<AbstractCondition<P>>();
		for(AbstractCondition<P> o : this.bcs){
			res.bcs.add(o.clone());
		}
		return res;
	}

}
