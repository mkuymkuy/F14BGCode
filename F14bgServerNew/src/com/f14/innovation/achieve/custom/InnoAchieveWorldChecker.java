package com.f14.innovation.achieve.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.achieve.InnoAchieveChecker;
import com.f14.innovation.consts.InnoIcon;

/**
 * 特殊成就-世界 检查器
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveWorldChecker extends InnoAchieveChecker {

	public InnoAchieveWorldChecker(InnoGameMode gameMode) {
		super(gameMode);
	}

	@Override
	public boolean check(InnoPlayer player) {
		//达到12个时钟图标
		if(player.getIconCount(InnoIcon.CLOCK)>=12){
			return true;
		}
		return false;
	}

}
