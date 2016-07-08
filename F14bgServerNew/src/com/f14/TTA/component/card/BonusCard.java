package com.f14.TTA.component.card;

/**
 * 防御加成牌
 * 
 * @author F14eagle
 *
 */
public class BonusCard extends MilitaryCard {
	public int defense;
	public int colo;

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getColo() {
		return colo;
	}

	public void setColo(int colo) {
		this.colo = colo;
	}
}
