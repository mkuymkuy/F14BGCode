package com.f14.TTA.consts;

/**
 * TTA行动类型
 * 
 * @author F14eagle
 *
 */
public enum ActionType {
	/**
	 * 政治行动
	 */
	CIVIL, /**
			 * 军事行动
			 */
	MILITARY;

	/**
	 * 取得中文描述
	 * 
	 * @param actionType
	 * @return
	 */
	public static String getChinese(ActionType actionType) {
		switch (actionType) {
		case CIVIL:
			return "内政";
		case MILITARY:
			return "军事";
		}
		return "";
	}
}
