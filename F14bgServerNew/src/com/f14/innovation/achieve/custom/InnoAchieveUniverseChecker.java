package com.f14.innovation.achieve.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.achieve.InnoAchieveChecker;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.consts.InnoColor;

/**
 * 特殊成就-宇宙 检查器
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveUniverseChecker extends InnoAchieveChecker {

	public InnoAchieveUniverseChecker(InnoGameMode gameMode) {
		super(gameMode);
	}

	@Override
	public boolean check(InnoPlayer player) {
		for(InnoColor color : InnoColor.values()){
			InnoCardStack stack = player.getCardStack(color);
			if(stack==null){
				return false;
			}
			if(stack.getTopCard().level<8){
				return false;
			}
		}
		return true;
	}

}
