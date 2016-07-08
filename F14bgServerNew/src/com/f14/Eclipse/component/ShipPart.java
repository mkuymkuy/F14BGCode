package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f14.Eclipse.consts.DamageDice;
import com.f14.Eclipse.consts.ShipPartCategory;
import com.f14.Eclipse.consts.ShipPartType;
import com.f14.Eclipse.consts.ShipProperty;
import com.f14.Eclipse.consts.WeaponType;
import com.f14.bg.component.Card;

/**
 * 飞船配件
 *
 * @author f14eagle
 */
public class ShipPart extends Card {
	public ShipPartCategory shipPartCategory;
	public ShipPartType shipPartType;
	public WeaponType weaponType;
	public List<DamageDice> damageDice = new ArrayList<DamageDice>();
	protected EclipseShipProperty property = new EclipseShipProperty();
	
	public ShipPartCategory getShipPartCategory() {
		return shipPartCategory;
	}
	public void setShipPartCategory(ShipPartCategory shipPartCategory) {
		this.shipPartCategory = shipPartCategory;
	}
	public ShipPartType getShipPartType() {
		return shipPartType;
	}
	public void setShipPartType(ShipPartType shipPartType) {
		this.shipPartType = shipPartType;
	}
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
		this.damageDice = new ArrayList<DamageDice>();
		if(damageDice!=null){
			for(Object o : damageDice){
				this.damageDice.add(DamageDice.valueOf(o.toString()));
			}
		}
	}
	public EclipseShipProperty getProperty() {
		return property;
	}
	public void setShipProperty(Map<String,Integer> property) {
		this.property = new EclipseShipProperty();
		for(String key : property.keySet()){
			ShipProperty p = ShipProperty.valueOf(key);
			this.property.setProperty(p, property.get(key));
		}
	}
	
	@Override
	public ShipPart clone() {
		return (ShipPart)super.clone();
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = super.toMap();
		map.put("cardIndex", this.cardIndex);
		return map;
	}
}
