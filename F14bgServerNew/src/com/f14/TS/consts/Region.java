package com.f14.TS.consts;

/**
 * 区域
 * 
 * @author F14eagle
 *
 */
public enum Region {
	/**
	 * 亚洲
	 */
	ASIA,
	/**
	 * 欧洲
	 */
	EUROPE,
	/**
	 * 中东
	 */
	MIDDLE_EAST,
	/**
	 * 中美洲
	 */
	CENTRAL_AMERICA,
	/**
	 * 南美洲
	 */
	SOUTH_AMERICA,
	/**
	 * 非洲
	 */
	AFRICA;
	
	public static String getChineseDescr(Region region){
		switch(region){
		case EUROPE:
			return "欧洲";
		case MIDDLE_EAST:
			return "中东";
		case ASIA:
			return "亚洲";
		case CENTRAL_AMERICA:
			return "中美洲";
		case SOUTH_AMERICA:
			return "南美洲";
		case AFRICA:
			return "非洲";
		}
		return "";
	}
}
