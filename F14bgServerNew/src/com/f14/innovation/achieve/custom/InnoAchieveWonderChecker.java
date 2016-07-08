package com.f14.innovation.achieve.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.achieve.InnoAchieveChecker;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoSplayDirection;

/**
 * 特殊成就-奇迹 检查器
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveWonderChecker extends InnoAchieveChecker {

	public InnoAchieveWonderChecker(InnoGameMode gameMode) {
		super(gameMode);
	}

	@Override
	public boolean check(InnoPlayer player) {
		//拥有5种颜色,并且都向右或向上展开
		for(InnoColor color : InnoColor.values()){
			InnoCardStack stack = player.getCardStack(color);
			if(stack==null){
				return false;
			}
			if(stack.getSplayDirection()!=InnoSplayDirection.RIGHT && stack.getSplayDirection()!=InnoSplayDirection.UP){
				return false;
			}
		}
		return true;
	}

}
