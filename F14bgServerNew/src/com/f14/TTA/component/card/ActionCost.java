package com.f14.TTA.component.card;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.consts.ActionType;
import com.f14.TTA.consts.AdjustType;
import com.f14.TTA.consts.CardType;

/**
 * 行动点消耗对象
 * 
 * @author F14eagle
 *
 */
public class ActionCost {
	public ActionType actionType;
	public int actionCost;
	public AdjustType adjustType;
	public CardType targetType;

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public int getActionCost() {
		return actionCost;
	}

	public void setActionCost(int actionCost) {
		this.actionCost = actionCost;
	}

	public AdjustType getAdjustType() {
		return adjustType;
	}

	public void setAdjustType(AdjustType adjustType) {
		this.adjustType = adjustType;
	}

	public CardType getTargetType() {
		return targetType;
	}

	public void setTargetType(CardType targetType) {
		this.targetType = targetType;
	}

	/**
	 * 取得对目标玩家使用时需要的行动点数
	 * 
	 * @param target
	 * @return
	 */
	public int getActionCost(TTAPlayer target) {
		int res = 0;
		if (this.adjustType == null) {
			res = this.actionCost;
		} else {
			switch (this.adjustType) {
			case BY_LEVEL: // 按目标等级计算行动点数(暂时只有这一种方式)
				TTACard card = null;
				// 取得目标牌
				if (this.targetType != null) {
					switch (this.targetType) {
					case LEADER: // 当前的领袖
						card = target.getLeader();
						break;
					case WONDER: // 在建的奇迹
						card = target.getUncompleteWonder();
						break;
					}
				}
				if (card != null) {
					// actionCost为等级的基数
					res += card.level;
				}
				break;
			}
		}
		return res;
	}
}
