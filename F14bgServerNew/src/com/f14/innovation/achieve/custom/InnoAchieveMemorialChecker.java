package com.f14.innovation.achieve.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.achieve.InnoAchieveChecker;

/**
 * 特殊成就-纪念碑 检查器
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveMemorialChecker extends InnoAchieveChecker {

	public InnoAchieveMemorialChecker(InnoGameMode gameMode) {
		super(gameMode);
	}

	@Override
	public boolean check(InnoPlayer player) {
		//回合计分牌或垫底牌达到6张
		if((player.getRoundScoreCount()>=6 || player.getRoundTuckCount()>=6)){
			return true;
		}
		return false;
	}

}
