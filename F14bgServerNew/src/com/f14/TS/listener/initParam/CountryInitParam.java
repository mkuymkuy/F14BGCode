package com.f14.TS.listener.initParam;

import java.util.Collection;

import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.component.condition.TSCountryConditionGroup;
import com.f14.bg.component.ICondition;

/**
 * 国家相关的初始化参数
 * 
 * @author F14eagle
 *
 */
public abstract class CountryInitParam extends InitParam implements ICondition<TSCountry> {
	protected TSCountryConditionGroup conditionGroup = new TSCountryConditionGroup();
	
	public TSCountryConditionGroup getConditionGroup() {
		return conditionGroup;
	}
	public void setConditionGroup(TSCountryConditionGroup conditionGroup) {
		this.conditionGroup = conditionGroup;
	}
	public void addWc(TSCountryCondition o) {
		this.conditionGroup.addWcs(o);
	}
	public void addBc(TSCountryCondition o) {
		this.conditionGroup.addBcs(o);
	}
	/**
	 * 清除所有条件
	 */
	public void clearConditionGroup(){
		this.conditionGroup.clear();
	}
	@Override
	public boolean test(TSCountry o) {
		return this.conditionGroup.test(o);
	}
	@Override
	public boolean test(Collection<TSCountry> objects) {
		for(TSCountry object : objects){
			if(!this.test(object)){
				return false;
			}
		}
		return true;
	}
	@Override
	public CountryInitParam clone() {
		CountryInitParam res = (CountryInitParam) super.clone();
		res.conditionGroup = this.conditionGroup.clone();
		return res;
	}
}
