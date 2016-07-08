package com.f14.TS.consts;

/**
 * 触发类型
 * 
 * @author F14eagle
 *
 */
public enum TrigType {
	/**
	 * 触发类型 - 行动操作
	 */
	ACTION,
	/**
	 * 触发类型 - 发生事件
	 */
	EVENT;
	
	public static String getChinese(TrigType trigType){
		switch(trigType){
		case ACTION:
			return "行动";
		case EVENT:
			return "事件";
		}
		return null;
	}
}
