package com.f14.TS.consts;

/**
 * 阶段
 * 
 * @author F14eagle
 *
 */
public enum TSPhase {
	/**
	 * 冷战早期
	 */
	EARLY,
	/**
	 * 冷战中期
	 */
	MID,
	/**
	 * 冷战后期
	 */
	LATE;
	
	public static String getChineseDesc(TSPhase phase){
		if(phase!=null){
			switch(phase){
			case EARLY:
				return "冷战早期";
			case MID:
				return "冷战中期";
			case LATE:
				return "冷战后期";
			}
		}
		return "";
	}
}
