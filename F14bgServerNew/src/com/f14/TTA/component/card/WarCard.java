package com.f14.TTA.component.card;

import java.util.Map;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;

/**
 * 战争(侵略)牌
 * 
 * @author F14eagle
 *
 */
public class WarCard extends MilitaryCard implements IOvertimeCard {
	public EventAbility winnerEffect;
	public EventAbility loserEffect;
	public TTAPlayer owner;
	public TTAPlayer target;

	public EventAbility getWinnerEffect() {
		return winnerEffect;
	}

	public void setWinnerEffect(EventAbility winnerEffect) {
		this.winnerEffect = winnerEffect;
	}

	public EventAbility getLoserEffect() {
		return loserEffect;
	}

	public void setLoserEffect(EventAbility loserEffect) {
		this.loserEffect = loserEffect;
	}

	@Override
	public TTAPlayer getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(TTAPlayer owner) {
		this.owner = owner;
	}

	@Override
	public TTAPlayer getTarget() {
		return this.target;
	}

	@Override
	public void setTarget(TTAPlayer target) {
		this.target = target;
	}

	@Override
	public TTAPlayer getA() {
		return this.getOwner();
	}

	@Override
	public TTAPlayer getB() {
		return this.getTarget();
	}

	@Override
	public void setA(TTAPlayer player) {

	}

	@Override
	public void setB(TTAPlayer player) {

	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("owner", this.getOwner().position);
		map.put("a", this.getA().position);
		map.put("b", this.getB().position);
		return map;
	}
}
