package com.f14.TS.consts;

/**
 * 子区域
 * 
 * @author F14eagle
 *
 */
public enum SubRegion {
	/**
	 * 东欧
	 */
	EAST_EUROPE,
	/**
	 * 西欧
	 */
	WEST_EUROPE,
	/**
	 * 东南亚
	 */
	SOUTHEAST_ASIA;
	
	public static String getChineseDescr(SubRegion region){
		switch(region){
		case EAST_EUROPE:
			return "东欧";
		case WEST_EUROPE:
			return "西欧";
		case SOUTHEAST_ASIA:
			return "东南亚";
		}
		return "";
	}
}
