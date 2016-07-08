package com.f14.innovation.component.ability;

import java.util.List;

import net.sf.json.JSONObject;

import com.f14.innovation.component.condition.InnoCardCondition;
import com.f14.innovation.component.condition.InnoCardConditionGroup;
import com.f14.innovation.consts.InnoAbilityType;
import com.f14.innovation.param.InnoInitParam;

public class InnoAbility {
	public InnoAbilityType abilityType;
	public String abilityClass;
	public InnoInitParam initParam;
	public InnoCardConditionGroup cardCondGroup = new InnoCardConditionGroup();
	
	public InnoAbilityType getAbilityType() {
		return abilityType;
	}
	public void setAbilityType(InnoAbilityType abilityType) {
		this.abilityType = abilityType;
	}
	public String getAbilityClass() {
		return abilityClass;
	}
	public void setAbilityClass(String abilityClass) {
		this.abilityClass = abilityClass;
	}
	public InnoInitParam getInitParam() {
		return initParam;
	}
	public void setInitParam(InnoInitParam initParam) {
		this.initParam = initParam;
	}
	public InnoCardConditionGroup getCardCondGroup() {
		return cardCondGroup;
	}
	public void setCardCondGroup(InnoCardConditionGroup cardCondGroup) {
		this.cardCondGroup = cardCondGroup;
	}
	public void setCardwcs(List<InnoCardCondition> cardwcs) {
		for(Object o : cardwcs){
			InnoCardCondition a = (InnoCardCondition)JSONObject.toBean(JSONObject.fromObject(o), InnoCardCondition.class);
			this.cardCondGroup.addWcs(a);
		}
	}
	public void setCardbcs(List<InnoCardCondition> cardbcs) {
		for(Object o : cardbcs){
			InnoCardCondition a = (InnoCardCondition)JSONObject.toBean(JSONObject.fromObject(o), InnoCardCondition.class);
			this.cardCondGroup.addBcs(a);
		}
	}
}
