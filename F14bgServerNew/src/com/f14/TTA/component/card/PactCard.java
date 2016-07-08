package com.f14.TTA.component.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.ActiveAbility;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.consts.CivilizationProperty;

import net.sf.json.JSONObject;

/**
 * 条约牌
 * 
 * @author F14eagle
 *
 */
public class PactCard extends MilitaryCard implements IOvertimeCard {
	public boolean asymetric;
	public TTAPlayer owner;
	public TTAPlayer target;
	public TTAPlayer a;
	public TTAPlayer b;
	public TTAProperty propertyA = new TTAProperty();
	protected Map<String, Object> propertyMapA;
	public List<CivilCardAbility> abilitiesA = new ArrayList<CivilCardAbility>(0);
	public ActiveAbility activeAbilityA;
	public TTAProperty propertyB = new TTAProperty();
	protected Map<String, Object> propertyMapB;
	public List<CivilCardAbility> abilitiesB = new ArrayList<CivilCardAbility>(0);
	public ActiveAbility activeAbilityB;
	public TTAPlayer alian;

	public boolean isAsymetric() {
		return asymetric;
	}

	public void setAsymetric(boolean asymetric) {
		this.asymetric = asymetric;
	}

	public Map<String, Object> getPropertyMapA() {
		return propertyMapA;
	}

	public void setPropertyMapA(Map<String, Object> propertyMapA) {
		this.propertyMapA = propertyMapA;
		// 设置完propertyMap后自动将其转换成property
		if (this.propertyMapA != null) {
			for (String key : this.propertyMapA.keySet()) {
				CivilizationProperty pro = CivilizationProperty.valueOf(key);
				int num = (Integer) this.propertyMapA.get(key);
				this.propertyA.addProperty(pro, num);
			}
		}
	}

	public Map<String, Object> getPropertyMapB() {
		return propertyMapB;
	}

	public void setPropertyMapB(Map<String, Object> propertyMapB) {
		this.propertyMapB = propertyMapB;
		// 设置完propertyMap后自动将其转换成property
		if (this.propertyMapB != null) {
			for (String key : this.propertyMapB.keySet()) {
				CivilizationProperty pro = CivilizationProperty.valueOf(key);
				int num = (Integer) this.propertyMapB.get(key);
				this.propertyB.addProperty(pro, num);
			}
		}
	}

	public ActiveAbility getActiveAbilityA() {
		return activeAbilityA;
	}

	public void setActiveAbilityA(ActiveAbility activeAbilityA) {
		this.activeAbilityA = activeAbilityA;
	}

	public ActiveAbility getActiveAbilityB() {
		return activeAbilityB;
	}

	public void setActiveAbilityB(ActiveAbility activeAbilityB) {
		this.activeAbilityB = activeAbilityB;
	}

	public List<CivilCardAbility> getAbilitiesA() {
		return abilitiesA;
	}

	public void setAbilitiesA(List<CivilCardAbility> abilitiesA) {
		this.abilitiesA = new ArrayList<CivilCardAbility>();
		if (abilitiesA != null) {
			for (Object o : abilitiesA) {
				CivilCardAbility a = (CivilCardAbility) JSONObject.toBean(JSONObject.fromObject(o),
						CivilCardAbility.class);
				this.abilitiesA.add(a);
			}
		}
	}

	public List<CivilCardAbility> getAbilitiesB() {
		return abilitiesB;
	}

	public void setAbilitiesB(List<CivilCardAbility> abilitiesB) {
		this.abilitiesB = new ArrayList<CivilCardAbility>();
		if (abilitiesB != null) {
			for (Object o : abilitiesB) {
				CivilCardAbility a = (CivilCardAbility) JSONObject.toBean(JSONObject.fromObject(o),
						CivilCardAbility.class);
				this.abilitiesB.add(a);
			}
		}
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
		return this.a;
	}

	@Override
	public TTAPlayer getB() {
		return this.b;
	}

	@Override
	public void setA(TTAPlayer player) {
		this.a = player;
	}

	@Override
	public void setB(TTAPlayer player) {
		this.b = player;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("owner", this.getOwner().position);
		map.put("a", this.getA().position);
		map.put("b", this.getB().position);
		return map;
	}

	@Override
	public PactCard clone() {
		return (PactCard) super.clone();
	}
}
