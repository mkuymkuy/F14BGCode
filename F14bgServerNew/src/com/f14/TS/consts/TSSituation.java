package com.f14.TS.consts;

/**
 * 局势
 * 
 * @author F14eagle
 *
 */
public enum TSSituation{
	/**
	 * 无
	 */
	NONE,
	/**
	 * 在场
	 */
	PRESENCE,
	/**
	 * 支配
	 */
	DOMINATION,
	/**
	 * 控制
	 */
	CONTROL;
	
	public static String getDescr(TSSituation situation){
		switch(situation){
		case NONE:
			return "无";
		case PRESENCE:
			return "在场";
		case DOMINATION:
			return "支配";
		case CONTROL:
			return "控制";
		default:
			return null;
		}
	}
}
