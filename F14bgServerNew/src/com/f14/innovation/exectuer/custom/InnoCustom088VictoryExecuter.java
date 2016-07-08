package com.f14.innovation.exectuer.custom;

import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoVictoryExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #088-协同合作 判断胜利条件的执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom088VictoryExecuter extends InnoVictoryExecuter {

	public InnoCustom088VictoryExecuter(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public InnoPlayer getVictoryPlayer() {
		InnoPlayer player = this.getTargetPlayer();
		//如果拥有10张或以上的绿色牌,就获得胜利
		InnoCardStack stack = player.getCardStack(InnoColor.GREEN);
		if(stack!=null && stack.size()>=10){
			return player;
		}
		return null;
	}
	
}
