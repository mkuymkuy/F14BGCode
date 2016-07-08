package com.f14.RFTG.card;

import com.f14.RFTG.consts.GameState;

/**
 * 额外VP的能力
 * 
 * @author F14eagle
 *
 */
public class BonusAbility extends Ability {
	public int vp = 0;
	public int chip = 0;
	public GameState phase;

	public int getVp() {
		return vp;
	}

	public void setVp(int vp) {
		this.vp = vp;
	}

	public int getChip() {
		return chip;
	}

	public void setChip(int chip) {
		this.chip = chip;
	}

	public GameState getPhase() {
		return phase;
	}

	public void setPhase(GameState phase) {
		this.phase = phase;
	}
	
}
