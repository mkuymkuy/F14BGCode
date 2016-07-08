package com.f14.TS.consts;

/**
 * TS的持续效果类型
 * 
 * @author F14eagle
 *
 */
public enum EffectType {
	/**
	 * 调整OP
	 */
	ADJUST_OP,
	/**
	 * 额外OP,当所有点数都用在某个符合国家条件的情况下
	 */
	ADDITIONAL_OP,
	/**
	 * 只要进行政变就会输掉游戏
	 */
	COUP_TO_LOSE,
	/**
	 * 政变不会影响DEFCON
	 */
	FREE_DEFCON_COUP,
	/**
	 * 临时的行动轮数
	 */
	TEMPLATE_ACTION_ROUND,
	/**
	 * 调整阵营掷骰修正
	 */
	REALIGMENT_ROLL_MODIFIER,
	/**
	 * 政变的掷骰修正
	 */
	COUP_ROLL_MODIFIER,
	/**
	 * 额外的军事行动力 (该效果废弃了,配置中没有这些效果)
	 */
	ADDITIONAL_MA_POINT,
	/**
	 * 取消头条
	 */
	CANCEL_HEADLINE,
	/**
	 * 困境/捕熊陷阱
	 */
	QUAGMIRE,
	/**
	 * #49-导弹嫉妒的效果,下回合必须用导弹嫉妒作为行动
	 */
	_49_EFFECT,
	/**
	 * #50的效果,如果下个行动轮美国不打出联合国干涉,则苏联得到3VP
	 */
	_50_EFFECT,
	/**
	 * #59-鲜花反战 的效果,美国每打出一张战争牌,苏联+2VP
	 */
	_59_EFFECT,
	/**
	 * #60-U2事件 的效果,如果联合国作为事件方式被打出,苏联+1VP
	 */
	_60_EFFECT,
	/**
	 * #69-拉美暗杀队 的效果,自己在中美和南美的政变结果+1,对手-1
	 */
	_69_EFFECT,
	/**
	 * #73-穿梭外交 的效果,苏联在下一次中东或者亚洲计分时,战场国数量-1
	 */
	_73_EFFECT,
	/**
	 * #82-伊朗人质危机 的效果,美国在#92事件中要丢2张牌
	 */
	_82_EFFECT,
	/**
	 * #94-切尔诺贝利 的效果,不能在指定的区域通过使用OP的行动放置影响力
	 */
	_94_EFFECT,
	/**
	 * #100-台湾决议 的效果,美国在计分时如果控制台湾,则算作战场国
	 */
	_101_EFFECT,
	/**
	 * #111-尤里和萨曼莎 的效果,美国本轮每政变一次,苏联就+1VP
	 */
	_109_EFFECT,
	/**
	 * 太空竞赛特权1 - 每回合可以进行2次太空竞赛
	 */
	SR_PRIVILEGE_1,
	/**
	 * 太空竞赛特权2 - 对方先亮出头条
	 */
	SR_PRIVILEGE_2,
	/**
	 * 太空竞赛特权3 - 每回合结束时可以弃掉1张手牌
	 */
	SR_PRIVILEGE_3,
	/**
	 * 太空竞赛特权4 - 每回合可以进行8个行动轮
	 */
	SR_PRIVILEGE_4,
	
	/**
	 * 苏联不能在欧洲政变/调整阵营
	 */
	PROTECT_EUROPE,
	/**
	 * 苏联不能在日本政变/调整阵营
	 */
	PROTECT_JAPAN,
	/**
	 * 苏联不能在欧洲政变
	 */
	PROTECT_EUROPE_COUP,
	/**
	 * 取消对法国的保护
	 */
	PROTECT_CANCELD_FRANCE,
	/**
	 * 取消对西德的保护
	 */
	PROTECT_CANCELD_WEST_GERMAN,
}
