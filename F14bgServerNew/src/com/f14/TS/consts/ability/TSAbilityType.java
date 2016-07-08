package com.f14.TS.consts.ability;

/**
 * TS的能力类型
 * 
 * @author F14eagle
 *
 */
public enum TSAbilityType {
	/**
	 * 行动参数生效
	 */
	ACTION_PARAM_EFFECT,
	/**
	 * 调整影响力
	 */
	ADJUST_INFLUENCE,
	/**
	 * 选择国家并执行行动
	 */
	CHOOSE_COUNTRY_ACTION,
	/**
	 * 随机弃牌并生效
	 */
	ACTIVE_DISCARD_CARD,
	/**
	 * 选择卡牌并执行行动
	 */
	CHOOSE_CARD_ACTION,
	/**
	 * 查看对方手牌
	 */
	VIEW_OPPOSITE_HAND,
	/**
	 * 使用OP进行行动
	 */
	OP_ACTION,
	/**
	 * 行动执行器
	 */
	ACTION_EXECUTER,
	/**
	 * 行动监听器
	 */
	ACTION_LISTENER,
	/**
	 * 为玩家添加效果
	 */
	ADD_EFFECT,
	
	
	/**
	 * 取消头条
	 */
	CANCEL_HEADLINE,
}
