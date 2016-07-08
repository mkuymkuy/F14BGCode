package com.f14.TS.consts;

/**
 * 超级大国
 * 
 * @author F14eagle
 *
 */
public enum SuperPower {
	/**
	 * 美国
	 */
	USA,
	/**
	 * 苏联
	 */
	USSR,
	/**
	 * 无
	 */
	NONE,
	/**
	 * 当前回合玩家
	 */
	CURRENT_PLAYER,
	/**
	 * 出牌的玩家
	 */
	PLAYED_CARD_PLAYER,
	/**
	 * 出牌玩家的对手
	 */
	OPPOSITE_PLAYER;
	
	public static String getChinese(SuperPower power){
		switch(power){
		case USSR:
			return "苏联";
		case USA:
			return "美国";
		default:
			return "";
		}
	}
	
	/**
	 * 取得对方的超级大国
	 * 
	 * @param power
	 * @return
	 */
	public static SuperPower getOppositeSuperPower(SuperPower power){
		switch(power){
		case USSR:
			return USA;
		case USA:
			return USSR;
		default:
			return null;
		}
	}
}
