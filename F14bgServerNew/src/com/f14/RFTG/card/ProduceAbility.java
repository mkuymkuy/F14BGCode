package com.f14.RFTG.card;

/**
 * 生产阶段的能力
 * 
 * @author F14eagle
 *
 */
public class ProduceAbility extends Ability {
	public int drawNum = 0;
	public int drawAfterProduced = 0;
	public int onStartDrawNum = 0;
	public int discardNum = 0;
	public boolean canTargetSelf = true;
	
	public int getDrawNum() {
		return drawNum;
	}
	public void setDrawNum(int drawNum) {
		this.drawNum = drawNum;
	}
	public int getDrawAfterProduced() {
		return drawAfterProduced;
	}
	public void setDrawAfterProduced(int drawAfterProduced) {
		this.drawAfterProduced = drawAfterProduced;
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
	public boolean isCanTargetSelf() {
		return canTargetSelf;
	}
	public void setCanTargetSelf(boolean canTargetSelf) {
		this.canTargetSelf = canTargetSelf;
	}
}
