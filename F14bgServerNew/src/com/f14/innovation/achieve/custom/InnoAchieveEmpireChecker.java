package com.f14.innovation.achieve.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.achieve.InnoAchieveChecker;
import com.f14.innovation.consts.InnoIcon;

/**
 * 特殊成就-帝国 检查器
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveEmpireChecker extends InnoAchieveChecker {

	public InnoAchieveEmpireChecker(InnoGameMode gameMode) {
		super(gameMode);
	}

	@Override
	public boolean check(InnoPlayer player) {
		//所有图标都达到3个则成功
		for(InnoIcon icon : InnoIcon.values()){
			if(player.getIconCount(icon)<3){
				return false;
			}
		}
		return true;
	}

}
