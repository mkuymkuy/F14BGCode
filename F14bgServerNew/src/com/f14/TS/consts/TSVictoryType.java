package com.f14.TS.consts;

/**
 * 获胜方式
 * 
 * @author F14eagle
 *
 */
public enum TSVictoryType {
	/**
	 * VP到达20,获胜
	 */
	VP,
	/**
	 * DEFCON降为1,获胜
	 */
	DEFCON,
	/**
	 * 对方保留计分牌
	 */
	SCORE_CARD,
	/**
	 * 特殊胜利条件
	 */
	SPECIAL,
	/**
	 * VP获胜
	 */
	VP_VICTORY;
	
	public static String getChinese(TSVictoryType vt){
		switch(vt){
		case VP:
			return "VP达到20";
		case DEFCON:
			return "对手DEFCON降为1";
		case SCORE_CARD:
			return "对手保留计分牌";
		case SPECIAL:
			return "特殊胜利条件";
		case VP_VICTORY:
			return "VP高于对手";
		}
		return null;
	}
}
