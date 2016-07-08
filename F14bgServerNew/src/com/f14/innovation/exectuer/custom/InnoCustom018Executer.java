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
 * #018-发酵 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom018Executer extends InnoActionExecuter {

	public InnoCustom018Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//每有2个叶子,就抓一张2
		int i = this.getTargetPlayer().getIconCount(InnoIcon.LEAF);
		int num = i/2;
		if(num>0){
			gameMode.getGame().playerDrawCard(player, 2, num);
		}
	}

}
