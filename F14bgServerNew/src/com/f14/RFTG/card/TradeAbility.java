package com.f14.RFTG.card;

/**
 * 消费阶段时交易的能力
 * 
 * @author F14eagle
 *
 */
public class TradeAbility extends Ability {
	public int drawNum = 0;
	public boolean current = false;
	public Condition worldCondition;
	
	public int getDrawNum() {
		return drawNum;
	}
	public void setDrawNum(int drawNum) {
		this.drawNum = drawNum;
	}
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}
	public Condition getWorldCondition() {
		return worldCondition;
	}
	public void setWorldCondition(Condition worldCondition) {
		this.worldCondition = worldCondition;
	}
	
}
