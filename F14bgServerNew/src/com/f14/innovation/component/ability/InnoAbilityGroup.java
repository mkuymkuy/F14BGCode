package com.f14.innovation.component.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.bg.consts.ConditionResult;
import com.f14.innovation.consts.InnoActiveType;

public class InnoAbilityGroup {
	public int repeat;
	public List<InnoAbility> abilities = new ArrayList<InnoAbility>();
	public InnoActiveType activeType;
	protected Map<ConditionResult, InnoAbilityGroup> conditionAbilities = new HashMap<ConditionResult, InnoAbilityGroup>();
	
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public List<InnoAbility> getAbilities() {
		return abilities;
	}
	public void setAbilities(List<InnoAbility> abilities) {
		this.abilities.clear();
		if(abilities!=null){
			for(Object o : abilities){
				InnoAbility a = (InnoAbility)JSONObject.toBean(JSONObject.fromObject(o), InnoAbility.class);
				this.abilities.add(a);
			}
		}
	}
	public InnoActiveType getActiveType() {
		return activeType;
	}
	public void setActiveType(InnoActiveType activeType) {
		this.activeType = activeType;
	}
	public Map<ConditionResult, InnoAbilityGroup> getConditionAbilities() {
		return conditionAbilities;
	}
	public void setConditionAbilities(
			Map<?, ?> conditionAbilities) {
		this.conditionAbilities.clear();
		for(Object o : conditionAbilities.keySet()){
			ConditionResult key = ConditionResult.valueOf(o.toString());
			InnoAbilityGroup a = (InnoAbilityGroup)JSONObject.toBean(JSONObject.fromObject(conditionAbilities.get(o)), InnoAbilityGroup.class);
			this.conditionAbilities.put(key, a);
		}
	}
	/**
	 * 取得判断结果对应的InnoAbilityGroup
	 * 
	 * @param conditionResult
	 * @return
	 */
	public InnoAbilityGroup getConditionAbilityGroup(ConditionResult conditionResult){
		return this.conditionAbilities.get(conditionResult);
	}
	
}
