package com.f14.innovation.achieve;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;

/**
 * 成就检查器
 * 
 * @author F14eagle
 *
 */
public abstract class InnoAchieveChecker {
	protected InnoGameMode gameMode;
	
	public InnoAchieveChecker(InnoGameMode gameMode){
		this.gameMode = gameMode;
	}
	
	/**
	 * 执行检查的逻辑,返回结果
	 * 
	 * @param player
	 * @return
	 */
	public abstract boolean check(InnoPlayer player);
}
