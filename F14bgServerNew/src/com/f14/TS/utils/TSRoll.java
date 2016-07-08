package com.f14.TS.utils;

import com.f14.utils.DiceUtils;

/**
 * TS的骰子
 * 
 * @author F14eagle
 *
 */
public class TSRoll {
	public static final String DICE = "D6";

	/**
	 * 掷骰
	 * 
	 * @return
	 */
	public static int roll(){
		return DiceUtils.roll(DICE);
	}
}
