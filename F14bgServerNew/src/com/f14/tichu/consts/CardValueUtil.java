package com.f14.tichu.consts;

public class CardValueUtil {

	/**
	 * 取得牌值的描述
	 * 
	 * @param point
	 * @return
	 */
	public static String getCardValue(double point){
		int i = (int)point;
		switch(i){
		case 11:
			return "J";
		case 12:
			return "Q";
		case 13:
			return "K";
		case 14:
			return "A";
		default:
			return i + "";
		}
	}
}
