package com.f14.TTA.component.ability;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.consts.ActionAbilityType;
import com.f14.TTA.consts.CivilizationProperty;

/**
 * 行动牌的能力
 * 
 * @author F14eagle
 *
 */
public class ActionCardAbility extends CardAbility {
	public ActionAbilityType abilityType;
	/**
	 * 按排名得分的能力时用来区分排名类型的字段
	 */
	public CivilizationProperty rankProperty;
	/**
	 * 按排名得分的能力时用来区分得到的属性
	 */
	public CivilizationProperty getProperty;
	/**
	 * 按照游戏人数分组的得分状态
	 */
	public Map<String, Integer> rankValue = new HashMap<String, Integer>();

	public ActionAbilityType getAbilityType() {
		return abilityType;
	}

	public void setAbilityType(ActionAbilityType abilityType) {
		this.abilityType = abilityType;
	}

	public CivilizationProperty getRankProperty() {
		return rankProperty;
	}

	public void setRankProperty(CivilizationProperty rankProperty) {
		this.rankProperty = rankProperty;
	}

	public Map<String, Integer> getRankValue() {
		return rankValue;
	}

	public void setRankValue(Map<String, Integer> rankValue) {
		this.rankValue = rankValue;
	}

	public CivilizationProperty getGetProperty() {
		return getProperty;
	}

	public void setGetProperty(CivilizationProperty getProperty) {
		this.getProperty = getProperty;
	}
}
