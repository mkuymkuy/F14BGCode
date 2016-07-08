package com.f14.PuertoRico.component;

import java.util.Map;

import com.f14.PuertoRico.consts.Character;
import com.f14.bg.component.Card;

/**
 * 角色卡
 * 
 * @author F14eagle
 *
 */
public class CharacterCard extends Card {
	public int doubloon = 0;
	public Character character;
	public boolean canUse = false;
	public int getDoubloon() {
		return doubloon;
	}
	public void setDoubloon(int doubloon) {
		this.doubloon = doubloon;
	}
	public Character getCharacter() {
		return character;
	}
	public void setCharacter(Character character) {
		this.character = character;
	}
	public boolean isCanUse() {
		return canUse;
	}
	public void setCanUse(boolean canUse) {
		this.canUse = canUse;
	}
	@Override
	public CharacterCard clone() {
		return (CharacterCard)super.clone();
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("canUse", canUse);
		map.put("doubloon", doubloon);
		return map;
	}
}
