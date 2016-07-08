package com.f14.TTA.component.card;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;

/**
 * 战术牌
 * 
 * @author F14eagle
 *
 */
public class TacticsCard extends MilitaryCard {
	/**
	 * 部队类型常量
	 */
	protected static final CardSubType[] UNIT_TYPE = new CardSubType[] { CardSubType.INFANTRY, CardSubType.CAVALRY,
			CardSubType.ARTILLERY, CardSubType.AIR_FORCE };

	public int infantry;
	public int cavalry;
	public int artillery;
	public int armyBonus;
	public int secondArmyBonus;

	public int getInfantry() {
		return infantry;
	}

	public void setInfantry(int infantry) {
		this.infantry = infantry;
	}

	public int getCavalry() {
		return cavalry;
	}

	public void setCavalry(int cavalry) {
		this.cavalry = cavalry;
	}

	public int getArtillery() {
		return artillery;
	}

	public void setArtillery(int artillery) {
		this.artillery = artillery;
	}

	public int getArmyBonus() {
		return armyBonus;
	}

	public void setArmyBonus(int armyBonus) {
		this.armyBonus = armyBonus;
	}

	public int getSecondArmyBonus() {
		return secondArmyBonus;
	}

	public void setSecondArmyBonus(int secondArmyBonus) {
		this.secondArmyBonus = secondArmyBonus;
	}

	/**
	 * 计算给出的部队可以组成的军队情况
	 * 
	 * @param units
	 * @return
	 */
	public TacticsResult getTacticsResult(Map<TTACard, Integer> units) {
		Map<CardSubType, UnitValue> values = new LinkedHashMap<CardSubType, UnitValue>();
		// 初始化部队类型map
		for (CardSubType type : UNIT_TYPE) {
			UnitValue uv = new UnitValue();
			values.put(type, uv);
		}
		// 统计各个部队类型,各个等级的部队数量
		for (TTACard card : units.keySet()) {
			// 只处理部队类型的牌
			if (card.cardType == CardType.UNIT) {
				int num = units.get(card);
				UnitValue uv = values.get(card.cardSubType);
				if (card.level < (this.level - 1)) {
					// 如果部队等级小于战术牌等级1级,则只能作为次要部队数量
					uv.secondaryNum += num;
				} else {
					uv.mainNum += num;
				}
			}
		}
		// 统计军队数量
		TacticsResult res = new TacticsResult();
		while (true) {
			boolean mainArmy = true;
			// 检查步兵数量
			if (this.infantry > 0) {
				UnitValue uv = values.get(CardSubType.INFANTRY);
				// 检查部队是否够组成mainArmy
				mainArmy &= (uv.mainNum >= this.infantry);
				if (uv.decreaseNum(this.infantry) > 0) {
					// 如果已经不够军队的基本数量,则跳出循环
					break;
				}
			}
			// 检查骑兵数量
			if (this.cavalry > 0) {
				UnitValue uv = values.get(CardSubType.CAVALRY);
				// 检查部队是否够组成mainArmy
				mainArmy &= (uv.mainNum >= this.cavalry);
				if (uv.decreaseNum(this.cavalry) > 0) {
					// 如果已经不够军队的基本数量,则跳出循环
					break;
				}
			}
			// 检查炮兵数量
			if (this.artillery > 0) {
				UnitValue uv = values.get(CardSubType.ARTILLERY);
				// 检查部队是否够组成mainArmy
				mainArmy &= (uv.mainNum >= this.artillery);
				if (uv.decreaseNum(this.artillery) > 0) {
					// 如果已经不够军队的基本数量,则跳出循环
					break;
				}
			}
			// 增加军队的数量
			if (mainArmy) {
				res.mainArmyNum += 1;
			} else {
				res.secondaryArmyNum += 1;
			}
		}
		// 设置空军的数量
		UnitValue uv = values.get(CardSubType.AIR_FORCE);
		res.airForceNum = uv.getTotalNum();
		return res;
	}

	class UnitValue {
		int mainNum;
		int secondaryNum;

		/**
		 * 减去数量,从mainNum开始减,不够则继续从secondaryNum里减,返回不够扣除的数量
		 * 
		 * @param num
		 * @return
		 */
		int decreaseNum(int num) {
			int rest = num;
			// 如果存在mainNum,则从mainNum中扣除
			if (mainNum > 0) {
				if (mainNum >= rest) {
					mainNum = mainNum - rest;
					rest = 0;
				} else {
					rest = rest - mainNum;
					mainNum = 0;
				}
			}
			// 如果secondaryNum还是不够扣,则返回不够扣的数量
			if (secondaryNum > 0) {
				if (secondaryNum >= rest) {
					secondaryNum = secondaryNum - rest;
					rest = 0;
				} else {
					rest = rest - secondaryNum;
					secondaryNum = 0;
				}
			}
			return rest;
		}

		/**
		 * 取得总数
		 * 
		 * @return
		 */
		int getTotalNum() {
			return this.mainNum + this.secondaryNum;
		}
	}

	/**
	 * 战术军队结果
	 * 
	 * @author F14eagle
	 *
	 */
	public class TacticsResult {
		public int mainArmyNum;
		public int secondaryArmyNum;
		public int airForceNum;

		/**
		 * 取得军队和空军所有的军事力加成总值
		 * 
		 * @return
		 */
		public int getTotalMilitaryBonus() {
			int res = 0;
			res += mainArmyNum * TacticsCard.this.armyBonus;
			res += secondaryArmyNum * TacticsCard.this.secondArmyBonus;
			// 每个空军可以将一个军队的军事力加成加倍
			int rest = airForceNum;
			if (rest >= mainArmyNum) {
				// 如果空军剩余数量大于等于主力军数量,则加成军队数量取主力军数
				res += mainArmyNum * TacticsCard.this.armyBonus;
				rest -= mainArmyNum;
			} else {
				// 否则加成军队数量取空军剩余数
				res += rest * TacticsCard.this.armyBonus;
				rest = 0;
			}
			if (rest >= secondaryArmyNum) {
				// 如果空军剩余数量大于等于次级军数量,则加成军队数量取次级军数
				res += secondaryArmyNum * TacticsCard.this.secondArmyBonus;
				rest -= secondaryArmyNum;
			} else {
				// 否则加成军队数量取空军剩余数
				res += rest * TacticsCard.this.secondArmyBonus;
				rest = 0;
			}
			return res;
		}

		/**
		 * 取得军队中最好的单个军队奖励数值(如果存在空军,则该数值加倍)
		 * 
		 * @return
		 */
		public int getBestArmyBonus() {
			int multi = (this.airForceNum > 0) ? 2 : 1;
			if (this.mainArmyNum > 0) {
				return TacticsCard.this.armyBonus * multi;
			}
			if (this.secondaryArmyNum > 0) {
				return TacticsCard.this.secondArmyBonus * multi;
			}
			return 0;
		}
	}

}
