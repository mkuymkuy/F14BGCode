package com.f14.TTA.component.card;

import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.ActionCardAbility;

/**
 * 行动牌
 * 
 * @author F14eagle
 *
 */
public class ActionCard extends CivilCard {
	public ActionCardAbility actionAbility;

	public ActionCardAbility getActionAbility() {
		return actionAbility;
	}

	public void setActionAbility(ActionCardAbility actionAbility) {
		this.actionAbility = actionAbility;
	}

	/**
	 * 按照排名和人数取得最终调整数据的结果
	 * 
	 * @param rank
	 * @param playerNum
	 * @return
	 */
	public TTAProperty getFinalRankValue(int rank, int playerNum) {
		int value = this.actionAbility.rankValue.get(playerNum + "");
		int finalValue = (rank - 1) * value;
		TTAProperty property = new TTAProperty();
		property.setProperty(this.actionAbility.getProperty, finalValue);
		return property;
	}
}
