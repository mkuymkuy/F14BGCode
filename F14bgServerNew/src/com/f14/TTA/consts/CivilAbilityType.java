package com.f14.TTA.consts;

/**
 * 内政牌的能力类型
 * 
 * @author F14eagle
 *
 */
public enum CivilAbilityType {
	/**
	 * 直接将个体的能力加倍
	 */
	DOUBLE_PROPERTY, /**
						 * 直接调整个体的能力
						 */
	ADJUST_UNIT_PROPERTY, /**
							 * 调整属性值调整
							 */
	ADJUST_PROPERTY, /**
						 * 所有个体生产资源
						 */
	PRODUCE_RESOURCE,

	/**
	 * 增长人口时的费用调整
	 */
	PA_POPULATION_COST, /**
						 * 拿牌后触发的能力
						 */
	PA_TAKE_CARD, /**
					 * 拿取奇迹时的费用调整
					 */
	PA_TAKE_WONDER_COST, /**
							 * 出牌后触发的能力
							 */
	PA_PLAY_CARD, /**
					 * 革命时将使用军事行动点代替内政行动点
					 */
	PA_MILITARY_REVOLUTION, /**
							 * 出牌费用的调整
							 */
	PA_PLAY_CARD_COST, /**
						 * 建造费用的调整
						 */
	PA_BUILD_COST, /**
					 * 被宣战时得分的能力
					 */
	PA_SCORE_UNDERWAR, /**
						 * 被宣战时建造费用的调整
						 */
	PA_BUILD_COST_UNDERWAR, /**
							 * 影响全局玩家建筑费用调整
							 */
	PA_BUILD_COST_GLOBAL, /**
							 * 影响全局玩家升级费用调整
							 */
	PA_UPGRADE_COST_GLOBAL, /**
							 * 一次可以建造奇迹的步骤
							 */
	PA_WONDER_STEP, /**
					 * 每个回合的临时资源
					 */
	PA_TEMPLATE_RESOURCE, /**
							 * 无视战术牌效果
							 */
	PA_IGNORE_TACTICS, /**
						 * 额外战术牌奖励
						 */
	PA_ADDITIONAL_TACTICS_BONUS, /**
									 * 卡牌使用限制
									 */
	PA_USE_CARD_LIMIT, /**
						 * 对方使用额外的军事行动点
						 */
	PA_ADDITIONAL_MILITARY_COST, /**
									 * 强化加值卡
									 */
	PA_ENHANCE_BONUS_CARD, /**
							 * 强化防御卡
							 */
	PA_ENHANCE_DEFENSE_CARD, /**
								 * 进攻盟友时的属性调整
								 */
	PA_ATTACK_ALIAN_ADJUST, /**
							 * 不能进攻盟友
							 */
	PA_CANNOT_ATTACK_ALIAN, /**
							 * 与盟友的科技合作
							 */
	PA_SCIENCE_ASSIST, /**
						 * 按照盟友的属性调整自己的属性
						 */
	ADJUST_PROPERTY_BY_ALIAN, /**
								 * 进攻盟友后摧毁条约
								 */
	PA_END_WHEN_ATTACK_ALIAN, /**
								 * 不能被作为目标
								 */
	PA_CANNOT_BE_TARGET, /**
							 * 特斯拉的能力
							 */
	PA_TESLA_ABILITY,
}
