package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #059-工业化 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom059Executer extends InnoActionExecuter {

	public InnoCustom059Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//每有2个工厂符号,就抓一张[6]垫底
		InnoPlayer player = this.getTargetPlayer();
		int num = player.getIconCount(InnoIcon.FACTORY)/2;
		if(num>0){
			gameMode.getGame().playerDrawAndTuckCard(player, 6, num);
			this.setPlayerActived(player);
		}
	}

}
