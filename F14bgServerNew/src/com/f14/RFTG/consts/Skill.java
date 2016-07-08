package com.f14.RFTG.consts;

/**
 * 各阶段的特殊能力
 * 
 * @author F14eagle
 *
 */
public enum Skill {
	/**
	 * 弃牌后免费扩张
	 */
	DISCARD_FOR_FREE,
	/**
	 * 购买军事星球
	 */
	BUY_MILITARY_WORLD,
	/**
	 * 弃牌后加军事力
	 */
	DISCARD_FOR_MILITARY,
	/**
	 * 弃牌手牌后加军事力
	 */
	DISCARD_HAND_FOR_MILITARY,
	/**
	 * 可以一次扩张2个星球
	 */
	DOUBLE_SETTLE,
	/**
	 * 交易能力
	 */
	TRADE,
	/**
	 * 消费能力
	 */
	CONSUME,
	/**
	 * 不同种类货物的消费能力
	 */
	DIFFERENT_GOOD_CONSUME,
	/**
	 * 消费所有剩下的货物得到货物数量-1VP
	 */
	CONSUME_REMAINING,
	/**
	 * 消费自己的货物
	 */
	SELF_CONSUME,
	/**
	 * 弃手牌换VP
	 */
	DISCARD_HANDS_FOR_VP,
	/**
	 * 意外星球生产货物
	 */
	PRODUCE_WINDFALL,
	/**
	 * 每个生产货物的星球摸牌
	 */
	DRAW_FOR_PRODUCED_WORLD,
	/**
	 * 每个生产的货物种类摸牌
	 */
	DRAW_FOR_PRODUCED_GOOD_TYPE,
	/**
	 * 生产最多时摸牌
	 */
	DRAW_FOR_MUST_PRODUCED,
	/**
	 * 每个星球摸牌
	 */
	DRAW_FOR_WORLD,
	/**
	 * 弃手牌换取生产货物
	 */
	DISCARD_HAND_FOR_PRODUCE,
	/**
	 * 额外VP - 军事力
	 */
	VP_BONUS_MILITARY,
	/**
	 * 额外VP - VP筹码
	 */
	VP_BONUS_CHIP_PER_VP,
	
	//以下是第二扩充新增的能力
	
	/**
	 * 探索阶段可以混合手牌
	 */
	EXPLORE_HAND,
	/**
	 * 弃牌后减少开发费用
	 */
	DISCARD_FOR_DEVELOP_COST,
	/**
	 * 星球决定军事力
	 */
	WORLD_TO_MILITARY,
	/**
	 * 弃牌后以军事力占领普通星球
	 */
	DISCARD_TO_CONQUER_NON_MILITARY,
	/**
	 * 购买军事星球时的费用调整
	 */
	BUY_MILITARY_COST,
	/**
	 * 弃手牌换牌
	 */
	DISCARD_HANDS_FOR_CARD,
	/**
	 * 不同种类货物的消费能力2
	 */
	DIFFERENT_GOOD_CONSUME_2,
	/**
	 * 额外VP - 不同类型的星球
	 */
	VP_BONUS_DIFFERENT_KINDS_WORLD,
	/**
	 * 特殊能力 - 手牌上限调整
	 */
	SPECIAL_HAND_LIMIT,
	/**
	 * 特殊能力 - 星球上限调整
	 */
	SPECIAL_WORLD_LIMIT,
	/**
	 * 特殊能力 - 赌博
	 */
	SPECIAL_GAMBLE
}
