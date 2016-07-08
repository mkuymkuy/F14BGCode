package com.f14.RFTG.card;

import com.f14.RFTG.consts.ActiveType;
import com.f14.RFTG.consts.Skill;

/**
 * 卡牌的能力
 * 
 * @author F14eagle
 *
 */
public abstract class Ability {
	public boolean active = false;
	public Skill skill = null;
	public boolean discardAfterActived = false;
	public int maxNum = 1;
	public ActiveType activeType = null;
	
	public Condition whiteCondition;
	public Condition blackCondition;
	
	/**
	 * 结合黑白条件判断该牌是否符合规则
	 * 
	 * @param card
	 * @return
	 */
	public boolean test(RaceCard card){
		boolean wc = (whiteCondition==null)?true:whiteCondition.test(card);
		boolean bc = (blackCondition==null)?true:!blackCondition.test(card);
		return wc & bc;
	}

	public Condition getWhiteCondition() {
		return whiteCondition;
	}

	public void setWhiteCondition(Condition whiteCondition) {
		this.whiteCondition = whiteCondition;
	}

	public Condition getBlackCondition() {
		return blackCondition;
	}

	public void setBlackCondition(Condition blackCondition) {
		this.blackCondition = blackCondition;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public boolean isDiscardAfterActived() {
		return discardAfterActived;
	}

	public void setDiscardAfterActived(boolean discardAfterActived) {
		this.discardAfterActived = discardAfterActived;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public ActiveType getActiveType() {
		return activeType;
	}

	public void setActiveType(ActiveType activeType) {
		this.activeType = activeType;
	}
	
}
