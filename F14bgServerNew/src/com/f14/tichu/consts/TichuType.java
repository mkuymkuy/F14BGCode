package com.f14.tichu.consts;

/**
 * 大小地主
 * 
 * @author F14eagle
 *
 */
public enum TichuType {
	/**
	 * 大地主
	 */
	BIG_TICHU,
	/**
	 * 小地主
	 */
	SMALL_TICHU;
	
	public static String getChinese(TichuType tichuType){
		switch(tichuType){
		case BIG_TICHU:
			return "大地主";
		case SMALL_TICHU:
			return "小地主";
		}
		return null;
	}
}
