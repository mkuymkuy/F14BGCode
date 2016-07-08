package com.f14.TS.consts;

/**
 * TS的一些常量
 * 
 * @author F14eagle
 *
 */
public class TSConsts {
	/**
	 * 中国牌的编号
	 */
	public static final int CHINA_CARD_NO = 6;
	/**
	 * #103-背叛者的编号
	 */
	public static final int DEFACTOR_CARD_NO = 103;
	/**
	 * #32-联合国干涉的编号
	 */
	public static final int UNI_CARD_NO = 32;
	/**
	 * 最大回合数
	 */
	public static final int MAX_ROUND = 10;
	
	/**
	 * 取得指定回合的手牌数量
	 * 
	 * @param round
	 * @return
	 */
	public static int getRoundHandsNum(int round){
		if(round<4){
			return 8;
		}else{
			return 9;
		}
	}
	
	/**
	 * 取得指定回合的默认轮数
	 * 
	 * @param round
	 * @return
	 */
	public static int getRoundTurnNum(int round){
		return getRoundHandsNum(round)-2;
	}
	
}
