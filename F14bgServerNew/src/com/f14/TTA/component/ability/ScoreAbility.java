package com.f14.TTA.component.ability;

import java.util.HashMap;
import java.util.Map;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.AdjustType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.ScoreAbilityType;

/**
 * 卡牌的得分能力
 * 
 * @author F14eagle
 *
 */
public class ScoreAbility extends CardAbility {
	public AdjustType scoreType;
	public ScoreAbilityType scoreAbilityType;
	public int aboveNum;
	public Map<Integer, String> rankValue = new HashMap<Integer, String>();
	public int maxValue;

	public AdjustType getScoreType() {
		return scoreType;
	}

	public void setScoreType(AdjustType scoreType) {
		this.scoreType = scoreType;
	}

	public ScoreAbilityType getScoreAbilityType() {
		return scoreAbilityType;
	}

	public void setScoreAbilityType(ScoreAbilityType scoreAbilityType) {
		this.scoreAbilityType = scoreAbilityType;
	}

	public int getAboveNum() {
		return aboveNum;
	}

	public void setAboveNum(int aboveNum) {
		this.aboveNum = aboveNum;
	}

	public Map<Integer, String> getRankValue() {
		return rankValue;
	}

	public void setRankValue(Map<Integer, String> rankValue) {
		this.rankValue = rankValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * 取得指定卡牌能够得到的分数
	 * 
	 * @param card
	 * @return
	 */
	protected int getScoreCulturePoint(TTACard card) {
		int res = 0;
		if (this.test(card)) {
			int cp = this.property.getProperty(CivilizationProperty.CULTURE);
			switch (this.scoreAbilityType) {
			case NORMAL: // 普通得分方式
				switch (this.scoreType) {
				case BY_NUM:
					res += card.getAvailableCount() * cp;
					break;
				case BY_LEVEL:
					res += card.level * cp;
					break;
				case BY_NUM_LEVEL:
					res += card.getAvailableCount() * card.level * cp;
					break;
				case BY_TECHNOLOGY_NUM:
					res += 1 * cp;
					break;
				}
				break;
			// 其他能力暂时不得分
			}

		}
		return res;
	}

	/**
	 * 取得玩家可以从该卡牌得到的分数
	 * 
	 * @param player
	 * @return
	 */
	public int getScoreCulturePoint(TTAPlayer player) {
		int res = 0;
		switch (this.scoreAbilityType) {
		case NORMAL: // 普通得分方式
			// 需要检查所有牌能够带来的分数
			for (TTACard card : player.getAllPlayedCard()) {
				res += this.getScoreCulturePoint(card);
			}
			break;
		case BY_PROPERTY: // 按属性得分
			Map<CivilizationProperty, Integer> pros = this.property.getAllProperties();
			for (CivilizationProperty key : pros.keySet()) {
				int num = pros.get(key);
				// 如果该属性有调整值,则取玩家的属性x调整值为最终得分
				if (num != 0) {
					res += player.getProperty(key) * num;
				}
			}
			break;
		case FOOD_PRODUCTION: // 按食物的生产力得分
			res += player.getFoodProduction();
			break;
		case RESOURCE_PRODUCTION: // 按资源的生产力得分
			res += player.getResourceProduction();
			break;
		case DISCONTENT_WORKER: // 按不满的工人数得分
			res += player.tokenPool.getUnhappyWorkers() * this.property.getProperty(CivilizationProperty.CULTURE);
			break;
		case BY_WORKER: // 按工人数得分
			// 减去忽略的工人数
			int num = (player.getWorkers() - this.aboveNum) * this.property.getProperty(CivilizationProperty.CULTURE);
			// 该事件中不能产生负数
			num = Math.max(0, num);
			res += num;
			break;
		}
		// 检查最大值限制
		if (this.maxValue > 0) {
			res = Math.min(this.maxValue, res);
		}
		return res;
	}

	/**
	 * 按照排名,取得玩家可以从该卡牌得到的分数
	 * 
	 * @param player
	 * @param playerNumber
	 *            玩家数
	 * @param rank
	 *            排名(从1开始)
	 * @return
	 */
	public int getScoreCulturePoint(TTAPlayer player, int playerNumber, int rank) {
		int res = 0;
		switch (this.scoreAbilityType) {
		case BY_RANK:
			String str = this.rankValue.get(playerNumber + "");
			String[] vals = str.split(",");
			res += Integer.valueOf(vals[(rank - 1)]);
			break;
		}
		// 检查最大值限制
		if (this.maxValue > 0) {
			res = Math.min(this.maxValue, res);
		}
		return res;
	}
}
