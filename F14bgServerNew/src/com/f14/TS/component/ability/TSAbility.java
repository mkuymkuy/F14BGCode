package com.f14.TS.component.ability;

import java.util.List;

import net.sf.json.JSONObject;

import com.f14.TS.action.ActionParam;
import com.f14.TS.component.condition.TSCardCondition;
import com.f14.TS.component.condition.TSCardConditionGroup;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.component.condition.TSCountryConditionGroup;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.ability.TSAbilityTrigType;
import com.f14.TS.consts.ability.TSAbilityType;

/**
 * TS的卡牌能力
 * 
 * @author F14eagle
 *
 */
public class TSAbility {
	public TSAbilityType abilityType;
	public TSAbilityTrigType trigType;
	public SuperPower trigPower;
	public ActionParam actionParam;
	protected TSCountryConditionGroup countryCondGroup = new TSCountryConditionGroup();
	protected TSCardConditionGroup cardCondGroup = new TSCardConditionGroup();
	
	public TSAbilityType getAbilityType() {
		return abilityType;
	}
	public void setAbilityType(TSAbilityType abilityType) {
		this.abilityType = abilityType;
	}
	public TSAbilityTrigType getTrigType() {
		return trigType;
	}
	public void setTrigType(TSAbilityTrigType trigType) {
		this.trigType = trigType;
	}
	public ActionParam getActionParam() {
		return actionParam;
	}
	public void setActionParam(ActionParam actionParam) {
		this.actionParam = actionParam;
	}
	public SuperPower getTrigPower() {
		return trigPower;
	}
	public void setTrigPower(SuperPower trigPower) {
		this.trigPower = trigPower;
	}
	public TSCountryConditionGroup getCountryCondGroup() {
		return countryCondGroup;
	}
	public void setCountryCondGroup(TSCountryConditionGroup countryCondGroup) {
		this.countryCondGroup = countryCondGroup;
	}
	public void setCountrywcs(List<TSCountryCondition> countrywcs) {
		for(Object o : countrywcs){
			TSCountryCondition a = (TSCountryCondition)JSONObject.toBean(JSONObject.fromObject(o), TSCountryCondition.class);
			this.countryCondGroup.addWcs(a);
		}
	}
	public void setCountrybcs(List<TSCountryCondition> countrybcs) {
		for(Object o : countrybcs){
			TSCountryCondition a = (TSCountryCondition)JSONObject.toBean(JSONObject.fromObject(o), TSCountryCondition.class);
			this.countryCondGroup.addBcs(a);
		}
	}
	public TSCardConditionGroup getCardCondGroup() {
		return cardCondGroup;
	}
	public void setCardCondGroup(TSCardConditionGroup cardCondGroup) {
		this.cardCondGroup = cardCondGroup;
	}
	public void setCardwcs(List<TSCardCondition> cardwcs) {
		for(Object o : cardwcs){
			TSCardCondition a = (TSCardCondition)JSONObject.toBean(JSONObject.fromObject(o), TSCardCondition.class);
			this.cardCondGroup.addWcs(a);
		}
	}
	public void setCardbcs(List<TSCardCondition> cardbcs) {
		for(Object o : cardbcs){
			TSCardCondition a = (TSCardCondition)JSONObject.toBean(JSONObject.fromObject(o), TSCardCondition.class);
			this.cardCondGroup.addBcs(a);
		}
	}
}
