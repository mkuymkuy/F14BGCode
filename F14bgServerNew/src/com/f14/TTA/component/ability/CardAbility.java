package com.f14.TTA.component.ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f14.TTA.component.Condition;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.bg.component.ICondition;

import net.sf.json.JSONObject;

/**
 * 卡牌的能力
 * 
 * @author F14eagle
 *
 */
public class CardAbility implements ICondition<TTACard> {
	/**
	 * 白名单条件
	 */
	public List<Condition> wcs = new ArrayList<Condition>(0);
	/**
	 * 黑名单条件
	 */
	public List<Condition> bcs = new ArrayList<Condition>(0);
	public TTAProperty property = new TTAProperty();
	public String descr;
	protected Map<String, Object> propertyMap;

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public List<Condition> getWcs() {
		return wcs;
	}

	public void setWcs(List<Condition> wcs) {
		this.wcs = new ArrayList<Condition>();
		if (wcs != null) {
			for (Object o : wcs) {
				Condition c = (Condition) JSONObject.toBean(JSONObject.fromObject(o), Condition.class);
				this.wcs.add(c);
			}
		}
	}

	public List<Condition> getBcs() {
		return bcs;
	}

	public void setBcs(List<Condition> bcs) {
		this.bcs = new ArrayList<Condition>();
		if (bcs != null) {
			for (Object o : bcs) {
				Condition c = (Condition) JSONObject.toBean(JSONObject.fromObject(o), Condition.class);
				this.bcs.add(c);
			}
		}
	}

	public Map<String, Object> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, Object> propertyMap) {
		this.propertyMap = propertyMap;
		// 设置完propertyMap后自动将其转换成property
		if (this.propertyMap != null) {
			for (String key : this.propertyMap.keySet()) {
				CivilizationProperty pro = CivilizationProperty.valueOf(key);
				int num = (Integer) this.propertyMap.get(key);
				this.property.addProperty(pro, num);
			}
		}
	}

	/**
	 * 测试卡牌是否符合该能力的条件
	 * 
	 * @param card
	 * @return
	 */
	@Override
	public boolean test(TTACard card) {
		// 白黑名单中的条件均为 "或" 关系
		boolean res = true;
		for (Condition c : this.wcs) {
			if (c.test(card)) {
				return true;
			} else {
				res = false;
			}
		}
		for (Condition c : this.bcs) {
			if (c.test(card)) {
				return false;
			}
		}
		return res;
	}

	@Override
	public boolean test(Collection<TTACard> objects) {
		for (TTACard object : objects) {
			if (!this.test(object)) {
				return false;
			}
		}
		return true;
	}
}
