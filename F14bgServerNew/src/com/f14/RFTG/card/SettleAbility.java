package com.f14.RFTG.card;


public class SettleAbility extends Ability {
	public int cost = 0;
	public int military = 0;
	public int afterSettleDrawNum = 0;
	public int discardNum = 0;
	public int discardMilitary = 0;
	public int discardCost = 0;
	public int buyCost = 0;
	
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getMilitary() {
		return military;
	}
	public void setMilitary(int military) {
		this.military = military;
	}
	public int getAfterSettleDrawNum() {
		return afterSettleDrawNum;
	}
	public void setAfterSettleDrawNum(int afterSettleDrawNum) {
		this.afterSettleDrawNum = afterSettleDrawNum;
	}
	public int getDiscardNum() {
		return discardNum;
	}
	public void setDiscardNum(int discardNum) {
		this.discardNum = discardNum;
	}
	public int getDiscardMilitary() {
		return discardMilitary;
	}
	public void setDiscardMilitary(int discardMilitary) {
		this.discardMilitary = discardMilitary;
	}
	public int getDiscardCost() {
		return discardCost;
	}
	public void setDiscardCost(int discardCost) {
		this.discardCost = discardCost;
	}
	public int getBuyCost() {
		return buyCost;
	}
	public void setBuyCost(int buyCost) {
		this.buyCost = buyCost;
	}
	
}
