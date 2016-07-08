package com.f14.RFTG.card;

/**
 * 探索阶段的能力
 * 
 * @author F14eagle
 *
 */
public class ExploreAbility extends Ability {
	public int drawNum = 0;
	public int keepNum = 0;
	
	public int getDrawNum() {
		return drawNum;
	}
	public void setDrawNum(int drawNum) {
		this.drawNum = drawNum;
	}
	public int getKeepNum() {
		return keepNum;
	}
	public void setKeepNum(int keepNum) {
		this.keepNum = keepNum;
	}
}
