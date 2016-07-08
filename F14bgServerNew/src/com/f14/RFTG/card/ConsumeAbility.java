package com.f14.RFTG.card;

/**
 * 消费阶段的能力
 * 
 * @author F14eagle
 *
 */
public class ConsumeAbility extends Ability {
	public int vp = 0;
	public int drawNum = 0;
	public int goodNum = 0;
	public int onStartDrawNum = 0;
	public int discardNum = 0;
	public boolean tradeWithSkill = false;
	public int onStartVp = 0;
	
	public int getVp() {
		return vp;
	}
	public void setVp(int vp) {
		this.vp = vp;
	}
	public int getDrawNum() {
		return drawNum;
	}
	public void setDrawNum(int drawNum) {
		this.drawNum = drawNum;
	}
	public int getGoodNum() {
		return goodNum;
	}
	public void setGoodNum(int goodNum) {
		this.goodNum = goodNum;
	}
	public int getOnStartDrawNum() {
		return onStartDrawNum;
	}
	public void setOnStartDrawNum(int onStartDrawNum) {
		this.onStartDrawNum = onStartDrawNum;
	}
	public int getDiscardNum() {
		return discardNum;
	}
	public void setDiscardNum(int discardNum) {
		this.discardNum = discardNum;
	}
	public boolean isTradeWithSkill() {
		return tradeWithSkill;
	}
	public void setTradeWithSkill(boolean tradeWithSkill) {
		this.tradeWithSkill = tradeWithSkill;
	}
	public int getOnStartVp() {
		return onStartVp;
	}
	public void setOnStartVp(int onStartVp) {
		this.onStartVp = onStartVp;
	}
	
	
}
