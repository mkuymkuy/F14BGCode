package com.f14.TTA.component.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.ActiveAbility;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.bg.component.Card;
import com.f14.bg.component.PartPool;

import net.sf.json.JSONObject;

public class TTACard extends Card implements Comparable<TTACard> {
	public int qty2p;
	public int qty3p;
	public int qty4p;
	public int level;
	public ActionType actionType;
	public CardType cardType;
	public CardSubType cardSubType;
	public boolean isTechnologyCard;
	public TTAProperty property = new TTAProperty();
	public PartPool tokens = new PartPool();
	public ActiveAbility activeAbility;
	public ActionCost actionCost;
	public List<CivilCardAbility> abilities = new ArrayList<CivilCardAbility>(0);

	protected Map<String, Object> propertyMap;
	protected Map<String, Object> tokenMap;

	public int getQty2p() {
		return qty2p;
	}

	public void setQty2p(int qty2p) {
		this.qty2p = qty2p;
	}

	public int getQty3p() {
		return qty3p;
	}

	public void setQty3p(int qty3p) {
		this.qty3p = qty3p;
	}

	public int getQty4p() {
		return qty4p;
	}

	public void setQty4p(int qty4p) {
		this.qty4p = qty4p;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public CardSubType getCardSubType() {
		return cardSubType;
	}

	public ActionCost getActionCost() {
		return actionCost;
	}

	public void setActionCost(ActionCost actionCost) {
		this.actionCost = actionCost;
	}

	public void setCardSubType(CardSubType cardSubType) {
		this.cardSubType = cardSubType;
	}

	public Map<String, Object> getPropertyMap() {
		return propertyMap;
	}

	public ActiveAbility getActiveAbility() {
		return activeAbility;
	}

	public void setActiveAbility(ActiveAbility activeAbility) {
		this.activeAbility = activeAbility;
	}

	public List<CivilCardAbility> getAbilities() {
		return abilities;
	}

	public void setAbilities(List<CivilCardAbility> abilities) {
		this.abilities = new ArrayList<CivilCardAbility>();
		if (abilities != null) {
			for (Object o : abilities) {
				CivilCardAbility a = (CivilCardAbility) JSONObject.toBean(JSONObject.fromObject(o),
						CivilCardAbility.class);
				this.abilities.add(a);
			}
		}
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

	public Map<String, Object> getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(Map<String, Object> tokenMap) {
		this.tokenMap = tokenMap;
	}

	public void setTechnologyCard(boolean isTechnologyCard) {
		this.isTechnologyCard = isTechnologyCard;
	}

	/**
	 * 判断该卡牌是否是科技卡
	 * 
	 * @return
	 */
	public boolean isTechnologyCard() {
		return this.isTechnologyCard;
	}

	/**
	 * 判断该卡牌是否需要工人才能生效
	 * 
	 * @return
	 */
	public boolean needWorker() {
		switch (this.cardType) {
		case BUILDING:
		case PRODUCTION:
		case UNIT:
			return true;
		default:
			return false;
		}
	}

	/**
	 * 取得有效的基数,需要工人的牌返回当前工人数量,否则返回1
	 * 
	 * @return
	 */
	public int getAvailableCount() {
		return 1;
	}

	@Override
	public int compareTo(TTACard o) {
		if (this.level > o.level) {
			return 1;
		} else if (this.level < o.level) {
			return -1;
		} else {
			double n1 = Double.valueOf(this.cardNo);
			double n2 = Double.valueOf(o.cardNo);
			if (n1 > n2) {
				return 1;
			} else if (n1 < n2) {
				return -1;
			}
		}
		return 0;
	}

	@Override
	public TTACard clone() {
		try {
			TTACard c = (TTACard) super.clone();
			c.property = this.property.clone();
			c.tokens = this.tokens.clone();
			return c;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getReportString() {
		return "[" + this.getAgeString(this.level) + this.name + "]";
	}
	
	private String getAgeString(int age){
		switch (age){
		case 0:
			return "Ａ";
		case 1:
			return "Ⅰ";
		case 2:
			return "Ⅱ";
		case 3:
			return "Ⅲ";
		default: 
			return "";
		}
	}
}
