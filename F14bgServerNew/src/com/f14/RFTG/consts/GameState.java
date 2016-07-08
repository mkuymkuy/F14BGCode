package com.f14.RFTG.consts;

import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.ConsumeAbility;
import com.f14.RFTG.card.DevelopAbility;
import com.f14.RFTG.card.ExploreAbility;
import com.f14.RFTG.card.ProduceAbility;
import com.f14.RFTG.card.SettleAbility;
import com.f14.RFTG.card.TradeAbility;

/**
 * 游戏状态
 * 
 * @author F14eagle
 *
 */
public enum GameState {
	/**
	 * 游戏开始时弃牌
	 */
	STARTING_DISCARD,
	/**
	 * 回合结束时弃牌
	 */
	ROUND_DISCARD,
	/**
	 * 玩家选择行动阶段
	 */
	CHOOSE_ACTION,
	/**
	 * 探索阶段
	 */
	ACTION_EXPLORE,
	/**
	 * 开发阶段
	 */
	ACTION_DEVELOP,
	/**
	 * 开发2阶段
	 */
	ACTION_DEVELOP_2,
	/**
	 * 扩张阶段
	 */
	ACTION_SETTLE,
	/**
	 * 扩张2阶段
	 */
	ACTION_SETTLE_2,
	/**
	 * 交易阶段
	 */
	ACTION_TRADE,
	/**
	 * 消费阶段
	 */
	ACTION_CONSUME,
	/**
	 * 生产阶段
	 */
	ACTION_PRODUCE;
	
	/**
	 * 按照阶段取得对应的能力类
	 * 
	 * @param state
	 * @return
	 */
	public static Class<? extends Ability> getPhaseClass(GameState state){
		switch(state){
		case ACTION_EXPLORE:
			return ExploreAbility.class;
		case ACTION_DEVELOP:
			return DevelopAbility.class;
		case ACTION_SETTLE:
			return SettleAbility.class;
		case ACTION_TRADE:
			return TradeAbility.class;
		case ACTION_CONSUME:
			return ConsumeAbility.class;
		case ACTION_PRODUCE:
			return ProduceAbility.class;
		default:
			return null;
		}
	}
}
