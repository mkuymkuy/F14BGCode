package com.f14.TTA.component;

import java.util.Collection;

import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;
import com.f14.bg.component.ICondition;

/**
 * 用于匹配对象属性的条件对象
 * 
 * @author F14eagle
 *
 */
public class Condition implements ICondition<TTACard> {
	public ActionType actionType;
	public CardType cardType;
	public CardSubType cardSubType;
	public Integer level;
	public String cardNo;
	public Boolean isTechnologyCard;

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

	public void setCardSubType(CardSubType cardSubType) {
		this.cardSubType = cardSubType;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Boolean getIsTechnologyCard() {
		return isTechnologyCard;
	}

	public void setIsTechnologyCard(Boolean isTechnologyCard) {
		this.isTechnologyCard = isTechnologyCard;
	}

	/**
	 * 判断指定的牌是否符合该条件对象
	 * 
	 * @param card
	 * @return
	 */
	@Override
	public boolean test(TTACard card) {
		if (this.cardNo != null && !this.cardNo.equals(card.cardNo)) {
			return false;
		}
		if (this.cardSubType != null && this.cardSubType != card.cardSubType) {
			return false;
		}
		if (this.cardType != null && this.cardType != card.cardType) {
			return false;
		}
		if (this.actionType != null && this.actionType != card.actionType) {
			return false;
		}
		if (this.isTechnologyCard != null && this.isTechnologyCard.booleanValue() != card.isTechnologyCard) {
			return false;
		}
		if (this.level != null && this.level.intValue() != card.level) {
			return false;
		}
		return true;
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
