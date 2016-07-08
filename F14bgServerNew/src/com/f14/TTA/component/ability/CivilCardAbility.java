package com.f14.TTA.component.ability;

import java.util.List;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.Condition;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.AdjustTarget;
import com.f14.TTA.consts.AdjustType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;

/**
 * 内政牌的能力
 * 
 * @author F14eagle
 *
 */
public class CivilCardAbility extends CardAbility {
	public CivilAbilityType abilityType;
	/**
	 * DOUBLE_PROPERTY 能力中被加倍的属性
	 */
	public CivilizationProperty doubleProperty;
	/**
	 * 该能力最多能应用的个体数量
	 */
	int limit;
	/**
	 * 能力调整方式
	 */
	public AdjustType adjustType;
	/**
	 * 能力调整的目标
	 */
	public AdjustTarget adjustTarget;
	/**
	 * 调整时参照的能力值
	 */
	public CivilizationProperty byProperty;
	/**
	 * 全局能力中是否会影响到自己
	 */
	public boolean effectSelf;
	/**
	 * 一次可建造奇迹的步骤数
	 */
	public int buildStep;
	public int amount;

	public CivilizationProperty getDoubleProperty() {
		return doubleProperty;
	}

	public void setDoubleProperty(CivilizationProperty doubleProperty) {
		this.doubleProperty = doubleProperty;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public AdjustType getAdjustType() {
		return adjustType;
	}

	public void setAdjustType(AdjustType adjustType) {
		this.adjustType = adjustType;
	}

	public CivilizationProperty getByProperty() {
		return byProperty;
	}

	public void setByProperty(CivilizationProperty byProperty) {
		this.byProperty = byProperty;
	}

	public AdjustTarget getAdjustTarget() {
		return adjustTarget;
	}

	public void setAdjustTarget(AdjustTarget adjustTarget) {
		this.adjustTarget = adjustTarget;
	}

	public CivilAbilityType getAbilityType() {
		return abilityType;
	}

	public void setAbilityType(CivilAbilityType abilityType) {
		this.abilityType = abilityType;
	}

	public boolean isEffectSelf() {
		return effectSelf;
	}

	public void setEffectSelf(boolean effectSelf) {
		this.effectSelf = effectSelf;
	}

	public int getBuildStep() {
		return buildStep;
	}

	public void setBuildStep(int buildStep) {
		this.buildStep = buildStep;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * 按照能力类型,取得符合能力条件的基数数量
	 * 
	 * @param player
	 * @return
	 */
	public int getAvailableNumber(TTAPlayer player) {
		int res = 0;
		switch (this.adjustTarget) {
		case ALL: // 对象是全部
			switch (this.adjustType) {
			case BY_NUM: // 计算个体数量
				for (TTACard card : player.getBuildings().getCards()) {
					if (this.test(card)) {
						// 加上其工人的数量
						res += card.getAvailableCount();
					}
				}
				// 限制只在计算个体数量时使用
				if (limit > 0 && res > limit) {
					res = limit;
				}
				break;
			case BY_PROPERTY: // 计算个体的属性
				for (TTACard card : player.getBuildings().getCards()) {
					if (this.test(card)) {
						res += card.property.getProperty(byProperty) * card.getAvailableCount();
					}
				}
				break;
			case BY_LEVEL: // 计算个体的等级
				for (TTACard card : player.getBuildings().getCards()) {
					if (this.test(card)) {
						res += card.level;
					}
				}
				break;
			case BY_NUM_LEVEL: // 计算个体的等级数量
				for (TTACard card : player.getBuildings().getCards()) {
					if (this.test(card)) {
						// 加上其工人的数量 x 等级
						res += card.getAvailableCount() * card.level;
					}
				}
				break;
			case BY_GROUP_NUM: // 计算组合的数量
				res += this.getGroupNumber(player.getBuildings().getCards());
				break;
			case CONST: // 常量
				res += this.amount;
				break;
			}
			break;
		case BEST: // 对象是玩家所拥有最好的
			TTACard best = this.getBestCard(player.getBuildings().getCards());
			// 只有当存在最好的牌时才会计算数量
			if (best != null) {
				switch (this.adjustType) {
				case BY_LEVEL: // 按等级
					res += best.level;
					break;
				case BY_PROPERTY: // 按属性
					res += best.property.getProperty(byProperty);
					break;
				case CONST: // 常量
					res += this.amount;
					break;
				}
			}
			break;
		}

		return res;
	}

	/**
	 * 取得按当前白名单中所有条件分组统计得到的最小数量
	 * 
	 * @param cards
	 * @return
	 */
	protected int getGroupNumber(List<TTACard> cards) {
		Integer res = null;
		for (Condition con : this.wcs) {
			// 统计cards中符合各个白名单条件的个数
			int count = 0;
			for (TTACard card : cards) {
				if (con.test(card)) {
					count += card.getAvailableCount();
				}
			}
			// 取最小的值
			if (res == null || count < res) {
				res = count;
			}
		}
		return res;
	}

	/**
	 * 取得符合技能条件中最高等级的一张牌,并且是有基数的
	 * 
	 * @param cards
	 * @return
	 */
	protected TTACard getBestCard(List<TTACard> cards) {
		TTACard res = null;
		for (TTACard card : cards) {
			if (this.test(card)) {
				// 必须是有基数的才可能成为最好的
				if (card.getAvailableCount() > 0) {
					if (res == null || res.level < card.level) {
						res = card;
					}
				}
			}
		}
		return res;
	}

}
