package com.f14.TS.action;

import com.f14.TS.component.condition.TSCountryConditionGroup;
import com.f14.TS.consts.EffectType;

/**
 * TS的持续效果对象
 * 
 * @author F14eagle
 *
 */
public class TSEffect extends TSGameAction {
	public EffectType effectType;
	protected TSCountryConditionGroup countryCondGroup;

	public EffectType getEffectType() {
		return effectType;
	}
	public void setEffectType(EffectType effectType) {
		this.effectType = effectType;
	}
	public TSCountryConditionGroup getCountryCondGroup() {
		return countryCondGroup;
	}
	public void setCountryCondGroup(TSCountryConditionGroup countryCondGroup) {
		this.countryCondGroup = countryCondGroup;
	}
	
}
