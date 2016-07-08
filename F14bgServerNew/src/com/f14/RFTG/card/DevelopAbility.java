package com.f14.RFTG.card;

/**
 * 开发阶段的能力
 * 
 * @author F14eagle
 *
 */
public class DevelopAbility extends Ability {
	public int cost = 0;
	public int onStartDrawNum = 0;
	public int afterDevelopDrawNum = 0;
	public int discardCost = 0;
	
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getOnStartDrawNum() {
		return onStartDrawNum;
	}
	public void setOnStartDrawNum(int onStartDrawNum) {
		this.onStartDrawNum = onStartDrawNum;
	}
	public int getAfterDevelopDrawNum() {
		return afterDevelopDrawNum;
	}
	public void setAfterDevelopDrawNum(int afterDevelopDrawNum) {
		this.afterDevelopDrawNum = afterDevelopDrawNum;
	}
	public int getDiscardCost() {
		return discardCost;
	}
	public void setDiscardCost(int discardCost) {
		this.discardCost = discardCost;
	}
	
}
