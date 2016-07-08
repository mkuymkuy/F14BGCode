package com.f14.innovation.exectuer.custom;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCardStack;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoSplayDirection;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #033-纸张 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom033Executer extends InnoActionExecuter {

	public InnoCustom033Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}

	@Override
	public void doAction() throws BoardGameException {
		//你每有一种向左展开的颜色,就抓一张[4]
		InnoPlayer player = this.getTargetPlayer();
		int num = 0;
		for(InnoColor color : InnoColor.values()){
			InnoCardStack stack = player.getCardStack(color);
			if(stack!=null && !stack.isEmpty() && stack.getSplayDirection()==InnoSplayDirection.LEFT){
				num += 1;
			}
		}
		if(num>0){
			gameMode.getGame().playerDrawCard(player, 4, num);
			this.setPlayerActived(player);
		}
	}
	
}
