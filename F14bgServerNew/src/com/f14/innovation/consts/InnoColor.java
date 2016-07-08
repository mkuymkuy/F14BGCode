package com.f14.innovation.consts;

/**
 * Innovation中的卡牌颜色
 * 
 * @author F14eagle
 *
 */
public enum InnoColor {
	/**
	 * 红
	 */
	RED,
	/**
	 * 黄
	 */
	YELLOW,
	/**
	 * 绿
	 */
	GREEN,
	/**
	 * 蓝
	 */
	BLUE,
	/**
	 * 紫
	 */
	PURPLE;
	
	public static String getDescr(InnoColor color){
		switch(color){
		case BLUE:
			return "蓝色";
		case GREEN:
			return "绿色";
		case PURPLE:
			return "紫色";
		case RED:
			return "红色";
		case YELLOW:
			return "黄色";
		}
		return "";
	}
}
