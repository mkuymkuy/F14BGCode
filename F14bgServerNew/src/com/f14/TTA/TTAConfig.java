package com.f14.TTA;

import com.f14.TTA.consts.TTAMode;
import com.f14.bg.BoardGameConfig;

/**
 * TTA的配置对象
 * 
 * @author F14eagle
 *
 */
public class TTAConfig extends BoardGameConfig {
	/**
	 * 最大世纪的限制
	 */
	public int ageLimit;
	/**
	 * 是否会腐败
	 */
	public boolean corruption;
	/**
	 * 是否会暴动
	 */
	public boolean uprising;
	/**
	 * 时代变化时是否会减少人口
	 */
	public boolean darkAge;
	/**
	 * 额外奖励分数模式开关
	 */
	public boolean bonusCardFlag = false;
	/**
	 * 额外奖励分数模式下计分牌的数量
	 */
	public int bonusCardNumber = 4;
	/**
	 * 游戏模式
	 */
	public TTAMode mode;
	/**
	 * 暴动摸牌
	 */
	public boolean revoltDraw;

	public int getAgeLimit() {
		return ageLimit;
	}

	public void setAgeLimit(int ageLimit) {
		this.ageLimit = ageLimit;
	}

	public boolean isCorruption() {
		return corruption;
	}

	public void setCorruption(boolean corruption) {
		this.corruption = corruption;
	}

	public boolean isDarkAge() {
		return darkAge;
	}

	public void setDarkAge(boolean darkAge) {
		this.darkAge = darkAge;
	}

	public boolean isUprising() {
		return uprising;
	}

	public void setUprising(boolean uprising) {
		this.uprising = uprising;
	}

	public boolean isBonusCardFlag() {
		return bonusCardFlag;
	}

	public void setBonusCardFlag(boolean bonusCardFlag) {
		this.bonusCardFlag = bonusCardFlag;
	}

	public int getBonusCardNumber() {
		return bonusCardNumber;
	}

	public void setBonusCardNumber(int bonusCardNumber) {
		this.bonusCardNumber = bonusCardNumber;
	}

	public boolean isRevoltDraw() {
		return revoltDraw;
	}

	public void setRevoltDraw(boolean revoltDraw) {
		this.revoltDraw = revoltDraw;
	}

	public TTAMode getMode() {
		return mode;
	}

	public void setMode(TTAMode mode) {
		this.mode = mode;
	}

}
