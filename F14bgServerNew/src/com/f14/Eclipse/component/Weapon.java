package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.List;

import com.f14.Eclipse.consts.DamageDice;
import com.f14.Eclipse.consts.WeaponType;

/**
 * 武器
 *
 * @author f14eagle
 */
public class Weapon {
	public WeaponType weaponType;
	public List<DamageDice> damageDice = new ArrayList<DamageDice>();
	public WeaponType getWeaponType() {
		return weaponType;
	}
	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}
	public List<DamageDice> getDamageDice() {
		return damageDice;
	}
	public void setDamageDice(List<DamageDice> damageDice) {
		this.damageDice = damageDice;
	}
}
