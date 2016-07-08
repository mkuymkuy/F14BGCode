package com.f14.PuertoRico.consts;

/**
 * 货物类型
 * 
 * @author F14eagle
 *
 */
public enum GoodType {
	/**
	 * 玉米
	 */
	CORN,
	/**
	 * 染料
	 */
	INDIGO,
	/**
	 * 糖
	 */
	SUGAR,
	/**
	 * 烟草
	 */
	TOBACCO,
	/**
	 * 咖啡
	 */
	COFFEE;
	
	/**
	 * 取得货物类型对应的中文描述
	 * 
	 * @param goodType
	 * @return
	 */
	public static String getChinese(GoodType goodType){
		switch(goodType){
		case CORN:
			return "玉米";
		case INDIGO:
			return "染料";
		case SUGAR:
			return "糖";
		case TOBACCO:
			return "烟草";
		case COFFEE:
			return "咖啡";
		default:
			return "";
		}
	}
}
