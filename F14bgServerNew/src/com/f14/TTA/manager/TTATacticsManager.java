package com.f14.TTA.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.TacticsCard;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.TTAConsts;

/**
 * 战术管理类
 * 
 * @author F14eagle
 *
 */
public class TTATacticsManager {
	/**
	 * 部队类型常量
	 */
	protected final CardSubType[] unitType = new CardSubType[] { CardSubType.INFANTRY, CardSubType.CAVALRY,
			CardSubType.ARTILLERY, CardSubType.AIR_FORCE };

	public void getTacticsResult(TacticsCard tactics, Map<TTACard, Integer> units) {
		Map<CardSubType, UnitValue> values = new LinkedHashMap<CardSubType, UnitValue>();
		// 统计各个部队类型,各个等级的部队数量
		for (TTACard card : units.keySet()) {
			// 只处理部队类型的牌
			if (card.cardType == CardType.UNIT) {
				UnitValue uv = values.get(card.cardSubType);
				if (uv == null) {
					uv = new UnitValue(card.cardSubType);
					values.put(card.cardSubType, uv);
				}
				uv.increaseNum(card.level, units.get(card));
			}
		}
	}

	class UnitValue {
		CardSubType type;
		Map<Integer, Integer> nums = new LinkedHashMap<Integer, Integer>();

		UnitValue(CardSubType type) {
			this.type = type;
			for (int i = 0; i < TTAConsts.MAX_AGE; i++) {
				nums.put(i, 0);
			}
		}

		/**
		 * 添加指定等级的数量
		 * 
		 * @param level
		 * @param num
		 */
		void increaseNum(int level, int num) {
			int i = this.nums.get(level);
			this.nums.put(level, i + num);
		}

		/**
		 * 减去指定等级的数量(最多减到0,返回实际减去的数量)
		 * 
		 * @param level
		 * @param num
		 * @return
		 */
		int decreaseNum(int level, int num) {
			int i = this.nums.get(level);
			int res = i - num;
			this.nums.put(level, Math.max(0, res));
			return (i - Math.abs(res));
		}
	}
}
