package com.f14.utils;

import java.util.Vector;

import org.jdice.DiceParser;
import org.jdice.DieRoll;

/**
 * 骰子
 * 
 * @author F14eagle
 *
 */
public class DiceUtils {

	/**
	 * 取得掷骰结果
	 * 
	 * @param s
	 * @return
	 */
	public static int roll(String s){
		Vector<DieRoll> r = DiceParser.parseRoll(s);
		if(r.isEmpty()){
			return -1;
		}else{
			return r.get(0).makeRoll().getTotal();
		}
	}
}
