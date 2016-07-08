package com.f14.TTA.consts;

/**
 * TTA发送指令时用到的一些字符串常量
 * 
 * @author F14eagle
 *
 */
public class TTACmdString {
	/**
	 * 当前世纪
	 */
	public static final String CURRENT_AGE = "CURRENT_AGE";
	/**
	 * 当前文明牌剩余数量
	 */
	public static final String CIVIL_REMAIN = "CIVIL_REMAIN";
	/**
	 * 当前事件剩余数量
	 */
	public static final String EVENT_REMAIN = "EVENT_REMAIN";
	/**
	 * 当前军事牌剩余数量
	 */
	public static final String MILITARY_REMAIN = "MILITARY_REMAIN";

	/**
	 * 玩家结束回合
	 */
	public static final String ACTION_PASS = "pass";
	/**
	 * 玩家从摸牌区拿牌
	 */
	public static final String ACTION_TAKE_CARD = "ACTION_TAKE_CARD";
	/**
	 * 玩家建造建筑/部队/奇迹
	 */
	public static final String ACTION_BUILD = "ACTION_BUILD";
	/**
	 * 玩家建造的请求
	 */
	public static final String REQUEST_BUILD = "REQUEST_BUILD";
	/**
	 * 玩家升级建筑/部队
	 */
	public static final String ACTION_UPGRADE = "ACTION_UPGRADE";
	/**
	 * 玩家升级对象的请求
	 */
	public static final String REQUEST_UPGRADE = "REQUEST_UPGRADE";
	/**
	 * 玩家升级目标的请求
	 */
	public static final String REQUEST_UPGRADE_TO = "REQUEST_UPGRADE_TO";
	/**
	 * 玩家摧毁建筑/部队
	 */
	public static final String ACTION_DESTORY = "ACTION_DESTORY";
	/**
	 * 玩家摧毁建造的请求
	 */
	public static final String REQUEST_DESTORY = "REQUEST_DESTORY";
	/**
	 * 玩家扩张人口
	 */
	public static final String ACTION_POPULATION = "ACTION_POPULATION";
	/**
	 * 玩家打出手牌
	 */
	public static final String ACTION_PLAY_CARD = "ACTION_PLAY_CARD";
	/**
	 * 玩家更换政府
	 */
	public static final String ACTION_CHANGE_GOVERMENT = "ACTION_CHANGE_GOVERMENT";
	/**
	 * 玩家选择建造奇迹的步骤
	 */
	public static final String ACTION_WONDER_STEP = "ACTION_WONDER_STEP";
	/**
	 * 玩家选择失去人口
	 */
	public static final String ACTION_LOSE_POPULATION = "ACTION_LOSE_POPULATION";
	/**
	 * 玩家选择殖民地
	 */
	public static final String ACTION_CHOOSE_COLONY = "ACTION_CHOOSE_COLONY";
	/**
	 * 玩家选择奇迹
	 */
	public static final String ACTION_CHOOSE_WONDER = "ACTION_CHOOSE_WONDER";
	/**
	 * 玩家使用卡牌能力
	 */
	public static final String ACTION_ACTIVE_CARD = "ACTION_ACTIVE_CARD";
	/**
	 * 玩家选择科技
	 */
	public static final String ACTION_CHOOSE_SCIENCE = "ACTION_CHOOSE_SCIENCE";
	/**
	 * 玩家请求废除条约
	 */
	public static final String REQUEST_BREAK_PACT = "REQUEST_BREAK_PACT";
	/**
	 * 玩家选择废除条约
	 */
	public static final String ACTION_BREAK_PACT = "ACTION_BREAK_PACT";

	/**
	 * 政治行动 - 结束政治行动
	 */
	public static final String POLITICAL_PASS = "POLITICAL_PASS";
	/**
	 * 政治行动 - 放置事件牌
	 */
	public static final String POLITICAL_EVENT = "POLITICAL_EVENT";
	/**
	 * 政治行动 - 弃军事牌
	 */
	public static final String POLITICAL_DISCARD = "POLITICAL_DISCARD";
	/**
	 * 体面退出游戏
	 */
	public static final String RESIGN = "RESIGN";
}
