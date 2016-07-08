package com.f14.TS.component.ability;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.ActionParam;
import com.f14.TS.component.condition.TSGameCondition;
import com.f14.TS.component.condition.TSGameConditionGroup;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.ability.TSAbilityGroupType;

/**
 * TS能力组合
 * 
 * @author F14eagle
 *
 */
public class TSAbilityGroup {
	public TSAbilityGroupType groupType;
	public List<TSAbility> abilities = new ArrayList<TSAbility>();
	public List<TSAbility> abilitiesGroup1 = new ArrayList<TSAbility>();
	public List<TSAbility> abilitiesGroup2 = new ArrayList<TSAbility>();
	public String descr1;
	public String descr2;
	public SuperPower trigPower;
	protected TSGameConditionGroup gameConditionGroup = new TSGameConditionGroup();
	public ActionParam activeParam;
	
	public TSAbilityGroupType getGroupType() {
		return groupType;
	}
	public void setGroupType(TSAbilityGroupType groupType) {
		this.groupType = groupType;
	}
	public List<TSAbility> getAbilities() {
		return abilities;
	}
	public void setAbilities(List<TSAbility> abilities) {
		this.abilities.clear();
		if(abilities!=null){
			for(Object o : abilities){
				TSAbility a = (TSAbility)JSONObject.toBean(JSONObject.fromObject(o), TSAbility.class);
				this.abilities.add(a);
			}
		}
	}
	public List<TSAbility> getAbilitiesGroup1() {
		return abilitiesGroup1;
	}
	public void setAbilitiesGroup1(List<TSAbility> abilitiesGroup1) {
		this.abilitiesGroup1.clear();
		if(abilitiesGroup1!=null){
			for(Object o : abilitiesGroup1){
				TSAbility a = (TSAbility)JSONObject.toBean(JSONObject.fromObject(o), TSAbility.class);
				this.abilitiesGroup1.add(a);
			}
		}
	}
	public List<TSAbility> getAbilitiesGroup2() {
		return abilitiesGroup2;
	}
	public void setAbilitiesGroup2(List<TSAbility> abilitiesGroup2) {
		this.abilitiesGroup2.clear();
		if(abilitiesGroup2!=null){
			for(Object o : abilitiesGroup2){
				TSAbility a = (TSAbility)JSONObject.toBean(JSONObject.fromObject(o), TSAbility.class);
				this.abilitiesGroup2.add(a);
			}
		}
	}
	public String getDescr1() {
		return descr1;
	}
	public void setDescr1(String descr1) {
		this.descr1 = descr1;
	}
	public String getDescr2() {
		return descr2;
	}
	public void setDescr2(String descr2) {
		this.descr2 = descr2;
	}
	public SuperPower getTrigPower() {
		return trigPower;
	}
	public void setTrigPower(SuperPower trigPower) {
		this.trigPower = trigPower;
	}
	public TSGameConditionGroup getGameConditionGroup() {
		return gameConditionGroup;
	}
	public void setGameConditionGroup(TSGameConditionGroup gameConditionGroup) {
		this.gameConditionGroup = gameConditionGroup;
	}
	public void setGamewcs(List<TSGameCondition> gamewcs) {
		for(Object o : gamewcs){
			TSGameCondition a = (TSGameCondition)JSONObject.toBean(JSONObject.fromObject(o), TSGameCondition.class);
			this.gameConditionGroup.addWcs(a);
		}
	}
	public void setGamebcs(List<TSGameCondition> gamebcs) {
		for(Object o : gamebcs){
			TSGameCondition a = (TSGameCondition)JSONObject.toBean(JSONObject.fromObject(o), TSGameCondition.class);
			this.gameConditionGroup.addBcs(a);
		}
	}
	public boolean test(TSGameMode o, TSPlayer player) {
		return this.gameConditionGroup.test(o, player);
	}
	public ActionParam getActiveParam() {
		return activeParam;
	}
	public void setActiveParam(ActionParam activeParam) {
		this.activeParam = activeParam;
	}
	
}
